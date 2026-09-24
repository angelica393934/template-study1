package bsb.dev.bsb_bangking_jp.feature.transfer.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import bsb.dev.bsb_bangking_jp.core.components.AppCheckBox
import bsb.dev.bsb_bangking_jp.core.components.InitialAvatar
import bsb.dev.bsb_bangking_jp.core.theme.extendedColors

@Composable
fun AccountTile(
    initials: String,
    radius: Double = 26.0,
    nama: String,
    bank: String,
    accountNumber: String,
    modifier: Modifier = Modifier,
    onTap: (() -> Unit)? = null,
    onEdit: (() -> Unit)? = null,
    onDeleteTap: (() -> Unit)? = null,
    showCheckbox: Boolean = false,
    checkboxValue: Boolean = false,
    onCheckboxChanged: ((Boolean) -> Unit)? = null,
    isSelected: Boolean = false,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                if (isSelected) MaterialTheme.colorScheme.tertiaryContainer
                else Color.Transparent
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(enabled = onTap != null) { onTap?.invoke() },
//                .padding(start = 24.dp, end = 24.dp, top = 11.dp, bottom = 11.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // leading: checkbox (opsional) + avatar
            if (showCheckbox) {
                AppCheckBox(value =checkboxValue, onChanged = onCheckboxChanged)
                Spacer(modifier = Modifier.width(12.dp))

            }

              InitialAvatar(
                  radius = radius,
                initials = initials,
                isSelected = isSelected,
            )

            Spacer(modifier = Modifier.width(12.dp))

            // title + subtitle
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = nama,
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    text = bank,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.extendedColors.textSecondary,
                )
                Text(
                    text = accountNumber,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.extendedColors.textSecondary,
                )
            }

            // trailing: onEdit ATAU onDeleteTap (saling eksklusif, sama seperti )
            when {
                onEdit != null -> Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Ubah alias",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .size(20.dp)
                        .clickable { onEdit() },
                )
                onDeleteTap != null -> Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Hapus",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .size(20.dp)
                        .clickable { onDeleteTap() },
                )
            }
        }
    }
}