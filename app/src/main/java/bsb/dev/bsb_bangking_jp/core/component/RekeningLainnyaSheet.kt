// core/component/RekeningLainnyaSheet.kt
package bsb.dev.bsb_bangking_jp.core.component

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import bsb.dev.bsb_bangking_jp.R
import bsb.dev.bsb_bangking_jp.core.theme.extendedColors
import bsb.dev.bsb_bangking_jp.core.util.RupiahFormat
import bsb.dev.bsb_bangking_jp.shared.rekening_lainnya.data.RekeningItem
import bsb.dev.bsb_bangking_jp.shared.rekening_lainnya.data.cashBalanceValue

enum class RekeningSheetMode {
    PILIH_REKENING_UTAMA,
    LIHAT_REKENING_LAIN,
    REKENING_SUMBER,
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RekeningLainnyaSheet(
    daftarRekening: List<RekeningItem>,
    mode: RekeningSheetMode,
    onDismiss: () -> Unit,
    onSelected: (RekeningItem) -> Unit,
    rekeningAktif: String? = null,
    title: String? = null,
    buttonText: String? = null,
    showCopy: Boolean = true,
) {
    var selectedRekening by remember { mutableStateOf<RekeningItem?>(null) }
    val useButton = mode == RekeningSheetMode.PILIH_REKENING_UTAMA
    val isButtonEnabled = selectedRekening != null

    fun isActive(item: RekeningItem): Boolean {
        return if (useButton) {
            val current = selectedRekening
            if (current == null) item.number == rekeningAktif else current.number == item.number
        } else {
            item.number == rekeningAktif
        }
    }

    fun onItemTap(item: RekeningItem) {
        if (useButton) {
            selectedRekening = item
        } else {
            onSelected(item)
            onDismiss()
        }
    }

    val resolvedTitle = title ?: when (mode) {
        RekeningSheetMode.PILIH_REKENING_UTAMA -> "Pilih Rekening Utama"
        RekeningSheetMode.LIHAT_REKENING_LAIN -> "Rekening Lainnya"
        RekeningSheetMode.REKENING_SUMBER -> "Pilih Rekening Sumber"
    }

    AppModalBottomSheet(
        onDismissRequest = onDismiss,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {

            if (resolvedTitle.isNotEmpty()) {
                Text(
                    text = resolvedTitle,
                    style = MaterialTheme.typography.titleLarge,
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            daftarRekening.forEach { item ->
                RekeningItemCard(
                    rekening = item,
                    isActive = isActive(item),
                    showCopy = showCopy,
                    onTap = { onItemTap(item) },
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            if (useButton) {
                Spacer(modifier = Modifier.height(6.dp))
                AppButton(
                    text = buttonText ?: "Pilih Rekening Utama",
                    enabled = isButtonEnabled,
                    onClick = {
                        selectedRekening?.let {
                            onSelected(it)
                            onDismiss()
                        }
                    },
                )
            }
        }
    }
}

@Composable
private fun RekeningItemCard(
    rekening: RekeningItem,
    isActive: Boolean,
    showCopy: Boolean,
    onTap: () -> Unit,
    modifier: Modifier = Modifier,

) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    val borderColor = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.extendedColors.textDisabled
    val backgroundColor = if (isActive) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.background
    val toastState = LocalToastState.current

    val saldoFormatted = RupiahFormat(rekening.cashBalanceValue().toInt())

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .border(
                width = 1.5.dp,
                color = borderColor,
                shape = RoundedCornerShape(16.dp),
            )
            .clickable { onTap() }
    ) {
        // Watermark logo transparan, padanan Positioned.fill + Transform.scale(4) di .
        Image(
            painter = painterResource(id = R.drawable.bg_card),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .matchParentSize()
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = "Tabungan Sekarang",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.extendedColors.textSecondary,
                )
                Text(
                    text = rekening.accountTypeName.ifEmpty { "Tabungan" },
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.extendedColors.textPrimary,
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = saldoFormatted,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )

                if (showCopy) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable {
                            clipboardManager.setText(
                                AnnotatedString(
                                    "Bank Sumsel Babel\n${rekening.number}\n${rekening.name}"
                                )
                            )
                            toastState.showSuccess( "Rekening berhasil disalin")
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Salin",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Salin",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = rekening.number,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.extendedColors.textSecondary,
            )
        }
    }
}