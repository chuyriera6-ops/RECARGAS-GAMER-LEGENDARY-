package com.example.util

import com.example.data.model.SensitivityCalculation

object FreeFireToolsUtil {

    val phoneBrandsWithModels = listOf(
        "Xiaomi / POCO" to listOf("POCO X3 Pro / X4 / X5 Pro", "POCO X6 / X6 Pro", "Redmi Note 12 / 13 Pro", "Redmi 10 / 12 / 13C", "Xiaomi 13 / 14T"),
        "Samsung" to listOf("Galaxy A54 / A55 5G", "Galaxy A34 / A35", "Galaxy A14 / A15", "Galaxy S22 / S23 / S24", "Galaxy S20 FE / S21 FE"),
        "iPhone / Apple" to listOf("iPhone 11 / 11 Pro", "iPhone 12 / 12 Pro", "iPhone 13 / 13 Pro Max", "iPhone 14 / 14 Pro", "iPhone 15 / 15 Pro / Max"),
        "Motorola" to listOf("Moto G54 / G84 5G", "Moto G14 / G24 / G34", "Edge 40 / 50 Neo / Pro", "Moto G60 / G72"),
        "Infinix / Tecno" to listOf("Infinix Note 30 / 40 Pro", "Infinix Hot 30 / 40 Free Fire Ed.", "Tecno Pova 5 / 6 Pro", "Tecno Camon 20 / 30", "Tecno Spark 20 Pro"),
        "Realme" to listOf("Realme C53 / C55 / C67", "Realme 11 / 12 Pro+", "Realme GT Master / Neo 3")
    )

    val playStyles = listOf(
        "Rusher (Corta Distancia / SMG)",
        "Escopetero (M1014 / Two-Shot M1887)",
        "Preciso en Mira (Media / Larga Distancia)",
        "Soporte / Francotirador (AWM / Barrett)",
        "Equilibrado (Para Todo Tipo de Armas)"
    )

    fun calculateSensitivity(
        phoneModel: String,
        dpi: Int,
        playStyle: String,
        fingers: Int
    ): SensitivityCalculation {
        // Base values tailored for modern Free Fire meta (Scale 0 to 200)
        var baseGeneral = 192
        var baseRedDot = 186
        var baseScope2x = 178
        var baseScope4x = 168
        var baseSniper = 110
        var baseFreeLook = 155
        var buttonSize = 48
        val dpiRec: Int

        // Phone adjustments
        when {
            phoneModel.contains("iPhone") -> {
                // iOS has high touch sample rate and low native DPI modification
                baseGeneral = 184
                baseRedDot = 176
                baseScope2x = 170
                baseScope4x = 162
                baseSniper = 98
                buttonSize = 42
                dpiRec = 0 // iOS uses native sensitivity
            }
            phoneModel.contains("Xiaomi") || phoneModel.contains("POCO") -> {
                baseGeneral = 196
                baseRedDot = 192
                baseScope2x = 184
                baseScope4x = 176
                baseSniper = 118
                buttonSize = 46
                dpiRec = if (dpi > 0) dpi else 580
            }
            phoneModel.contains("Samsung") -> {
                baseGeneral = 198
                baseRedDot = 194
                baseScope2x = 186
                baseScope4x = 178
                baseSniper = 112
                buttonSize = 47
                dpiRec = if (dpi > 0) dpi else 600
            }
            phoneModel.contains("Infinix") || phoneModel.contains("Tecno") -> {
                baseGeneral = 200
                baseRedDot = 196
                baseScope2x = 190
                baseScope4x = 182
                baseSniper = 124
                buttonSize = 50
                dpiRec = if (dpi > 0) dpi else 520
            }
            else -> {
                baseGeneral = 194
                baseRedDot = 188
                baseScope2x = 180
                baseScope4x = 172
                baseSniper = 115
                buttonSize = 48
                dpiRec = if (dpi > 0) dpi else 560
            }
        }

        // Adjust based on playstyle
        when {
            playStyle.contains("Rusher") -> {
                baseGeneral = (baseGeneral + 6).coerceAtMost(200)
                baseRedDot = (baseRedDot + 4).coerceAtMost(200)
                buttonSize = (buttonSize - 4).coerceAtLeast(38)
            }
            playStyle.contains("Escopetero") -> {
                baseGeneral = 200
                baseRedDot = 198
                buttonSize = 44
            }
            playStyle.contains("Preciso") -> {
                baseGeneral = (baseGeneral - 8).coerceAtLeast(150)
                baseScope2x = (baseScope2x + 8).coerceAtMost(200)
                baseScope4x = (baseScope4x + 10).coerceAtMost(200)
                buttonSize = (buttonSize + 3).coerceAtMost(56)
            }
            playStyle.contains("Soporte") -> {
                baseSniper = (baseSniper + 25).coerceAtMost(165)
                baseScope4x = (baseScope4x + 6).coerceAtMost(196)
            }
        }

        // Fingers modification
        when (fingers) {
            2 -> {
                // 2 fingers needs slightly larger button for consistency
                buttonSize = (buttonSize + 3).coerceIn(44, 55)
            }
            3 -> {
                buttonSize = (buttonSize - 1).coerceIn(40, 50)
            }
            4 -> {
                // 4 fingers can afford smaller button and faster movement
                buttonSize = (buttonSize - 4).coerceIn(36, 45)
                baseGeneral = (baseGeneral + 4).coerceAtMost(200)
            }
        }

        val tips = mutableListOf<String>()
        tips.add("🔥 Subida de mira (Meta 200): Con la nueva escala de Free Fire hasta 200, la subida en 'J' responde con mayor aceleración.")
        tips.add("🎯 Botón de disparo: Ubícalo en la mitad inferior a $buttonSize% para maximizar el área de deslizamiento del pulgar.")
        if (dpiRec > 0) {
            tips.add("📱 DPI recomendado en Desarrollador: $dpiRec DPI (Velocidad de puntero al 100%).")
        } else {
            tips.add("🍎 Para iPhone: Velocidad de desplazamiento al máximo y Cursor móvil en 120 (Refinado).")
        }
        tips.add("🛡️ Pared Gloo: Coloca el botón de pared al lado izquierdo a más de 85% para poner paredes agachado al instante.")

        return SensitivityCalculation(
            general = baseGeneral.coerceIn(80, 200),
            redDot = baseRedDot.coerceIn(80, 200),
            scope2x = baseScope2x.coerceIn(80, 200),
            scope4x = baseScope4x.coerceIn(80, 200),
            sniperAwm = baseSniper.coerceIn(40, 180),
            freeLook = baseFreeLook.coerceIn(80, 200),
            buttonSizePercent = buttonSize,
            dpiRecommendation = dpiRec,
            tips = tips
        )
    }

