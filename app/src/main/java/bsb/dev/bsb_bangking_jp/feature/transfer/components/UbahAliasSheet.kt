package bsb.dev.bsb_bangking_jp.feature.transfer.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import bsb.dev.bsb_bangking_jp.core.components.AppButton
import bsb.dev.bsb_bangking_jp.core.components.AppModalBottomSheet
import bsb.dev.bsb_bangking_jp.core.components.AppTextField
import bsb.dev.bsb_bangking_jp.core.components.LocalLoadingOverlay
import bsb.dev.bsb_bangking_jp.core.components.LocalToastState
import bsb.dev.bsb_bangking_jp.core.theme.extendedColors
import bsb.dev.bsb_bangking_jp.feature.transfer.saved_recipient.domain.SavedRecipientItem
import bsb.dev.bsb_bangking_jp.feature.transfer.saved_recipient.presentation.SavedRecipientUiEvent
import bsb.dev.bsb_bangking_jp.feature.transfer.saved_recipient.presentation.SavedRecipientViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UbahAliasSheet(
    item: SavedRecipientItem,
    viewModel: SavedRecipientViewModel,
    onDismiss: () -> Unit,
) {
    var alias by remember(item.id) {
        mutableStateOf(item.alias)
    }

    var localError by remember(item.id) {
        mutableStateOf<String?>(null)
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val loadingOverlay = LocalLoadingOverlay.current
    val toastState = LocalToastState.current
// 🔹 Cuma SHOW di sini. HIDE sengaja dipindah ke handler event di bawah,
    // supaya urutannya pasti: overlay tertutup dulu, baru sheet dismiss --
    // tidak bergantung pada dua LaunchedEffect terpisah yang bisa beda timing.
    LaunchedEffect(uiState.isUpdating) {
        if (uiState.isUpdating) loadingOverlay.show()
    }

    // ============================================================
    // UPDATE SUCCESS
    // ============================================================

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is SavedRecipientUiEvent.AliasUpdated -> {
                    loadingOverlay.hide() // tutup overlay dulu...
                    onDismiss()           // ...baru tutup sheet
                }
                is SavedRecipientUiEvent.ShowToastError -> {
                    loadingOverlay.hide() // gagal -> overlay tetap harus ditutup
                    // toast-nya sendiri sudah ditampilkan oleh collector global
                    // di TransferHomePage (lihat poin 3), jadi tidak perlu diulang di sini.
                }
                else -> Unit
            }
        }
    }

    AppModalBottomSheet(
        onDismissRequest = onDismiss,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
        ) {

            // ====================================================
            // PEMILIK REKENING
            // ====================================================

            Text(
                text = "Pemilik Rekening",
                style = MaterialTheme.typography.titleMedium,
            )

            Spacer(
                modifier = Modifier.height(15.dp)
            )

            // ====================================================
            // ACCOUNT INFO
            // ====================================================

            AccountTile(
                initials = item.alias,
                nama = item.alias,
                bank = item.bankName,
                accountNumber = item.accountNumber,
            )

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            // ====================================================
            // DIVIDER
            // ====================================================

            HorizontalDivider(
                thickness = 1.dp,
                color = MaterialTheme.extendedColors.strip,
            )

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            // ====================================================
            // TITLE
            // ====================================================

            Text(
                text = "Ubah Nama Alias",
                style = MaterialTheme.typography.titleMedium,
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            Text(
                text = "Perbarui nama rekening agar mudah dikenali saat melakukan transfer.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.extendedColors.cardBackground,
            )

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            // ====================================================
            // INPUT ALIAS
            // ====================================================

            val error = localError ?: uiState.updateError

            AppTextField(
                value = alias,
                onValueChange = { value ->
                    alias = value

                    if (value.isNotEmpty()) {
                        localError = null
                        viewModel.clearUpdateError()
                    }
                },
                hintText = "Masukkan Nama Alias",
                icon = Icons.Default.Person,
                errorText = error,
                showError = error != null,
            )

            Spacer(
                modifier = Modifier.height(15.dp)
            )

            // ====================================================
            // BUTTON SIMPAN
            // ====================================================

            AppButton(
                text = "Simpan",
                onClick = {

                    val value = alias.trim()

                    // -------------------------------
                    // Alias kosong
                    // -------------------------------

                    if (value.isEmpty()) {
                        localError = "Nama alias tidak boleh kosong"
                        return@AppButton
                    }

                    // -------------------------------
                    // Alias tidak berubah
                    // -------------------------------

                    if (value == item.alias) {
                        localError = "Nama alias tidak berubah"
                        return@AppButton
                    }

                    // -------------------------------
                    // Update
                    // -------------------------------

                    localError = null
                    viewModel.updateAlias(
                        item.id,
                        value,
                    )
                },
            )
        }
    }
}