package bsb.dev.bsb_bangking_jp.shared.transaction_result

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.provider.MediaStore
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import bsb.dev.bsb_bangking_jp.R
import bsb.dev.bsb_bangking_jp.core.component.AppButton
import bsb.dev.bsb_bangking_jp.core.component.LocalToastState
import bsb.dev.bsb_bangking_jp.core.component.TransactionDetailRow
import bsb.dev.bsb_bangking_jp.core.theme.extendedColors
import bsb.dev.bsb_bangking_jp.core.util.RupiahFormat
import bsb.dev.bsb_bangking_jp.core.util.maskAccountNumber
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * supaya reusable di fitur lain (mis. detail message), karena hasilnya BELUM TENTU "berhasil".
 * Judul + ikon header di bawah MENGIKUTI [TransactionResultInfo.status].
 */
@Composable
fun TransactionResultPage(
    data: TransactionResultInfo,
    modifier: Modifier = Modifier,
    onClose: () -> Unit = {},
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val graphicsLayer = rememberGraphicsLayer()
    val toastState = LocalToastState.current


    var isSaving by remember { mutableStateOf(false) }

    val tanggalFormatted = remember(data.transactionDate) {
        SimpleDateFormat("dd MMMM yyyy - HH:mm 'WIB'", Locale("id", "ID")).format(data.transactionDate)
    }

    fun saveScreenshot() {
        coroutineScope.launch {
            isSaving = true
            try {
                val bitmap = graphicsLayer.toImageBitmap().asAndroidBitmap()
                val saved = saveBitmapToGallery(context, bitmap)
                if (saved) toastState.showSuccess("Bukti transaksi disimpan ke galeri")
                else toastState.showError("Gagal menyimpan bukti transaksi")
            } catch (e: Exception) {
                toastState.showError("Gagal menyimpan: ${e.message}")
            } finally {
                isSaving = false
            }
        }
    }

    fun shareScreenshot() {
        coroutineScope.launch {
            isSaving = true
            try {
                val bitmap = graphicsLayer.toImageBitmap().asAndroidBitmap()
                shareBitmap(context, bitmap)
            } catch (e: Exception) {
                toastState.showError("Gagal membagikan: ${e.message}")
            } finally {
                isSaving = false
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .drawWithContent {
                    graphicsLayer.record { this@drawWithContent.drawContent() }
                    drawLayer(graphicsLayer)
                },
        ) {
            if (isSaving) {
                Image(
                    painter = painterResource(id = R.drawable.watermark),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.bg),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.FillWidth,
                    alignment = Alignment.TopCenter,
                )
            }

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

                    // 🔹 Titik utama perubahan -- gambar/ikon header sekarang MENGIKUTI status,
                    // bukan selalu R.drawable.cheklist seperti versi lama.
                    StatusVisual(status = data.status)

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = statusTitle(data.status),
                        style = MaterialTheme.typography.displaySmall
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "$tanggalFormatted\nRef:${data.referenceNumber}",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                        color = MaterialTheme.extendedColors.textSecondary,
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 18.dp),
                        color = MaterialTheme.extendedColors.divider)
                }

                Text(
                    text = "Penerima",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.extendedColors.textSecondary,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = data.beneficiaryName.uppercase(), style = MaterialTheme.typography.titleMedium)
                Text(
                    text = "${data.beneficiaryBankName} - ${data.beneficiaryAccountNo}",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                    color = MaterialTheme.extendedColors.textSecondary,
                )
                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 18.dp),
                    color = MaterialTheme.extendedColors.divider)
                Text(
                    text = "Detail Transaksi",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.extendedColors.textSecondary,
                )
                Spacer(modifier = Modifier.height(3.dp))

                TransactionDetailRow("Layanan Transfer", data.serviceLabel ?: "-")
                TransactionDetailRow("Nominal Transfer", RupiahFormat(data.amount.toInt()))
                TransactionDetailRow("Biaya Layanan", RupiahFormat(data.adminFee.toInt()))
                Spacer(modifier = Modifier.height(8.dp))
                TransactionDetailRow(
                    title = "Total Transfer",
                    value = RupiahFormat(data.totalDebit.toInt()),
                    titleStyle = MaterialTheme.typography.titleMedium,
                    valueStyle = MaterialTheme.typography.titleLarge,
                )
                Spacer(modifier = Modifier.height(15.dp))
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 18.dp),
                    color = MaterialTheme.extendedColors.divider)
                Spacer(modifier = Modifier.height(8.dp))

                // 🔹 Bagian "Pengirim" opsional -- auto-hide kalau senderName tidak ada
                // (mis. dari detail message yang tidak mengirim rekening pengirim).
                if (!data.senderName.isNullOrBlank()) {
                    Text(
                        text = "Pengirim",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.extendedColors.textSecondary,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = data.senderName.uppercase(), style = MaterialTheme.typography.titleMedium)
                    if (!data.senderAccountNo.isNullOrBlank()) {
                        Text(text = "${data.beneficiaryBankName} - ${maskAccountNumber(data.senderAccountNo)}")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 18.dp),
                        color = MaterialTheme.extendedColors.divider)
                }

                TransactionDetailRow(
                    title = "Keterangan",
                    value = data.remark?.takeIf { it.isNotBlank() } ?: "-",
                    titleStyle = MaterialTheme.typography.bodySmall,
                    valueStyle = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.extendedColors.textSecondary),
                )
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 18.dp),
                    color = MaterialTheme.extendedColors.divider)
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Resi ini merupakan bukti transaksi yang sah.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.extendedColors.textSecondary,
                    )
                    Spacer(modifier = Modifier.height(15.dp))
                }

                if (!isSaving) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        AppButton(
                            text = "Simpan",
                            icon = Icons.Default.Download,
                            iconBeforeText = true,
                            backgroundColor = MaterialTheme.colorScheme.inverseSurface,
                            textColor = MaterialTheme.colorScheme.primary,
                            onClick = { saveScreenshot() },
                            modifier = Modifier.weight(1f),
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        AppButton(
                            text = "Bagikan",
                            icon = Icons.Default.Share,
                            iconBeforeText = true,
                            onClick = { shareScreenshot() },
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        if (!isSaving) {
            IconButton(
                onClick = onClose,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 25.dp, end = 10.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Tutup",
                    tint = MaterialTheme.extendedColors.textPrimary,
                )
            }
        }

        if (isSaving) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.extendedColors.inputBackground.copy(alpha = 0.9f)),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }
        }
    }
}

