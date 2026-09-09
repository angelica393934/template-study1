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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import bsb.dev.bsb_bangking_jp.core.component.AppHeader
import bsb.dev.bsb_bangking_jp.core.component.CustomRefreshIndicator
import bsb.dev.bsb_bangking_jp.core.component.EmptyState
import bsb.dev.bsb_bangking_jp.core.component.FilterChipBar
import bsb.dev.bsb_bangking_jp.core.component.SaldoCardEmpty
import bsb.dev.bsb_bangking_jp.core.skeleton.SkeletonList
import bsb.dev.bsb_bangking_jp.core.skeleton.SkeletonSaldoCard
import bsb.dev.bsb_bangking_jp.core.util.RupiahFormat
import bsb.dev.bsb_bangking_jp.core.util.DateFormatterUtil
import bsb.dev.bsb_bangking_jp.core.util.groupByDateSortedDesc
import bsb.dev.bsb_bangking_jp.core.filter.FilterTransaksiModal
import bsb.dev.bsb_bangking_jp.core.filter.TransactionFilterPayload
import bsb.dev.bsb_bangking_jp.core.util.TransactionFilterChipMapper
import bsb.dev.bsb_bangking_jp.feature.activity.data.HistoryItem
import bsb.dev.bsb_bangking_jp.feature.activity.presentation.ActivityHistoryViewModel
import bsb.dev.bsb_bangking_jp.feature.activity.section.SaldoCardSelector
import bsb.dev.bsb_bangking_jp.shared.rekening_lainnya.presentation.RekeningLainnyaViewModel
import org.koin.compose.koinInject
import kotlin.math.roundToInt
private val PULL_REFRESH_MAX_PUSH = 30.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityPage(
    activityViewModel: ActivityHistoryViewModel = koinInject(), // 🔹 koinInject, bukan koinViewModel
    rekeningViewModel: RekeningLainnyaViewModel = koinInject(),
) {
    val rekeningUiState by rekeningViewModel.uiState.collectAsStateWithLifecycle()

    val activityState by activityViewModel.uiState.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()
    val pullToRefreshState = rememberPullToRefreshState()
    val density = LocalDensity.current

    var showFilterModal by remember { mutableStateOf(false) }

    // 🔹 Auto-load rekening tetap perlu dipicu dari suatu tempat -- Beranda sudah
    // melakukannya di halamannya sendiri, tapi kalau user buka app dan langsung ke tab
    // lain, ini jaga-jaga supaya tetap ke-trigger.
    LaunchedEffect(Unit) {
        if (rekeningUiState.rekeningList == null) rekeningViewModel.load()
    }

    // accountNo & pemicu fetch histori SEKARANG sepenuhnya dikelola di dalam
    // ActivityHistoryViewModel (lihat observeRekeningUntukAutoLoad()) -- tidak perlu
    // LaunchedEffect kedua di sini lagi.

    val accountNo = activityState.accountNumber

    // 🔹 Status refreshing "murni" utk PullToRefreshBox & efek dorong-turun --
    // pada titik kode ini (branch `else ->` di bawah) items SUDAH pasti tidak kosong,
    // jadi isLoading di sini artinya "sedang refresh", bukan "loading pertama kali".
    val isRefreshing = activityState.isLoading

    val shouldLoadMore by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val lastVisible = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            val totalItems = layoutInfo.totalItemsCount
            totalItems > 0 && lastVisible >= totalItems - 3
        }
    }
    LaunchedEffect(shouldLoadMore, activityState.hasMore, activityState.isLoadMore, activityState.isLoading) {
        if (shouldLoadMore && activityState.hasMore && !activityState.isLoadMore && !activityState.isLoading) {
            activityViewModel.loadMore()
        }
    }

    val grouped = remember(activityState.items) { groupByDateSortedDesc(activityState.items) }
    val chips = remember(activityState.activeFilter) {
        TransactionFilterChipMapper.fromPayload(activityState.activeFilter)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        // ===== HEADER + SALDO CARD =====
        Box(modifier = Modifier.fillMaxWidth().height(200.dp)) {
            AppHeader(title = "", showBackButton = false, height = 160.dp)
            Box(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .offset(y = 65.dp),
            ) {
                when {
                    rekeningUiState.isLoading && rekeningUiState.rekeningList == null -> {
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
                            onRetry = { rekeningViewModel.load(forceRefresh = true) },
                        )
                    }
                }
            }
        }

        // ===== "Transaksi Bulan Ini" + "Cari Transaksi" ===== (tidak berubah)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 40.dp, bottom = 10.dp, start = 24.dp, end = 24.dp),
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
                    text = "Cari Transaksi",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }

        FilterChipBar(
            items = chips,
            onClearAll = if (chips.isEmpty()) null else {
                { accountNo?.let { activityViewModel.getInitial(it) } }
            },
            onRemove = { chip ->
                val current = activityState.activeFilter ?: return@FilterChipBar
                val updated = TransactionFilterChipMapper.removeChip(current, chip.key)
                activityViewModel.applyFilter(updated)
            },
        )

        // ===== LIST TRANSAKSI =====
        Box(modifier = Modifier.weight(1f)) {
            when {
                accountNo == null && rekeningUiState.error != null -> {
                    EmptyState(
                        modifier = Modifier.fillMaxSize(),
                        message = "Data rekening tidak dapat dimuat.",
                        subMessage = "Riwayat transaksi butuh data rekening terlebih dahulu.\nPeriksa koneksi Anda dan coba lagi.",
                        actionText = "Coba Lagi",
                        onAction = { rekeningViewModel.load(forceRefresh = true) },
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
                        subMessage = "Terjadi kesalahan saat mengambil data.\nPeriksa koneksi anda dan coba lagi.",
                        actionText = "Coba Lagi",
                        onAction = { activityViewModel.getInitial(accountNo) },
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
                    val maxPushPx = with(density) { PULL_REFRESH_MAX_PUSH.toPx() }
                    val pushOffsetPx = if (isRefreshing) {
                        maxPushPx
                    } else {
                        (pullToRefreshState.distanceFraction.coerceIn(0f, 1f) * maxPushPx)
                    }

                    PullToRefreshBox(
                        isRefreshing = isRefreshing,
                        onRefresh = { activityViewModel.refresh() },
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
                                // 🔹 KONTEN IKUT TERDORONG TURUN sesuai jarak tarikan / status refreshing
                                .offset {
                                    IntOffset(x = 0, y = pushOffsetPx.roundToInt())
                                },
                            state = listState,
                        ) {
                            grouped.forEach { (tanggal, itemsForDate) ->
                                item(key = "header_$tanggal") {
                                    TanggalHeader(tanggal = DateFormatterUtil.fromYYMMDD(tanggal))
                                }
                                itemsIndexed(
                                    items = itemsForDate,
                                    key = { index, it -> it.transactionId.ifEmpty { "$tanggal-$index" } },
                                ) { _, transaksi ->
                                    TransaksiItem(transaksi = transaksi)
                                }
                            }
                            if (activityState.isLoadMore) {
                                item(key = "load_more") {
                                    Box(
                                        modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        CircularProgressIndicator(modifier = Modifier.size(28.dp))
                                    }
                                }
                            }
                            item(key = "bottom_spacer") {
                                Spacer(modifier = Modifier.height(40.dp))
                            }
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(80.dp))
    }

    if (showFilterModal) {
        FilterTransaksiModal(
            currentFilter = activityState.activeFilter ?:TransactionFilterPayload.initial(),
            onDismiss = { showFilterModal = false },
            onApply = { updated -> activityViewModel.applyFilter(updated) },
        )
    }
}

@Composable
private fun TanggalHeader(tanggal: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 24.dp, vertical = 12.dp),
    ) {
        Text(text = tanggal, style = MaterialTheme.typography.titleMedium)
    }
}

