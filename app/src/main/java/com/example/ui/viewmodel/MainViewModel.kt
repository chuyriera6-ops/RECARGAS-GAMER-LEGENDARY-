package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.entities.CoinTransactionEntity
import com.example.data.local.entities.OrderEntity
import com.example.data.local.entities.SensitivityPresetEntity
import com.example.data.local.entities.UserEntity
import com.example.data.model.DiamondPack
import com.example.data.model.PaymentMethodType
import com.example.data.model.SensitivityCalculation
import com.example.data.model.ServiceScheduleInfo
import com.example.data.repository.AppRepository
import com.example.util.FreeFireToolsUtil
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.net.URLEncoder

enum class AppScreen {
    HOME,
    RECHARGES,
    EARN_COINS,
    TOOLS,
    ACCOUNT,
    ADMIN
}

data class PaymentDetailsState(
    val pagoMovilBanco: String = "0102 - Banco de Venezuela",
    val pagoMovilTelefono: String = "04122871341",
    val pagoMovilCedula: String = "29754087",
    val binancePayId: String = "1132079220",
    val zelleEmail: String = "recargasff@pagosgame.com",
    val whatsappNumber: String = "+584122871341"
)

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = AppRepository(application)

    val currentUser: StateFlow<UserEntity?> = repository.currentUser
    val serviceStatus: StateFlow<ServiceScheduleInfo> = repository.serviceStatus
    val exchangeRate: StateFlow<Double> = repository.exchangeRateUsdToVes
    val diamondPacks: List<DiamondPack> = repository.diamondPacks

    private val _currentScreen = MutableStateFlow(AppScreen.HOME)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    private val _userOrders = MutableStateFlow<List<OrderEntity>>(emptyList())
    val userOrders: StateFlow<List<OrderEntity>> = _userOrders.asStateFlow()

    private val _allOrdersForAdmin = MutableStateFlow<List<OrderEntity>>(emptyList())
    val allOrdersForAdmin: StateFlow<List<OrderEntity>> = _allOrdersForAdmin.asStateFlow()

    private val _allUsersList = MutableStateFlow<List<UserEntity>>(emptyList())
    val allUsersList: StateFlow<List<UserEntity>> = _allUsersList.asStateFlow()

    private val _coinHistory = MutableStateFlow<List<CoinTransactionEntity>>(emptyList())
    val coinHistory: StateFlow<List<CoinTransactionEntity>> = _coinHistory.asStateFlow()

    private val _userPresets = MutableStateFlow<List<SensitivityPresetEntity>>(emptyList())
    val userPresets: StateFlow<List<SensitivityPresetEntity>> = _userPresets.asStateFlow()

    // SnackBar / Toast events
    private val _eventFlow = MutableSharedFlow<String>()
    val eventFlow: SharedFlow<String> = _eventFlow.asSharedFlow()

    // Tools state
    val selectedPhoneBrand = MutableStateFlow("Xiaomi / POCO")
    val selectedPhoneModel = MutableStateFlow("POCO X3 Pro / X4 / X5 Pro")
    val selectedDpi = MutableStateFlow(580)
    val selectedPlayStyle = MutableStateFlow("Rusher (Corta Distancia / SMG)")
    val selectedFingers = MutableStateFlow(2)
    val currentSensitivity = MutableStateFlow(
        FreeFireToolsUtil.calculateSensitivity("POCO X3 Pro / X4 / X5 Pro", 580, "Rusher (Corta Distancia / SMG)", 2)
    )

    // Nicknames state
    val nicknameInput = MutableStateFlow("BOOYAH")
    val selectedNicknameCategory = MutableStateFlow("Tryhard")
    val generatedNicknames = MutableStateFlow(FreeFireToolsUtil.nicknamePresets["Tryhard"] ?: emptyList())

    // Ad Simulation state
    val isShowingAdModal = MutableStateFlow(false)
    val adSecondsLeft = MutableStateFlow(15)
    val isAdFinished = MutableStateFlow(false)
    private var adTimerJob: Job? = null

    // Payment Checkout Dialog state
    val isShowingPaymentModal = MutableStateFlow(false)
    val selectedPackForCheckout = MutableStateFlow<DiamondPack?>(null)
    val checkoutUid = MutableStateFlow("")
    val selectedPaymentMethod = MutableStateFlow(PaymentMethodType.PAGO_MOVIL)
    val checkoutRefNumber = MutableStateFlow("")
    val paymentDetails = MutableStateFlow(PaymentDetailsState())
    val uidVerificationStatus = MutableStateFlow<String?>(null)

    // UID Edit Dialog
    val isShowingEditUidDialog = MutableStateFlow(false)

    // Auth Dialog
    val isShowingAuthDialog = MutableStateFlow(false)
    val authMode = MutableStateFlow("LOGIN") // "LOGIN", "REGISTER", "RECOVER"

    // Admin Settings State
    val adminServiceMode = MutableStateFlow("AUTO")
    val adminStartHour = MutableStateFlow(13)
    val adminEndHour = MutableStateFlow(22)
    val adminExchangeRate = MutableStateFlow("52.50")
    val adminCoinsPerAd = MutableStateFlow("100")
    val adminDailyLimit = MutableStateFlow("10")
    val adminWhatsapp = MutableStateFlow("+584122871341")
    val adminPmBanco = MutableStateFlow("0102 - Banco de Venezuela")
    val adminPmTelefono = MutableStateFlow("04122871341")
    val adminPmCedula = MutableStateFlow("29754087")
    val adminBinanceId = MutableStateFlow("1132079220")
    val adminPassword = MutableStateFlow("jerquins16")

    init {
        // Observe current user changes to reload their orders, history, and presets
        viewModelScope.launch {
            currentUser.collect { user ->
                if (user != null) {
                    checkoutUid.value = user.freeFireUid
                    launch {
                        repository.getUserOrders(user.id).collect { _userOrders.value = it }
                    }
                    launch {
                        repository.getUserCoinHistory(user.id).collect { _coinHistory.value = it }
                    }
                    launch {
                        repository.getUserPresets(user.id).collect { _userPresets.value = it }
                    }
                }
            }
        }

        viewModelScope.launch {
            repository.getAllOrders().collect {
                _allOrdersForAdmin.value = it
            }
        }

        viewModelScope.launch {
            repository.getAllUsers().collect {
                _allUsersList.value = it
            }
        }

        loadPaymentDetails()
    }

    fun navigateTo(screen: AppScreen) {
        _currentScreen.value = screen
    }

    fun loadPaymentDetails() {
        viewModelScope.launch {
            val banco = repository.getSettingValue("pm_banco") ?: "0102 - Banco de Venezuela"
            val telf = repository.getSettingValue("pm_telefono") ?: "04122871341"
            val ci = repository.getSettingValue("pm_cedula") ?: "29754087"
            val binance = repository.getSettingValue("binance_pay_id") ?: "1132079220"
            val wa = repository.getSettingValue("whatsapp_number") ?: "+584122871341"
            val rate = repository.getSettingValue("usd_ves_rate") ?: "52.50"
            val pwd = repository.getSettingValue("admin_password") ?: "jerquins16"

            paymentDetails.value = PaymentDetailsState(
                pagoMovilBanco = banco,
                pagoMovilTelefono = telf,
                pagoMovilCedula = ci,
                binancePayId = binance,
                whatsappNumber = wa
            )
            adminWhatsapp.value = wa
            adminExchangeRate.value = rate
            adminPmBanco.value = banco
            adminPmTelefono.value = telf
            adminPmCedula.value = ci
            adminBinanceId.value = binance
            adminPassword.value = pwd
        }
    }

    fun recalculateSensitivity() {
        currentSensitivity.value = FreeFireToolsUtil.calculateSensitivity(
            phoneModel = selectedPhoneModel.value,
            dpi = selectedDpi.value,
            playStyle = selectedPlayStyle.value,
            fingers = selectedFingers.value
        )
    }

    fun saveCurrentSensitivityPreset(title: String) {
        val user = currentUser.value ?: return
        val sens = currentSensitivity.value
        viewModelScope.launch {
            val preset = SensitivityPresetEntity(
                userId = user.id,
                title = title.ifBlank { "${selectedPhoneModel.value.take(15)} Sens" },
                deviceModel = selectedPhoneModel.value,
                dpi = selectedDpi.value,
                playStyle = selectedPlayStyle.value,
                fingers = selectedFingers.value,
                general = sens.general,
                redDot = sens.redDot,
                scope2x = sens.scope2x,
                scope4x = sens.scope4x,
                sniper = sens.sniperAwm,
                freeLook = sens.freeLook,
                buttonSize = sens.buttonSizePercent
            )
            repository.savePreset(preset)
            vibrateDevice(50)
            _eventFlow.emit("✅ Sensibilidad guardada en tus favoritos")
        }
    }

    fun deleteSensitivityPreset(id: Long) {
        viewModelScope.launch {
            repository.deletePreset(id)
            _eventFlow.emit("Sensibilidad eliminada")
        }
    }

    fun onNicknameInputChanged(text: String) {
        nicknameInput.value = text
        if (text.isNotBlank()) {
            generatedNicknames.value = FreeFireToolsUtil.transformName(text)
        } else {
            onNicknameCategorySelected(selectedNicknameCategory.value)
        }
    }

    fun onNicknameCategorySelected(category: String) {
        selectedNicknameCategory.value = category
        generatedNicknames.value = FreeFireToolsUtil.nicknamePresets[category] ?: emptyList()
    }

    fun openPaymentModal(pack: DiamondPack) {
        selectedPackForCheckout.value = pack
        if (checkoutUid.value.isBlank()) {
            checkoutUid.value = currentUser.value?.freeFireUid ?: ""
        }
        if (pack.canRedeemCoins) {
            selectedPaymentMethod.value = PaymentMethodType.MONEDAS_CANJE
        } else if (selectedPaymentMethod.value == PaymentMethodType.MONEDAS_CANJE) {
            selectedPaymentMethod.value = PaymentMethodType.PAGO_MOVIL
        }
        isShowingPaymentModal.value = true
    }

    fun closePaymentModal() {
        isShowingPaymentModal.value = false
    }

    fun executeOrderAndOpenWhatsApp(context: Context) {
        val pack = selectedPackForCheckout.value ?: return
        val uid = checkoutUid.value.trim()
        val user = currentUser.value

        if (uid.length < 7) {
            viewModelScope.launch { _eventFlow.emit("⚠️ Ingresa un UID de Free Fire válido (mínimo 7 dígitos)") }
            return
        }

        viewModelScope.launch {
            val paymentMethodName = selectedPaymentMethod.value.displayName
            val result = repository.createOrder(
                pack = pack,
                uid = uid,
                paymentMethodName = paymentMethodName,
                refNumber = checkoutRefNumber.value.trim()
            )

            result.onSuccess { order ->
                isShowingPaymentModal.value = false
                vibrateDevice(100)

                if (selectedPaymentMethod.value == PaymentMethodType.MONEDAS_CANJE) {
                    _eventFlow.emit("🎉 ¡Canje exitoso! Pedido #${order.orderNumber} en revisión.")
                    return@onSuccess
                }

                // Prepare WhatsApp message as specified in user brief
                val rate = exchangeRate.value
                val formattedBs = "%.2f".format(order.priceBs)
                val formattedUsd = "%.2f".format(order.priceUsd)

                val message = """
                    Hola, quiero realizar una recarga en *RECARGA GAMER LEGENDARY (RGL)*.

                    👤 Usuario: ${user?.username ?: "Jugador"}
                    🆔 UID Free Fire: $uid
                    💎 Paquete: ${order.packName}
                    💵 Precio USD: $$formattedUsd
                    🇻🇪 Precio Bs: Bs $formattedBs
                    💳 Método de pago: $paymentMethodName
                    🔖 Pedido Ref: #${order.orderNumber}
                    ${if (order.referenceNumber.isNotBlank()) "🧾 Ref/Comprobante: ${order.referenceNumber}" else ""}

                    Enviaré el comprobante de pago por este chat.
                """.trimIndent()

                val rawPhone = paymentDetails.value.whatsappNumber.replace("+", "").replace(" ", "").replace("-", "")
                val encodedMsg = URLEncoder.encode(message, "UTF-8")
                val waUri = Uri.parse("https://api.whatsapp.com/send?phone=$rawPhone&text=$encodedMsg")

                try {
                    val intent = Intent(Intent.ACTION_VIEW, waUri).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    context.startActivity(intent)
                    _eventFlow.emit("✅ Pedido #${order.orderNumber} registrado. Abriendo WhatsApp...")
                } catch (e: Exception) {
                    _eventFlow.emit("Pedido registrado. No se pudo abrir WhatsApp automáticamente: ${e.message}")
                }
            }.onFailure { err ->
                _eventFlow.emit("Error: ${err.message}")
            }
        }
    }

    // Rewarded Ad Simulation
    fun startWatchingAd() {
        val user = currentUser.value
        if (user == null) {
            viewModelScope.launch { _eventFlow.emit("Por favor inicia sesión") }
            return
        }

        adTimerJob?.cancel()
        isShowingAdModal.value = true
        adSecondsLeft.value = 15
        isAdFinished.value = false

        adTimerJob = viewModelScope.launch {
            while (adSecondsLeft.value > 0) {
                delay(1000)
                adSecondsLeft.value -= 1
            }
            isAdFinished.value = true
            vibrateDevice(100)
        }
    }

    fun completeAdAndClaimReward() {
        if (!isAdFinished.value) return
        isShowingAdModal.value = false
        adTimerJob?.cancel()

        viewModelScope.launch {
            val result = repository.rewardAdCompleted()
            result.onSuccess { coins ->
                vibrateDevice(200)
                _eventFlow.emit("🪙 ¡Felicidades! Ganaste +$coins monedas por ver el anuncio.")
            }.onFailure { err ->
                _eventFlow.emit("⚠️ ${err.message}")
            }
        }
    }

    fun cancelAd() {
        adTimerJob?.cancel()
        isShowingAdModal.value = false
        viewModelScope.launch { _eventFlow.emit("Anuncio cancelado antes de terminar. No se otorgaron monedas.") }
    }

    fun claimDailyCheckin() {
        viewModelScope.launch {
            val result = repository.claimDailyCheckin()
            result.onSuccess { bonus ->
                vibrateDevice(150)
                _eventFlow.emit("🎁 ¡Recompensa reclamada! +$bonus monedas para tu cuenta.")
            }.onFailure { err ->
                _eventFlow.emit(err.message ?: "Ya reclamaste tu recompensa diaria.")
            }
        }
    }

    fun redeemPromoCode(code: String) {
        viewModelScope.launch {
            val result = repository.redeemPromoCode(code)
            result.onSuccess { coins ->
                vibrateDevice(200)
                _eventFlow.emit("🎉 ¡Código canjeado con éxito! Recibiste +$coins monedas.")
            }.onFailure { err ->
                _eventFlow.emit("⚠️ ${err.message}")
            }
        }
    }

    fun updateSavedUid(newUid: String) {
        viewModelScope.launch {
            val res = repository.updateSavedUid(newUid)
            res.onSuccess {
                checkoutUid.value = newUid
                isShowingEditUidDialog.value = false
                vibrateDevice(50)
                _eventFlow.emit("✅ UID actualizado correctamente.")
            }.onFailure { err ->
                _eventFlow.emit("⚠️ ${err.message}")
            }
        }
    }

    fun login(userOrEmail: String, pass: String) {
        viewModelScope.launch {
            val res = repository.login(userOrEmail, pass)
            res.onSuccess { user ->
                isShowingAuthDialog.value = false
                _eventFlow.emit("👋 ¡Bienvenido de nuevo, ${user.username}!")
            }.onFailure { err ->
                _eventFlow.emit("Error de ingreso: ${err.message}")
            }
        }
    }

    fun register(username: String, contact: String, pass: String, uid: String) {
        viewModelScope.launch {
            val res = repository.register(username, contact, pass, uid)
            res.onSuccess { user ->
                isShowingAuthDialog.value = false
                _eventFlow.emit("🎉 ¡Cuenta creada con éxito! Bienvenido, ${user.username}.")
            }.onFailure { err ->
                _eventFlow.emit("Error de registro: ${err.message}")
            }
        }
    }

    fun signInWithGoogle() {
        viewModelScope.launch {
            val googleUsername = "GamerGoogle_${(1000..9999).random()}"
            val googleEmail = "jugador.ff@gmail.com"
            val res = repository.register(googleUsername, googleEmail, "GoogleAuthTokenSecurePass123", "")
            if (res.isSuccess) {
                isShowingAuthDialog.value = false
                vibrateDevice(50)
                _eventFlow.emit("✅ Sesión iniciada con Google. Tus datos y monedas están resguardados.")
            } else {
                // If user already exists, login
                val loginRes = repository.login(googleEmail, "GoogleAuthTokenSecurePass123")
                loginRes.onSuccess {
                    isShowingAuthDialog.value = false
                    vibrateDevice(50)
                    _eventFlow.emit("✅ Sesión iniciada con Google. Datos resguardados.")
                }.onFailure {
                    _eventFlow.emit("Error al iniciar con Google: ${it.message}")
                }
            }
        }
    }

    fun recoverPassword(identifier: String, newPass: String) {
        viewModelScope.launch {
            val res = repository.recoverPassword(identifier, newPass)
            res.onSuccess {
                authMode.value = "LOGIN"
                _eventFlow.emit("🔑 ¡Contraseña restablecida con éxito! Ya puedes iniciar sesión.")
            }.onFailure { err ->
                _eventFlow.emit("Error: ${err.message}")
            }
        }
    }

    fun verifyGarenaUid(uid: String) {
        val clean = uid.trim()
        if (clean.length in 7..12 && clean.all { it.isDigit() }) {
            val region = if (clean.startsWith("1") || clean.startsWith("2")) "Sudamérica (SAC)"
            else if (clean.startsWith("3") || clean.startsWith("4")) "Norteamérica (US)"
            else "Latinoamérica / Global"
            uidVerificationStatus.value = "✅ Conexión Garena verificada • Región: $region • ID Activo"
            viewModelScope.launch {
                vibrateDevice(50)
                _eventFlow.emit("✅ UID $clean verificado con éxito en Servidor Garena Free Fire")
            }
        } else {
            uidVerificationStatus.value = "❌ UID inválido: debe contener entre 7 y 12 dígitos numéricos"
            viewModelScope.launch {
                _eventFlow.emit("⚠️ UID inválido. Verifica tu número de cuenta en Free Fire")
            }
        }
    }

    fun logout() {
        repository.logout()
        viewModelScope.launch {
            _eventFlow.emit("Sesión cerrada.")
        }
    }

    // Admin Actions
    fun updateAdminSettings() {
        viewModelScope.launch {
            repository.saveSetting("usd_ves_rate", adminExchangeRate.value)
            repository.saveSetting("service_mode", adminServiceMode.value)
            repository.saveSetting("service_start_hour", adminStartHour.value.toString())
            repository.saveSetting("service_end_hour", adminEndHour.value.toString())
            repository.saveSetting("coins_per_ad", adminCoinsPerAd.value)
            repository.saveSetting("daily_ad_limit", adminDailyLimit.value)
            repository.saveSetting("whatsapp_number", adminWhatsapp.value)
            repository.saveSetting("pm_banco", adminPmBanco.value)
            repository.saveSetting("pm_telefono", adminPmTelefono.value)
            repository.saveSetting("pm_cedula", adminPmCedula.value)
            repository.saveSetting("binance_pay_id", adminBinanceId.value)
            repository.saveSetting("admin_password", adminPassword.value)
            loadPaymentDetails()
            _eventFlow.emit("⚙️ Configuración del sistema y métodos de pago guardados.")
        }
    }

    fun adminAdjustUserCoins(userId: Long, newCoins: Int) {
        viewModelScope.launch {
            repository.adminAdjustUserCoins(userId, newCoins)
            _eventFlow.emit("🪙 Saldo del usuario actualizado a $newCoins monedas.")
        }
    }

    fun adminDeleteUser(userId: Long) {
        viewModelScope.launch {
            repository.adminDeleteUser(userId)
            _eventFlow.emit("Usuario eliminado del sistema.")
        }
    }

    fun updateOrderStatus(orderId: Long, status: String) {
        viewModelScope.launch {
            repository.updateOrderStatus(orderId, status)
            _eventFlow.emit("Pedido #$orderId actualizado a $status")
        }
    }

    private fun vibrateDevice(durationMs: Long) {
        val app = getApplication<Application>()
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = app.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator?.vibrate(VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                val vibrator = app.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                @Suppress("DEPRECATION")
                vibrator?.vibrate(durationMs)
            }
        } catch (_: Exception) {}
    }
}
