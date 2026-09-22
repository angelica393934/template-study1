package bsb.dev.bsb_bangking_jp.core.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import bsb.dev.bsb_bangking_jp.core.theme.extendedColors
import bsb.dev.bsb_bangking_jp.feature.beranda.section.SaldoCardBase

/**
 * Varian SaldoCardBase untuk kondisi rekening GAGAL dimuat & belum pernah ada data
 * (isRekeningLoading == false, rekeningList == null, rekeningError != null).
 * Bentuk card tetap sama persis dengan versi normal, cuma isinya diganti "-" semua
 * supaya layout tidak "loncat" dan user tetap tahu ada slot saldo di situ, bukan
 * hilang begitu saja. Tap teks "Coba Lagi" untuk retry fetch.
 */
@Composable
fun SaldoCardEmpty(
    modifier: Modifier = Modifier,
    onRetry: (() -> Unit)? = null,
) {
    SaldoCardBase(
        nama = "-",
        rekening = "-",
        saldo = "-",
        modifier = modifier,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Data rekening tidak tersedia",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.extendedColors.textSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )
            if (onRetry != null) {
                Spacer(modifier = Modifier.width(12.dp))
                Row(
                    modifier = Modifier.clickable { onRetry() },
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.width(16.dp),
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Coba Lagi",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }
        }
    }
}