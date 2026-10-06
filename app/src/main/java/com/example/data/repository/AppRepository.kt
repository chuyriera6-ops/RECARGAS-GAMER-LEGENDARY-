package com.example.data.repository

import android.content.Context
import com.example.data.local.AppDatabase
import com.example.data.local.entities.AppSettingsEntity
import com.example.data.local.entities.CoinTransactionEntity
import com.example.data.local.entities.OrderEntity
import com.example.data.local.entities.SensitivityPresetEntity
import com.example.data.local.entities.UserEntity
import com.example.data.model.DiamondPack
import com.example.data.model.ServiceScheduleInfo
import com.example.util.SecurityUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID

class AppRepository(context: Context) {
    private val db = AppDatabase.getInstance(context)
    private val userDao = db.userDao()
    private val orderDao = db.orderDao()
    private val coinTransactionDao = db.coinTransactionDao()
    private val settingsDao = db.settingsDao()
    private val sensitivityDao = db.sensitivityDao()

    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

    private val _serviceStatus = MutableStateFlow(
        ServiceScheduleInfo(
            isOpen = false,
            startHour = 13,
            endHour = 22,
            statusText = "FUERA DE SERVICIO",
            detailsText = "Horario de atención: 1:00 PM - 10:00 PM"
        )
    )
    val serviceStatus: StateFlow<ServiceScheduleInfo> = _serviceStatus.asStateFlow()

    private val _exchangeRateUsdToVes = MutableStateFlow(52.50)
    val exchangeRateUsdToVes: StateFlow<Double> = _exchangeRateUsdToVes.asStateFlow()

    // Default Diamond Packs as requested
    val diamondPacks = listOf(
        DiamondPack(id = "pack_100", diamonds = 100, bonus = 10, priceUsd = 1.15, canRedeemCoins = true, coinPrice = 10000, tag = "Económico"),
        DiamondPack(id = "pack_310", diamonds = 310, bonus = 31, priceUsd = 3.35, isPopular = true, canRedeemCoins = false, tag = "Popular"),
        DiamondPack(id = "pack_520", diamonds = 520, bonus = 52, priceUsd = 5.45, canRedeemCoins = false, tag = "Más Vendido"),
        DiamondPack(id = "pack_1060", diamonds = 1060, bonus = 106, priceUsd = 10.75, isPopular = true, canRedeemCoins = false, tag = "Recomendado"),
        DiamondPack(id = "pack_2180", diamonds = 2180, bonus = 218, priceUsd = 21.50, canRedeemCoins = false, tag = "Mega Pack"),
        DiamondPack(id = "pack_5600", diamonds = 5600, bonus = 560, priceUsd = 52.00, canRedeemCoins = false, tag = "Tryhard"),
        DiamondPack(id = "pass_booyah", diamonds = 499, bonus = 0, priceUsd = 4.20, canRedeemCoins = false, tag = "Pase Booyah")
    )

    init {
        CoroutineScope(Dispatchers.IO).launch {
            initDefaultSettingsAndAdmin()
            checkServiceStatus()
            loadOrInitGuestUser()
        }
    }

    private suspend fun initDefaultSettingsAndAdmin() {
        val defaultSettings = mapOf(
            "usd_ves_rate" to "52.50",
            "service_start_hour" to "13", // 1:00 PM
            "service_end_hour" to "22",   // 10:00 PM
            "service_mode" to "AUTO",     // "AUTO", "ALWAYS_OPEN", "ALWAYS_CLOSED"
            "coins_per_ad" to "100",
            "daily_ad_limit" to "10",
            "ad_cooldown_seconds" to "25",
            "whatsapp_number" to "+584122871341",
            "pm_banco" to "0102 - Banco de Venezuela",
            "pm_telefono" to "04122871341",
            "pm_cedula" to "29754087",
            "binance_pay_id" to "1132079220",
            "admin_password" to "jerquins16"
        )
        for ((key, value) in defaultSettings) {
            settingsDao.insertSetting(AppSettingsEntity(key, value))
        }
        val rateSetting = settingsDao.getSettingSync("usd_ves_rate")
        _exchangeRateUsdToVes.value = rateSetting?.value?.toDoubleOrNull() ?: 52.50
    }

