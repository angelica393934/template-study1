package bsb.dev.bsb_bangking_jp.feature.message.section

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import bsb.dev.bsb_bangking_jp.core.theme.Green700
import bsb.dev.bsb_bangking_jp.core.theme.Red700
import bsb.dev.bsb_bangking_jp.core.util.RupiahFormat
import bsb.dev.bsb_bangking_jp.feature.message.domain.MessageItem

/**
 * Padanan dari Pages/message/section/messageItem.dart (messageSectionTanggal + messageItem).
 * Sekarang terhubung ke data asli dari getmessage (MessageItem), bukan dummy lagi.
 *
 * title/subtitle/status di bawah masih pemetaan sementara dari field getmessage
 * (jenisTransaksi/note/status) -- sesuaikan lagi kalau field yang lebih pas
 * ternyata datang dari respons getmessagebyid.
 */
@Composable
fun MessageSectionTanggal(
    tanggal: String,
    items: List<MessageItem>,
    modifier: Modifier = Modifier,
    onItemClick: (MessageItem) -> Unit = {},
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primaryContainer)
                .padding(horizontal = 24.dp, vertical = 8.dp),
        ) {
            Text(
                text = tanggal,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        items.forEach { message ->
            MessageItemRow(
                message = message,
                onClick = { onItemClick(message) },
            )
        }
    }
}

/** Dibuka (bukan private) supaya bisa dipakai langsung dari MessagePage kalau perlu. */
@Composable
fun MessageItemRow(
    message: MessageItem,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
) {
    val title = message.jenisTransaksi.ifBlank { message.type.ifBlank { "Transaksi" } }
    val destination = message.accountDestinationName.ifBlank {
        message.accountDestination
    }
    val subtitle = listOf(
        message.bankName.trim(),
        destination.trim()
    )
        .filter { it.isNotBlank() }
        .joinToString(" - ")
        .ifBlank { "-" }
    val amountText = RupiahFormat(message.totalAmount.toInt())
    val displayAmount = if (message.totalAmount < 0) amountText else "- $amountText"
    val icon = getMessageIcon(title)
    val statusColor = getStatusColor(message.status)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.primary,
            )
            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, style = MaterialTheme.typography.titleSmall)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.outline,
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = displayAmount,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(statusColor)
                        .padding(horizontal = 8.dp, vertical = 2.dp),
                ) {
                    Text(
                        text = message.status.ifBlank { "-" },
                        color = MaterialTheme.colorScheme.background,
                        fontSize = 12.sp,
                    )
                }
            }
        }
        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 24.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            thickness = 1.dp,
        )
    }
}

/** Padanan dari _getIcon() di Dart -- deteksi ikon otomatis berdasarkan title/jenis. */
private fun getMessageIcon(title: String): ImageVector {
    val t = title.lowercase()
    return when {
        t.contains("transfer") -> Icons.Filled.CompareArrows
        t.contains("top up") -> Icons.Filled.AccountBalanceWallet
        t.contains("qris") -> Icons.Filled.QrCode
        t.contains("tagihan") -> Icons.AutoMirrored.Filled.ReceiptLong
        else -> Icons.Filled.Notifications
    }
}

/** Padanan dari _getStatusColor() di Dart -- warna badge status otomatis. */
@Composable
private fun getStatusColor(status: String): Color = when (status.uppercase()) {
    "SUCCESS" -> Green700
    "FAIL" -> Red700
    "PENDING" -> MaterialTheme.colorScheme.outline
    else -> MaterialTheme.colorScheme.onSurfaceVariant
}