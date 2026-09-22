package bsb.dev.bsb_bangking_jp.feature.manage_scheduled_transfer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import bsb.dev.bsb_bangking_jp.core.components.AppButton
import bsb.dev.bsb_bangking_jp.core.components.AppHeader
import bsb.dev.bsb_bangking_jp.core.components.AppModalConfirm
import bsb.dev.bsb_bangking_jp.core.components.EmptyState
import bsb.dev.bsb_bangking_jp.core.components.LocalToastState
import bsb.dev.bsb_bangking_jp.core.components.AppCheckBox
import bsb.dev.bsb_bangking_jp.core.skeleton.SkeletonList
import bsb.dev.bsb_bangking_jp.core.skeleton.SkeletonListLayout
import bsb.dev.bsb_bangking_jp.core.theme.extendedColors
import bsb.dev.bsb_bangking_jp.feature.manage_scheduled_transfer.domain.ScheduledTransferItem
import bsb.dev.bsb_bangking_jp.feature.manage_scheduled_transfer.presentation.ScheduledTransferUiState
import bsb.dev.bsb_bangking_jp.feature.manage_scheduled_transfer.presentation.ScheduledTransferViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageScheduledTransferPage(
    viewModel: ScheduledTransferViewModel,
    onBackClick: () -> Unit,
    onItemClick: (id: Int) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val toastState = LocalToastState.current

    var isDeleteMode by remember { mutableStateOf(false) }
    val selectedIds = remember { mutableStateListOf<Int>() }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    // 🔹 Padanan initState() + didPopNext() -- fetch ulang tiap kali halaman ini dimasuki
    // (termasuk saat kembali dari Detail page, karena composable ini dibuat ulang).
    LaunchedEffect(Unit) { viewModel.getList() }

    // 🔹 Padanan MultiBlocListener -- reaksi terhadap ActionSuccess/Failure dari delete massal.
    LaunchedEffect(uiState) {
        when (val state = uiState) {
            is ScheduledTransferUiState.ActionSuccess -> {
                if (state.action == "delete") {
                    toastState.showSuccess("Transfer terjadwal berhasil dihapus")
                }
                viewModel.getList(forceRefresh = true)
            }
            is ScheduledTransferUiState.Failure -> {
                toastState.showError(state.respMessage)
            }
            else -> Unit
        }
    }

    Scaffold(
        topBar = {
            AppHeader(title = "Transfer Terjadwal", onBackClick = onBackClick)
        },
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            val listState = uiState as? ScheduledTransferUiState.ListSuccess

            // Toggle "Hapus" / "Batal" -- hanya muncul kalau daftar tidak kosong.
            if (listState != null && listState.items.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 24.dp, top = 15.dp, bottom = 10.dp),
                    horizontalArrangement = Arrangement.End,
                ) {
                    Row(
                        modifier = Modifier.clickable {
                            isDeleteMode = !isDeleteMode
                            selectedIds.clear()
                        },
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = if (isDeleteMode) Icons.Default.Close else Icons.Default.Delete,
                            contentDescription = null,
                            tint = if (isDeleteMode) MaterialTheme.extendedColors.danger else MaterialTheme.colorScheme.primary,
                            modifier = Modifier.width(18.dp),
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isDeleteMode) "Batal" else "Hapus",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isDeleteMode) MaterialTheme.extendedColors.danger else MaterialTheme.colorScheme.primary,
                        )
                    }
                }
            }

            Box(modifier = Modifier.weight(1f)) {
                when (val state = uiState) {
                    is ScheduledTransferUiState.Initial,
                    is ScheduledTransferUiState.Loading -> {
                        SkeletonList(
                            layout = SkeletonListLayout.FULL_ROW,
                            itemCount = 6,
                            showDateHeader = false,
                        )
                    }

                    is ScheduledTransferUiState.Failure -> {
                        EmptyState(
                            modifier = Modifier.fillMaxSize(),
                            message = "Data aktivitas tidak dapat dimuat.",
                            subMessage = "Terjadi kesalahan saat mengambil data.\nPeriksa koneksi anda dan coba lagi.",
                            actionText = "Coba Lagi",
                            onAction = { viewModel.getList() },
                        )
                    }

                    is ScheduledTransferUiState.ListSuccess -> {
                        if (state.items.isEmpty()) {
                            EmptyState(
                                modifier = Modifier.fillMaxSize(),
                                message = "Belum ada transfer terjadwal",
                                subMessage = "Anda belum membuat jadwal transfer. Tambahkan transfer terjadwal untuk mempermudah transaksi otomatis.",
                                actionText = null,
                            )
                        } else {
                            LazyColumn(
                                contentPadding = PaddingValues(
                                    start = if (isDeleteMode) 10.dp else 24.dp,
                                    end = 24.dp,
                                ),
                            ) {
                                items(state.items, key = { it.id }) { item ->
                                    val isSelected = selectedIds.contains(item.id)
                                    ScheduledTransferRow(
                                        item = item,
                                        isDeleteMode = isDeleteMode,
                                        isSelected = isSelected,
                                        onCheckedChange = { checked ->
                                            if (checked) selectedIds.add(item.id) else selectedIds.remove(item.id)
                                        },
                                        onClick = {
                                            if (isDeleteMode) {
                                                if (isSelected) selectedIds.remove(item.id) else selectedIds.add(item.id)
                                            } else {
                                                onItemClick(item.id)
                                            }
                                        },
                                    )
                                }
                            }
                        }
                    }

                    else -> Unit // ActionSuccess/DetailSuccess ditangani listener di atas
                }
            }

            // Tombol bawah -- hanya muncul kalau ListSuccess.
            if (listState != null) {
                Box(modifier = Modifier.padding(24.dp)) {
                    AppButton(
                        text = if (isDeleteMode) "Hapus" else "Buat Transfer Terjadwal",
                        enabled = !isDeleteMode || selectedIds.isNotEmpty(),
                        onClick = {
                            if (!isDeleteMode) {
                                onBackClick()
                            } else {
                                showDeleteConfirm = true
                            }
                        },
                    )
                }
            }
        }
    }

    if (showDeleteConfirm) {
        AppModalConfirm(
            onDismissRequest = { showDeleteConfirm = false },
            title = "Hapus Transfer Terjadwal?",
            description = "Transfer terjadwal yang dihapus tidak dapat dipulihkan.",
            cancelText = "Batal",
            onCancel = { showDeleteConfirm = false },
            confirmText = "Hapus",
            onConfirm = {
                showDeleteConfirm = false
                viewModel.delete(selectedIds.toList())
                selectedIds.clear()
                isDeleteMode = false
            },
        )
    }
}

@Composable
private fun ScheduledTransferRow(
    item: ScheduledTransferItem,
    isDeleteMode: Boolean,
    isSelected: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.Top,
    ) {
        if (isDeleteMode) {
            AppCheckBox(value = isSelected, onChanged = onCheckedChange)
            Spacer(modifier = Modifier.width(10.dp))
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(text = item.tanggalFormatted, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = item.beneficiaryName,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleSmall,
            )
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                text = item.bankInfo,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.extendedColors.textSecondary,
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(horizontalAlignment = Alignment.End) {
            Text(text = item.nominalFormatted, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (item.isActive) MaterialTheme.extendedColors.success else MaterialTheme.extendedColors.textDisabled)
                    .padding(horizontal = 14.dp, vertical = 4.dp),
            ) {
                Text(text = item.statusLabel, color = Color.White, fontSize = 10.sp)
            }
        }
    }
}