    // Hangul filler character used in Free Fire for invisible space
    const val INVISIBLE_SPACE_CHAR = "\u3164"

    val nicknamePresets = mapOf(
        "Tryhard" to listOf(
            "亗 ＡＫ４７ 亗",
            "꧁༺ ₦Ї₦ℑ₳ ༻꧂",
            "⚡ ＤＥＳＴＲＯＹ ⚡",
            "亗 ＫＩＬＬＥＲ 亗",
            "✞ 𝔖𝔥𝔞𝔡𝔬𝔴 ✞",
            "★ 𝚅𝙴𝙽𝙾𝙼 ★",
            "꧁ঔৣ☬✞ＰＲＯ✞☬ঔৣ꧂",
            "亗 ＩＭＰＡＣＴ 亗",
            "ᴮᴼˢˢ 々 ＺＥＵＳ",
            "×͜× ＳＡＤ ＢＯＹ"
        ),
        "Clanes" to listOf(
            "炎 ＴＥＡＭ 炎",
            "꧁༒₭ÏḼḼ℥℟༒꧂",
            "亗 ＥＳＰＯＲＴＳ 亗",
            "⚡ ＢＯＯＹＡＨ ⚡",
            "★ ＭＡＦＩＡ ★",
            "⚔️ ＶＡＬＫＹＲＩＥ ⚔️",
            "亗 ＩＮＦＥＲＮＯ 亗",
            "꧁ ＫＩＮＧＳ ꧂"
        ),
        "Cortos" to listOf(
            "亗 ＺＥＤ",
            "ＫＡＩ",
            "ＮＥＯ",
            "ＲＥＸ",
            "ＶＯＸ",
            "ＺＯＤ",
            "ＡＸＥ",
            "ＦＯＸ"
        ),
        "Con Símbolos" to listOf(
            "꧁༺ 𝕯𝖊𝖆𝖙𝖍 ༻꧂",
            "⚡亗 ＮＩＧＨＴ 亗⚡",
            "✿ ＱＵＥＥＮ ✿",
            "♛ ＫＩＮＧ ♛",
            "☠︎ ＳＫＵＬＬ ☠︎",
            "꧁༒•P丹N匕HΣR•༒꧂",
            "『ᵀᶜ』ＢＡＤ ＢＯＹ",
            "★彡[ ＧＯＤ ]彡★"
        ),
        "Agresivos" to listOf(
            "亗 ＨＥＡＤＳＨＯＴ 亗",
            "ＴＯＸＩＣ ☠️",
            "ＲＵＳＨＥＲ ⚡",
            "ＤＥＭＯＮ 亗",
            "ＳＩＬＥＮＴ ＫＩＬＬ",
            "ＮＯ ＭＥＲＣＹ ⚔️"
        ),
        "Graciosos" to listOf(
            "ＭＡＮＣＯ ＰＥＲＯ ＦＥＬＩＺ",
            "ＴＵ ＰＡＰＡ 亗",
            "ＮＯ ＭＥ ＭＡＴＥＳ",
            "ＬＡＧ ９９９+",
            "ＢＯＴ ＣＯＮ ＲＯＰＡ",
            "ＭＥ ＣＡＩ ＤＥＬ ＭＡＰＡ"
        )
    )

    fun transformName(base: String): List<String> {
        val clean = base.trim().ifEmpty { "BOOYAH" }
        val upper = clean.uppercase()
        val wide = clean.map { c ->
            when (c) {
                in 'a'..'z' -> (c.code - 'a'.code + 0xFF41).toChar()
                in 'A'..'Z' -> (c.code - 'A'.code + 0xFF21).toChar()
                in '0'..'9' -> (c.code - '0'.code + 0xFF10).toChar()
                else -> c
            }
        }.joinToString("")

        val fraktur = clean.map { c ->
            when (c) {
                in 'A'..'Z' -> String(Character.toChars(0x1D504 + (c - 'A')))
                in 'a'..'z' -> String(Character.toChars(0x1D51E + (c - 'a')))
                else -> c.toString()
            }
        }.joinToString("")

        val script = clean.map { c ->
            when (c) {
                in 'A'..'Z' -> String(Character.toChars(0x1D4D0 + (c - 'A')))
                in 'a'..'z' -> String(Character.toChars(0x1D4EA + (c - 'a')))
                else -> c.toString()
            }
        }.joinToString("")

        return listOf(
            "亗 $wide 亗",
            "꧁༺ $clean ༻꧂",
            "⚡ $wide ⚡",
            "✞ $fraktur ✞",
            "×͜× $upper",
            "★ $clean ★",
            "ᴮᴼˢˢ 々 $upper",
            "『ᵀᶜ』$wide",
            "✿ $script ✿",
            "☠︎ $upper ☠︎"
        )
    }
}
