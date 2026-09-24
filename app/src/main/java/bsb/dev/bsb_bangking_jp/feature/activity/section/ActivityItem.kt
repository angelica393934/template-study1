package bsb.dev.bsb_bangking_jp.feature.activity.section

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CompareArrows
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import bsb.dev.bsb_bangking_jp.core.util.RupiahFormat
import bsb.dev.bsb_bangking_jp.feature.activity.data.HistoryItem

@Composable
fun ActivityDateSection(
    tanggal: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(
                horizontal = 24.dp,
                vertical = 12.dp,
            ),
    ) {
        Text(
            text = tanggal,
            style = MaterialTheme.typography.titleMedium,
        )
    }
}

@Composable
fun ActivityItemRow(
    transaksi: HistoryItem,
    modifier: Modifier = Modifier,
) {
    val warnaNominal = if (transaksi.isMasuk) {
        Color(0xFF2E7D32)
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    val jenisLower = transaksi.jenisTransaksi.lowercase()

    val icon: ImageVector = when {
        "transfer" in jenisLower -> Icons.Filled.CompareArrows
        "tagihan" in jenisLower -> Icons.AutoMirrored.Filled.ReceiptLong
        "top" in jenisLower -> Icons.Filled.AccountBalanceWallet
        else -> Icons.AutoMirrored.Filled.HelpOutline
    }

    // deskripsiTransaksi = "Jenis arah\nrekeningTujuan"
    // Baris pertama menjadi title, baris kedua menjadi subtitle.
    val descLines = transaksi.deskripsiTransaksi.split("\n")

    val title = descLines
        .getOrNull(0)
        ?.takeIf { it.isNotBlank() }
        ?: transaksi.jenisTransaksi.ifBlank { "Transaksi" }

    val subtitle = descLines
        .getOrNull(1)
        .orEmpty()

    val nominalInt = transaksi.amountValue.toInt()
    val nominalFormatted = RupiahFormat(nominalInt)

    val nominalDisplay = if (transaksi.isMasuk) {
        "+ $nominalFormatted"
    } else {
        "- $nominalFormatted"
    }
    Column(
        modifier = modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 24.dp,
                    vertical = 15.dp,
                ),
            verticalAlignment = Alignment.Top,
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(25.dp),
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                    )

                    if (subtitle.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = subtitle,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = nominalDisplay,
                    style = MaterialTheme.typography.titleMedium,
                    color = warnaNominal,
                )
            }
        }

        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 24.dp),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.surfaceVariant,
        )
    }
}