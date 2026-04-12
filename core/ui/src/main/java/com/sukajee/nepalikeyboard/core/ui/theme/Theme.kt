package com.sukajee.nepalikeyboard.core.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// ── Material3 standard color schemes ───────────────────────────────────────

private val LightColorScheme = lightColorScheme(
    primary = NepaliRed,
    onPrimary = Color.White,
    primaryContainer = NepaliRedLight,
    surface = KeyboardSurfaceLight,
    onSurface = KeyTextLight,
    background = KeyboardBackgroundLight,
    onBackground = KeyTextLight,
)

private val DarkColorScheme = darkColorScheme(
    primary = NepaliRedLight,
    onPrimary = Color.Black,
    primaryContainer = NepaliRedDark,
    surface = KeyboardSurfaceDark,
    onSurface = KeyTextDark,
    background = KeyboardBackgroundDark,
    onBackground = KeyTextDark,
)

// ── Keyboard-specific colors (not part of Material3 roles) ─────────────────

data class KeyboardColors(
    val keyBackground: Color,
    val keyBackgroundSpecial: Color,
    val keyText: Color,
    val keyShadow: Color,
    val keyboardBackground: Color,
    val suggestionBarBackground: Color,
    val suggestionText: Color,
    val spaceKeyBackground: Color,
    val enterKeyBackground: Color,
    val enterKeyText: Color,
)

val LightKeyboardColors = KeyboardColors(
    keyBackground = KeyBackgroundLight,
    keyBackgroundSpecial = KeyBackgroundSpecialLight,
    keyText = KeyTextLight,
    keyShadow = KeyShadowLight,
    keyboardBackground = KeyboardBackgroundLight,
    suggestionBarBackground = SuggestionBarBackgroundLight,
    suggestionText = SuggestionTextLight,
    spaceKeyBackground = SpaceKeyBackgroundLight,
    enterKeyBackground = EnterKeyBackground,
    enterKeyText = EnterKeyText,
)

val DarkKeyboardColors = KeyboardColors(
    keyBackground = KeyBackgroundDark,
    keyBackgroundSpecial = KeyBackgroundSpecialDark,
    keyText = KeyTextDark,
    keyShadow = KeyShadowDark,
    keyboardBackground = KeyboardBackgroundDark,
    suggestionBarBackground = SuggestionBarBackgroundDark,
    suggestionText = SuggestionTextDark,
    spaceKeyBackground = SpaceKeyBackgroundDark,
    enterKeyBackground = EnterKeyBackground,
    enterKeyText = EnterKeyText,
)

val LocalKeyboardColors = staticCompositionLocalOf { LightKeyboardColors }

// ── Public theme entry point ───────────────────────────────────────────────

@Composable
fun NepaliKeyboardTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep false — keyboard has its own palette
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val keyboardColors = if (darkTheme) DarkKeyboardColors else LightKeyboardColors

    CompositionLocalProvider(LocalKeyboardColors provides keyboardColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = NepaliKeyboardTypography,
            content = content,
        )
    }
}

// Convenience accessor — use anywhere in Compose tree
object NepaliKeyboardThemeTokens {
    val keyboardColors: KeyboardColors
        @Composable get() = LocalKeyboardColors.current
}

//@Composable
//fun NepaliKeyboardTheme(
//    darkTheme: Boolean = isSystemInDarkTheme(),
//    // Dynamic color is available on Android 12+
//    dynamicColor: Boolean = true,
//    content: @Composable () -> Unit
//) {
//    val colorScheme = when {
//        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
//            val context = LocalContext.current
//            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
//        }
//
//        darkTheme -> DarkColorScheme
//        else -> LightColorScheme
//    }
//
//    MaterialTheme(
//        colorScheme = colorScheme,
//        typography = Typography,
//        content = content
//    )
//}