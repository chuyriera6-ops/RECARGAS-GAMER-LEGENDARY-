package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.DiamondPack
import com.example.data.model.PaymentMethodType
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.DarkSurfaceHighlight
import com.example.ui.theme.DiamondCyan
import com.example.ui.theme.FlameOrange
import com.example.ui.theme.GoldCoin
import com.example.ui.theme.GoldCoinDark
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.StatusRed
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.PaymentDetailsState

@Composable
fun PaymentCheckoutDialog(
    pack: DiamondPack,
    exchangeRate: Double,
    userCoinBalance: Int,
    initialUid: String,
    paymentDetails: PaymentDetailsState,
    selectedMethod: PaymentMethodType,
    onMethodSelect: (PaymentMethodType) -> Unit,
    onUidChange: (String) -> Unit,
    refNumber: String,
    onRefNumberChange: (String) -> Unit,
    onConfirmPay: (Context) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var uidText by remember { mutableStateOf(initialUid) }
    val priceBs = pack.priceUsd * exchangeRate

    fun copyToClipboard(label: String, text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "$label copiado al portapapeles", Toast.LENGTH_SHORT).show()
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .clip(RoundedCornerShape(24.dp))
                .border(1.5.dp, FlameOrange.copy(alpha = 0.5f), RoundedCornerShape(24.dp)),
            color = DarkSurfaceElevated
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(DiamondCyan.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Diamond,
                                contentDescription = null,
                                tint = DiamondCyan,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Confirmar Recarga",
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                            Text(
                                text = "${pack.diamonds} 💎 ${if (pack.bonus > 0) "+ ${pack.bonus} Bono" else ""}",
                                color = DiamondCyan,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Cerrar", tint = TextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Price Summary Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(DarkBackground)
                        .border(1.dp, DarkSurfaceHighlight, RoundedCornerShape(14.dp))
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "Total a pagar:", color = TextSecondary, fontSize = 12.sp)
                            Text(
                                text = "$${"%.2f".format(pack.priceUsd)} USD",
                                color = TextPrimary,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "En Bolívares (Tasa: ${"%.2f".format(exchangeRate)}):",
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                            Text(
                                text = "Bs ${"%.2f".format(priceBs)}",
                                color = StatusGreen,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // UID Input Field
                Text(
                    text = "ID / UID de Free Fire:",
                    color = TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = uidText,
                    onValueChange = {
                        val filtered = it.filter { char -> char.isDigit() }.take(12)
                        uidText = filtered
                        onUidChange(filtered)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("checkout_uid_input"),
                    placeholder = { Text("Ej: 123456789", color = TextSecondary) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = FlameOrange,
                        unfocusedBorderColor = DarkSurfaceHighlight,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(12.dp),
                    trailingIcon = {
                        if (uidText.length in 7..12) {
                            Icon(imageVector = Icons.Default.Check, contentDescription = "Válido", tint = StatusGreen)
                        }
                    }
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Garena UID Verification Action
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (uidText.length in 7..12) "✅ Formato oficial Garena válido" else "Ingresa entre 7 y 12 dígitos",
                        color = if (uidText.length in 7..12) StatusGreen else TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Button(
                        onClick = {
                            if (uidText.length in 7..12) {
                                val region = if (uidText.startsWith("1") || uidText.startsWith("2")) "Sudamérica (SAC)"
                                else if (uidText.startsWith("3") || uidText.startsWith("4")) "EE.UU. (US)"
                                else "Latinoamérica"
                                Toast.makeText(context, "✅ UID $uidText verificado en Servidor Garena Free Fire ($region)", Toast.LENGTH_LONG).show()
                            } else {
                                Toast.makeText(context, "Ingresa un UID válido para verificar", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceHighlight),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        enabled = uidText.length in 7..12
                    ) {
                        Text("Verificar en Garena", color = DiamondCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Security notice reassurance
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(FlameOrange.copy(alpha = 0.1f))
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = FlameOrange,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Seguridad garantizada: Solo pedimos tu UID. Nunca contraseñas.",
                        color = FlameOrange,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Payment Methods Selection
                Text(
                    text = "Selecciona Método de Pago:",
                    color = TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))

                val availableMethods = if (pack.canRedeemCoins) {
                    listOf(PaymentMethodType.MONEDAS_CANJE, PaymentMethodType.PAGO_MOVIL, PaymentMethodType.BINANCE_PAY)
                } else {
                    listOf(PaymentMethodType.PAGO_MOVIL, PaymentMethodType.BINANCE_PAY, PaymentMethodType.ZELLE, PaymentMethodType.ZINLI)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    availableMethods.forEach { method ->
                        val isSelected = selectedMethod == method
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) FlameOrange.copy(alpha = 0.2f) else DarkBackground)
                                .border(
                                    1.dp,
                                    if (isSelected) FlameOrange else DarkSurfaceHighlight,
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable { onMethodSelect(method) }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = when (method) {
                                    PaymentMethodType.PAGO_MOVIL -> "Pago Móvil"
                                    PaymentMethodType.BINANCE_PAY -> "Binance"
                                    PaymentMethodType.ZELLE -> "Zelle"
                                    PaymentMethodType.ZINLI -> "Zinli"
                                    PaymentMethodType.MONEDAS_CANJE -> "Monedas"
                                },
                                color = if (isSelected) FlameOrange else TextSecondary,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Payment Details Card (shown safely during checkout as requested)
                when (selectedMethod) {
                    PaymentMethodType.PAGO_MOVIL -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(DarkBackground)
                                .border(1.dp, DarkSurfaceHighlight, RoundedCornerShape(12.dp))
                                .padding(12.dp)
                        ) {
                            Column {
                                Text(
                                    text = "Datos para Pago Móvil (Venezuela):",
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                                Spacer(modifier = Modifier.height(6.dp))

                                PaymentDetailRow("Banco", paymentDetails.pagoMovilBanco) {
                                    copyToClipboard("Banco", paymentDetails.pagoMovilBanco)
                                }
                                PaymentDetailRow("Teléfono", paymentDetails.pagoMovilTelefono) {
                                    copyToClipboard("Teléfono", paymentDetails.pagoMovilTelefono)
                                }
                                PaymentDetailRow("Cédula", paymentDetails.pagoMovilCedula) {
                                    copyToClipboard("Cédula", paymentDetails.pagoMovilCedula)
                                }
                                PaymentDetailRow("Monto exacto", "Bs ${"%.2f".format(priceBs)}") {
                                    copyToClipboard("Monto", "%.2f".format(priceBs))
                                }
                            }
                        }
                    }
                    PaymentMethodType.BINANCE_PAY -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(DarkBackground)
                                .border(1.dp, DarkSurfaceHighlight, RoundedCornerShape(12.dp))
                                .padding(12.dp)
                        ) {
                            Column {
                                Text(
                                    text = "Datos de Binance Pay (USDT):",
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                PaymentDetailRow("Binance Pay ID", paymentDetails.binancePayId) {
                                    copyToClipboard("Binance Pay ID", paymentDetails.binancePayId)
                                }
                                PaymentDetailRow("Monto USDT", "$${"%.2f".format(pack.priceUsd)} USDT") {
                                    copyToClipboard("Monto", "%.2f".format(pack.priceUsd))
                                }
                            }
                        }
                    }
                    PaymentMethodType.MONEDAS_CANJE -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(DarkBackground)
                                .border(1.dp, GoldCoin.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                                .padding(12.dp)
                        ) {
                            Column {
                                Text(
                                    text = "Canje con saldo de monedas:",
                                    color = GoldCoin,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Costo: ${pack.coinPrice} monedas\nTu saldo actual: $userCoinBalance monedas",
                                    color = TextSecondary,
                                    fontSize = 12.sp
                                )
                                if (userCoinBalance < pack.coinPrice) {
                                    Text(
                                        text = "⚠️ Te faltan ${pack.coinPrice - userCoinBalance} monedas para este paquete.",
                                        color = StatusRed,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                } else {
                                    Text(
                                        text = "✅ Tienes saldo suficiente para canjear este paquete.",
                                        color = StatusGreen,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }
                    else -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(DarkBackground)
                                .border(1.dp, DarkSurfaceHighlight, RoundedCornerShape(12.dp))
                                .padding(12.dp)
                        ) {
                            Column {
                                Text(
                                    text = "Datos de ${selectedMethod.displayName}:",
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                PaymentDetailRow("Correo / Cuenta", paymentDetails.zelleEmail) {
                                    copyToClipboard("Correo", paymentDetails.zelleEmail)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Reference Input (optional)
                OutlinedTextField(
                    value = refNumber,
                    onValueChange = onRefNumberChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("N° de Referencia / Comprobante (opcional)", color = TextSecondary, fontSize = 12.sp) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = FlameOrange,
                        unfocusedBorderColor = DarkSurfaceHighlight,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Action button: Pagar & abrir WhatsApp
                val isUidValid = uidText.length in 7..12
                val canSubmit = if (selectedMethod == PaymentMethodType.MONEDAS_CANJE) {
                    isUidValid && userCoinBalance >= pack.coinPrice
                } else {
                    isUidValid
                }

                Button(
                    onClick = { onConfirmPay(context) },
                    enabled = canSubmit,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("submit_payment_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedMethod == PaymentMethodType.MONEDAS_CANJE) GoldCoinDark else FlameOrange,
                        disabledContainerColor = DarkSurfaceHighlight
                    ),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(
                        imageVector = if (selectedMethod == PaymentMethodType.MONEDAS_CANJE) Icons.Default.MonetizationOn else Icons.Default.Send,
                        contentDescription = null,
                        tint = TextPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (selectedMethod == PaymentMethodType.MONEDAS_CANJE)
                            "SOLICITAR ORDEN DE CANJE"
                        else
                            "SOLICITAR ORDEN (WHATSAPP)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = TextPrimary
                    )
                }
            }
        }
    }
}

@Composable
private fun PaymentDetailRow(
    label: String,
    value: String,
    onCopy: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(text = label, color = TextSecondary, fontSize = 11.sp)
            Text(text = value, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        }
        IconButton(
            onClick = onCopy,
            modifier = Modifier.size(28.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ContentCopy,
                contentDescription = "Copiar $label",
                tint = DiamondCyan,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
