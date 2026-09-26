package com.mohamedzaitoon.apputilx.apps.examplekotlin.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * CompositionLocal holding whether the Xiaomi liquid glass effect is active.
 */
val LocalGlassEffect = compositionLocalOf { false }

/**
 * Returns a clean, uncolored ambient neutral background brush for liquid glass mode.
 */
fun glassAppBackground(isDark: Boolean): Brush {
    return if (isDark) {
        Brush.verticalGradient(
            listOf(
                Color(0xFF121215),
                Color(0xFF1A1A1E),
                Color(0xFF0D0D10)
            )
        )
    } else {
        Brush.verticalGradient(
            listOf(
                Color(0xFFEDEFF3),
                Color(0xFFF4F6F9),
                Color(0xFFE8EBF0)
            )
        )
    }
}

/**
 * Applies a crystal-clear, uncolored liquid glass style (wet gloss gradient + bright liquid stroke + floating shadow)
 * WITHOUT blurring internal children or text.
 */
@Composable
fun Modifier.glassContainer(
    enabled: Boolean = LocalGlassEffect.current,
    isDark: Boolean,
    cornerRadius: Dp = 20.dp,
    elevation: Dp = 8.dp
): Modifier {
    if (!enabled) return this

    val shape = RoundedCornerShape(cornerRadius)

    val backgroundColors = if (isDark) {
        listOf(
            Color(0x2DFFFFFF),
            Color(0x0EFFFFFF),
            Color(0x1CFFFFFF)
        )
    } else {
        listOf(
            Color(0xF0FFFFFF),
            Color(0x88FFFFFF),
            Color(0xD0FFFFFF)
        )
    }

    val borderColor = if (isDark) {
        Color(0x80FFFFFF)
    } else {
        Color(0xCCFFFFFF)
    }

    return this
        .shadow(
            elevation = elevation,
            shape = shape,
            clip = false,
            ambientColor = if (isDark) Color(0xCC000000) else Color(0x30000000),
            spotColor = if (isDark) Color(0xFF000000) else Color(0x40000000)
        )
        .clip(shape)
        .background(
            brush = Brush.verticalGradient(backgroundColors),
            shape = shape
        )
        .border(
            width = 1.5.dp,
            brush = Brush.verticalGradient(
                listOf(
                    borderColor,
                    borderColor.copy(alpha = 0.20f)
                )
            ),
            shape = shape
        )
}
