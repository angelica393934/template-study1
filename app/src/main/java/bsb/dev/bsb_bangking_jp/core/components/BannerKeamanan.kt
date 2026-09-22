package bsb.dev.bsb_bangking_jp.core.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import bsb.dev.bsb_bangking_jp.R
import bsb.dev.bsb_bangking_jp.core.device.AppPreferences
import bsb.dev.bsb_bangking_jp.core.theme.Orange500
import bsb.dev.bsb_bangking_jp.feature.beranda.domain.get_banner.BannerItem
import kotlinx.coroutines.delay
import org.koin.compose.koinInject

private data class BannerContent(val title: String?, val desc: String)

/**
 * Padanan `widgets/banner_keamanan.dart` (message statis, mis. peringatan keamanan di
 * halaman PIN) -- 1 message tetap, bisa ditutup. Sekali ditutup, TIDAK tampil lagi hari itu.
 */
@Composable
fun BannerKeamanan(
    title: String? = stringResource(R.string.banner_keamanan_title),
    desc: String = stringResource(R.string.banner_keamanan_desc),
    backgroundColor: Color? = null,
    showCloseButton: Boolean = true,
    persistKey: String = "keamanan_statis",
    modifier: Modifier = Modifier,
) {
    DismissibleBanner(
        contents = listOf(BannerContent(title, desc)),
        backgroundColor = backgroundColor,
        showCloseButton = showCloseButton,
        persistKey = persistKey,
        modifier = modifier,
    )
}

/**
 * Padanan `BannerPemberitahuan.dart` -- daftar pengumuman dari API (getbanner), berputar
 * otomatis tiap 5 detik kalau item > 1, bisa ditutup. Sekali ditutup, TIDAK tampil lagi
 * hari itu (khusus beranda -- key berbeda dari overload statis di atas).
 */
@Composable
fun BannerKeamanan(
    banners: List<BannerItem>,
    modifier: Modifier = Modifier,
    backgroundColor: Color? = null,
    showCloseButton: Boolean = true,
    persistKey: String = "beranda_pemberitahuan",
) {
    if (banners.isEmpty()) return

    DismissibleBanner(
        contents = banners.map { BannerContent(it.name.ifBlank { null }, it.description) },
        backgroundColor = backgroundColor,
        showCloseButton = showCloseButton,
        persistKey = persistKey,
        modifier = modifier,
    )
}

@Composable
private fun DismissibleBanner(
    contents: List<BannerContent>,
    backgroundColor: Color?,
    showCloseButton: Boolean,
    persistKey: String,
    modifier: Modifier,
) {
    val appPreferences: AppPreferences = koinInject()

    // 🔹 default TRUE selagi status belum sempat dicek -- baru disembunyikan kalau
    // ternyata memang sudah ditutup hari ini (dibaca sekali, sinkron, murah karena SharedPreferences).
    var isVisible by remember(persistKey) {
        mutableStateOf(!appPreferences.isBannerDismissedToday(persistKey))
    }
    var currentIndex by remember(persistKey) { mutableIntStateOf(0) }

    // 🔹 Auto-rotate tiap 5 detik, padanan Timer.periodic -- hanya jalan kalau item > 1.
    LaunchedEffect(contents.size, isVisible) {
        if (contents.size <= 1 || !isVisible) return@LaunchedEffect
        while (true) {
            delay(5000)
            currentIndex = (currentIndex + 1) % contents.size
        }
    }

    if (!isVisible) return

    val announcement = contents[currentIndex.coerceIn(0, contents.lastIndex)]

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 5.dp),
    ) {
        AnimatedContent(
            targetState = currentIndex,
            transitionSpec = {
                (slideInVertically(animationSpec = tween(600)) { it } + fadeIn(tween(600)))
                    .togetherWith(
                        slideOutVertically(animationSpec = tween(600)) { -it } + fadeOut(tween(600))
                    )
            },
            label = "bannerPemberitahuanSwitch",
        ) { _ ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(backgroundColor ?: Orange500)
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    if (!announcement.title.isNullOrEmpty()) {
                        Text(
                            text = announcement.title,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                        )
                    }
                    Text(
                        text = announcement.desc,
                        fontSize = 10.sp,
                        color = Color.White,
                    )
                }

                if (showCloseButton) {
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = {
                            isVisible = false
                            appPreferences.dismissBannerToday(persistKey)
                        },
                        modifier = Modifier.size(18.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = stringResource(R.string.cd_tutup_banner),
                            tint = Color.White,
                        )
                    }
                }
            }
        }
    }
}