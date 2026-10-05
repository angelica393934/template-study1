package bsb.dev.bsb_bangking_jp.core.util

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// ini card di spalsh
fun getResponsiveCardHeight(screenHeight: Dp): Dp {
    val ratio = when {
        screenHeight < 600.dp -> 0.55f
        screenHeight < 700.dp -> 0.45f
        screenHeight < 800.dp -> 0.40f
        screenHeight < 900.dp -> 0.35f
        else -> 0.3f
    }

    return screenHeight * ratio
}