package com.corechiper.assistant.core.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

// Light Palette (#F6F7F9, #FFFFFF, #304F73, #6F91B5, #17212F, #758092, #E7EAF0)
val LightBackground = Color(0xFFF6F7F9)
val LightSurface = Color(0xFFFFFFFF)
val LightPrimary = Color(0xFF304F73)
val LightSecondary = Color(0xFF6F91B5)
val LightTextPrimary = Color(0xFF17212F)
val LightTextSecondary = Color(0xFF758092)
val LightBorder = Color(0xFFE7EAF0)

// Dark Palette (#10151D, #1A2431, #89A9CC, #607F9F, #F1F4F8, #A5B0BE, #303B49)
val DarkBackground = Color(0xFF10151D)
val DarkSurface = Color(0xFF1A2431)
val DarkPrimary = Color(0xFF89A9CC)
val DarkSecondary = Color(0xFF607F9F)
val DarkTextPrimary = Color(0xFFF1F4F8)
val DarkTextSecondary = Color(0xFFA5B0BE)
val DarkBorder = Color(0xFF303B49)

private val LightColorScheme = lightColorScheme(
    primary = LightPrimary,
    onPrimary = Color.White,
    secondary = LightSecondary,
    onSecondary = Color.White,
    background = LightBackground,
    onBackground = LightTextPrimary,
    surface = LightSurface,
    onSurface = LightTextPrimary,
    surfaceVariant = LightBackground,
    onSurfaceVariant = LightTextSecondary,
    outline = LightBorder
)

private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = Color(0xFF10151D),
    secondary = DarkSecondary,
    onSecondary = Color.White,
    background = DarkBackground,
    onBackground = DarkTextPrimary,
    surface = DarkSurface,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkSurface,
    onSurfaceVariant = DarkTextSecondary,
    outline = DarkBorder
)

val CoreChiperShapes = Shapes(
    small = RoundedCornerShape(14.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(20.dp)
)

@Composable
fun CoreChiperTheme(
    themeMode: String = "SYSTEM",
    content: @Composable () -> Unit
) {
    val useDark = when (themeMode) {
        "LIGHT" -> false
        "DARK" -> true
        else -> isSystemInDarkTheme()
    }
    MaterialTheme(
        colorScheme = if (useDark) DarkColorScheme else LightColorScheme,
        shapes = CoreChiperShapes,
        content = content
    )
}
