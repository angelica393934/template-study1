package bsb.dev.bsb_bangking_jp.feature.manage_scheduled_transfer

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Timer
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import bsb.dev.bsb_bangking_jp.core.components.AppButton
import bsb.dev.bsb_bangking_jp.core.components.AppHeader
import bsb.dev.bsb_bangking_jp.core.components.AppModalConfirm
import bsb.dev.bsb_bangking_jp.core.components.InitialAvatar
import bsb.dev.bsb_bangking_jp.core.components.LocalToastState
import bsb.dev.bsb_bangking_jp.core.skeleton.SkeletonDetailTransferTerjadwal
import bsb.dev.bsb_bangking_jp.core.theme.extendedColors
import bsb.dev.bsb_bangking_jp.core.util.RupiahFormat
import bsb.dev.bsb_bangking_jp.feature.manage_scheduled_transfer.domain.ScheduledTransferDetail
import bsb.dev.bsb_bangking_jp.feature.manage_scheduled_transfer.presentation.ScheduledTransferUiState
import bsb.dev.bsb_bangking_jp.feature.manage_scheduled_transfer.presentation.ScheduledTransferViewModel
import bsb.dev.bsb_bangking_jp.shared.rekening_lainnya.data.cashBalanceValue
import bsb.dev.bsb_bangking_jp.shared.rekening_lainnya.presentation.RekeningLainnyaViewModel
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduledTransferDetailPage(
    id: Int,
    viewModel: ScheduledTransferViewModel,
    onBackClick: () -> Unit,
    rekeningViewModel: RekeningLainnyaViewModel = koinInject(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val rekeningUiState by rekeningViewModel.uiState.collectAsStateWithLifecycle()
    val toastState = LocalToastState.current

    var showDeleteConfirm by remember { mutableStateOf(false) }

    LaunchedEffect(id) {
        viewModel.getDetail(id)
        if (rekeningUiState.rekeningList == null) rekeningViewModel.load()
    }

    LaunchedEffect(uiState) {
        when (val state = uiState) {
            is ScheduledTransferUiState.ActionSuccess -> {
                when (state.action) {
                    "toggle" -> {
                        toastState.showSuccess("Status transfer berhasil diperbarui")
                        viewModel.getDetail(id, forceRefresh = true)
                    }
                    "delete" -> {
                        toastState.showSuccess("Transfer terjadwal berhasil dihapus")
                        onBackClick()
                    }
                }
            }
            is ScheduledTransferUiState.Failure -> toastState.showError(state.respMessage)
            else -> Unit
        }
    }

    Scaffold(
        topBar = { AppHeader(title = "Detail Transfer Terjadwal", onBackClick = onBackClick) },
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (val state = uiState) {
                is ScheduledTransferUiState.Initial,
                is ScheduledTransferUiState.Loading -> SkeletonDetailTransferTerjadwal()

                is ScheduledTransferUiState.Failure -> {
                    Text(
                        text = state.respMessage,
                        modifier = Modifier.padding(24.dp),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }

                is ScheduledTransferUiState.DetailSuccess -> {
                    DetailContent(
                        detail = state.detail,
                        sourceBalance = rekeningUiState.rekeningList
                            ?.firstOrNull { it.number == state.detail.sourceAccountNo }
                            ?.cashBalanceValue(),
                        onTogglePause = { viewModel.togglePause(state.detail.id) },
                        onDeleteClick = { showDeleteConfirm = true },
                    )
                }

                else -> Unit
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
                viewModel.delete(listOf(id))
            },
        )
    }
}

@Composable
private fun DetailContent(
    detail: ScheduledTransferDetail,
    sourceBalance: Double?,
    onTogglePause: () -> Unit,
    onDeleteClick: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
        ) {
            Row {
                InitialAvatar(radius = 30.0, initials = detail.beneficiaryName)
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(text = detail.beneficiaryName, style = MaterialTheme.typography.titleMedium)
                    Text(
                        text = detail.beneficiaryBankName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.extendedColors.textSecondary,
                    )
                    Text(
                        text = detail.beneficiaryAccountNo,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.extendedColors.textSecondary,
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = MaterialTheme.extendedColors.divider)
            Spacer(modifier = Modifier.height(8.dp))

            Text(text = "Detail Transaksi", style = MaterialTheme.typography.titleMedium)

            DetailRow("Status", detail.statusLabel, isChip = true, isActive = detail.isActive)
            DetailRow("Frekuensi", detail.scheduleTypeLabel)

            if (detail.scheduleTypeLabel == "Sekali") {
                DetailRow("Tanggal Transfer", detail.tanggalFormatted)
            } else if (detail.scheduleTypeLabel == "Setiap Bulan") {
                DetailRow("Tanggal Mulai", detail.scheduledLabel)
            }

            if (detail.startDateFormatted != "-") DetailRow("Mulai", detail.startDateFormatted)
            if (detail.endDateFormatted != "-") DetailRow("Sampai", detail.endDateFormatted)

            DetailRow("Jumlah Transfer", detail.nominalFormatted)

            HorizontalDivider(color = MaterialTheme.extendedColors.divider)

            DetailRow("Keterangan", detail.remark.ifEmpty { "-" })

            HorizontalDivider(color = MaterialTheme.extendedColors.divider)
            Spacer(modifier = Modifier.height(10.dp))

            Text(text = "Rekening Sumber", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(10.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(25.dp))
                    .border(1.dp, MaterialTheme.extendedColors.textDisabled, RoundedCornerShape(25.dp))
                    .padding(16.dp),
            ) {
                Column {
                    Text(
                        text = "Saldo Sekarang",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.extendedColors.textSecondary,
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = RupiahFormat((sourceBalance ?: 0.0).toInt()),
                        style = MaterialTheme.typography.titleLarge,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = detail.sourceAccountNo,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.extendedColors.textSecondary,
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Pastikan saldo Anda mencukupi sebelum jadwal transaksi.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.extendedColors.textSecondary,
            )
        }

        Row(modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 24.dp)) {
            AppButton(
                text = if (detail.isActive) "Jeda" else "Lanjutkan",
                icon = if (detail.isActive) Icons.Default.Timer else Icons.Default.PlayArrow,
                iconBeforeText = true,
                backgroundColor = if (detail.isActive) MaterialTheme.extendedColors.inputBackground else MaterialTheme.colorScheme.primaryContainer,
                textColor = if (detail.isActive) MaterialTheme.extendedColors.textSecondary else MaterialTheme.colorScheme.primary,
                modifier = Modifier.weight(1f),
                onClick = onTogglePause,
            )

            if (detail.canDelete) {
                Spacer(modifier = Modifier.width(12.dp))
                AppButton(
                    text = "Hapus",
                    icon = Icons.Default.Delete,
                    iconBeforeText = true,
                    modifier = Modifier.weight(1f),
                    onClick = onDeleteClick,
                )
            }
        }
    }
}

@Composable
private fun DetailRow(
    label: String,
    value: String,
    isChip: Boolean = false,
    isActive: Boolean = true,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.extendedColors.textSecondary,
            modifier = Modifier.weight(1f),
        )

        if (isChip) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (isActive) MaterialTheme.extendedColors.success else MaterialTheme.extendedColors.textDisabled)
                    .padding(horizontal = 12.dp, vertical = 4.dp),
            ) {
                Text(text = value, fontSize = 10.sp, color = Color.White)
            }
        } else {
            Text(text = value, style = MaterialTheme.typography.titleMedium)
        }
    }
}