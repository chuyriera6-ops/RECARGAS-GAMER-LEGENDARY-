package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ServiceScheduleInfo
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.StatusRed
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun ServiceStatusBanner(
    statusInfo: ServiceScheduleInfo,
    modifier: Modifier = Modifier
) {
    var showDialog by remember { mutableStateOf(false) }

    val statusBgColor by animateColorAsState(
        targetValue = if (statusInfo.isOpen) StatusGreen.copy(alpha = 0.15f) else StatusRed.copy(alpha = 0.15f),
        label = "statusBg"
    )
    val statusBorderColor by animateColorAsState(
        targetValue = if (statusInfo.isOpen) StatusGreen.copy(alpha = 0.45f) else StatusRed.copy(alpha = 0.45f),
        label = "statusBorder"
    )
    val dotColor by animateColorAsState(
        targetValue = if (statusInfo.isOpen) StatusGreen else StatusRed,
        label = "dotColor"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(statusBgColor)
            .border(1.dp, statusBorderColor, RoundedCornerShape(14.dp))
            .clickable { showDialog = true }
            .padding(horizontal = 14.dp, vertical = 10.dp)
            .testTag("service_status_banner")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(dotColor)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = statusInfo.statusText,
                        color = if (statusInfo.isOpen) StatusGreen else StatusRed,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = if (statusInfo.isOpen) "Atendiendo 1:00 PM a 10:00 PM" else "Cerrado temporalmente (Horario 1PM-10PM)",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }
            }

            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = "Detalles de horario",
                tint = if (statusInfo.isOpen) StatusGreen else StatusRed,
                modifier = Modifier.size(18.dp)
            )
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        tint = if (statusInfo.isOpen) StatusGreen else StatusRed
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Horario de Servicio", color = TextPrimary)
                }
            },
            text = {
                Column {
                    Text(
                        text = statusInfo.detailsText,
                        color = TextSecondary,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "• Recargas manuales verificadas por WhatsApp\n• Los pedidos realizados fuera del horario serán procesados inmediatamente al iniciar el servicio a la 1:00 PM.\n• Las herramientas y canjes de monedas están disponibles 24/7.",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Entendido", color = MaterialTheme.colorScheme.primary)
                }
            },
            containerColor = DarkSurfaceElevated
        )
    }
}
