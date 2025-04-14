package com.dscorp.ispadmin.presentation.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.dscorp.ispadmin.R

val fontStyle = FontStyle(R.font.robotoflex)
val myTypography = Typography(
    displayLarge = TextStyle(
        fontWeight = FontWeight.W400,
        fontSize = 57.sp,
        letterSpacing = (-0.25).sp,
        lineHeight = 64.sp,
        fontStyle = fontStyle
    ),
    displayMedium = TextStyle(
        fontWeight = FontWeight.W400,
        fontSize = 45.sp,
        letterSpacing = 0.sp,
        lineHeight = 52.sp,
        fontStyle = fontStyle

    ),
    displaySmall = TextStyle(
        fontWeight = FontWeight.W400,
        fontSize = 36.sp,
        letterSpacing = 0.sp,
        lineHeight = 44.sp,
        fontStyle = fontStyle

    ),
    headlineLarge = TextStyle(
        fontWeight = FontWeight.W400,
        fontSize = 32.sp,
        letterSpacing = 0.sp,
        lineHeight = 40.sp,
        fontStyle = fontStyle

    ),
    headlineMedium = TextStyle(
        fontWeight = FontWeight.W400,
        fontSize = 28.sp,
        letterSpacing = 0.sp,
        lineHeight = 36.sp,
        fontStyle = fontStyle

    ),
    headlineSmall = TextStyle(
        fontWeight = FontWeight.W400,
        fontSize = 24.sp,
        letterSpacing = 0.sp,
        lineHeight = 32.sp,
        fontStyle = fontStyle

    ),
    titleLarge = TextStyle(
        fontWeight = FontWeight.W400,
        fontSize = 22.sp,
        letterSpacing = 0.sp,
        lineHeight = 28.sp,
        fontStyle = fontStyle

    ),
    titleMedium = TextStyle(
        fontWeight = FontWeight.W500,
        fontSize = 16.sp,
        letterSpacing = 0.15.sp,
        lineHeight = 24.sp,
        fontStyle = fontStyle

    ),
    titleSmall = TextStyle(
        fontWeight = FontWeight.W500,
        fontSize = 14.sp,
        letterSpacing = 0.1.sp,
        lineHeight = 20.sp,
        fontStyle = fontStyle

    ),
    labelLarge = TextStyle(
        fontWeight = FontWeight.W500,
        fontSize = 14.sp,
        letterSpacing = 0.1.sp,
        lineHeight = 20.sp,
        fontStyle = fontStyle

    ),
    labelMedium = TextStyle(
        fontWeight = FontWeight.W500,
        fontSize = 12.sp,
        letterSpacing = 0.5.sp,
        lineHeight = 16.sp,
        fontStyle = fontStyle

    ),
    labelSmall = TextStyle(
        fontWeight = FontWeight.W500,
        fontSize = 11.sp,
        letterSpacing = 0.5.sp,
        lineHeight = 16.sp,
        fontStyle = fontStyle

    ),
    bodyLarge = TextStyle(
        fontWeight = FontWeight.W400,
        fontSize = 16.sp,
        letterSpacing = 0.5.sp,
        lineHeight = 24.sp,
        fontStyle = fontStyle

    ),
    bodyMedium = TextStyle(
        fontWeight = FontWeight.W400,
        fontSize = 14.sp,
        letterSpacing = 0.25.sp,
        lineHeight = 20.sp,
        fontStyle = fontStyle

    ),
    bodySmall = TextStyle(
        fontWeight = FontWeight.W400,
        fontSize = 12.sp,
        letterSpacing = 0.4.sp,
        lineHeight = 16.sp,
        fontStyle = fontStyle

    )
)
