// core/components/CustomPullToRefreshIndicator.kt
package bsb.dev.bsb_bangking_jp.core.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp

/**
 * Indikator pull-to-refresh custom -- gaya sama seperti LoadingOverlayHost
 * (lingkaran putih + spinner warna primary), tapi lebih kecil dan TANPA
 * background gelap. Posisi & mekanisme tarik tetap bawaan PullToRefreshBox,
 * ini cuma ganti tampilan visualnya saja.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomRefreshIndicator(
    state: PullToRefreshState,
    isRefreshing: Boolean,
    modifier: Modifier = Modifier,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pull_refresh_rotation")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing),
        ),
        label = "rotation",
    )

    Box(
        modifier = modifier
            .size(40.dp) // 🔹 lebih kecil dari LoadingOverlay (yang 70dp)
            .graphicsLayer {
                // Saat masih ditarik (belum full refresh), ikuti progress tarikan.
                // Saat sedang refreshing, biarkan selalu terlihat penuh.
                alpha = if (isRefreshing) 1f else state.distanceFraction.coerceIn(0f, 1f)
                scaleX = if (isRefreshing) 1f else state.distanceFraction.coerceIn(0.6f, 1f)
                scaleY = if (isRefreshing) 1f else state.distanceFraction.coerceIn(0.6f, 1f)
            }
            .clip(CircleShape)
            .background(Color.White),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(
            modifier = Modifier
                .size(20.dp)
                .graphicsLayer {
                    // Putar manual mengikuti progress tarikan kalau belum full refresh,
                    // dan animasi muter terus kalau sedang benar-benar refreshing.
                    rotationZ = if (isRefreshing) rotation else state.distanceFraction * 360f
                },
            strokeWidth = 3.dp,
            color = MaterialTheme.colorScheme.primary,
            trackColor = Color.Transparent,
        )
    }
}