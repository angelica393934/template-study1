package bsb.dev.bsb_bangking_jp.feature.activity.section

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
import androidx.compose.ui.unit.dp
import bsb.dev.bsb_bangking_jp.core.component.RekeningLainnyaSheet
import bsb.dev.bsb_bangking_jp.core.component.RekeningSheetMode
import bsb.dev.bsb_bangking_jp.core.util.RupiahFormat
import bsb.dev.bsb_bangking_jp.shared.rekening_lainnya.data.RekeningItem
import bsb.dev.bsb_bangking_jp.shared.rekening_lainnya.data.cashBalanceValue
import bsb.dev.bsb_bangking_jp.feature.beranda.section.SaldoCardBase

/**
 * Padanan SaldoCardSelector.dart -- BEDA dengan SaldoCardDashboard (di Beranda):
 * memilih rekening di sini HANYA mengganti rekening yang sedang "dilihat" histori
 * transaksinya (accountNo lokal di ActivityPage), TIDAK memanggil API setprimaryaccount.
 */
@Composable
fun SaldoCardSelector(
    rekeningList: List<RekeningItem>,
    activeAccountNumber: String?,
    onRekeningSelected: (RekeningItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (rekeningList.isEmpty()) return

    var showSheet by remember { mutableStateOf(false) }

    val rekeningAktif = rekeningList.firstOrNull { it.number == activeAccountNumber }
        ?: rekeningList.firstOrNull { it.isPrimary }
        ?: rekeningList.first()

    SaldoCardBase(
        nama = rekeningAktif.name,
        rekening = rekeningAktif.number,
        saldo = RupiahFormat(rekeningAktif.cashBalanceValue().toInt()),
        modifier = modifier,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Rekening Aktif",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Row(
                modifier = Modifier.clickable(enabled = rekeningList.size > 1) { showSheet = true },
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Ganti Rekening",
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
            mode = RekeningSheetMode.LIHAT_REKENING_LAIN,
            rekeningAktif = rekeningAktif.number,
            title = "Pilih Rekening",
            onDismiss = { showSheet = false },
            onSelected = { selected ->
                showSheet = false
                onRekeningSelected(selected)
            },
        )
    }
}