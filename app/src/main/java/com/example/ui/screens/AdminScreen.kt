package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.OrderEntity
import com.example.data.local.entities.UserEntity
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.DarkSurfaceHighlight
import com.example.ui.theme.DiamondCyan
import com.example.ui.theme.FlameOrange
import com.example.ui.theme.GoldCoin
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.StatusRed
import com.example.ui.theme.StatusYellow
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.MainViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AdminScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val orders by viewModel.allOrdersForAdmin.collectAsState()
    val users by viewModel.allUsersList.collectAsState()

    var activeAdminTab by remember { mutableStateOf(0) } // 0: Pedidos, 1: Usuarios, 2: Métricas, 3: Pagos & WhatsApp, 4: Ajustes & Tasa

    var orderFilterStatus by remember { mutableStateOf("TODOS") } // "TODOS", "PENDIENTE", "EN_REVISION", "COMPLETADO", "RECHAZADO"

    // Settings state
    val serviceMode by viewModel.adminServiceMode.collectAsState()
    val rateText by viewModel.adminExchangeRate.collectAsState()
    val coinsPerAd by viewModel.adminCoinsPerAd.collectAsState()
    val dailyLimit by viewModel.adminDailyLimit.collectAsState()
    val whatsappText by viewModel.adminWhatsapp.collectAsState()
    val pmBanco by viewModel.adminPmBanco.collectAsState()
    val pmTelefono by viewModel.adminPmTelefono.collectAsState()
    val pmCedula by viewModel.adminPmCedula.collectAsState()
    val binanceId by viewModel.adminBinanceId.collectAsState()
    val adminPassword by viewModel.adminPassword.collectAsState()

    // Adjust user coins modal state
    var selectedUserForCoins by remember { mutableStateOf<UserEntity?>(null) }
    var adjustCoinsInput by remember { mutableStateOf("") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Admin Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { viewModel.navigateTo(AppScreen.ACCOUNT) },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver",
                        tint = TextPrimary
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Panel de Control Total",
                        color = TextPrimary,
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Administración general de Free Fire Zone",
                        color = FlameOrange,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // Subtabs
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                val adminTabs = listOf(
                    "📦 Pedidos (${orders.size})",
                    "👥 Usuarios (${users.size})",
                    "📊 Métricas & Caja",
                    "💳 Pagos & WhatsApp",
                    "⚙️ Tasa & Horarios"
                )
                items(adminTabs.size) { index ->
                    val isSelected = activeAdminTab == index
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) FlameOrange else DarkSurfaceElevated)
                            .clickable { activeAdminTab = index }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = adminTabs[index],
                            color = if (isSelected) TextPrimary else TextSecondary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        when (activeAdminTab) {
            0 -> {
                // TAB 0: PEDIDOS MANAGER
                item {
                    // Filters row
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        val filters = listOf("TODOS", "PENDIENTE", "EN_REVISION", "COMPLETADO", "RECHAZADO")
                        items(filters) { f ->
                            val isSel = orderFilterStatus == f
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSel) DarkSurfaceHighlight else DarkSurfaceElevated)
                                    .clickable { orderFilterStatus = f }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = when (f) {
                                        "TODOS" -> "Todos"
                                        "PENDIENTE" -> "🟡 Pendientes"
                                        "EN_REVISION" -> "🔵 En Revisión"
                                        "COMPLETADO" -> "🟢 Completados"
                                        else -> "🔴 Rechazados"
                                    },
                                    color = if (isSel) FlameOrange else TextSecondary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                val filteredOrders = if (orderFilterStatus == "TODOS") {
                    orders
                } else {
                    orders.filter { it.status == orderFilterStatus }
                }

                if (filteredOrders.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No hay pedidos en este estado.", color = TextSecondary, fontSize = 13.sp)
                        }
                    }
                } else {
                    items(filteredOrders) { order ->
                        AdminOrderCard(
                            order = order,
                            whatsappNumber = whatsappText,
                            onUpdateStatus = { newStatus -> viewModel.updateOrderStatus(order.id, newStatus) },
                            onOpenWhatsApp = {
                                val url = "https://wa.me/${whatsappText.replace("+", "").replace(" ", "")}"
                                try {
                                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                                } catch (_: Exception) {}
                            }
                        )
                    }
                }
            }

            1 -> {
                // TAB 1: GESTIÓN DE USUARIOS
                items(users) { u ->
                    AdminUserCard(
                        user = u,
                        onEditCoins = {
                            selectedUserForCoins = u
                            adjustCoinsInput = u.coinBalance.toString()
                        },
                        onDeleteUser = { viewModel.adminDeleteUser(u.id) }
                    )
                }
            }

            2 -> {
                // TAB 2: MÉTRICAS Y FINANZAS
                item {
                    val completedOrders = orders.filter { it.status == "COMPLETADO" }
                    val totalUsd = completedOrders.sumOf { it.priceUsd }
                    val totalBs = completedOrders.sumOf { it.priceBs }
                    val totalDiamonds = completedOrders.sumOf { it.packDiamonds + it.bonusDiamonds }
                    val totalCoinsCirculating = users.sumOf { it.coinBalance }

                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated)
                        ) {
                            Column(modifier = Modifier.padding(18.dp)) {
                                Text(
                                    text = "📊 Balance de Ventas y Recargas",
                                    color = TextPrimary,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(14.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    MetricCard(
                                        title = "Total Recaudado USD",
                                        value = "$${"%.2f".format(totalUsd)}",
                                        icon = Icons.Default.AttachMoney,
                                        color = StatusGreen,
                                        modifier = Modifier.weight(1f)
                                    )
                                    MetricCard(
                                        title = "Total Recaudado Bs",
                                        value = "Bs ${"%.2f".format(totalBs)}",
                                        icon = Icons.Default.CurrencyExchange,
                                        color = DiamondCyan,
                                        modifier = Modifier.weight(1f)
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    MetricCard(
                                        title = "Diamantes Entregados",
                                        value = "$totalDiamonds 💎",
                                        icon = Icons.Default.Diamond,
                                        color = FlameOrange,
                                        modifier = Modifier.weight(1f)
                                    )
                                    MetricCard(
                                        title = "Pedidos Completados",
                                        value = "${completedOrders.size} / ${orders.size}",
                                        icon = Icons.Default.ShoppingBag,
                                        color = GoldCoin,
                                        modifier = Modifier.weight(1f)
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    MetricCard(
                                        title = "Jugadores Registrados",
                                        value = "${users.size}",
                                        icon = Icons.Default.Group,
                                        color = TextPrimary,
                                        modifier = Modifier.weight(1f)
                                    )
                                    MetricCard(
                                        title = "Monedas en Billeteras",
                                        value = "$totalCoinsCirculating 🪙",
                                        icon = Icons.Default.MonetizationOn,
                                        color = GoldCoin,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            3 -> {
                // TAB 3: MÉTODOS DE PAGO Y WHATSAPP
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated)
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CreditCard, contentDescription = null, tint = FlameOrange)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Datos de Pago y WhatsApp de Atención",
                                    color = TextPrimary,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Text(
                                text = "Estos datos se muestran al usuario al presionar PAGAR.",
                                color = TextSecondary,
                                fontSize = 11.sp
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            OutlinedTextField(
                                value = pmBanco,
                                onValueChange = { viewModel.adminPmBanco.value = it },
                                modifier = Modifier.fillMaxWidth(),
                                label = { Text("Banco Pago Móvil", color = TextSecondary) },
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = FlameOrange,
                                    unfocusedBorderColor = DarkSurfaceHighlight,
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary
                                ),
                                shape = RoundedCornerShape(10.dp)
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = pmTelefono,
                                onValueChange = { viewModel.adminPmTelefono.value = it },
                                modifier = Modifier.fillMaxWidth(),
                                label = { Text("Teléfono Pago Móvil", color = TextSecondary) },
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = FlameOrange,
                                    unfocusedBorderColor = DarkSurfaceHighlight,
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary
                                ),
                                shape = RoundedCornerShape(10.dp)
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = pmCedula,
                                onValueChange = { viewModel.adminPmCedula.value = it },
                                modifier = Modifier.fillMaxWidth(),
                                label = { Text("Cédula Pago Móvil", color = TextSecondary) },
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = FlameOrange,
                                    unfocusedBorderColor = DarkSurfaceHighlight,
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary
                                ),
                                shape = RoundedCornerShape(10.dp)
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = binanceId,
                                onValueChange = { viewModel.adminBinanceId.value = it },
                                modifier = Modifier.fillMaxWidth(),
                                label = { Text("Binance Pay ID (USDT)", color = TextSecondary) },
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = FlameOrange,
                                    unfocusedBorderColor = DarkSurfaceHighlight,
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary
                                ),
                                shape = RoundedCornerShape(10.dp)
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = whatsappText,
                                onValueChange = { viewModel.adminWhatsapp.value = it },
                                modifier = Modifier.fillMaxWidth(),
                                label = { Text("WhatsApp de Recepción de Captures", color = TextSecondary) },
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = FlameOrange,
                                    unfocusedBorderColor = DarkSurfaceHighlight,
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary
                                ),
                                shape = RoundedCornerShape(10.dp)
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = { viewModel.updateAdminSettings() },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = FlameOrange),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.Save, contentDescription = null, tint = TextPrimary)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("GUARDAR MÉTODOS DE PAGO", fontWeight = FontWeight.Bold, color = TextPrimary)
                            }
                        }
                    }
                }
            }

            else -> {
                // TAB 4: AJUSTES, TASA BINANCE & HORARIOS
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated)
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CurrencyExchange, contentDescription = null, tint = StatusGreen)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Tasa Dólar Binance (USDT / VES)",
                                    color = TextPrimary,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Text(
                                text = "Actualiza el precio en Bolívares en toda la aplicación.",
                                color = TextSecondary,
                                fontSize = 11.sp
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = rateText,
                                onValueChange = { viewModel.adminExchangeRate.value = it },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("admin_rate_input"),
                                label = { Text("Tasa de Cambio (Bs / USD)", color = TextSecondary) },
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = FlameOrange,
                                    unfocusedBorderColor = DarkSurfaceHighlight,
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary
                                ),
                                shape = RoundedCornerShape(10.dp)
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Schedule, contentDescription = null, tint = FlameOrange)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Horario de Atención (1:00 PM a 10:00 PM)",
                                    color = TextPrimary,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf(
                                    "AUTO" to "Auto (1PM-10PM)",
                                    "ALWAYS_OPEN" to "🟢 Forzar Activo",
                                    "ALWAYS_CLOSED" to "🔴 Forzar Cerrado"
                                ).forEach { (mode, label) ->
                                    val isSelected = serviceMode == mode
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (isSelected) FlameOrange else DarkBackground)
                                            .clickable { viewModel.adminServiceMode.value = mode }
                                            .padding(vertical = 10.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = label,
                                            color = if (isSelected) TextPrimary else TextSecondary,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = GoldCoin)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Economía de Monedas y Anuncios",
                                    color = TextPrimary,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = coinsPerAd,
                                    onValueChange = { viewModel.adminCoinsPerAd.value = it },
                                    modifier = Modifier.weight(1f),
                                    label = { Text("Monedas / Anuncio", color = TextSecondary, fontSize = 11.sp) },
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = FlameOrange,
                                        unfocusedBorderColor = DarkSurfaceHighlight,
                                        focusedTextColor = TextPrimary,
                                        unfocusedTextColor = TextPrimary
                                    ),
                                    shape = RoundedCornerShape(10.dp)
                                )
                                OutlinedTextField(
                                    value = dailyLimit,
                                    onValueChange = { viewModel.adminDailyLimit.value = it },
                                    modifier = Modifier.weight(1f),
                                    label = { Text("Límite Diario", color = TextSecondary, fontSize = 11.sp) },
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = FlameOrange,
                                        unfocusedBorderColor = DarkSurfaceHighlight,
                                        focusedTextColor = TextPrimary,
                                        unfocusedTextColor = TextPrimary
                                    ),
                                    shape = RoundedCornerShape(10.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Lock, contentDescription = null, tint = DiamondCyan)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Seguridad del Administrador",
                                    color = TextPrimary,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(DarkBackground)
                                    .border(1.dp, DarkSurfaceHighlight, RoundedCornerShape(12.dp))
                                    .padding(14.dp)
                            ) {
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Lock, contentDescription = null, tint = StatusGreen, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "Acceso Maestro Protegido (Privado)",
                                            color = TextPrimary,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "La contraseña confidencial está activa y protegida. Por seguridad, no se muestra en pantalla. Solo tú la conoces para acceder al panel.",
                                        color = TextSecondary,
                                        fontSize = 12.sp,
                                        lineHeight = 16.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(18.dp))

                            Button(
                                onClick = { viewModel.updateAdminSettings() },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("save_admin_settings_button"),
                                colors = ButtonDefaults.buttonColors(containerColor = FlameOrange),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.Save, contentDescription = null, tint = TextPrimary)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("GUARDAR TODOS LOS CAMBIOS", fontWeight = FontWeight.Bold, color = TextPrimary)
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal to adjust user coins
    if (selectedUserForCoins != null) {
        val targetUser = selectedUserForCoins!!
        AlertDialog(
            onDismissRequest = { selectedUserForCoins = null },
            title = {
                Text("Ajustar Saldo de Monedas", color = TextPrimary, fontWeight = FontWeight.Bold)
            },
            text = {
                Column {
                    Text(
                        text = "Usuario: ${targetUser.username}\nUID Free Fire: ${targetUser.freeFireUid.ifBlank { "Sin asignar" }}",
                        color = TextSecondary,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = adjustCoinsInput,
                        onValueChange = { adjustCoinsInput = it.filter { c -> c.isDigit() } },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Nuevo Saldo de Monedas", color = TextSecondary) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = FlameOrange,
                            unfocusedBorderColor = DarkSurfaceHighlight,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        shape = RoundedCornerShape(10.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Button(
                            onClick = {
                                val current = adjustCoinsInput.toIntOrNull() ?: targetUser.coinBalance
                                adjustCoinsInput = (current + 500).toString()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceHighlight),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text("+500 🪙", color = GoldCoin, fontSize = 11.sp)
                        }
                        Button(
                            onClick = {
                                val current = adjustCoinsInput.toIntOrNull() ?: targetUser.coinBalance
                                adjustCoinsInput = (current + 1000).toString()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceHighlight),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text("+1000 🪙", color = GoldCoin, fontSize = 11.sp)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val newCoins = adjustCoinsInput.toIntOrNull() ?: targetUser.coinBalance
                        viewModel.adminAdjustUserCoins(targetUser.id, newCoins)
                        selectedUserForCoins = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = FlameOrange)
                ) {
                    Text("Actualizar Saldo", color = TextPrimary)
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedUserForCoins = null }) {
                    Text("Cancelar", color = TextSecondary)
                }
            },
            containerColor = DarkSurfaceElevated
        )
    }
}

