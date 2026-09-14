package bsb.dev.bsb_bangking_jp.feature.transfer.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import bsb.dev.bsb_bangking_jp.core.component.AppButton
import bsb.dev.bsb_bangking_jp.core.theme.extendedColors
import bsb.dev.bsb_bangking_jp.core.util.RupiahFormat
import bsb.dev.bsb_bangking_jp.feature.transfer.TransferFormResult

private const val SUMBER_BANK_NAME = "Bank Sumsel Babel"

data class PeriksaKembaliData(
    val penerimaName: String,
    val penerimaBank: String,
    val penerimaAccountNumber: String,
    val result: TransferFormResult,
)

@Composable
fun PeriksaKembaliSheet(
    data: PeriksaKembaliData,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier,
    isSubmitting: Boolean = false,
) {
    val result = data.result

    val biayaLayananInt = result.biayaLayanan.filter { it.isDigit() }.toIntOrNull() ?: 0
    val total = result.jumlah + biayaLayananInt

    Column(
        modifier = modifier
            .fillMaxWidth(),
    ) {
        Text(
            text = "Periksa Kembali Transaksi Anda",
            style = MaterialTheme.typography.titleLarge,
        )
        Spacer(modifier = Modifier.height(17.dp))
        Text(
            text = "Pastikan Penerima Sudah Sesuai",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.extendedColors.textSecondary,
        )
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = data.penerimaName.uppercase(),
            style = MaterialTheme.typography.titleLarge,
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = "${data.penerimaBank} - ${data.penerimaAccountNumber}",
            style = MaterialTheme.typography.bodyMedium,
        )
        Spacer(modifier = Modifier.height(8.dp))

        HorizontalDivider(color = MaterialTheme.extendedColors.divider)

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Detail Transaksi",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.extendedColors.textSecondary,
        )
        Spacer(modifier = Modifier.height(4.dp))

        DetailRow("Pilihan Transaksi", if (result.isScheduled) "Terjadwal" else "Segera")
        DetailRow("Layanan Transfer", result.layananTransfer)
        if (result.tujuanTransfer.isNotEmpty()) {
            DetailRow("Tujuan Transfer", result.tujuanTransfer)
        }
        DetailRow("Biaya Layanan", result.biayaLayanan)
        DetailRow("Nominal Transaksi", RupiahFormat(result.jumlah))
        DetailRow(
            title = "Total Transaksi",
            value = RupiahFormat(total),
            titleStyle = MaterialTheme.typography.titleLarge,
            valueStyle = MaterialTheme.typography.titleLarge,
        )
        Spacer(modifier = Modifier.height(10.dp))

        HorizontalDivider(color = MaterialTheme.extendedColors.divider)

        DetailRow(
            title = "Keterangan",
            value = result.keterangan.ifEmpty { "-" },
            titleStyle = MaterialTheme.typography.titleSmall,
            valueStyle = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.extendedColors.textSecondary),
        )
        HorizontalDivider(color = MaterialTheme.extendedColors.divider)
        Spacer(modifier = Modifier.height(10.dp))

        if (result.isScheduled) {
            DetailRow(
                "Frekuensi",
                result.frekuensi,
                titleStyle = MaterialTheme.typography.bodyMedium,
                valueStyle = MaterialTheme.typography.titleMedium,
            )
            if (result.frekuensi == "Sekali") {
                DetailRow(
                    "Tanggal Transaksi",
                    result.tanggal.ifEmpty { "-" },
                    titleStyle = MaterialTheme.typography.bodyMedium,
                    valueStyle = MaterialTheme.typography.titleMedium,
                )
            } else {
                DetailRow(
                    "Setiap Tanggal",
                    result.tanggal.ifEmpty { "-" },
                    titleStyle = MaterialTheme.typography.bodyMedium,
                    valueStyle = MaterialTheme.typography.titleMedium,
                )
                DetailRow(
                    "Mulai",
                    result.mulai.ifEmpty { "-" },
                    titleStyle = MaterialTheme.typography.bodyMedium,
                    valueStyle = MaterialTheme.typography.titleMedium,
                )
                DetailRow(
                    "Sampai Dengan",
                    result.sampai.ifEmpty { "-" },
                    titleStyle = MaterialTheme.typography.bodyMedium,
                    valueStyle = MaterialTheme.typography.titleMedium,
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
        }

        Text(
            text = "Rekening Sumber",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.extendedColors.textSecondary,
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = result.sumber.name.uppercase(),
            style = MaterialTheme.typography.titleLarge,
        )
        Text(
            text = "$SUMBER_BANK_NAME - ${result.sumber.number}",
            style = MaterialTheme.typography.bodyMedium,
        )
        Spacer(modifier = Modifier.height(10.dp))
        HorizontalDivider(color = MaterialTheme.extendedColors.divider)
        Spacer(modifier = Modifier.height(10.dp))

        AppButton(
            text = if (isSubmitting) "Memproses..." else "Lanjutkan",
            icon =  Icons.AutoMirrored.Filled.ArrowForward,
            enabled = !isSubmitting,
            onClick = onConfirm,
        )
        Spacer(modifier = Modifier.height(10.dp))
    }
}

@Composable
private fun DetailRow(
    title: String,
    value: String,
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
        modifier = Modifier
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