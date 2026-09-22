// feature/beranda/section/SaldoCardDashboard.kt
package bsb.dev.bsb_bangking_jp.feature.beranda.section

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import bsb.dev.bsb_bangking_jp.R
import bsb.dev.bsb_bangking_jp.core.components.RekeningLainnyaSheet
import bsb.dev.bsb_bangking_jp.core.components.RekeningSheetMode
import bsb.dev.bsb_bangking_jp.core.util.RupiahFormat
import bsb.dev.bsb_bangking_jp.shared.rekening_lainnya.data.RekeningItem
import bsb.dev.bsb_bangking_jp.shared.rekening_lainnya.data.cashBalanceValue

@Composable
fun SaldoCardDashboard(
    rekeningList: List<RekeningItem>,
    onSelectPrimaryAccount: (accountNumber: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (rekeningList.isEmpty()) return

    var showSheet by remember { mutableStateOf(false) }

    val primary = rekeningList.firstOrNull { it.isPrimary } ?: rekeningList.first()

    SaldoCardBase(
        nama = primary.name,
        rekening = primary.number,
        saldo = RupiahFormat(primary.cashBalanceValue().toInt()),
        modifier = modifier,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.label_tabungan_utama),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )
            Spacer(modifier = Modifier.width(12.dp))
            Row(
                modifier = Modifier.clickable { showSheet = true },
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.label_rekening_lainnya),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary,
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Filled.ArrowForwardIos,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp),
                )
            }
        }
    }

    if (showSheet) {
        val sortedForSheet = rekeningList
            .filter { it.visible }
            .sortedByDescending { it.isPrimary }

        RekeningLainnyaSheet(
            daftarRekening = sortedForSheet,
            mode = RekeningSheetMode.PILIH_REKENING_UTAMA,
            rekeningAktif = primary.number,
            title = stringResource(R.string.title_pilih_rekening_utama),
            onDismiss = { showSheet = false },
            onSelected = { selected ->
                showSheet = false
                onSelectPrimaryAccount(selected.number)
            },
        )
    }
}