@Composable
private fun MetricCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(DarkBackground)
            .border(1.dp, DarkSurfaceHighlight, RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = title, color = TextSecondary, fontSize = 11.sp)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, color = color, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
        }
    }
}

@Composable
private fun AdminUserCard(
    user: UserEntity,
    onEditCoins: () -> Unit,
    onDeleteUser: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(DarkSurfaceElevated)
            .border(1.dp, DarkSurfaceHighlight, RoundedCornerShape(14.dp))
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = user.username, color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(6.dp))
                    if (user.role == "ADMIN") {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(FlameOrange)
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                        ) {
                            Text("ADMIN", color = TextPrimary, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                Text(text = "Contacto: ${user.emailOrPhone}", color = TextSecondary, fontSize = 11.sp)
                Text(
                    text = "UID Free Fire: ${if (user.freeFireUid.isNotBlank()) user.freeFireUid else "Sin UID"}",
                    color = DiamondCyan,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Saldo: ${user.coinBalance} 🪙 • Diamantes canjeados: ${user.diamondsRedeemed} 💎",
                    color = GoldCoin,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Button(
                    onClick = onEditCoins,
                    colors = ButtonDefaults.buttonColors(containerColor = GoldCoin),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text("🪙 Editar", color = DarkBackground, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                if (user.role != "ADMIN") {
                    IconButton(onClick = onDeleteUser, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = StatusRed, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminOrderCard(
    order: OrderEntity,
    whatsappNumber: String,
    onUpdateStatus: (String) -> Unit,
    onOpenWhatsApp: () -> Unit
) {
    val dateStr = SimpleDateFormat("dd/MM/yyyy • HH:mm", Locale.getDefault()).format(Date(order.createdAt))

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(DarkSurfaceElevated)
            .border(1.dp, DarkSurfaceHighlight, RoundedCornerShape(14.dp))
            .padding(14.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Orden #${order.orderNumber}",
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "• $dateStr", color = TextSecondary, fontSize = 11.sp)
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            when (order.status) {
                                "COMPLETADO" -> StatusGreen.copy(alpha = 0.2f)
                                "RECHAZADO" -> StatusRed.copy(alpha = 0.2f)
                                "EN_REVISION" -> DiamondCyan.copy(alpha = 0.2f)
                                else -> StatusYellow.copy(alpha = 0.2f)
                            }
                        )
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = order.status,
                        color = when (order.status) {
                            "COMPLETADO" -> StatusGreen
                            "RECHAZADO" -> StatusRed
                            "EN_REVISION" -> DiamondCyan
                            else -> StatusYellow
                        },
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "💎 ${order.packName} • Usuario: ${order.username}",
                color = DiamondCyan,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp
            )
            Text(
                text = "🆔 UID Free Fire: ${order.freeFireUid}",
                color = FlameOrange,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
            Text(
                text = "Monto: $${"%.2f".format(order.priceUsd)} USD / Bs ${"%.2f".format(order.priceBs)} • Método: ${order.paymentMethod}",
                color = TextSecondary,
                fontSize = 12.sp
            )
            if (order.referenceNumber.isNotBlank()) {
                Text(
                    text = "Referencia / Comprobante: ${order.referenceNumber}",
                    color = GoldCoin,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Admin Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = onOpenWhatsApp,
                    colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceHighlight),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(Icons.Default.Send, contentDescription = null, tint = StatusGreen, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Chat WhatsApp", fontSize = 11.sp, color = TextPrimary)
                }

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Button(
                        onClick = { onUpdateStatus("COMPLETADO") },
                        colors = ButtonDefaults.buttonColors(containerColor = StatusGreen),
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text("Aprobar", fontSize = 10.sp, color = DarkBackground, fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = { onUpdateStatus("EN_REVISION") },
                        colors = ButtonDefaults.buttonColors(containerColor = DiamondCyan),
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text("Revisar", fontSize = 10.sp, color = DarkBackground, fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = { onUpdateStatus("RECHAZADO") },
                        colors = ButtonDefaults.buttonColors(containerColor = StatusRed),
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text("Rechazar", fontSize = 10.sp, color = TextPrimary, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
