package com.sukajee.nepalikeyboard.core.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Typography system.
 *
 * Note on Devanagari rendering:
 * Android's default font stack (Noto Sans Devanagari) handles Nepali script
 * well out of the box. We use FontFamily.Default here which resolves to
 * Roboto for Latin and Noto Sans Devanagari for Devanagari automatically.
 *
 * If you want to bundle a custom Devanagari font (e.g. a rounder/bolder style),
 * add the .ttf to res/font/ and replace FontFamily.Default below.
 */
val NepaliKeyboardTypography = Typography(
    // Used for key labels (Devanagari characters)
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 20.sp,
        lineHeight = 28.sp,
    ),
    // Used for suggestion bar words
    bodyMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        lineHeight = 22.sp,
    ),
    // Used for secondary key labels (long-press characters)
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 10.sp,
        lineHeight = 14.sp,
    ),
    // Used for action key labels (Space, Enter text)
    labelMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
    ),
    // Used for settings screen headings
    titleMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        lineHeight = 24.sp,
    ),
)
