package com.example.ui.screens

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
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SmartButton
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
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
import com.example.data.local.entities.SensitivityPresetEntity
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.DarkSurfaceHighlight
import com.example.ui.theme.DiamondCyan
import com.example.ui.theme.FlameOrange
import com.example.ui.theme.GoldCoin
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.MainViewModel
import com.example.util.FreeFireToolsUtil

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToolsScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var activeToolTab by remember { mutableStateOf(0) } // 0: Sensibilidad, 1: Nombres, 2: HUD

    fun copyToClipboard(label: String, text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "Copiado: $text", Toast.LENGTH_SHORT).show()
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Tab Navigation
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(DarkSurfaceElevated)
                    .padding(4.dp)
            ) {
                val tabs = listOf("🎯 Sensibilidad", "✨ Nombres", "📱 HUD Layout")
                tabs.forEachIndexed { index, title ->
                    val isSelected = activeToolTab == index
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) FlameOrange else DarkSurfaceElevated)
                            .clickable { activeToolTab = index }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = title,
                            color = if (isSelected) TextPrimary else TextSecondary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        when (activeToolTab) {
            0 -> {
                // SENSITIVITY GENERATOR
                item {
                    SensitivitySection(viewModel = viewModel, onCopy = ::copyToClipboard)
                }
            }
            1 -> {
                // NICKNAME GENERATOR
                item {
                    NicknameSection(viewModel = viewModel, onCopy = ::copyToClipboard)
                }
            }
            else -> {
                // HUD CALCULATOR
                item {
                    HudSection(onCopy = ::copyToClipboard)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SensitivitySection(
    viewModel: MainViewModel,
    onCopy: (String, String) -> Unit
) {
    val selectedBrand by viewModel.selectedPhoneBrand.collectAsState()
    val selectedModel by viewModel.selectedPhoneModel.collectAsState()
    val selectedDpi by viewModel.selectedDpi.collectAsState()
    val selectedPlayStyle by viewModel.selectedPlayStyle.collectAsState()
    val selectedFingers by viewModel.selectedFingers.collectAsState()
    val sensitivity by viewModel.currentSensitivity.collectAsState()
    val presets by viewModel.userPresets.collectAsState()

    var brandExpanded by remember { mutableStateOf(false) }
    var modelExpanded by remember { mutableStateOf(false) }
    var playStyleExpanded by remember { mutableStateOf(false) }
    var presetNameInput by remember { mutableStateOf("") }

    val brands = FreeFireToolsUtil.phoneBrandsWithModels.map { it.first }
    val modelsForBrand = FreeFireToolsUtil.phoneBrandsWithModels.firstOrNull { it.first == selectedBrand }?.second ?: emptyList()

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Configuración del Dispositivo",
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Brand Dropdown
                ExposedDropdownMenuBox(
                    expanded = brandExpanded,
                    onExpandedChange = { brandExpanded = !brandExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedBrand,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Marca del Dispositivo", color = TextSecondary) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = brandExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = FlameOrange,
                            unfocusedBorderColor = DarkSurfaceHighlight,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        shape = RoundedCornerShape(10.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = brandExpanded,
                        onDismissRequest = { brandExpanded = false },
                        modifier = Modifier.background(DarkSurfaceElevated)
                    ) {
                        brands.forEach { brand ->
                            DropdownMenuItem(
                                text = { Text(brand, color = TextPrimary) },
                                onClick = {
                                    viewModel.selectedPhoneBrand.value = brand
                                    val firstModel = FreeFireToolsUtil.phoneBrandsWithModels.firstOrNull { it.first == brand }?.second?.firstOrNull() ?: ""
                                    viewModel.selectedPhoneModel.value = firstModel
                                    viewModel.recalculateSensitivity()
                                    brandExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Model Dropdown
                ExposedDropdownMenuBox(
                    expanded = modelExpanded,
                    onExpandedChange = { modelExpanded = !modelExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedModel,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Modelo del Teléfono", color = TextSecondary) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = modelExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = FlameOrange,
                            unfocusedBorderColor = DarkSurfaceHighlight,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        shape = RoundedCornerShape(10.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = modelExpanded,
                        onDismissRequest = { modelExpanded = false },
                        modifier = Modifier.background(DarkSurfaceElevated)
                    ) {
                        modelsForBrand.forEach { model ->
                            DropdownMenuItem(
                                text = { Text(model, color = TextPrimary) },
                                onClick = {
                                    viewModel.selectedPhoneModel.value = model
                                    viewModel.recalculateSensitivity()
                                    modelExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Playstyle Dropdown
                ExposedDropdownMenuBox(
                    expanded = playStyleExpanded,
                    onExpandedChange = { playStyleExpanded = !playStyleExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedPlayStyle,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Estilo de Juego", color = TextSecondary) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = playStyleExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = FlameOrange,
                            unfocusedBorderColor = DarkSurfaceHighlight,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        shape = RoundedCornerShape(10.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = playStyleExpanded,
                        onDismissRequest = { playStyleExpanded = false },
                        modifier = Modifier.background(DarkSurfaceElevated)
                    ) {
                        FreeFireToolsUtil.playStyles.forEach { style ->
                            DropdownMenuItem(
                                text = { Text(style, color = TextPrimary) },
                                onClick = {
                                    viewModel.selectedPlayStyle.value = style
                                    viewModel.recalculateSensitivity()
                                    playStyleExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Fingers Selector (2, 3, 4 Dedos)
                Text(text = "Cantidad de Dedos:", color = TextSecondary, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(2, 3, 4).forEach { fingers ->
                        val isSelected = selectedFingers == fingers
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) FlameOrange else DarkBackground)
                                .clickable {
                                    viewModel.selectedFingers.value = fingers
                                    viewModel.recalculateSensitivity()
                                }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "$fingers Dedos",
                                color = if (isSelected) TextPrimary else TextSecondary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // DPI Slider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "DPI (Ancho Mínimo):", color = TextSecondary, fontSize = 12.sp)
                    Text(
                        text = if (selectedDpi == 0) "Por Defecto" else "$selectedDpi DPI",
                        color = DiamondCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Slider(
                    value = selectedDpi.toFloat(),
                    onValueChange = {
                        viewModel.selectedDpi.value = it.toInt()
                        viewModel.recalculateSensitivity()
                    },
                    valueRange = 360f..960f,
                    steps = 14,
                    colors = SliderDefaults.colors(
                        thumbColor = FlameOrange,
                        activeTrackColor = FlameOrange,
                        inactiveTrackColor = DarkSurfaceHighlight
                    )
                )
            }
        }

        // Calculated Sensitivities Results Card
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
                        Icon(Icons.Default.SportsEsports, contentDescription = null, tint = FlameOrange)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Sensibilidad Optimizada (Escala 0-200)",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }

                    IconButton(
                        onClick = {
                            val text = """
                                🔥 Sensibilidad Free Fire (${selectedModel})
                                • General: ${sensitivity.general}
                                • Punto Rojo: ${sensitivity.redDot}
                                • Mira 2X: ${sensitivity.scope2x}
                                • Mira 4X: ${sensitivity.scope4x}
                                • Francotirador AWM: ${sensitivity.sniperAwm}
                                • Cámara Libre: ${sensitivity.freeLook}
                                • Botón de Disparo: ${sensitivity.buttonSizePercent}%
                                • DPI: ${if (sensitivity.dpiRecommendation > 0) "${sensitivity.dpiRecommendation} DPI" else "Por defecto"}
                            """.trimIndent()
                            onCopy("Sensibilidad", text)
                        }
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "Copiar", tint = DiamondCyan)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                SensitivityItemRow("General (Giro y movimiento)", sensitivity.general, FlameOrange)
                SensitivityItemRow("Punto Rojo (Mira básica)", sensitivity.redDot, DiamondCyan)
                SensitivityItemRow("Mira 2X", sensitivity.scope2x, StatusGreen)
                SensitivityItemRow("Mira 4X", sensitivity.scope4x, StatusGreen)
                SensitivityItemRow("Francotirador (AWM / Barrett)", sensitivity.sniperAwm, GoldCoin)
                SensitivityItemRow("Cámara Libre 360°", sensitivity.freeLook, TextSecondary)

                Spacer(modifier = Modifier.height(12.dp))

                // Button Size & DPI recommendations
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(DarkBackground)
                            .padding(10.dp)
                    ) {
                        Column {
                            Text(text = "Botón de Disparo:", color = TextSecondary, fontSize = 11.sp)
                            Text(
                                text = "${sensitivity.buttonSizePercent}%",
                                color = FlameOrange,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(DarkBackground)
                            .padding(10.dp)
                    ) {
                        Column {
                            Text(text = "DPI Recomendado:", color = TextSecondary, fontSize = 11.sp)
                            Text(
                                text = if (sensitivity.dpiRecommendation > 0) "${sensitivity.dpiRecommendation}" else "Nativo",
                                color = DiamondCyan,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Pro Tips
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(DarkBackground)
                        .padding(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Lightbulb, contentDescription = null, tint = GoldCoin, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Consejos para Levantar Mira:", color = GoldCoin, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    sensitivity.tips.forEach { tip ->
                        Text(text = tip, color = TextSecondary, fontSize = 11.sp, lineHeight = 16.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Save Preset
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = presetNameInput,
                        onValueChange = { presetNameInput = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Nombre del preset (ej: Mi Sens)", color = TextSecondary, fontSize = 12.sp) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = FlameOrange,
                            unfocusedBorderColor = DarkSurfaceHighlight,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Button(
                        onClick = {
                            viewModel.saveCurrentSensitivityPreset(presetNameInput)
                            presetNameInput = ""
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = FlameOrange),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.height(52.dp)
                    ) {
                        Icon(Icons.Default.Bookmark, contentDescription = null, tint = TextPrimary)
                    }
                }
            }
        }

        // Saved Presets List
        if (presets.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Mis Presets Guardados (${presets.size})",
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    presets.forEach { preset ->
                        PresetRowItem(
                            preset = preset,
                            onCopy = {
                                val text = "Sensibilidad ${preset.title}: Gen ${preset.general} | RedDot ${preset.redDot} | 2X ${preset.scope2x} | 4X ${preset.scope4x} | Botón ${preset.buttonSize}%"
                                onCopy(preset.title, text)
                            },
                            onDelete = { viewModel.deleteSensitivityPreset(preset.id) }
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun SensitivityItemRow(name: String, value: Int, color: Color) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = name, color = TextSecondary, fontSize = 12.sp)
            Text(text = "$value", color = color, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(2.dp))
        LinearProgressIndicator(
            progress = { value / 200f },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = color,
            trackColor = DarkBackground
        )
    }
}

@Composable
private fun PresetRowItem(
    preset: SensitivityPresetEntity,
    onCopy: () -> Unit,
    onDelete: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(DarkBackground)
            .padding(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = preset.title, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Text(
                    text = "${preset.deviceModel} • Gen: ${preset.general} • Botón: ${preset.buttonSize}%",
                    color = TextSecondary,
                    fontSize = 11.sp
                )
            }
            Row {
                IconButton(onClick = onCopy, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.ContentCopy, contentDescription = "Copiar", tint = DiamondCyan, modifier = Modifier.size(16.dp))
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = TextSecondary, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

@Composable
private fun NicknameSection(
    viewModel: MainViewModel,
    onCopy: (String, String) -> Unit
) {
    val nicknameInput by viewModel.nicknameInput.collectAsState()
    val selectedCategory by viewModel.selectedNicknameCategory.collectAsState()
    val generatedNicknames by viewModel.generatedNicknames.collectAsState()

    val categories = listOf("Tryhard", "Clanes", "Cortos", "Con Símbolos", "Agresivos", "Graciosos")

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Name Transformer Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Personaliza Tu Nombre",
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = nicknameInput,
                    onValueChange = { viewModel.onNicknameInputChanged(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("nickname_custom_input"),
                    label = { Text("Escribe tu apodo o palabra", color = TextSecondary) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = FlameOrange,
                        unfocusedBorderColor = DarkSurfaceHighlight,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Invisible Space Helper Tool (Essential for Free Fire players)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(DarkBackground)
                        .border(1.dp, DiamondCyan.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "Espacio Invisible Free Fire (ㅤ)", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text(text = "Copia el caracter invisible para nombres separados", color = TextSecondary, fontSize = 10.sp)
                        }
                        Button(
                            onClick = { onCopy("Espacio Invisible", FreeFireToolsUtil.INVISIBLE_SPACE_CHAR) },
                            colors = ButtonDefaults.buttonColors(containerColor = DiamondCyan),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text("COPIAR", color = DarkBackground, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Category Pills
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 2.dp)
        ) {
            items(categories) { category ->
                val isSelected = selectedCategory == category
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) FlameOrange else DarkSurfaceElevated)
                        .clickable { viewModel.onNicknameCategorySelected(category) }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = category,
                        color = if (isSelected) TextPrimary else TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Generated Nicknames Grid
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            generatedNicknames.forEach { nick ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(DarkSurfaceElevated)
                        .border(1.dp, DarkSurfaceHighlight, RoundedCornerShape(12.dp))
                        .clickable { onCopy("Apodo", nick) }
                        .padding(horizontal = 16.dp, vertical = 14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = nick,
                            color = TextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "Copiar", color = FlameOrange, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.Default.ContentCopy, contentDescription = null, tint = FlameOrange, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HudSection(
    onCopy: (String, String) -> Unit
) {
    var fingers by remember { mutableStateOf(2) }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Calculadora & Layout HUD Personalizado",
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Configuración óptima de botones para tiro fantasma y pared agachado.",
                    color = TextSecondary,
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(2, 3, 4).forEach { f ->
                        val isSelected = fingers == f
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) FlameOrange else DarkBackground)
                                .clickable { fingers = f }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "$f Dedos Claw",
                                color = if (isSelected) TextPrimary else TextSecondary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                when (fingers) {
                    2 -> {
                        HudButtonSpec("Botón de Disparo Principal", "45% - 48%", "Inferior derecha, centrado con margen para pulgar")
                        HudButtonSpec("Botón Pared Gloo", "85% - 90%", "Izquierda media, fácil de tocar con pulgar izquierdo")
                        HudButtonSpec("Botón de Agacharse", "70%", "Justo al lado de la pared gloo para pulsar rápido")
                        HudButtonSpec("Botón de Salto", "65%", "Superior derecha del botón de disparo")
                        HudButtonSpec("Cambio Rápido de Armas", "80%", "Centro izquierda")
                    }
                    3 -> {
                        HudButtonSpec("Botón de Disparo Principal", "42% - 45%", "Inferior derecha")
                        HudButtonSpec("Botón Pared Gloo", "95% - 100%", "Superior izquierda (dedo índice izquierdo)")
                        HudButtonSpec("Botón de Agacharse", "75%", "Cerca del pulgar izquierdo")
                        HudButtonSpec("Botón de Salto", "70%", "Zona derecha superior")
                        HudButtonSpec("Cambio Rápido de Armas", "85%", "Índice o pulgar izquierdo")
                    }
                    else -> {
                        HudButtonSpec("Botón de Disparo (Secundario/Izquierdo)", "85%", "Superior izquierda (índice izq)")
                        HudButtonSpec("Botón Pared Gloo", "100%", "Superior izquierda o pulgar")
                        HudButtonSpec("Botón de Salto", "80%", "Superior derecha (índice derecho)")
                        HudButtonSpec("Botón de Disparo (Levantar)", "38% - 42%", "Inferior derecha (pulgar derecho)")
                        HudButtonSpec("Botón de Agacharse", "75%", "Superior derecha o pulgar izq")
                    }
                }
            }
        }
    }
}

@Composable
private fun HudButtonSpec(name: String, size: String, position: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(DarkBackground)
            .padding(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = name, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Text(text = position, color = TextSecondary, fontSize = 11.sp)
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(FlameOrange.copy(alpha = 0.2f))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(text = size, color = FlameOrange, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