    fun checkServiceStatus() {
        CoroutineScope(Dispatchers.IO).launch {
            val mode = settingsDao.getSettingSync("service_mode")?.value ?: "AUTO"
            val startHour = settingsDao.getSettingSync("service_start_hour")?.value?.toIntOrNull() ?: 13
            val endHour = settingsDao.getSettingSync("service_end_hour")?.value?.toIntOrNull() ?: 22

            val calendar = Calendar.getInstance()
            val currentHour = calendar.get(Calendar.HOUR_OF_DAY)

            val isOpen = when (mode) {
                "ALWAYS_OPEN" -> true
                "ALWAYS_CLOSED" -> false
                else -> currentHour in startHour until endHour
            }

            val startLabel = if (startHour > 12) "${startHour - 12}:00 PM" else "$startHour:00 AM"
            val endLabel = if (endHour > 12) "${endHour - 12}:00 PM" else "$endHour:00 AM"

            _serviceStatus.value = ServiceScheduleInfo(
                isOpen = isOpen,
                startHour = startHour,
                endHour = endHour,
                statusText = if (isOpen) "🟢 EN SERVICIO" else "🔴 FUERA DE SERVICIO",
                detailsText = if (isOpen)
                    "Atendiendo recargas activamente ($startLabel a $endLabel)"
                else
                    "Horario de atención de $startLabel a $endLabel. Los pedidos se procesarán al abrir."
            )
        }
    }

    private suspend fun loadOrInitGuestUser() {
        val users = userDao.getAllUsers().firstOrNull()
        if (users.isNullOrEmpty()) {
            val salt = SecurityUtil.generateSalt()
            val guest = UserEntity(
                username = "Player_FF",
                emailOrPhone = "jugador@freefire.com",
                passwordHash = SecurityUtil.hashPassword("123456", salt),
                salt = salt,
                freeFireUid = "123456789",
                coinBalance = 500,
                role = "USER",
                lastAdDate = getTodayDateString()
            )
            val id = userDao.insertUser(guest)
            val created = guest.copy(id = id)
            _currentUser.value = created
            coinTransactionDao.insertTransaction(
                CoinTransactionEntity(
                    userId = id,
                    amount = 500,
                    description = "Bono de bienvenida a Recarga Gamer Legendary (RGL)",
                    type = "INITIAL_BONUS"
                )
            )
        } else {
            _currentUser.value = users.first()
        }
    }

    private fun getTodayDateString(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }

    suspend fun login(identifier: String, pass: String): Result<UserEntity> = withContext(Dispatchers.IO) {
        val user = userDao.getUserByUsername(identifier) ?: userDao.getUserByEmailOrPhone(identifier)
        if (user == null) {
            return@withContext Result.failure(Exception("Usuario o correo no encontrado."))
        }
        val isOk = SecurityUtil.verifyPassword(pass, user.salt, user.passwordHash)
        if (!isOk) {
            return@withContext Result.failure(Exception("Contraseña incorrecta."))
        }
        _currentUser.value = user
        return@withContext Result.success(user)
    }

