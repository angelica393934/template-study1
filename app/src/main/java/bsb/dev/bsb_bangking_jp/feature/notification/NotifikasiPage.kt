package bsb.dev.bsb_bangking_jp.feature.notification

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import bsb.dev.bsb_bangking_jp.core.components.AppHeader
import bsb.dev.bsb_bangking_jp.core.components.AppModalBottomSheet
import bsb.dev.bsb_bangking_jp.core.components.EmptyState
import bsb.dev.bsb_bangking_jp.core.theme.appLayout
import bsb.dev.bsb_bangking_jp.core.theme.extendedColors
import bsb.dev.bsb_bangking_jp.feature.notification.components.NotifDetailContent
import bsb.dev.bsb_bangking_jp.feature.notification.components.NotifRow
import bsb.dev.bsb_bangking_jp.feature.notification.domain.NotifItem
import bsb.dev.bsb_bangking_jp.feature.notification.presentation.NotifUiState
import bsb.dev.bsb_bangking_jp.feature.notification.presentation.NotifViewModel
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
                        modifier = Modifier.padding(horizontal = appLayout.defaultPadding, vertical = appLayout.verticalPadding16),
                    )
                    HorizontalDivider(color = extendedColors.strip)

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
                                HorizontalDivider(color = extendedColors.textDisabled)
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

