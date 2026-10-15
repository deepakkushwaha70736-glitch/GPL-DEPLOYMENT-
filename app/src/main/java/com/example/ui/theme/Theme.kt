package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = PoliceBlue600,
    onPrimary = Color.White,
    primaryContainer = PoliceNavy800,
    onPrimaryContainer = PoliceBlue100,
    secondary = PoliceGold,
    onSecondary = PoliceNavy900,
    secondaryContainer = Color(0xFF382D05),
    onSecondaryContainer = PoliceGoldLight,
    background = DarkBackground,
    onBackground = DarkInk,
    surface = DarkSurface,
    onSurface = DarkInk,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkInk,
    outline = DarkOutline,
    outlineVariant = Color(0xFF1E3352)
)

private val LightColorScheme = lightColorScheme(
    primary = PoliceBlue700,
    onPrimary = Color.White,
    primaryContainer = PoliceBlue100,
    onPrimaryContainer = PoliceNavy900,
    secondary = PoliceNavy800,
    onSecondary = Color.White,
    secondaryContainer = PoliceGoldLight,
    onSecondaryContainer = PoliceNavy900,
    background = LightBackground,
    onBackground = LightInk,
    surface = LightSurface,
    onSurface = LightInk,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightInk,
    outline = LightOutline,
    outlineVariant = Color(0xFFD9E2EC)
)

@Composable
fun HathrasPoliceTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Use intentional police brand colors
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

// Retain alias for existing usages
@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) = HathrasPoliceTheme(darkTheme = darkTheme, dynamicColor = dynamicColor, content = content)
