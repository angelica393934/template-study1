package bsb.dev.bsb_bangking_jp.feature.transfer

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import android.net.Uri
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import bsb.dev.bsb_bangking_jp.core.components.LocalLoadingOverlay
import bsb.dev.bsb_bangking_jp.feature.transfer.transfer_core.presentation.TransferNavEvent
import bsb.dev.bsb_bangking_jp.feature.transfer.transfer_core.presentation.TransferUiEvent
import bsb.dev.bsb_bangking_jp.feature.transfer.transfer_core.presentation.TransferViewModel
import org.koin.compose.koinInject
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import bsb.dev.bsb_bangking_jp.R
import bsb.dev.bsb_bangking_jp.core.components.AccountTile
import bsb.dev.bsb_bangking_jp.core.components.AppButton
import bsb.dev.bsb_bangking_jp.core.components.AppHeader
import bsb.dev.bsb_bangking_jp.core.components.EmptyState
import bsb.dev.bsb_bangking_jp.core.components.SearchTextField
import bsb.dev.bsb_bangking_jp.core.theme.extendedColors
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import bsb.dev.bsb_bangking_jp.core.components.LocalToastState
import bsb.dev.bsb_bangking_jp.core.skeleton.SkeletonList
import bsb.dev.bsb_bangking_jp.feature.transfer.component.DeleteConfirmSheet
import bsb.dev.bsb_bangking_jp.feature.transfer.component.UbahAliasSheet
import bsb.dev.bsb_bangking_jp.feature.transfer.saved_recipient.domain.SavedRecipientItem
import org.koin.androidx.compose.koinViewModel
import bsb.dev.bsb_bangking_jp.feature.transfer.last_transfer.presentation.LastTransferUiState
import bsb.dev.bsb_bangking_jp.feature.transfer.last_transfer.presentation.LastTransferViewModel
import bsb.dev.bsb_bangking_jp.feature.transfer.saved_recipient.presentation.SavedRecipientUiEvent
import bsb.dev.bsb_bangking_jp.feature.transfer.saved_recipient.presentation.SavedRecipientViewModel

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun TransferHomePage(
    navController: NavController,
    onBackClick: () -> Unit = {},
    onTransferSekarang: () -> Unit = {},
    onAturTerjadwalClick: () -> Unit = {},
    savedRecipientViewModel: SavedRecipientViewModel = koinViewModel(),
    lastTransferViewModel: LastTransferViewModel = koinViewModel(),
    transferViewModel: TransferViewModel = koinInject()
) {
    var showRecent by remember { mutableStateOf(true) }
    var isDeleteMode by remember { mutableStateOf(false) }
    val selectedAccounts = remember { mutableStateListOf<String>() }
    var query by remember { mutableStateOf("") }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    var editingItem by remember { mutableStateOf<SavedRecipientItem?>(null) }

    val lastTransferState by lastTransferViewModel.uiState.collectAsStateWithLifecycle()

    val savedState by savedRecipientViewModel.uiState.collectAsStateWithLifecycle()
    val toastState = LocalToastState.current
    LaunchedEffect(Unit) {
        lastTransferViewModel.load()
    }

    val transferUiState by transferViewModel.uiState.collectAsStateWithLifecycle()
    val loadingOverlay = LocalLoadingOverlay.current

    // 🔹 Overlay menyala saat: (a) proses getAccountDest (tap daftar tersimpan),
    // ATAU (b) proses hapus rekening tersimpan sedang berlangsung.
    LaunchedEffect(transferUiState.isInquiryLoading, savedState.isDeleting) {
        if (transferUiState.isInquiryLoading || savedState.isDeleting) {
            loadingOverlay.show()
        } else {
            loadingOverlay.hide()
        }
    }

    // 🔹 getAccountDest sukses -> arahkan ke form transfer sesuai jenis rekening
    // tujuan (sesama BSB / bank lain), persis logic yang dipakai transfer_baru.
    LaunchedEffect(Unit) {
        transferViewModel.navEvent.collect { event ->
            if (event is TransferNavEvent.ToDetailRekening) {
                val inquiry = event.inquiry
                val destination = if (inquiry.isOnUs) "transfer_bsb" else "transfer_umum"
                val bank = Uri.encode(inquiry.bankName)
                val accountNumber = Uri.encode(inquiry.beneficiaryAccountNo)
                val name = Uri.encode(inquiry.beneficiaryName)
                navController.navigate("$destination/$bank/$accountNumber/$name")
            }
        }
    }

    LaunchedEffect(Unit) {
        transferViewModel.uiEvent.collect { event ->
            if (event is TransferUiEvent.ShowToastError) toastState.showError(event.message)
        }
    }

    val filteredSavedRecipients = remember(query, savedState.list) {
        savedState.list?.filter {
            it.alias.contains(query, ignoreCase = true) ||
                    it.bankName.contains(query, ignoreCase = true) ||
                    it.accountNumber.contains(query)
        } ?: emptyList()
    }

    LaunchedEffect(Unit) {
        savedRecipientViewModel.uiEvent.collect { event ->
            when (event) {
                is SavedRecipientUiEvent.ShowToastSuccess -> toastState.showSuccess(event.message)
                is SavedRecipientUiEvent.ShowToastError -> toastState.showError(event.message)
                else -> Unit
            }
        }
    }

    Scaffold(
        topBar = {
            AppHeader(
                title = "Transfer",
                onBackClick = onBackClick,
            )
        },
        bottomBar = {
            Box(modifier = Modifier.padding(horizontal = 24.dp, vertical =10.dp)) {
                AppButton(
                    text = if (isDeleteMode) "Hapus Rekening Terpilih" else "Transfer Sekarang",
                    enabled = !(isDeleteMode && selectedAccounts.isEmpty()),
                    onClick = {
                        if (isDeleteMode) {
                            showDeleteConfirm = true
                        } else {
                            onTransferSekarang()
                        }
                    },
                )
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            // Banner "Transfer Terjadwal"
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 24.dp, end = 24.dp, top = 10.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.secondaryContainer)
                    .clickable { onAturTerjadwalClick() }
                    .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    painter = painterResource(id = R.drawable.asset_terjadwal),
                    contentDescription = "Terjadwal",
                    modifier = Modifier.size(53.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Transfer terjadwal lebih mudah!",
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Pantau dan kelola jadwal transfer sesuai kebutuhan anda.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.extendedColors.textSecondary,
                    )
                }
                Icon(
                    imageVector =  Icons.AutoMirrored.Filled.ArrowForwardIos,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp),
                )
            }

            // Tab "Transfer Terakhir" / "Daftar Tersimpan"
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 15.dp),
                horizontalArrangement = Arrangement.SpaceAround,
            ) {
                TransferTab(
                    title = "Transfer Terakhir",
                    active = showRecent,
                    onClick = { showRecent = true },
                    modifier = Modifier.weight(1f),
                )
                TransferTab(
                    title = "Daftar tersimpan",
                    active = !showRecent,
                    onClick = { showRecent = false },
                    modifier = Modifier.weight(1f),
                )
            }

            if (!showRecent) {
                Column(modifier = Modifier.padding(horizontal = 24.dp))
                {
                    SearchTextField(
                        value = query,
                        onValueChange = { query = it },
                        hintText = "Cari Daftar Tersimpan",
                    )
                    Spacer(modifier = Modifier.height(15.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                isDeleteMode = !isDeleteMode
                                selectedAccounts.clear()
                            },
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = if (isDeleteMode) Icons.Default.Close else Icons.Default.Delete,
                            contentDescription = null,
                            tint = if (isDeleteMode) MaterialTheme.extendedColors.danger else MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp),
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isDeleteMode) "Batal" else "Hapus Daftar",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isDeleteMode) MaterialTheme.extendedColors.danger else MaterialTheme.colorScheme.primary,
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            Box(modifier = Modifier.weight(1f)) {
                if (showRecent) {
                    when (val state = lastTransferState) {
                        is LastTransferUiState.Initial,
                        is LastTransferUiState.Loading -> {
                            SkeletonList(itemCount = 5, showDateHeader = false)
                        }

                        is LastTransferUiState.Error -> {
                            EmptyState(
                                message = "Gagal memuat data transfer terakhir.",
                                subMessage = "Terjadi kesalahan saat mengambil data.\nPeriksa koneksi anda dan coba lagi.",
                                actionText = "Coba Lagi",
                                onAction = { lastTransferViewModel.retry() },
                            )
                        }

                        is LastTransferUiState.Success -> {
                            if (state.items.isEmpty()) {
                                EmptyState(
                                    message = "Belum ada riwayat transfer.",
                                    subMessage = "Data transfer terakhir akan ditampilkan setelah Anda melakukan transaksi.",
                                    actionText = null,
                                )
                            } else {
                                LazyColumn {
                                    items(state.items, key = { it.accountDestination }) { item ->
                                        Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)) {
                                            AccountTile(
                                                initials = item.accountDestinationName,
                                                nama = item.accountDestinationName,
                                                bank = item.bankName,
                                                accountNumber = item.accountDestination,
                                                onTap = {
                                                    transferViewModel.getAccountDest(
                                                        code = item.bankCode,
                                                        accountNumber = item.accountDestination,
                                                    )
                                                },
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }else {
                    when {
                        savedState.isLoading && savedState.list == null -> {
                            SkeletonList(itemCount = 5, showDateHeader = false)
                        }
                        savedState.error != null && savedState.list == null -> {
                            EmptyState(
                                message = "Gagal memuat daftar rekening tersimpan.",
                                subMessage = "Terjadi kesalahan saat mengambil data.\nPeriksa koneksi Anda dan coba lagi.",
                                actionText = "Coba Lagi",
                                onAction = { savedRecipientViewModel.getSavedRecipients() },
                            )
                        }
                        filteredSavedRecipients.isEmpty() -> {
                            EmptyState(
                                message = "Belum ada rekening tersimpan.",
                                subMessage = "Tambahkan rekening tersimpan untuk mempermudah transfer berikutnya.",
                                actionText = null,
                            )
                        }
                        else -> {
                            LazyColumn {
                                items(filteredSavedRecipients, key = { it.id }) { item ->
                                    val isSelected = selectedAccounts.contains(item.id)
                                    Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)) {
                                        AccountTile(
                                            initials = item.alias,
                                            nama = item.alias,
                                            bank = item.bankName,
                                            accountNumber = item.accountNumber,
                                            isSelected = isSelected,
                                            showCheckbox = isDeleteMode,
                                            checkboxValue = isSelected,
                                            onCheckboxChanged = { checked ->
                                                if (checked) selectedAccounts.add(item.id)
                                                else selectedAccounts.remove(item.id)
                                            },
                                            onTap = {
                                                if (isDeleteMode) {
                                                    if (isSelected) selectedAccounts.remove(item.id)
                                                    else selectedAccounts.add(item.id)
                                                } else {
                                                    transferViewModel.getAccountDest(
                                                        code = item.bankCode,
                                                        accountNumber = item.accountNumber,
                                                    )
                                                }
                                            },
                                            onEdit = if (!isDeleteMode) {
                                                { editingItem = item }
                                            } else null,
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    //sheet hapus
    if (showDeleteConfirm) {
        DeleteConfirmSheet(
            onDismiss = {
                showDeleteConfirm = false
            },
            onConfirm = {
                savedRecipientViewModel.deleteSavedRecipients(
                    selectedAccounts.toList()
                )

                selectedAccounts.clear()
                isDeleteMode = false
                showDeleteConfirm = false
            }
        )
    }
    // Sheet ubah alias
    editingItem?.let { item ->
        UbahAliasSheet(
            item = item,
            viewModel = savedRecipientViewModel,
            onDismiss = { editingItem = null },
        )
    }

}

@Composable
private fun TransferTab(
    title: String,
    active: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = title,
            fontWeight = FontWeight.Bold,
            color = if (active) MaterialTheme.extendedColors.textPrimary else MaterialTheme.extendedColors.textDisabled,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .height(3.dp)
                .width(120.dp)
                .background(
                    if (active) MaterialTheme.colorScheme.primary else androidx.compose.ui.graphics.Color.Transparent
                )
        )
    }
}