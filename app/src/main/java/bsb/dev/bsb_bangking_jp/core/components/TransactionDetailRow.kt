package bsb.dev.bsb_bangking_jp.core.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import bsb.dev.bsb_bangking_jp.core.theme.extendedColors

/**
 * Padanan `_buildDetailRow` yang berulang di beberapa halaman  (PeriksaKembaliSheet,
 * TransferBerhasilPage, TransferBerhasilDijadwalkanPage) -- disatukan di sini supaya tidak ada
 * 3 copy fungsi yang isinya sama.
 *
 * Auto hijau kalau `value` == "gratis" (case-insensitive), sama seperti versi  aslinya.
 */
@Composable
fun TransactionDetailRow(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
    titleStyle: TextStyle? = null,
    valueStyle: TextStyle? = null,
) {
    val isGratis = value.trim().equals("gratis", ignoreCase = true)

    val finalValueStyle = valueStyle ?: if (isGratis) {
        MaterialTheme.typography.titleMedium.copy(color = MaterialTheme.extendedColors.success)
    } else {
        MaterialTheme.typography.bodyMedium.copy(
            color = MaterialTheme.extendedColors.textSecondary,
            fontWeight = FontWeight.Medium,
        )
    }
    val finalTitleStyle = titleStyle ?: MaterialTheme.typography.bodyMedium.copy(
        color = MaterialTheme.extendedColors.textSecondary,
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(text = title, style = finalTitleStyle)
        Text(
            text = value,
            style = finalValueStyle,
            textAlign = TextAlign.End,
            modifier = Modifier.padding(start = 12.dp),
        )
    }
}