    suspend fun register(username: String, contact: String, pass: String, defaultUid: String): Result<UserEntity> = withContext(Dispatchers.IO) {
        if (username.length < 3) return@withContext Result.failure(Exception("El nombre debe tener al menos 3 caracteres."))
        if (pass.length < 5) return@withContext Result.failure(Exception("La contraseña debe tener al menos 5 caracteres."))
        
        val existingUsername = userDao.getUserByUsername(username)
        if (existingUsername != null) return@withContext Result.failure(Exception("El nombre de usuario ya está registrado."))
        
        val existingContact = userDao.getUserByEmailOrPhone(contact)
        if (existingContact != null) return@withContext Result.failure(Exception("Este contacto ya está registrado."))

        val salt = SecurityUtil.generateSalt()
        val hash = SecurityUtil.hashPassword(pass, salt)
        val newUser = UserEntity(
            username = username,
            emailOrPhone = contact,
            passwordHash = hash,
            salt = salt,
            freeFireUid = defaultUid.trim(),
            coinBalance = 500,
            role = if (username.equals("admin", ignoreCase = true)) "ADMIN" else "USER",
            lastAdDate = getTodayDateString()
        )
        val id = userDao.insertUser(newUser)
        val user = newUser.copy(id = id)
        _currentUser.value = user
        coinTransactionDao.insertTransaction(
            CoinTransactionEntity(
                userId = id,
                amount = 500,
                description = "Bono de bienvenida por registro",
                type = "INITIAL_BONUS"
            )
        )
        return@withContext Result.success(user)
    }

    suspend fun updateSavedUid(newUid: String): Result<Unit> = withContext(Dispatchers.IO) {
        val user = _currentUser.value ?: return@withContext Result.failure(Exception("No hay sesión activa"))
        if (!SecurityUtil.isValidFreeFireUid(newUid)) {
            return@withContext Result.failure(Exception("El UID debe ser numérico y contener entre 7 y 12 dígitos."))
        }
        userDao.updateUid(user.id, newUid.trim())
        val updated = user.copy(freeFireUid = newUid.trim())
        _currentUser.value = updated
        Result.success(Unit)
    }

    suspend fun rewardAdCompleted(): Result<Int> = withContext(Dispatchers.IO) {
        val user = _currentUser.value ?: return@withContext Result.failure(Exception("Inicia sesión para ganar monedas"))
        val today = getTodayDateString()

        val dailyLimit = settingsDao.getSettingSync("daily_ad_limit")?.value?.toIntOrNull() ?: 10
        val coinsPerAd = settingsDao.getSettingSync("coins_per_ad")?.value?.toIntOrNull() ?: 100
        val cooldownSec = settingsDao.getSettingSync("ad_cooldown_seconds")?.value?.toIntOrNull() ?: 25

        var watchedToday = user.adsWatchedToday
        if (user.lastAdDate != today) {
            watchedToday = 0
        }

        if (watchedToday >= dailyLimit) {
            return@withContext Result.failure(Exception("Has alcanzado el límite diario de $dailyLimit anuncios. Vuelve mañana para seguir acumulando."))
        }

        val now = System.currentTimeMillis()
        val diffSec = (now - user.lastAdWatchTimestamp) / 1000
        if (diffSec < cooldownSec && user.lastAdWatchTimestamp > 0) {
            val remain = cooldownSec - diffSec
            return@withContext Result.failure(Exception("Espera ${remain}s antes de ver otro anuncio."))
        }

        val newBalance = user.coinBalance + coinsPerAd
        val newWatchedCount = watchedToday + 1
        val updated = user.copy(
            coinBalance = newBalance,
            adsWatchedToday = newWatchedCount,
            lastAdDate = today,
            lastAdWatchTimestamp = now
        )
        userDao.updateUser(updated)
        _currentUser.value = updated

        coinTransactionDao.insertTransaction(
            CoinTransactionEntity(
                userId = user.id,
                amount = coinsPerAd,
                description = "Recompensa por ver anuncio publicitario",
                type = "AD_REWARD"
            )
        )

        Result.success(coinsPerAd)
    }

