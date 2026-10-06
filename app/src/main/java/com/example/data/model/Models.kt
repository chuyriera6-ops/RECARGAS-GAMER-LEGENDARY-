package com.example.data.model

data class DiamondPack(
    val id: String,
    val diamonds: Int,
    val bonus: Int,
    val priceUsd: Double,
    val isPopular: Boolean = false,
    val canRedeemCoins: Boolean = false,
    val coinPrice: Int = 0,
    val tag: String = ""
)

enum class PaymentMethodType(val displayName: String, val iconName: String) {
    PAGO_MOVIL("Pago Móvil (Bs)", "pago_movil"),
    BINANCE_PAY("Binance Pay (USDT)", "binance"),
    ZELLE("Zelle ($)", "zelle"),
    ZINLI("Zinli ($)", "zinli"),
    MONEDAS_CANJE("Canje con Monedas", "coins")
}

data class ServiceScheduleInfo(
    val isOpen: Boolean,
    val startHour: Int = 13, // 1:00 PM
    val endHour: Int = 22,   // 10:00 PM
    val statusText: String,
    val detailsText: String
)

data class SensitivityCalculation(
    val general: Int,
    val redDot: Int,
    val scope2x: Int,
    val scope4x: Int,
    val sniperAwm: Int,
    val freeLook: Int,
    val buttonSizePercent: Int,
    val dpiRecommendation: Int,
    val tips: List<String>
)
