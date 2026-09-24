package bsb.dev.bsb_bangking_jp.feature.activity

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
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import bsb.dev.bsb_bangking_jp.core.components.AppHeader
import bsb.dev.bsb_bangking_jp.core.components.CustomRefreshIndicator
import bsb.dev.bsb_bangking_jp.core.components.EmptyState
import bsb.dev.bsb_bangking_jp.core.components.FilterChipBar
import bsb.dev.bsb_bangking_jp.core.components.LocalToastState
import bsb.dev.bsb_bangking_jp.core.components.SaldoCardEmpty
import bsb.dev.bsb_bangking_jp.core.filter.FilterTransaksiModal
import bsb.dev.bsb_bangking_jp.core.filter.TransactionFilterPayload
import bsb.dev.bsb_bangking_jp.core.skeleton.SkeletonList
import bsb.dev.bsb_bangking_jp.core.skeleton.SkeletonSaldoCard
import bsb.dev.bsb_bangking_jp.core.theme.appLayout
import bsb.dev.bsb_bangking_jp.core.theme.appSpacing
import bsb.dev.bsb_bangking_jp.core.util.DateFormatterUtil
import bsb.dev.bsb_bangking_jp.core.util.TransactionFilterChipMapper
import bsb.dev.bsb_bangking_jp.core.util.groupByDateSortedDesc
import bsb.dev.bsb_bangking_jp.feature.activity.presentation.ActivityHistoryViewModel
import bsb.dev.bsb_bangking_jp.feature.activity.section.ActivityDateSection
import bsb.dev.bsb_bangking_jp.feature.activity.section.ActivityItemRow
import bsb.dev.bsb_bangking_jp.feature.activity.section.SaldoCardSelector
import bsb.dev.bsb_bangking_jp.shared.rekening_lainnya.presentation.RekeningLainnyaViewModel
import kotlinx.coroutines.delay
import org.koin.compose.koinInject
import kotlin.math.roundToInt