/** Ikon/gambar header -- mengikuti [TransactionResultInfo.status]. */
@Composable
private fun StatusVisual(status: TransactionResultStatus) {
    when (status) {
        TransactionResultStatus.SUCCESS, TransactionResultStatus.SCHEDULED -> {
            Image(
                painter = painterResource(id = R.drawable.cheklist),
                contentDescription = null,
                modifier = Modifier.size(120.dp),
            )
        }
        TransactionResultStatus.FAILED -> {
            // TODO: ganti ke ilustrasi resmi kalau sudah ada asetnya dari desain.
            Icon(
                imageVector = Icons.Filled.Cancel,
                contentDescription = null,
                tint = MaterialTheme.extendedColors.danger,
                modifier = Modifier.size(120.dp),
            )
        }
        TransactionResultStatus.PENDING -> {
            Icon(
                imageVector = Icons.Filled.HourglassEmpty,
                contentDescription = null,
                tint = MaterialTheme.extendedColors.warning,
                modifier = Modifier.size(120.dp),
            )
        }
    }
}

private fun statusTitle(status: TransactionResultStatus): String = when (status) {
    TransactionResultStatus.SUCCESS -> "Transaksi Berhasil"
    TransactionResultStatus.SCHEDULED -> "Transaksi Telah Dijadwalkan"
    TransactionResultStatus.FAILED -> "Transaksi Gagal"
    TransactionResultStatus.PENDING -> "Transaksi Sedang Diproses"
}

private fun saveBitmapToGallery(context: Context, bitmap: Bitmap): Boolean {
    return try {
        val filename = "bukti_transaksi_${System.currentTimeMillis()}.png"
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, filename)
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/Transaksi New BSB")
        }
        val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        uri?.let { target ->
            context.contentResolver.openOutputStream(target)?.use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }
        }
        uri != null
    } catch (_: Exception) {
        false
    }
}

private fun shareBitmap(context: Context, bitmap: Bitmap) {
    val cacheDir = File(context.cacheDir, "images").apply { mkdirs() }
    val file = File(cacheDir, "bukti_transaksi.png")
    FileOutputStream(file).use { out -> bitmap.compress(Bitmap.CompressFormat.PNG, 100, out) }

    val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "image/png"
        putExtra(Intent.EXTRA_STREAM, uri)
        putExtra(Intent.EXTRA_TEXT, "Bukti transfer New BSB")
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(Intent.createChooser(intent, "Bagikan bukti transaksi"))
}