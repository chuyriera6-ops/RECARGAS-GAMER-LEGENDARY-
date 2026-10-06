package com.example.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.DiamondPack
import com.example.ui.components.ServiceStatusBanner
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.DarkSurfaceHighlight
import com.example.ui.theme.DiamondCyan
import com.example.ui.theme.DiamondCyanVariant
import com.example.ui.theme.FlameOrange
import com.example.ui.theme.GoldCoin
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.MainViewModel

@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val serviceStatus by viewModel.serviceStatus.collectAsState()
    val exchangeRate by viewModel.exchangeRate.collectAsState()
    val diamondPacks = viewModel.diamondPacks

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Card with banner and App Title
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(190.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_rgl_icon),
                        contentDescription = "Recarga Gamer Legendary Banner",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    // Gradient overlay
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    listOf(
                                        Color.Transparent,
                                        DarkBackground.copy(alpha = 0.6f),
                                        DarkBackground.copy(alpha = 0.96f)
                                    )
                                )
                            )
                    )

                    // Text & Branding on bottom
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "RECARGA GAMER",
                                color = TextPrimary,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "LEGENDARY",
                                color = FlameOrange,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(DiamondCyan.copy(alpha = 0.25f))
                                    .border(1.dp, DiamondCyan.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "RGL",
                                    color = DiamondCyan,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }
                        Text(
                            text = "Recargas Seguras & Al Instante • Free Fire UID",
                            color = DiamondCyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        // Service Status Banner: 🟢 EN SERVICIO (1:00 PM a 10:00 PM) / 🔴 FUERA DE SERVICIO
        item {
            ServiceStatusBanner(statusInfo = serviceStatus)
        }

        // Garena ID Verification Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.isShowingEditUidDialog.value = true },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
                border = androidx.compose.foundation.BorderStroke(1.dp, DiamondCyan.copy(alpha = 0.4f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(DiamondCyan.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.SportsEsports, contentDescription = null, tint = DiamondCyan, modifier = Modifier.size(22.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (currentUser?.freeFireUid.isNullOrBlank()) "Verifica tu ID con Garena" else "ID Verificado: ${currentUser?.freeFireUid}",
                                color = TextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (currentUser?.freeFireUid.isNullOrBlank()) "Ingresa tu UID oficial para validar tu cuenta antes de recargar" else "✅ Conexión con Servidor Garena Free Fire activa",
                                color = if (currentUser?.freeFireUid.isNullOrBlank()) FlameOrange else StatusGreen,
                                fontSize = 11.sp
                            )
                        }
                    }
                    Button(
                        onClick = { viewModel.isShowingEditUidDialog.value = true },
                        colors = ButtonDefaults.buttonColors(containerColor = if (currentUser?.freeFireUid.isNullOrBlank()) FlameOrange else DarkSurfaceHighlight),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (currentUser?.freeFireUid.isNullOrBlank()) "VERIFICAR" else "CAMBIAR",
                            color = TextPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Wallet & Player Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(FlameOrange.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = FlameOrange,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = currentUser?.username ?: "Jugador",
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.clickable { viewModel.isShowingEditUidDialog.value = true }
                                ) {
                                    Text(
                                        text = if (currentUser?.freeFireUid.isNullOrBlank()) "UID: Sin configurar" else "UID: ${currentUser?.freeFireUid}",
                                        color = DiamondCyan,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Editar UID",
                                        tint = DiamondCyan,
                                        modifier = Modifier.size(13.dp)
                                    )
                                }
                            }
                        }

                        // Daily Checkin Action
                        Button(
                            onClick = { viewModel.claimDailyCheckin() },
                            colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceHighlight),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                            modifier = Modifier.testTag("daily_streak_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.CardGiftcard,
                                contentDescription = null,
                                tint = GoldCoin,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Día ${currentUser?.dailyStreak ?: 0}",
                                color = GoldCoin,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Balances Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Coins Card
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(14.dp))
                                .background(DarkBackground)
                                .border(1.dp, GoldCoin.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
                                .clickable { viewModel.navigateTo(AppScreen.EARN_COINS) }
                                .padding(12.dp)
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.MonetizationOn,
                                        contentDescription = null,
                                        tint = GoldCoin,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = "Monedas", color = TextSecondary, fontSize = 11.sp)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "${currentUser?.coinBalance ?: 0}",
                                    color = TextPrimary,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Text(
                                    text = "+ Ganar monedas",
                                    color = FlameOrange,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Diamonds Redeemed Card
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(14.dp))
                                .background(DarkBackground)
                                .border(1.dp, DiamondCyan.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
                                .clickable { viewModel.navigateTo(AppScreen.RECHARGES) }
                                .padding(12.dp)
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Diamond,
                                        contentDescription = null,
                                        tint = DiamondCyan,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = "Canjeados", color = TextSecondary, fontSize = 11.sp)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "${currentUser?.diamondsRedeemed ?: 0} 💎",
                                    color = TextPrimary,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Text(
                                    text = "10.000 🪙 = 100 💎",
                                    color = DiamondCyan,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        // Quick Access Section (Acceso Rápido as requested)
        item {
            Text(
                text = "Acceso Rápido",
                color = TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(10.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    QuickActionCard(
                        title = "Recargar Diamantes",
                        subtitle = "Precios USD y Bs",
                        icon = Icons.Default.Diamond,
                        color = DiamondCyan,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.navigateTo(AppScreen.RECHARGES) }
                    )
                    QuickActionCard(
                        title = "Ganar Monedas",
                        subtitle = "Ver anuncios (+🪙)",
                        icon = Icons.Default.MonetizationOn,
                        color = GoldCoin,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.navigateTo(AppScreen.EARN_COINS) }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    QuickActionCard(
                        title = "Sensibilidad",
                        subtitle = "DPI y subida de mira",
                        icon = Icons.Default.SportsEsports,
                        color = FlameOrange,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.navigateTo(AppScreen.TOOLS) }
                    )
                    QuickActionCard(
                        title = "Nombres & Letras",
                        subtitle = "Tryhard y símbolos",
                        icon = Icons.Default.TextFields,
                        color = DiamondCyanVariant,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.navigateTo(AppScreen.TOOLS) }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    QuickActionCard(
                        title = "Historial",
                        subtitle = "Tus compras y canjes",
                        icon = Icons.Default.History,
                        color = StatusGreen,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.navigateTo(AppScreen.ACCOUNT) }
                    )
                    QuickActionCard(
                        title = "Mi Cuenta",
                        subtitle = "Perfil y seguridad",
                        icon = Icons.Default.Person,
                        color = TextSecondary,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.navigateTo(AppScreen.ACCOUNT) }
                    )
                }
            }
        }

        // Featured Diamond Packs
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Paquetes Destacados",
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Ver todos",
                    color = FlameOrange,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable { viewModel.navigateTo(AppScreen.RECHARGES) }
                )
            }
        }

        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(horizontal = 2.dp)
            ) {
                items(diamondPacks.take(4)) { pack ->
                    HomePackCard(
                        pack = pack,
                        exchangeRate = exchangeRate,
                        onBuy = { viewModel.openPaymentModal(pack) }
                    )
                }
            }
        }
    }
}

