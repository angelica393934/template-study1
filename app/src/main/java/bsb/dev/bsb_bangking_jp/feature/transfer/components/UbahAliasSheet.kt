package bsb.dev.bsb_bangking_jp.feature.transfer.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import bsb.dev.bsb_bangking_jp.core.components.AppButton
import bsb.dev.bsb_bangking_jp.core.components.AppModalBottomSheet
import bsb.dev.bsb_bangking_jp.core.components.AppTextField
import bsb.dev.bsb_bangking_jp.core.theme.extendedColors
import bsb.dev.bsb_bangking_jp.feature.transfer.saved_recipient.domain.SavedRecipientItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UbahAliasSheet(
    item: SavedRecipientItem,
    errorText: String? = null,          // error dari server (updateError)
    onClearError: () -> Unit = {},
    onDismiss: () -> Unit,
    onConfirm: (alias: String) -> Unit,
) {
    var alias by remember(item.id) { mutableStateOf(item.alias) }
    var localError by remember(item.id) { mutableStateOf<String?>(null) }
    val error = localError ?: errorText

    AppModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(text = "Pemilik Rekening", style = MaterialTheme.typography.titleMedium)
            AccountTile(
                initials = item.alias,
                nama = item.alias,
                bank = item.bankName,
                accountNumber = item.accountNumber,
            )
            HorizontalDivider(thickness = 1.dp, color = extendedColors.strip)
            Text(text = "Ubah Nama Alias", style = MaterialTheme.typography.titleMedium)
            Text(
                text = "Perbarui nama rekening agar mudah dikenali saat melakukan transfer.",
                style = MaterialTheme.typography.bodySmall,
                color = extendedColors.cardBackground,
            )
            AppTextField(
                value = alias,
                onValueChange = { value ->
                    alias = value
                    if (value.isNotEmpty()) {
                        localError = null
                        onClearError()
                    }
                },
                hintText = "Masukkan Nama Alias",
                icon = Icons.Default.Person,
                errorText = error,
                showError = error != null,
            )

            AppButton(
                text = "Simpan",
                onClick = {
                    val value = alias.trim()
                    when {
                        value.isEmpty() -> localError = "Nama alias tidak boleh kosong"
                        value == item.alias -> localError = "Nama alias tidak berubah"
                        else -> {
                            localError = null
                            onConfirm(value)
                        }
                    }
                },
            )
        }
    }
}