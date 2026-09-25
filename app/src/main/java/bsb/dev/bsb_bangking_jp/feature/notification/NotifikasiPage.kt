package bsb.dev.bsb_bangking_jp.feature.notification

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import bsb.dev.bsb_bangking_jp.core.components.AppButton
import bsb.dev.bsb_bangking_jp.core.components.AppHeader
import bsb.dev.bsb_bangking_jp.core.components.AppModalBottomSheet
import bsb.dev.bsb_bangking_jp.core.components.EmptyState
import bsb.dev.bsb_bangking_jp.core.theme.appLayout
import bsb.dev.bsb_bangking_jp.core.theme.appSpacing
import bsb.dev.bsb_bangking_jp.core.theme.extendedColors
import bsb.dev.bsb_bangking_jp.core.util.DateFormatterUtil
import bsb.dev.bsb_bangking_jp.feature.notification.domain.NotifItem
import bsb.dev.bsb_bangking_jp.feature.notification.presentation.NotifUiState
import bsb.dev.bsb_bangking_jp.feature.notification.presentation.NotifViewModel
import bsb.dev.bsb_bangking_jp.shared.get_image.NetworkImage
import bsb.dev.bsb_bangking_jp.shared.get_image.domain.ImageCategory
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotifikasiPage(
    onBackClick: () -> Unit = {},
    viewModel: NotifViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedNotif by remember { mutableStateOf<NotifItem?>(null) }

    LaunchedEffect(Unit) { viewModel.load() }

    Scaffold(
        topBar = {
            AppHeader(
                title = "Notifikasi",
                onBackClick = onBackClick,
            )
        },
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            when (val state = uiState) {
                is NotifUiState.Initial, is NotifUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                is NotifUiState.Error -> {
                    EmptyState(
                        modifier = Modifier.fillMaxSize(),
                        message = "Data Notifikasi tidak dapat dimuat.",
                        subMessage = "Terjadi kesalahan saat mengambil data.\nPeriksa koneksi anda dan coba lagi.",
                        actionText = "Coba Lagi",
                        onAction = { viewModel.load(forceRefresh = true) },
                    )
                }

                is NotifUiState.Success -> {
                    Text(
                        text = "Semua Notifikasi",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(horizontal = appLayout.defaultPadding, vertical = appLayout.verticalPadding),
                    )
                    HorizontalDivider(color = MaterialTheme.extendedColors.strip)

                    if (state.items.isEmpty()) {
                        EmptyState(
                            modifier = Modifier.fillMaxSize(),
                            message = "Data Notifikasi tidak dapat dimuat.",
                            subMessage = "Terjadi kesalahan saat mengambil data.\nPeriksa koneksi anda dan coba lagi.",
                            actionText = "Coba Lagi",
                            onAction = { viewModel.load(forceRefresh = true) },
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(bottom = appLayout.defaultPadding),
                        ) {
                            items(state.items, key = { it.id }) { notif ->
                                NotifRow(
                                    notif = notif,
                                    onClick = { selectedNotif = notif },
                                )
                                HorizontalDivider(color = MaterialTheme.extendedColors.textDisabled)
                            }
                        }
                    }
                }
            }
        }
    }

    selectedNotif?.let { notif ->
        AppModalBottomSheet(onDismissRequest = { selectedNotif = null }) {
            NotifDetailContent(
                notif = notif,
                onClose = { selectedNotif = null },
            )
        }
    }
}

@Composable
private fun NotifRow(
    notif: NotifItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(0.dp))
            .clickable(onClick = onClick)
            .then(Modifier)
            .padding(0.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = appLayout.defaultPadding, vertical = appLayout.verticalPadding)
                .then(Modifier),
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = notif.name,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.weight(1f),
                    )
                    Spacer(modifier = Modifier.width(appSpacing.xxxs))
                    Text(
                        text = DateFormatterUtil.toShortDate(notif.date),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.extendedColors.textSecondary,
                    )
                }
                Spacer(modifier = Modifier.height(appSpacing.xxxxs))
                Text(
                    text = notif.description,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.extendedColors.textSecondary,
                )
            }
        }
    }.also {
        Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(0.dp)))
    }
}

@Composable
private fun NotifDetailContent(
    notif: NotifItem,
    onClose: () -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(appSpacing.xs),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(appSpacing.xxxs),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            NetworkImage(
                path = notif.pathImage,
                category = ImageCategory.NEWS,
                contentDescription = notif.name,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .clip(RoundedCornerShape(12.dp)),
            )

            Text(
                text = notif.name,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                style = MaterialTheme.typography.displaySmall,
            )

            Text(
                text = DateFormatterUtil.toFullDateTime(notif.date),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.extendedColors.textSecondary,
            )

            Text(
                text = notif.description,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.extendedColors.textSecondary,
            )
        }
        AppButton(
            text = "Baiklah",
            onClick = onClose,
        )
    }
}