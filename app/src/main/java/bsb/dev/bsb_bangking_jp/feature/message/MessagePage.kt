package bsb.dev.bsb_bangking_jp.feature.message

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import bsb.dev.bsb_bangking_jp.R
import bsb.dev.bsb_bangking_jp.core.component.AppHeader
import bsb.dev.bsb_bangking_jp.core.component.EmptyState
import bsb.dev.bsb_bangking_jp.core.filter.TransactionFilterPayload
import bsb.dev.bsb_bangking_jp.core.skeleton.SkeletonList
import bsb.dev.bsb_bangking_jp.core.util.TransactionFilterChipMapper
import bsb.dev.bsb_bangking_jp.core.component.FilterChipBar
import bsb.dev.bsb_bangking_jp.core.component.LocalLoadingOverlay
import bsb.dev.bsb_bangking_jp.core.component.LocalToastState
import bsb.dev.bsb_bangking_jp.core.filter.FilterTransaksiModal
import bsb.dev.bsb_bangking_jp.feature.message.domain.MessageItem
import bsb.dev.bsb_bangking_jp.feature.message.domain.toTransactionResultInfo
import bsb.dev.bsb_bangking_jp.feature.message.presentation.MessageDetailUiState
import bsb.dev.bsb_bangking_jp.feature.message.presentation.MessageDetailViewModel
import bsb.dev.bsb_bangking_jp.feature.message.presentation.MessageHistoryViewModel
import bsb.dev.bsb_bangking_jp.feature.message.section.MessageSectionTanggal
import bsb.dev.bsb_bangking_jp.shared.transaction_result.TransactionResultPage
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessagePage(
    messageViewModel: MessageHistoryViewModel = koinInject(),
    detailViewModel: MessageDetailViewModel = koinViewModel(),
) {
    val state by messageViewModel.uiState.collectAsStateWithLifecycle()
    val detailState by detailViewModel.uiState.collectAsStateWithLifecycle()

    var showFilterModal by remember { mutableStateOf(false) }
    var selectedMessageId by remember { mutableStateOf<Int?>(null) }

    val chips = remember(state.activeFilter) {
        TransactionFilterChipMapper.fromPayload(state.activeFilter)
    }

    val grouped = remember(state.items) { groupMessagesByDate(state.items) }
    val loadingOverlay = LocalLoadingOverlay.current
    val toastState = LocalToastState.current
    LaunchedEffect(detailState) {
        when (val ds = detailState) {
            is MessageDetailUiState.Loading -> loadingOverlay.show()
            is MessageDetailUiState.Success -> loadingOverlay.hide()
            is MessageDetailUiState.Error -> {
                loadingOverlay.hide()
                toastState.showError(ds.message)
                selectedMessageId = null
                detailViewModel.reset()
            }
            is MessageDetailUiState.Initial -> loadingOverlay.hide()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        AppHeader(title = "message", showBackButton = false)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.label_semua_message),
                style = MaterialTheme.typography.titleMedium,
            )
            Row(
                modifier = Modifier.clickable { showFilterModal = true },
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(15.dp),
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = stringResource(R.string.label_cari_message),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }

        FilterChipBar(
            items = chips,
            onClearAll = if (chips.isEmpty()) null else {
                { state.accountNumber?.let { messageViewModel.getInitial(it) } }
            },
            onRemove = { chip ->
                val current = state.activeFilter ?: return@FilterChipBar
                val updated = TransactionFilterChipMapper.removeChip(current, chip.key)
                messageViewModel.applyFilter(updated)
            },
        )

        Box(modifier = Modifier.weight(1f)) {
            when {
                state.accountNumber == null -> SkeletonList()
                state.isLoading && state.items.isEmpty() -> SkeletonList()
                state.error != null && state.items.isEmpty() -> {
                    EmptyState(
                        modifier = Modifier.fillMaxSize(),
                        message = "Data message tidak dapat dimuat.",
                        subMessage = "Terjadi kesalahan saat mengambil data.\nPeriksa koneksi anda dan coba lagi.",
                        actionText = "Coba Lagi",
                        onAction = { messageViewModel.refresh() },
                    )
                }
                state.items.isEmpty() -> {
                    EmptyState(
                        modifier = Modifier.fillMaxSize(),
                        message = stringResource(R.string.msg_tidak_ada_message),
                        subMessage = stringResource(R.string.msg_belum_ada_message),
                        actionText = null,
                    )
                }
                else -> {
                    val listState = androidx.compose.foundation.lazy.rememberLazyListState()
                    val shouldLoadMore by remember {
                        derivedStateOf {
                            val layoutInfo = listState.layoutInfo
                            val lastVisible = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
                            val totalItems = layoutInfo.totalItemsCount
                            totalItems > 0 && lastVisible >= totalItems - 3
                        }
                    }
                    LaunchedEffect(shouldLoadMore, state.hasMore, state.isLoadMore, state.isLoading) {
                        if (shouldLoadMore && state.hasMore && !state.isLoadMore && !state.isLoading) {
                            messageViewModel.loadMore()
                        }
                    }

                    LazyColumn(modifier = Modifier.fillMaxSize(), state = listState) {
                        grouped.forEach { (tanggalLabel, itemsForDate) ->
                            item(key = "section_$tanggalLabel") {
                                MessageSectionTanggal(
                                    tanggal = tanggalLabel,
                                    items = itemsForDate,
                                    onItemClick = { pesan ->
                                        selectedMessageId = pesan.id
                                        detailViewModel.load(pesan.id)
                                    },
                                )
                            }
                        }
                        if (state.isLoadMore) {
                            item(key = "load_more") {
                                Box(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    CircularProgressIndicator(modifier = Modifier.size(28.dp))
                                }
                            }
                        }
                        item(key = "bottom_spacer") { Spacer(modifier = Modifier.height(40.dp)) }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(80.dp))
    }

    if (showFilterModal) {
        FilterTransaksiModal(
            currentFilter = state.activeFilter ?: TransactionFilterPayload.initial(),
            onDismiss = { showFilterModal = false },
            onApply = { updated -> messageViewModel.applyFilter(updated) },
        )
    }

    // 🔹 Detail message sekarang pakai TransactionResultPage (full-screen dialog)
    if (selectedMessageId != null) {
        val ds = detailState
        if (ds is MessageDetailUiState.Success) {
            Dialog(
                onDismissRequest = { selectedMessageId = null; detailViewModel.reset() },
                properties = DialogProperties(usePlatformDefaultWidth = false),
            ) {
                TransactionResultPage(
                    data = ds.detail.toTransactionResultInfo(),
                    onClose = { selectedMessageId = null; detailViewModel.reset() },
                )
            }
        }
    }
}

/** Padanan groupByDateSortedDesc, tapi berbasis Date lengkap dari createdDate. */
private fun groupMessagesByDate(items: List<MessageItem>): Map<String, List<MessageItem>> {
    val formatter = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
    val sorted = items.sortedByDescending { it.createdDate }
    return sorted.groupBy { formatter.format(it.createdDate) }
}