@Composable
private fun TransaksiItem(transaksi: HistoryItem, modifier: Modifier = Modifier) {
    val warnaNominal = if (transaksi.isMasuk) Color(0xFF2E7D32) else MaterialTheme.colorScheme.onSurface

    val jenisLower = transaksi.jenisTransaksi.lowercase()
    val icon: ImageVector = when {
        "transfer" in jenisLower -> Icons.Filled.CompareArrows
        "tagihan" in jenisLower -> Icons.AutoMirrored.Filled.ReceiptLong
        "top" in jenisLower -> Icons.Filled.AccountBalanceWallet
        else -> Icons.AutoMirrored.Filled.HelpOutline
    }

    // deskripsiTransaksi = "Jenis arah\nrekeningTujuan" -- baris 1 jadi title, baris 2 jadi subtitle
    val descLines = transaksi.deskripsiTransaksi.split("\n")
    val title = descLines.getOrNull(0)?.takeIf { it.isNotBlank() }
        ?: transaksi.jenisTransaksi.ifBlank { "Transaksi" }
    val subtitle = descLines.getOrNull(1).orEmpty()

    val nominalInt = transaksi.amountValue.toInt()
    val nominalFormatted = RupiahFormat(nominalInt)
    val nominalDisplay = if (transaksi.isMasuk) "+ $nominalFormatted" else "- $nominalFormatted"

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 15.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(25.dp),
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = title, style = MaterialTheme.typography.titleMedium, maxLines = 1)
                    if (subtitle.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = subtitle,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = nominalDisplay,
                    style = MaterialTheme.typography.titleMedium,
                    color = warnaNominal,
                )
            }
        }
        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 24.dp),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.surfaceVariant,
        )
    }
}