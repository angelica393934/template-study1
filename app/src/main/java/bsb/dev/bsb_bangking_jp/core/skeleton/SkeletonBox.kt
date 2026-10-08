package bsb.dev.bsb_bangking_jp.core.skeleton

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import bsb.dev.bsb_bangking_jp.core.theme.extendedColors

/**
 * Brush shimmer yang bergerak (mirip [Shimmer.fromColors] dari package `shimmer` di ).
 * Warna base/highlight otomatis menyesuaikan light/dark mode.
 */
@Composable
fun rememberShimmerBrush(): Brush {
    val baseColor = extendedColors.strip
    val highlightColor = extendedColors.onSuccess

    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1300, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerTranslate"
    )

    return Brush.linearGradient(
        colors = listOf(baseColor, highlightColor, baseColor),
        start = Offset(translateAnim - 500f, 0f),
        end = Offset(translateAnim, 0f)
    )
}

/**
 * Kotak dasar untuk semua skeleton. Setara dengan `SkeletonBox` di .
 *
 * - `width = null` -> mengisi lebar penuh parent (default : double.infinity).
 * - `isCircular = true` -> jadi lingkaran (bisa dipakai langsung sebagai avatar,
 *   tanpa perlu widget `SkeletonAvatar` terpisah kalau kamu mau lebih ringkas).
 */
@Composable
fun SkeletonBox(
    height: Dp,
    modifier: Modifier = Modifier,
    width: Dp? = null,
    isCircular: Boolean = false,
    borderRadius: Dp = 8.dp,
) {
    val shape: Shape = if (isCircular) CircleShape else RoundedCornerShape(borderRadius)
    val brush = rememberShimmerBrush()

    val sizeModifier = if (width != null) {
        Modifier.width(width).height(height)
    } else {
        Modifier.fillMaxWidth().height(height)
    }

    Box(
        modifier = modifier
            .then(sizeModifier)
            .clip(shape)
            .background(brush)
    )
}
