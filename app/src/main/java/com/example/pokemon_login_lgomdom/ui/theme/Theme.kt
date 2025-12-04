package com.example.pokemon_login_lgomdom.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = PokemonRed,
    onPrimary = PokemonWhite,
    primaryContainer = PokemonYellow,
    onPrimaryContainer = PokemonGray,
    secondary = PokemonBlue,
    onSecondary = PokemonWhite,
    secondaryContainer = PokemonBlueDark,
    onSecondaryContainer = PokemonWhite,
    tertiary = PokemonYellow,
    background = PokemonGrayLight,
    onBackground = PokemonGray,
    surface = PokemonWhite,
    onSurface = PokemonGray,
    surfaceVariant = PokemonYellow.copy(alpha = 0.2f),
    error = PokemonRedDark
)

@Composable
fun Pokemon_login_lgomdomTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