    suspend fun claimDailyCheckin(): Result<Int> = withContext(Dispatchers.IO) {
        val user = _currentUser.value ?: return@withContext Result.failure(Exception("Inicia sesión primero"))
        val today = getTodayDateString()

        if (user.lastCheckInDate == today) {
            return@withContext Result.failure(Exception("Ya reclamaste tu recompensa diaria de hoy."))
        }

        val newStreak = (user.dailyStreak % 7) + 1
        val bonus = when (newStreak) {
            1 -> 150
            2 -> 200
            3 -> 300
            4 -> 400
            5 -> 500
            6 -> 700
            else -> 1000 // Day 7 jackpot
        }

        val newBalance = user.coinBalance + bonus
        val updated = user.copy(
            coinBalance = newBalance,
            dailyStreak = newStreak,
            lastCheckInDate = today
        )
        userDao.updateUser(updated)
        _currentUser.value = updated

        coinTransactionDao.insertTransaction(
            CoinTransactionEntity(
                userId = user.id,
                amount = bonus,
                description = "Recompensa de Racha Diaria (Día $newStreak)",
                type = "DAILY_CHECKIN"
            )
        )

        Result.success(bonus)
    }

    suspend fun redeemPromoCode(code: String): Result<Int> = withContext(Dispatchers.IO) {
        val user = _currentUser.value ?: return@withContext Result.failure(Exception("Inicia sesión"))
        val cleanCode = code.trim().uppercase()

        val validCodes = mapOf(
            "BOOYAH2026" to 500,
            "DIAMANTEFF" to 350,
            "FREEFIRE" to 250,
            "CHUYPRO" to 1000
        )

        val amount = validCodes[cleanCode]
            ?: return@withContext Result.failure(Exception("Código inválido o expirado."))

        // Check if user already used this promo code
        val history = coinTransactionDao.getTransactionsByUserId(user.id).firstOrNull() ?: emptyList()
        val alreadyUsed = history.any { it.description.contains(cleanCode) }
        if (alreadyUsed) {
            return@withContext Result.failure(Exception("Ya has canjeado este código anteriormente."))
        }

        val newBalance = user.coinBalance + amount
        val updated = user.copy(coinBalance = newBalance)
        userDao.updateUser(updated)
        _currentUser.value = updated

        coinTransactionDao.insertTransaction(
            CoinTransactionEntity(
                userId = user.id,
                amount = amount,
                description = "Código Promocional $cleanCode",
                type = "PROMO_CODE"
            )
        )

        Result.success(amount)
    }

    suspend fun createOrder(
        pack: DiamondPack,
        uid: String,
        paymentMethodName: String,
        refNumber: String = ""
    ): Result<OrderEntity> = withContext(Dispatchers.IO) {
        val user = _currentUser.value ?: return@withContext Result.failure(Exception("Inicia sesión"))
        if (!SecurityUtil.isValidFreeFireUid(uid)) {
            return@withContext Result.failure(Exception("El UID de Free Fire debe contener entre 7 y 12 dígitos."))
        }

        // If paying with coins
        if (paymentMethodName.contains("Monedas", ignoreCase = true)) {
            if (user.coinBalance < pack.coinPrice) {
                return@withContext Result.failure(Exception("Saldo insuficiente de monedas (${user.coinBalance}/${pack.coinPrice})."))
            }
            // Deduct coins
            val newBalance = user.coinBalance - pack.coinPrice
            val newRedeemed = user.diamondsRedeemed + pack.diamonds + pack.bonus
            val updatedUser = user.copy(coinBalance = newBalance, diamondsRedeemed = newRedeemed)
            userDao.updateUser(updatedUser)
            _currentUser.value = updatedUser

            coinTransactionDao.insertTransaction(
                CoinTransactionEntity(
                    userId = user.id,
                    amount = -pack.coinPrice,
                    description = "Canje de paquete ${pack.diamonds + pack.bonus} 💎 por monedas",
                    type = "CANJE_DIAMANTES"
                )
            )
        }

        val rate = _exchangeRateUsdToVes.value
        val priceBs = pack.priceUsd * rate
        val randomSuffix = (10000..99999).random()
        val orderNum = "FF-$randomSuffix"

        val order = OrderEntity(
            orderNumber = orderNum,
            userId = user.id,
            username = user.username,
            freeFireUid = uid.trim(),
            packId = pack.id,
            packDiamonds = pack.diamonds,
            bonusDiamonds = pack.bonus,
            packName = "${pack.diamonds} 💎 ${if (pack.bonus > 0) "+ ${pack.bonus} Bono" else ""}",
            priceUsd = pack.priceUsd,
            priceBs = priceBs,
            paymentMethod = paymentMethodName,
            status = if (paymentMethodName.contains("Monedas")) "EN_REVISION" else "PENDIENTE",
            referenceNumber = refNumber
        )

        val id = orderDao.insertOrder(order)
        Result.success(order.copy(id = id))
    }

