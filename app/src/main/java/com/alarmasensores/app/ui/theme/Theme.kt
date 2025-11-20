package com.alarmasensores.app.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryBlue,
    onPrimary = TextPrimaryDark,
    primaryContainer = PrimaryDark,
    onPrimaryContainer = TextPrimaryDark,
    
    secondary = AccentOrange,
    onSecondary = TextPrimaryDark,
    secondaryContainer = AccentDark,
    onSecondaryContainer = TextSecondaryDark,
    
    tertiary = SecureGreen,
    onTertiary = TextPrimaryDark,
    
    error = WarningRed,
    onError = TextPrimaryDark,
    
    background = BackgroundDark,
    onBackground = TextPrimaryDark,
    
    surface = CardDark,
    onSurface = TextPrimaryDark,
    surfaceVariant = AccentDark,
    onSurfaceVariant = TextSecondaryDark,
    
    outline = BorderDark,
    outlineVariant = SlateGray
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryBlue,
    onPrimary = TextPrimaryDark,
    primaryContainer = AccentLight,
    onPrimaryContainer = PrimaryDark,
    
    secondary = AccentOrange,
    onSecondary = TextPrimaryDark,
    secondaryContainer = AccentLight,
    onSecondaryContainer = PrimaryDark,
    
    tertiary = SecureGreen,
    onTertiary = TextPrimaryDark,
    
    error = WarningRed,
    onError = TextPrimaryDark,
    
    background = BackgroundLight,
    onBackground = TextPrimaryLight,
    
    surface = CardLight,
    onSurface = TextPrimaryLight,
    surfaceVariant = BackgroundLightAlt,
    onSurfaceVariant = TextSecondaryLight,
    
    outline = BorderLight,
    outlineVariant = SlateGray
)

@Composable
fun AlarmaSensoresTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
