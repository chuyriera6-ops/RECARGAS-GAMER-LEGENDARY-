package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val username: String,
    val emailOrPhone: String,
    val passwordHash: String,
    val salt: String,
    val freeFireUid: String = "",
    val coinBalance: Int = 500, // Welcome gift of 500 coins
    val diamondsRedeemed: Int = 0,
    val role: String = "USER", // "USER" or "ADMIN"
    val createdAt: Long = System.currentTimeMillis(),
    val lastAdWatchTimestamp: Long = 0L,
    val adsWatchedToday: Int = 0,
    val lastAdDate: String = "",
    val dailyStreak: Int = 0,
    val lastCheckInDate: String = ""
)

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val orderNumber: String,
    val userId: Long,
    val username: String,
    val freeFireUid: String,
    val packId: String,
    val packDiamonds: Int,
    val bonusDiamonds: Int,
    val packName: String,
    val priceUsd: Double,
    val priceBs: Double,
    val paymentMethod: String,
    val status: String = "PENDIENTE", // "PENDIENTE", "EN_REVISION", "COMPLETADO", "RECHAZADO"
    val referenceNumber: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val notes: String = ""
)

@Entity(tableName = "coin_transactions")
data class CoinTransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: Long,
    val amount: Int, // Positive for earnings, negative for redemptions
    val description: String,
    val type: String, // "AD_REWARD", "DAILY_CHECKIN", "PROMO_CODE", "CANJE_DIAMANTES", "INITIAL_BONUS"
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "app_settings")
data class AppSettingsEntity(
    @PrimaryKey
    val key: String,
    val value: String
)

@Entity(tableName = "sensitivity_presets")
data class SensitivityPresetEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: Long,
    val title: String,
    val deviceModel: String,
    val dpi: Int,
    val playStyle: String,
    val fingers: Int,
    val general: Int,
    val redDot: Int,
    val scope2x: Int,
    val scope4x: Int,
    val sniper: Int,
    val freeLook: Int,
    val buttonSize: Int,
    val createdAt: Long = System.currentTimeMillis()
)
