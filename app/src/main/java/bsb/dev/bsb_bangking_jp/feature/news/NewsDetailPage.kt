package bsb.dev.bsb_bangking_jp.feature.news

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Link
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import bsb.dev.bsb_bangking_jp.core.components.AppHeader
import bsb.dev.bsb_bangking_jp.core.components.EmptyState
import bsb.dev.bsb_bangking_jp.shared.get_image.NetworkImage
import bsb.dev.bsb_bangking_jp.shared.get_image.domain.ImageCategory
import bsb.dev.bsb_bangking_jp.feature.news.domain.NewsDetail
import bsb.dev.bsb_bangking_jp.feature.news.presentation.NewsDetailUiState
import bsb.dev.bsb_bangking_jp.feature.news.presentation.NewsDetailViewModel
import org.koin.androidx.compose.koinViewModel
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun NewsDetailPage(
    newsId: Int,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: NewsDetailViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(newsId) { viewModel.load(newsId) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        AppHeader(
            title = "Berita",
            onBackClick = onBackClick,
        )

        Box(modifier = Modifier.weight(1f)) {
            when (val state = uiState) {
                is NewsDetailUiState.Initial, is NewsDetailUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                is NewsDetailUiState.Error -> {
                    EmptyState(
                        modifier = Modifier.fillMaxSize(),
                        message = "Berita tidak dapat dimuat",
                        subMessage = "Terjadi kesalahan saat mengambil data.\nPeriksa koneksi anda dan coba lagi.",
                        actionText = "Coba Lagi",
                        onAction = { viewModel.load(newsId, forceRefresh = true) },
                    )
                }

                is NewsDetailUiState.Success -> {
                    NewsDetailContent(data = state.data)
                }
            }
        }
    }
}

@Composable
private fun NewsDetailContent(data: NewsDetail) {
    val formattedDate = remember(data.createdDate) {
        SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID")).format(data.createdDate)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 20.dp),
    ) {
        Text(text = data.subtitle, style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))

        if (data.pathImage.isNotEmpty()) {
            NetworkImage(
                path = data.pathImage,
                contentDescription = data.subtitle,
                contentScale = ContentScale.Fit,
                category = ImageCategory.NEWS,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .clip(RoundedCornerShape(12.dp)),
            )
            Spacer(modifier = Modifier.height(10.dp))
        }

        Text(text = formattedDate, style = MaterialTheme.typography.titleSmall)
        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = data.description,
            textAlign = TextAlign.Justify,
            style = MaterialTheme.typography.bodyMedium,
        )

        val targetUrl = data.targetUrl?.trim()
        if (!targetUrl.isNullOrEmpty()) {
            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Detail lebih lanjut dapat dilihat melalui tautan",
                style = MaterialTheme.typography.bodyMedium,
            )
            Spacer(modifier = Modifier.height(4.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = rememberOpenUrlModifier(targetUrl),
            ) {
                Icon(
                    imageVector = Icons.Default.Link,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.height(16.dp),
                )
                Spacer(modifier = Modifier.height(0.dp))
                Text(
                    text = targetUrl,
                    style = MaterialTheme.typography.titleSmall.copy(
                        color = MaterialTheme.colorScheme.primary,
                        textDecoration = TextDecoration.Underline,
                    ),
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

/**
 * Padanan `onTap` TapGestureRecognizer di  -- buka link eksternal.
 * Harus @Composable (bukan extension Modifier biasa) karena butuh LocalContext.current
 * dan remember{} yang cuma boleh dipanggil dari composable context.
 */
@Composable
private fun rememberOpenUrlModifier(rawUrl: String): Modifier {
    val context = LocalContext.current
    return Modifier.clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = null,
    ) {
        val url = if (rawUrl.startsWith("http")) rawUrl else "https://$rawUrl"
        try {
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        } catch (e: Exception) {
            // aman diabaikan -- tidak ada aplikasi yang bisa membuka link ini
        }
    }
}