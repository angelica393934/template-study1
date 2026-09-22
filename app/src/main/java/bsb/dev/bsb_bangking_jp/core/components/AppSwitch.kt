package bsb.dev.bsb_bangking_jp.core.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import bsb.dev.bsb_bangking_jp.core.theme.extendedColors

@Composable
fun AppSwitch(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    width: Dp = 52.dp,
    height: Dp = 32.dp,
    thumbPadding: Dp = 3.dp,
    trackColorOn: Color = MaterialTheme.colorScheme.inverseSurface,
    thumbColorOn: Color = MaterialTheme.colorScheme.primary,
    trackColorOff: Color =  MaterialTheme.extendedColors.strip,
    thumbColorOff: Color = MaterialTheme.extendedColors.divider,
) {
    val trackColor by animateColorAsState(
        targetValue = if (checked) trackColorOn else trackColorOff,
        animationSpec = tween(200),
        label = "AppSwitchTrackColor"
    )
    val thumbColor by animateColorAsState(
        targetValue = if (checked) thumbColorOn else thumbColorOff,
        animationSpec = tween(200),
        label = "AppSwitchThumbColor"
    )

    val thumbDiameter = height - thumbPadding * 2
    val maxOffset = width - thumbDiameter - thumbPadding

    val thumbOffset by animateDpAsState(
        targetValue = if (checked) maxOffset else thumbPadding,
        animationSpec = tween(200),
        label = "AppSwitchThumbOffset"
    )

    Box(
        modifier = modifier
            .size(width = width, height = height)
            .clip(CircleShape)
            .background(trackColor)
            .clickable(
                enabled = enabled && onCheckedChange != null,
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                onCheckedChange?.invoke(!checked)
            }
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .offset(x = thumbOffset)
                .size(thumbDiameter)
                .clip(CircleShape)
                .background(thumbColor)
        )
    }
}