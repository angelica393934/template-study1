package bsb.dev.bsb_bangking_jp.feature.transfer.component

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import bsb.dev.bsb_bangking_jp.core.component.AppModalConfirm

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeleteConfirmSheet(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    AppModalConfirm(
        onDismissRequest = onDismiss,
        title = "Hapus Rekening Terpilih?",
        description = "Rekening yang dihapus akan hilang dari daftar tersimpan dan tidak dapat dipulihkan.\n Lanjutkan menghapus rekening ini?",
        cancelText = "Batal",
        onCancel = onDismiss,
        confirmText = "Hapus",
        onConfirm = onConfirm,
    )
}