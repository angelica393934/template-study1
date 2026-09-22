package bsb.dev.bsb_bangking_jp.feature.news

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import bsb.dev.bsb_bangking_jp.R
import bsb.dev.bsb_bangking_jp.core.components.AppHeader
import bsb.dev.bsb_bangking_jp.core.components.EmptyState
import bsb.dev.bsb_bangking_jp.shared.get_image.NetworkImage
import bsb.dev.bsb_bangking_jp.shared.get_image.domain.ImageCategory
import bsb.dev.bsb_bangking_jp.core.theme.extendedColors
import bsb.dev.bsb_bangking_jp.feature.news.domain.AllNewsItem
import bsb.dev.bsb_bangking_jp.feature.news.presentation.AllNewsUiState
import bsb.dev.bsb_bangking_jp.feature.news.presentation.AllNewsViewModel
import org.koin.androidx.compose.koinViewModel
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun AllNewsPage(
    navController: NavController,
    onBeritaClick: (AllNewsItem) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AllNewsViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) { viewModel.load() }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        AppHeader(
            title = stringResource(R.string.label_berita),
            onBackClick = onBackClick,
        )

        when (val state = uiState) {
            is AllNewsUiState.Initial, is AllNewsUiState.Loading -> {
                Box(modifier = Modifier.weight(1f))
                // 🔹 padanan ArcLoadingIndicator di tengah layar
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }

            is AllNewsUiState.Error -> {
                EmptyState(
                    modifier = Modifier.weight(1f),
                    message = "Gagal memuat berita.",
                    subMessage = "Periksa koneksi anda dan coba lagi.",
                    actionText = "Coba Lagi",
                    onAction = { viewModel.retry() },
                )
            }

            is AllNewsUiState.Success -> {
                if (state.items.isEmpty()) {
                    EmptyState(
                        modifier = Modifier.weight(1f),
                        message = stringResource(R.string.msg_berita_tidak_tersedia),
                        subMessage = stringResource(R.string.msg_belum_ada_berita),
                        actionText = null,
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentPadding = PaddingValues(horizontal = 16.dp),
                    ) {
                        items(state.items, key = { it.id }) { item ->
                            BeritaCard(
                                item = item,
                                onClick = { onBeritaClick(item) },
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}

@Composable
private fun BeritaCard(
    item: AllNewsItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val formattedDate = remember(item.date) { formatBeritaDate(item.date) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(10.dp),
                ambientColor = MaterialTheme.extendedColors.divider,
                spotColor = MaterialTheme.extendedColors.divider,
            )
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.background)
            .clickable(onClick = onClick),
    ) {
        NetworkImage(
            path = item.pathImage,
            category = ImageCategory.NEWS,
            contentDescription = item.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)),
        )

        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = formattedDate,
                style = MaterialTheme.typography.titleSmall,
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = item.name,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

/** Padanan `DateFormat('yyyy-MM-dd HH:mm:ss').parse(...)` -> `DateFormat('d MMMM yyyy', 'id_ID')`. */
private fun formatBeritaDate(rawDate: String): String {
    return try {
        val parser = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
        val formatter = SimpleDateFormat("d MMMM yyyy", Locale("id", "ID"))
        formatter.format(parser.parse(rawDate) ?: return rawDate)
    } catch (e: Exception) {
        rawDate
    }
}