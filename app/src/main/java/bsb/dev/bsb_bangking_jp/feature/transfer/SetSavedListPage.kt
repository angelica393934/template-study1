package bsb.dev.bsb_bangking_jp.feature.transfer

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import bsb.dev.bsb_bangking_jp.core.components.AppButton
import bsb.dev.bsb_bangking_jp.core.components.AppCheckBox
import bsb.dev.bsb_bangking_jp.core.components.AppHeader
import bsb.dev.bsb_bangking_jp.core.components.EmptyState
import bsb.dev.bsb_bangking_jp.core.components.LocalLoadingOverlay
import bsb.dev.bsb_bangking_jp.core.components.LocalToastState
import bsb.dev.bsb_bangking_jp.core.skeleton.SkeletonList
import bsb.dev.bsb_bangking_jp.core.theme.extendedColors
import bsb.dev.bsb_bangking_jp.feature.transfer.components.AccountTile
import bsb.dev.bsb_bangking_jp.feature.transfer.components.DeleteConfirmSheet
import bsb.dev.bsb_bangking_jp.feature.transfer.components.UbahAliasSheet
import bsb.dev.bsb_bangking_jp.feature.transfer.saved_recipient.domain.SavedRecipientItem
import bsb.dev.bsb_bangking_jp.feature.transfer.saved_recipient.presentation.SavedRecipientUiEvent
import bsb.dev.bsb_bangking_jp.feature.transfer.saved_recipient.presentation.SavedRecipientViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun SetSavedListPage(
    onBackClick: () -> Unit = {},
    savedRecipientViewModel: SavedRecipientViewModel = koinViewModel(),
) {
    val selectedAccounts = remember { mutableStateListOf<String>() }
    val toastState = LocalToastState.current
    val loadingOverlay = LocalLoadingOverlay.current
    val savedState by savedRecipientViewModel.uiState.collectAsStateWithLifecycle()

    var query by remember { mutableStateOf("") }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    var editingItem by remember { mutableStateOf<SavedRecipientItem?>(null) }

    val filteredSavedRecipients = remember(query, savedState.list) {
        savedState.list?.filter {
            it.alias.contains(query, ignoreCase = true) ||
                    it.bankName.contains(query, ignoreCase = true) ||
                    it.accountNumber.contains(query)
        } ?: emptyList()
    }

    // ---- Pilih semua ----
    val isAllSelected = filteredSavedRecipients.isNotEmpty() &&
            filteredSavedRecipients.all { it.id in selectedAccounts }

    // ---- Ubah nama hanya aktif kalau tepat 1 yang dipilih ----
    val canEditAlias = selectedAccounts.size == 1

    // Loading overlay untuk hapus & ubah alias (sama seperti pola hapus)
    LaunchedEffect(savedState.isDeleting, savedState.isUpdating) {
        if (savedState.isDeleting || savedState.isUpdating) loadingOverlay.show()
        else loadingOverlay.hide()
    }

    LaunchedEffect(Unit) {
        savedRecipientViewModel.uiEvent.collect { event ->
            when (event) {
                is SavedRecipientUiEvent.ShowToastSuccess -> toastState.showSuccess(event.message)
                is SavedRecipientUiEvent.ShowToastError -> toastState.showError(event.message)
                is SavedRecipientUiEvent.AliasUpdated -> {
                    toastState.showSuccess("Nama alias berhasil diubah")
                    editingItem = null
                    selectedAccounts.clear()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            AppHeader(title = "Atur Daftar", onBackClick = onBackClick)
        },
        bottomBar = {
            Box(modifier = Modifier.padding(horizontal = 24.dp, vertical = 10.dp)) {
                Column {
                    val editColor =
                        if (canEditAlias) MaterialTheme.colorScheme.primary
                        else extendedColors.textDisabled

                    Row(
                        modifier = Modifier
                            .padding(vertical = 15.dp)
                            .fillMaxWidth()
                            .clickable {
                                when {
                                    selectedAccounts.isEmpty() -> toastState.showWarning(
                                        "Silakan pilih satu daftar terlebih dahulu untuk mengubah nama."
                                    )
                                    selectedAccounts.size > 1 -> toastState.showWarning(
                                        "Tidak dapat mengubah nama. Pilih satu daftar saja untuk melanjutkan."
                                    )
                                    else -> {
                                        editingItem = savedState.list
                                            ?.firstOrNull { it.id == selectedAccounts.first() }
                                    }
                                }
                            },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = null,
                            tint = editColor,
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "Ubah Nama",
                            style = MaterialTheme.typography.titleMedium,
                            color = editColor,
                            textAlign = TextAlign.Center,
                        )
                    }
                    AppButton(
                        text = "Hapus Daftar Rekening",
                        onClick = {
                            when {
                                selectedAccounts.isEmpty() -> toastState.showWarning(
                                    "Silakan pilih satu daftar terlebih dahulu untuk menghapus daftar tersimpan."
                                )
                                else -> {showDeleteConfirm = true }
                            }
                                  },
                        iconBeforeText = true,
                        icon = Icons.Default.Delete,
                    )
                }
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {

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
                    // ===== Baris "Pilih Semua" di pojok kanan =====
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 8.dp)
                            .clickable {
                                toggleSelectAll(filteredSavedRecipients, selectedAccounts, !isAllSelected)
                            },
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        AppCheckBox(
                            value = isAllSelected,
                            onChanged = { checked ->
                                toggleSelectAll(filteredSavedRecipients, selectedAccounts, checked)
                            },
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Pilih Semua",
                            style = MaterialTheme.typography.titleSmall,
                            color = extendedColors.textSecondary,
                        )
                    }

                    LazyColumn {
                        items(filteredSavedRecipients, key = { it.id }) { item ->
                            val isSelected = selectedAccounts.contains(item.id)
                            Column{
                                AccountTile(
                                    initials = item.alias,
                                    nama = item.alias,
                                    bank = item.bankName,
                                    accountNumber = item.accountNumber,
                                    isSelected = isSelected,
                                    showCheckbox = true,
                                    checkboxValue = isSelected,
                                    onCheckboxChanged = { checked ->
                                        if (checked) selectedAccounts.add(item.id)
                                        else selectedAccounts.remove(item.id)
                                    },
                                    onTap = {
                                        if (isSelected) selectedAccounts.remove(item.id)
                                        else selectedAccounts.add(item.id)
                                    },
                                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // ---- Sheet hapus ----
    if (showDeleteConfirm) {
        DeleteConfirmSheet(
            onDismiss = { showDeleteConfirm = false },
            onConfirm = {
                savedRecipientViewModel.deleteSavedRecipients(selectedAccounts.toList())
                selectedAccounts.clear()
                showDeleteConfirm = false
            }
        )
    }

    // ---- Sheet ubah alias (UI saja, logic di sini) ----
    editingItem?.let { item ->
        UbahAliasSheet(
            item = item,
            errorText = savedState.updateError,
            onClearError = { savedRecipientViewModel.clearUpdateError() },
            onDismiss = {
                editingItem = null
                savedRecipientViewModel.clearUpdateError()
            },
            onConfirm = { newAlias ->
                savedRecipientViewModel.updateAlias(item.id, newAlias)
                // sheet ditutup oleh event AliasUpdated di atas
            },
        )
    }
}

private fun toggleSelectAll(
    items: List<SavedRecipientItem>,
    selected: MutableList<String>,
    select: Boolean,
) {
    if (select) {
        items.forEach { if (it.id !in selected) selected.add(it.id) }
    } else {
        selected.removeAll(items.map { it.id }.toSet())
    }
}