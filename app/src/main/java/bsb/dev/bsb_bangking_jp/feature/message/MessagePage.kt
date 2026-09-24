package bsb.dev.bsb_bangking_jp.feature.message

import android.view.WindowManager
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import androidx.core.view.WindowCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import bsb.dev.bsb_bangking_jp.R
import bsb.dev.bsb_bangking_jp.core.components.AppHeader
import bsb.dev.bsb_bangking_jp.core.components.CustomRefreshIndicator
import bsb.dev.bsb_bangking_jp.core.components.EmptyState
import bsb.dev.bsb_bangking_jp.core.components.FilterChipBar
import bsb.dev.bsb_bangking_jp.core.components.LocalLoadingOverlay
import bsb.dev.bsb_bangking_jp.core.components.LocalToastState
import bsb.dev.bsb_bangking_jp.core.filter.FilterTransaksiModal
import bsb.dev.bsb_bangking_jp.core.filter.TransactionFilterPayload
import bsb.dev.bsb_bangking_jp.core.skeleton.SkeletonList
import bsb.dev.bsb_bangking_jp.core.theme.appLayout
import bsb.dev.bsb_bangking_jp.core.theme.appSpacing
import bsb.dev.bsb_bangking_jp.core.util.TransactionFilterChipMapper
import bsb.dev.bsb_bangking_jp.feature.message.domain.toTransactionResultInfo
import bsb.dev.bsb_bangking_jp.feature.message.presentation.MessageDetailUiState
import bsb.dev.bsb_bangking_jp.feature.message.presentation.MessageDetailViewModel
import bsb.dev.bsb_bangking_jp.feature.message.presentation.MessageHistoryViewModel
import bsb.dev.bsb_bangking_jp.feature.message.components.MessageDateSection
import bsb.dev.bsb_bangking_jp.feature.message.components.groupMessagesByDate
import bsb.dev.bsb_bangking_jp.shared.rekening_lainnya.presentation.RekeningLainnyaViewModel
import bsb.dev.bsb_bangking_jp.shared.transaction_result.TransactionResultPage
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import kotlin.math.roundToInt

private val PULL_REFRESH_MAX_PUSH = 50.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessagePage(
    rekeningViewModel: RekeningLainnyaViewModel = koinInject(),
    messageViewModel: MessageHistoryViewModel = koinInject(),
    detailViewModel: MessageDetailViewModel = koinViewModel(),
) {
    val messageState by messageViewModel.uiState.collectAsStateWithLifecycle()
    val rekeningUiState by rekeningViewModel.uiState.collectAsStateWithLifecycle()
    val detailState by detailViewModel.uiState.collectAsStateWithLifecycle()
    val accountNo = messageState.accountNumber
    val pullToRefreshState = rememberPullToRefreshState()
    val listState = rememberLazyListState()
    val density = LocalDensity.current
    val loadingOverlay = LocalLoadingOverlay.current
    val toastState = LocalToastState.current
    val isRefreshing = messageState.isLoading

    var showFilterModal by remember { mutableStateOf(false) }
    var selectedMessageId by remember { mutableStateOf<Int?>(null) }

    val chips = remember(messageState.activeFilter) {
        TransactionFilterChipMapper.fromPayload(messageState.activeFilter)
    }

    val grouped = remember(messageState.items) {
        groupMessagesByDate(messageState.items)
    }

    LaunchedEffect(Unit) {
        delay(3000)

        if (rekeningUiState.rekeningList == null) {
            rekeningViewModel.load()
        }
    }
    // loadmore
    val shouldLoadMore by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val lastVisible = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            val totalItems = layoutInfo.totalItemsCount

            totalItems > 0 && lastVisible >= totalItems - 3
        }
    }

    //message
    LaunchedEffect(
        shouldLoadMore,
        messageState.hasMore,
        messageState.isLoadMore,
        messageState.isLoading,
    ) {
        if (
            shouldLoadMore &&
            messageState.hasMore &&
            !messageState.isLoadMore &&
            !messageState.isLoading
        ) {
            messageViewModel.loadMore()
        }
    }
