package bsb.dev.bsb_bangking_jp.feature.transfer

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import bsb.dev.bsb_bangking_jp.R
import bsb.dev.bsb_bangking_jp.core.component.AppButton
import bsb.dev.bsb_bangking_jp.core.component.TransactionDetailRow
import bsb.dev.bsb_bangking_jp.feature.transfer.transfer_core.domain.ConfirmTransferResultItem
import bsb.dev.bsb_bangking_jp.core.theme.extendedColors
import bsb.dev.bsb_bangking_jp.core.component.InitialAvatar
import bsb.dev.bsb_bangking_jp.core.util.RupiahFormat
import bsb.dev.bsb_bangking_jp.core.util.maskAccountNumber
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun TransferBerhasilDijadwalkanPage(
    result: ConfirmTransferResultItem,
    sumberKlasifikasi: String,
    sumberSaldo: Int,
    modifier: Modifier = Modifier,
    onSelesai: () -> Unit = {},
) {
    val isOnce = result.scheduleType == "SCHEDULED" && result.frequency == "ONCE"
    val isMonthly = result.scheduleType == "SCHEDULED" && result.frequency == "MONTHLY"

    val tanggalFormatted = remember(result.transactionDate) {
        SimpleDateFormat("d MMMM yyyy - HH:mm 'WIB'", Locale("id", "ID")).format(result.transactionDate)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        Image(
            painter = painterResource(id = R.drawable.bg),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillWidth,
            alignment = Alignment.TopCenter,
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
        ) {
            Spacer(modifier = Modifier.height(90.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(R.drawable.logo_bsb),
                    contentDescription = "Logo BSB",
                    modifier = Modifier
                        .width(140.dp)
                        .aspectRatio(143f / 40f),
                    contentScale = ContentScale.Fit
                )
                Image(
                    painter = painterResource(id = R.drawable.cheklist),
                    contentDescription = null,
                    modifier = Modifier
                        .size(120.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "Transfer Telah Dijadwalkan",
                    style = MaterialTheme.typography.displaySmall
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = tanggalFormatted,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                    color = MaterialTheme.extendedColors.textSecondary,
                )
                Spacer(modifier = Modifier.height(2.dp))
                HorizontalDivider(color = MaterialTheme.extendedColors.divider)
                Spacer(modifier = Modifier.height(2.dp))
            }
            Text(
                text = "Penerima",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.extendedColors.textSecondary,
            )
            Spacer(modifier = Modifier.height(10.dp))

            Row(verticalAlignment = Alignment.Top) {
                InitialAvatar(initials = result.beneficiaryName)
                Spacer(modifier = Modifier.width(15.dp))
                Column {
                    Text(text = result.beneficiaryName.uppercase(), style = MaterialTheme.typography.titleSmall)
                    Text(
                        text = result.beneficiaryBankName,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.extendedColors.textSecondary,
                    )
                    Text(
                        text = result.beneficiaryAccountNo,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.extendedColors.textSecondary,
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 18.dp),
                color = MaterialTheme.extendedColors.divider)
            Text(
                text = "Detail Transaksi",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.extendedColors.textSecondary,
            )
            Spacer(modifier = Modifier.height(4.dp))
            TransactionDetailRow("Pilihan Transaksi", "Terjadwal")
            TransactionDetailRow("Nominal Transfer", RupiahFormat(result.amount))
            TransactionDetailRow("Biaya Layanan", RupiahFormat(result.adminFee))
            TransactionDetailRow(
                title = "Total Transfer",
                value = RupiahFormat(result.totalDebit),
                valueStyle = MaterialTheme.typography.titleMedium,
            )
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 18.dp),
                color = MaterialTheme.extendedColors.divider)
            TransactionDetailRow(
                title = "Keterangan",
                value = result.remark?.takeIf { it.isNotBlank() } ?: "-",
                titleStyle = MaterialTheme.typography.bodySmall,
                valueStyle = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.extendedColors.textSecondary),
            )
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 18.dp),
                color = MaterialTheme.extendedColors.divider)
            TransactionDetailRow(
                title = "Frekuensi",
                value = if (isOnce) "Sekali" else "Setiap Bulan",
                valueStyle = MaterialTheme.typography.titleMedium,
            )
            if (isOnce) {
                TransactionDetailRow(
                    title = "Tanggal Transaksi",
                    value = result.scheduleDate ?: "-",
                    valueStyle = MaterialTheme.typography.titleMedium,
                )
            }
            if (isMonthly) {
                TransactionDetailRow(
                    title = "Setiap Tanggal",
                    value = result.scheduleDate ?: "-",
                    valueStyle = MaterialTheme.typography.titleMedium,
                )
                TransactionDetailRow(
                    title = "Mulai",
                    value = result.startMonth ?: "-",
                    valueStyle = MaterialTheme.typography.titleMedium,
                )
                TransactionDetailRow(
                    title = "Sampai Dengan",
                    value = result.endMonth ?: "-",
                    valueStyle = MaterialTheme.typography.titleMedium,
                )
            }
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 18.dp),
                color = MaterialTheme.extendedColors.divider)
            Spacer(modifier = Modifier.height(3.dp))

            Text(
                text = "Rekening Sumber",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.background)
                    .border(1.dp, MaterialTheme.extendedColors.strip, RoundedCornerShape(16.dp))
                    .padding(15.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(text = "Saldo Sekarang", style = MaterialTheme.typography.bodySmall)
                    Text(
                        text = sumberKlasifikasi,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
                Text(text = RupiahFormat(sumberSaldo), style = MaterialTheme.typography.titleLarge)
                Text(
                    text = "${result.beneficiaryBankName} - ${maskAccountNumber(result.senderAccountNo)}",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Pastikan saldo Anda mencukupi sebelum jadwal transaksi.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.extendedColors.textSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(10.dp))
            AppButton(text = "Selesai", onClick = onSelesai)
            Spacer(modifier = Modifier.height(20.dp))
        }

        IconButton(
            onClick = onSelesai,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 8.dp, end = 8.dp),
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Tutup",
                tint = MaterialTheme.extendedColors.textPrimary,
            )
        }
    }
}