@Composable
private fun QuickActionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(DarkSurfaceElevated)
            .border(1.dp, DarkSurfaceHighlight, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(text = title, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text(text = subtitle, color = TextSecondary, fontSize = 10.sp)
            }
        }
    }
}

@Composable
private fun HomePackCard(
    pack: DiamondPack,
    exchangeRate: Double,
    onBuy: () -> Unit
) {
    val priceBs = pack.priceUsd * exchangeRate

    Box(
        modifier = Modifier
            .width(160.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(DarkSurfaceElevated)
            .border(1.dp, if (pack.isPopular) FlameOrange else DarkSurfaceHighlight, RoundedCornerShape(16.dp))
            .padding(12.dp)
    ) {
        Column {
            if (pack.isPopular) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(FlameOrange)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(text = "POPULAR", color = TextPrimary, fontSize = 9.sp, fontWeight = FontWeight.ExtraBold)
                }
                Spacer(modifier = Modifier.height(6.dp))
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.Diamond, contentDescription = null, tint = DiamondCyan, modifier = Modifier.size(22.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "${pack.diamonds}",
                    color = TextPrimary,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Black
                )
            }

            if (pack.bonus > 0) {
                Text(
                    text = "+${pack.bonus} Bono Extra",
                    color = StatusGreen,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "$${"%.2f".format(pack.priceUsd)} USD",
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Bs ${"%.2f".format(priceBs)}",
                color = TextSecondary,
                fontSize = 11.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = onBuy,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(36.dp),
                colors = ButtonDefaults.buttonColors(containerColor = FlameOrange),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(0.dp)
            ) {
                Text("RECARGAR", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            }
        }
    }
}