// detail message
    LaunchedEffect(detailState) {
        when (val ds = detailState) {
            is MessageDetailUiState.Loading -> {
                loadingOverlay.show()
            }
            is MessageDetailUiState.Success -> {
                loadingOverlay.hide()
            }
            is MessageDetailUiState.Error -> {
                loadingOverlay.hide()
                toastState.showError(ds.message)
                selectedMessageId = null
                detailViewModel.reset()
            }
            is MessageDetailUiState.Initial -> {
                loadingOverlay.hide()
            }
        }
    }
    // Jika refresh gagal ketika data lama masih tersedia, UI tetap
    // menampilkan data tersebut. Error ditampilkan melalui toast.
    LaunchedEffect(messageState.error) {
        val error = messageState.error

        if (error != null && messageState.items.isNotEmpty()) {
            toastState.showError(error)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        AppHeader(
            title = "pesan",
            showBackButton = false,
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
                .padding(all = appLayout.defaultPadding),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.label_semua_message),
                style = MaterialTheme.typography.titleMedium,
            )

            Row(
                modifier = Modifier.clickable {
                    showFilterModal = true
                },
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(15.dp),
                )
                Spacer(modifier = Modifier.width(appSpacing.xxxxs))

                Text(
                    text = stringResource(R.string.label_cari_message),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }

        FilterChipBar(
            items = chips,
            onClearAll = if (chips.isEmpty()) {
                null
            } else {
                {
                    messageState.accountNumber?.let {
                        messageViewModel.getInitial(it)
                    }
                }
            },
            onRemove = { chip ->
                val current = messageState.activeFilter
                    ?: return@FilterChipBar

                val updated = TransactionFilterChipMapper.removeChip(
                    current,
                    chip.key,
                )

                messageViewModel.applyFilter(updated)
            },
        )

        Box(
            modifier = Modifier.weight(1f),
        ) {
            when {
                accountNo == null && rekeningUiState.error != null -> {
                    EmptyState(
                        modifier = Modifier.fillMaxSize(),
                        message = "Data rekening tidak dapat dimuat.",
                        subMessage = "Riwayat pesan butuh data rekening terlebih dahulu.\nPeriksa koneksi Anda dan coba lagi.",
                        actionText = "Coba Lagi",
                        onAction = {
                            rekeningViewModel.load(forceRefresh = true)
                        },
                    )
                }
                messageState.accountNumber == null -> {
                    SkeletonList()
                }

                messageState.isLoading && messageState.items.isEmpty() -> {
                    SkeletonList()
                }

                messageState.error != null && messageState.items.isEmpty() -> {
                    EmptyState(
                        modifier = Modifier.fillMaxSize(),
                        message = "Data message tidak dapat dimuat.",
                        subMessage = "Terjadi kesalahan saat mengambil data.\nPeriksa koneksi anda dan coba lagi.",
                        actionText = "Coba Lagi",
                        onAction = {
                            messageViewModel.refresh()
                        },
                    )
                }

                messageState.items.isEmpty() -> {
                    EmptyState(
                        modifier = Modifier.fillMaxSize(),
                        message = stringResource(R.string.msg_tidak_ada_message),
                        subMessage = stringResource(R.string.msg_belum_ada_message),
                        actionText = null,
                    )
                }

                else -> {
                    val maxPushPx = with(density) {
                        PULL_REFRESH_MAX_PUSH.toPx()
                    }

                    val pushOffsetPx = if (isRefreshing) {
                        maxPushPx
                    } else {
                        pullToRefreshState.distanceFraction
                            .coerceIn(0f, 1f) * maxPushPx
                    }

                    PullToRefreshBox(
                        isRefreshing = isRefreshing,
                        onRefresh = {
                            messageViewModel.refresh()
                        },
                        state = pullToRefreshState,
                        indicator = {
                            CustomRefreshIndicator(
                                state = pullToRefreshState,
                                isRefreshing = isRefreshing,
                                modifier = Modifier.align(Alignment.TopCenter),
                            )
                        },
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .offset {
                                    IntOffset(
                                        x = 0,
                                        y = pushOffsetPx.roundToInt(),
                                    )
                                },
                            state = listState,
                        ) {
                            grouped.forEach { (tanggalLabel, itemsForDate) ->
                                item(key = "section_$tanggalLabel") {
                                    MessageDateSection(
                                        tanggal = tanggalLabel,
                                        items = itemsForDate,
                                        onItemClick = { pesan ->
                                            selectedMessageId = pesan.id
                                            detailViewModel.load(pesan.id)
                                        },
                                    )
                                }
                            }

                            if (messageState.isLoadMore) {
                                item(key = "load_more") {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = appLayout.defaultPadding),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(28.dp),
                                        )
                                    }
                                }
                            }

                            item(key = "bottom_spacer") {
                                Spacer(modifier = Modifier.height(100.dp))
                            }
                        }
                    }
                }
            }
        }
    }

    if (showFilterModal) {
        FilterTransaksiModal(
            currentFilter = messageState.activeFilter
                ?: TransactionFilterPayload.initial(),
            onDismiss = {
                showFilterModal = false
            },
            onApply = { updated ->
                messageViewModel.applyFilter(updated)
            },
        )
    }

    if (selectedMessageId != null) {
        val ds = detailState

        if (ds is MessageDetailUiState.Success) {
            Dialog(
                onDismissRequest = {
                    selectedMessageId = null
                    detailViewModel.reset()
                },
                properties = DialogProperties(
                    usePlatformDefaultWidth = false,
                ),
            ) {
                val dialogWindow =
                    (LocalView.current.parent as? DialogWindowProvider)?.window

                SideEffect {
                    dialogWindow?.let { window ->
                        WindowCompat.setDecorFitsSystemWindows(
                            window,
                            false,
                        )

                        window.setLayout(
                            WindowManager.LayoutParams.MATCH_PARENT,
                            WindowManager.LayoutParams.MATCH_PARENT,
                        )
                    }
                }

                TransactionResultPage(
                    data = ds.detail.toTransactionResultInfo(),
                    onClose = {
                        selectedMessageId = null
                        detailViewModel.reset()
                    },
                )
            }
        }
    }
}