    fun getUserOrders(userId: Long): Flow<List<OrderEntity>> = orderDao.getOrdersByUserId(userId)
    fun getAllOrders(): Flow<List<OrderEntity>> = orderDao.getAllOrders()
    fun getUserCoinHistory(userId: Long): Flow<List<CoinTransactionEntity>> = coinTransactionDao.getTransactionsByUserId(userId)
    fun getUserPresets(userId: Long): Flow<List<SensitivityPresetEntity>> = sensitivityDao.getPresetsByUserId(userId)

    suspend fun savePreset(preset: SensitivityPresetEntity): Long = withContext(Dispatchers.IO) {
        sensitivityDao.insertPreset(preset)
    }

    suspend fun deletePreset(id: Long) = withContext(Dispatchers.IO) {
        sensitivityDao.deletePreset(id)
    }

    suspend fun updateOrderStatus(orderId: Long, newStatus: String) = withContext(Dispatchers.IO) {
        orderDao.updateOrderStatus(orderId, newStatus)
    }

    suspend fun getSettingValue(key: String): String? = withContext(Dispatchers.IO) {
        settingsDao.getSettingSync(key)?.value
    }

    suspend fun saveSetting(key: String, value: String) = withContext(Dispatchers.IO) {
        settingsDao.insertSetting(AppSettingsEntity(key, value))
        if (key == "usd_ves_rate") {
            _exchangeRateUsdToVes.value = value.toDoubleOrNull() ?: 52.50
        }
        checkServiceStatus()
    }

    suspend fun recoverPassword(identifier: String, newPassword: String): Result<Unit> = withContext(Dispatchers.IO) {
        val user = userDao.getUserByUsername(identifier) ?: userDao.getUserByEmailOrPhone(identifier)
            ?: return@withContext Result.failure(Exception("No existe ninguna cuenta asociada a '$identifier'."))
        
        if (newPassword.length < 5) {
            return@withContext Result.failure(Exception("La nueva contraseña debe tener al menos 5 caracteres."))
        }

        val newSalt = SecurityUtil.generateSalt()
        val newHash = SecurityUtil.hashPassword(newPassword, newSalt)
        userDao.updatePassword(user.id, newHash, newSalt)
        Result.success(Unit)
    }

    fun getAllUsers(): Flow<List<UserEntity>> = userDao.getAllUsers()

    suspend fun adminAdjustUserCoins(userId: Long, newBalance: Int, reason: String = "Ajuste de Administrador") = withContext(Dispatchers.IO) {
        val user = userDao.getUserByIdSync(userId) ?: return@withContext
        val diff = newBalance - user.coinBalance
        userDao.updateCoins(userId, newBalance)
        coinTransactionDao.insertTransaction(
            CoinTransactionEntity(
                userId = userId,
                amount = diff,
                description = reason,
                type = "ADMIN_ADJUST"
            )
        )
        if (_currentUser.value?.id == userId) {
            _currentUser.value = user.copy(coinBalance = newBalance)
        }
    }

    suspend fun adminDeleteUser(userId: Long) = withContext(Dispatchers.IO) {
        userDao.deleteUser(userId)
        if (_currentUser.value?.id == userId) {
            _currentUser.value = null
            loadOrInitGuestUser()
        }
    }

    fun logout() {
        _currentUser.value = null
        CoroutineScope(Dispatchers.IO).launch {
            loadOrInitGuestUser()
        }
    }
}
