package bsb.dev.bsb_bangking_jp.feature.beranda.section

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.ImageNotSupported
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import bsb.dev.bsb_bangking_jp.R
import bsb.dev.bsb_bangking_jp.shared.get_image.NetworkImage
import bsb.dev.bsb_bangking_jp.shared.get_image.domain.ImageCategory
import bsb.dev.bsb_bangking_jp.core.skeleton.SkeletonBerita
import bsb.dev.bsb_bangking_jp.core.theme.extendedColors
import bsb.dev.bsb_bangking_jp.feature.news.presentation.NewsUiState
import bsb.dev.bsb_bangking_jp.feature.news.presentation.NewsViewModel
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel

@Composable
fun NewsSection(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: NewsViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) { viewModel.load() }

    when (val state = uiState) {
        is NewsUiState.Initial, is NewsUiState.Loading -> {
            SkeletonBerita(modifier = modifier)
        }

        is NewsUiState.Error -> {
            // 🔹 Kotak tetap ada, isinya ikon "gambar tidak tersedia" -- bukan hilang total.
            NewsSectionUnavailable(
                navController = navController,
                onRetry = { viewModel.load(forceRefresh = true) },
                modifier = modifier,
            )
        }

        is NewsUiState.Success -> {
            // 🔹 Padanan `if (news.isEmpty) SizedBox.shrink()` -- kalau sukses tapi memang
            // kosong, section boleh tidak ditampilkan sama sekali (bukan kegagalan).
            if (state.items.isEmpty()) return
            NewsSectionContent(
                navController = navController,
                berita = state.items.map { it.pathImage },
                modifier = modifier,
            )
        }
    }
}

/** Kondisi gagal fetch -- frame section tetap sama, cuma slider-nya diganti ikon kosong. */
@Composable
private fun NewsSectionUnavailable(
    navController: NavController,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp),
    ) {
        NewsSectionHeader(navController = navController)
        Spacer(modifier = Modifier.height(12.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.extendedColors.inputBackground)
                .clickable { onRetry() },
            contentAlignment = Alignment.Center,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.ImageNotSupported,
                    contentDescription = null,
                    tint = MaterialTheme.extendedColors.textDisabled,
                    modifier = Modifier.size(40.dp),
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Berita tidak tersedia",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.extendedColors.textSecondary,
                )
            }
        }
    }
}

@Composable
private fun NewsSectionHeader(navController: NavController) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(R.string.label_berita),
            style = MaterialTheme.typography.titleLarge,
        )
        Row(
            modifier = Modifier.clickable { navController.navigate("berita_list") },
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.label_lihat_semua),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
            )
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                imageVector =  Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(14.dp),
            )
        }
    }
}

@Composable
private fun NewsSectionContent(
    navController: NavController,
    berita: List<String>,
    modifier: Modifier = Modifier,
) {
    val pagerState = rememberPagerState(pageCount = { berita.size })

    if (berita.size > 1) {
        LaunchedEffect(pagerState) {
            while (true) {
                delay(5000)
                val next = (pagerState.currentPage + 1) % berita.size
                pagerState.animateScrollToPage(next)
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp),
    ) {
        NewsSectionHeader(navController = navController)
        Spacer(modifier = Modifier.height(12.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
                .clip(RoundedCornerShape(12.dp)),
        ) {
            HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { page ->
                // 🔹 Kalau 1 gambar tertentu gagal dimuat, NetworkImage sendiri sudah
                // otomatis fallback ke ikon kosong (lihat NetworkImageState.Failed).
                NetworkImage(
                    path = berita[page],
                    category = ImageCategory.NEWS,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
        ) {
            berita.indices.forEach { index ->
                val isActive = pagerState.currentPage == index
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .size(if (isActive) 9.dp else 8.dp)
                        .clip(RoundedCornerShape(50))
                        .background(
                            if (isActive) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.inverseSurface,
                        ),
                )
            }
        }
    }
}