private val PULL_REFRESH_MAX_PUSH = 30.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityPage(
    activityViewModel: ActivityHistoryViewModel = koinInject(),
    rekeningViewModel: RekeningLainnyaViewModel = koinInject(),
) {
    val activityState by activityViewModel.uiState.collectAsStateWithLifecycle()
    val rekeningUiState by rekeningViewModel.uiState.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()
    val pullToRefreshState = rememberPullToRefreshState()
    val density = LocalDensity.current
    val toastState = LocalToastState.current
    var showFilterModal by remember { mutableStateOf(false) }
    val accountNo = activityState.accountNumber
    val isRefreshing = activityState.isLoading

    // Auto-load rekening tetap perlu dipicu dari suatu tempat.
    LaunchedEffect(Unit) {
        delay(1000)

        if (rekeningUiState.rekeningList == null) {
            rekeningViewModel.load()
        }
    }

    // accountNo dan pemicu fetch histori sepenuhnya dikelola oleh
    // ActivityHistoryViewModel melalui observeRekeningUntukAutoLoad().
    // Tidak perlu LaunchedEffect kedua di sini.
    val shouldLoadMore by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val lastVisible = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            val totalItems = layoutInfo.totalItemsCount

            totalItems > 0 && lastVisible >= totalItems - 3
        }
    }

    LaunchedEffect(
        shouldLoadMore,
        activityState.hasMore,
        activityState.isLoadMore,
        activityState.isLoading,
    ) {
        if (
            shouldLoadMore &&
            activityState.hasMore &&
            !activityState.isLoadMore &&
            !activityState.isLoading
        ) {
            delay(1000)
            activityViewModel.loadMore()
        }
    }

    // -------------------------------------------------------------------------
    // Derived Data
    // -------------------------------------------------------------------------

    val grouped = remember(activityState.items) {
        groupByDateSortedDesc(activityState.items)
    }

    val chips = remember(activityState.activeFilter) {
        TransactionFilterChipMapper.fromPayload(activityState.activeFilter)
    }
    // Jika refresh gagal ketika data lama masih tersedia, UI tetap
    // menampilkan data tersebut. Error ditampilkan melalui toast.
    LaunchedEffect(activityState.error) {
        val error = activityState.error

        if (error != null && activityState.items.isNotEmpty()) {
            toastState.showError(error)
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
        ) {
            AppHeader(
                title = "",
                showBackButton = false,
                height = 160.dp,
            )

            Box(
                modifier = Modifier
                    .padding(horizontal = appLayout.defaultPadding)
                    .offset(y = 65.dp),
            ) {
                when {
                    rekeningUiState.isLoading &&
                            rekeningUiState.rekeningList == null -> {
                        SkeletonSaldoCard()
                    }

                    rekeningUiState.rekeningList != null -> {
                        SaldoCardSelector(
                            rekeningList = rekeningUiState.rekeningList!!,
                            activeAccountNumber = accountNo,
                            onRekeningSelected = { selected ->
                                activityViewModel.switchAccount(selected.number)
                            },
                        )
                    }

                    else -> {
                        SaldoCardEmpty(
                            onRetry = {
                                rekeningViewModel.load(forceRefresh = true)
                            },
                        )
                    }
                }
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = 40.dp,
                    bottom = 10.dp,
                    start = appLayout.defaultPadding,
                    end = appLayout.defaultPadding,
                ),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Transaksi Bulan Ini",
                style = MaterialTheme.typography.titleLarge,
                maxLines = 1,
                modifier = Modifier.weight(1f),
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
                    text = "Cari Transaksi",
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
                    accountNo?.let {
                        activityViewModel.getInitial(it)
                    }
                }
            },
            onRemove = { chip ->
                val current = activityState.activeFilter
                    ?: return@FilterChipBar

                val updated = TransactionFilterChipMapper.removeChip(
                    current,
                    chip.key,
                )

                activityViewModel.applyFilter(updated)
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
                        subMessage = "Riwayat aktivitas butuh data rekening terlebih dahulu.\nPeriksa koneksi Anda dan coba lagi.",
                        actionText = "Coba Lagi",
                        onAction = {
                            rekeningViewModel.load(forceRefresh = true)
                        },
                    )
                }

                accountNo == null -> {
                    SkeletonList()
                }

                activityState.isLoading && activityState.items.isEmpty() -> {
                    SkeletonList()
                }

                activityState.error != null && activityState.items.isEmpty() -> {
                    EmptyState(
                        modifier = Modifier.fillMaxSize(),
                        message = "Data aktivitas tidak dapat dimuat.",
                        subMessage = "Terjadi kesalahan saat pengambilan data.\nPeriksa koneksi anda dan coba lagi.",
                        actionText = "Coba Lagi",
                        onAction = {
                            activityViewModel.getInitial(accountNo)
                        },
                    )
                }

                activityState.items.isEmpty() -> {
                    EmptyState(
                        modifier = Modifier.fillMaxSize(),
                        message = "Tidak ada aktivitas transaksi.",
                        subMessage = "Belum ditemukan catatan transaksi pada periode ini.",
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
                            activityViewModel.refresh()
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
                            grouped.forEach { (tanggal, itemsForDate) ->
                                item(key = "header_$tanggal") {
                                    ActivityDateSection(
                                        tanggal = DateFormatterUtil.fromYYMMDD(tanggal),
                                    )
                                }

                                itemsIndexed(
                                    items = itemsForDate,
                                    key = { index, item ->
                                        item.transactionId.ifEmpty {
                                            "$tanggal-$index"
                                        }
                                    },
                                ) { _, transaksi ->
                                    ActivityItemRow(transaksi = transaksi)
                                }
                            }

                            if (activityState.isLoadMore) {
                                item(key = "load_more") {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 16.dp),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(28.dp),
                                        )
                                    }
                                }
                            }

                            item(key = "bottom_spacer") { Spacer(modifier = Modifier.height(100.dp),)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showFilterModal) {
        FilterTransaksiModal(
            currentFilter = activityState.activeFilter
                ?: TransactionFilterPayload.initial(),
            onDismiss = {
                showFilterModal = false
            },
            onApply = { updated ->
                activityViewModel.applyFilter(updated)
            },
        )
    }
}