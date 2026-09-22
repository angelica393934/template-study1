package bsb.dev.bsb_bangking_jp.core.filter

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import bsb.dev.bsb_bangking_jp.core.components.AppButton
import bsb.dev.bsb_bangking_jp.core.components.AppModalBottomSheet
import bsb.dev.bsb_bangking_jp.core.components.AppTextField
import bsb.dev.bsb_bangking_jp.core.components.PilihRentangTanggalSheet
import bsb.dev.bsb_bangking_jp.core.theme.extendedColors
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val jenisOptions = listOf("Semua", "Transfer Tunai", "Top Up", "Tagihan", "Qr Bayar", "Tarik Tunai")
private val kategoriOptions = listOf("Semua", "Berhasil", "Gagal", "Masuk", "Keluar")
private val quickRangeOptions = listOf(7, 15, 30)

private val backendFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
private val displayFormatter = DateTimeFormatter.ofPattern("d MMM")

/**
 * Padanan FilterTransaksiModal.dart -- bottom sheet filter jenis/kategori + rentang tanggal.
 * Date-range disederhanakan jadi dua field "Dari"/"Sampai", masing-masing buka PilihTanggalSheet
 * terpisah (project belum punya padanan CalendarDatePicker2 / kalender dua-titik).
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun FilterTransaksiModal(
    currentFilter:TransactionFilterPayload,
    onDismiss: () -> Unit,
    onApply: (TransactionFilterPayload) -> Unit,
) {
    var fromDate by remember { mutableStateOf(currentFilter.fromDate.takeUnless { currentFilter.isDefaultDate }) }
    var toDate by remember { mutableStateOf(currentFilter.toDate.takeUnless { currentFilter.isDefaultDate }) }
    var selectedQuickRange by remember { mutableStateOf(currentFilter.quickRange) }

    var selectedJenis by remember {
        mutableStateOf(
            if (currentFilter.isAllJenis || currentFilter.jenis.isNullOrEmpty()) setOf("Semua")
            else currentFilter.jenis.toSet()
        )
    }
    var selectedKategori by remember {
        mutableStateOf(
            if (currentFilter.isAllCategory || currentFilter.category.isNullOrEmpty()) setOf("Semua")
            else currentFilter.category.toSet()
        )
    }

    var showRangePicker by remember { mutableStateOf(false) }

    fun toggle(current: Set<String>, item: String): Set<String> {
        if (item == "Semua") return setOf("Semua")
        val withoutSemua = current - "Semua"
        val updated = if (item in withoutSemua) withoutSemua - item else withoutSemua + item
        return updated.ifEmpty { setOf("Semua") }
    }

    AppModalBottomSheet(onDismissRequest = onDismiss) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(text = "Filter Transaksi", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "Tanggal", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            AppTextField(
                value = if (fromDate != null && toDate != null) {
                    val start = LocalDate.parse(fromDate, backendFormatter).format(displayFormatter)
                    val end = LocalDate.parse(toDate, backendFormatter).format(displayFormatter)
                    "$start - $end"
                } else "",
                onValueChange = {},
                hintText = "Pilih Rentang Tanggal",
                icon = Icons.Default.CalendarToday,
                readOnly = true,
                isDropdown = true,
                onClick = { showRangePicker = true },
            )

            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Pilihan Cepat", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                quickRangeOptions.forEach { range ->
                    val isSelected = selectedQuickRange == range
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 4.dp)
                            .clip(RoundedCornerShape(100.dp))
                            .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.extendedColors.inputBackground)
                            .clickable {
                                selectedQuickRange = if (isSelected) null else range
                                if (selectedQuickRange != null) {
                                    fromDate = null
                                    toDate = null
                                }
                            }
                            .padding(vertical = 9.dp),
                    ) {
                        Text(
                            text = "$range Hari Terakhir",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) MaterialTheme.extendedColors.onSuccess else MaterialTheme.extendedColors.textSecondary,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            HorizontalDivider(color = MaterialTheme.extendedColors.divider)
            Spacer(modifier = Modifier.height(10.dp))

            Text(text = "Jenis Transaksi", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                jenisOptions.forEach { item ->
                    FilterChoiceChip(
                        label = item,
                        isSelected = item in selectedJenis,
                        onTap = { selectedJenis = toggle(selectedJenis, item) },
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Kategori Transaksi", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                kategoriOptions.forEach { item ->
                    FilterChoiceChip(
                        label = item,
                        isSelected = item in selectedKategori,
                        onTap = { selectedKategori = toggle(selectedKategori, item) },
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                AppButton(
                    text = "Atur Ulang",
                    backgroundColor = MaterialTheme.colorScheme.inverseSurface,
                    textColor =  MaterialTheme.colorScheme.inverseOnSurface,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        fromDate = null
                        toDate = null
                        selectedQuickRange = null
                        selectedJenis = setOf("Semua")
                        selectedKategori = setOf("Semua")
                    },
                )
                Spacer(modifier = Modifier.width(12.dp))
                AppButton(
                    text = "Lihat Hasil",
                    modifier = Modifier.weight(1f),
                    onClick = {
                        val jenisResult = if ("Semua" in selectedJenis) null else selectedJenis.toList()
                        val kategoriResult = if ("Semua" in selectedKategori) null else selectedKategori.toList()

                        onApply(
                            currentFilter.with(
                                fromDate = fromDate,
                                resetFromDate = fromDate == null,
                                toDate = toDate,
                                resetToDate = toDate == null,
                                quickRange = selectedQuickRange,
                                resetQuickRange = selectedQuickRange == null,
                                jenis = jenisResult,
                                resetJenis = jenisResult == null,
                                category = kategoriResult,
                                resetCategory = kategoriResult == null,
                                isAllJenis = jenisResult == null,
                                isAllCategory = kategoriResult == null,
                                isDefaultDate = fromDate == null && selectedQuickRange == null,
                            )
                        )
                        onDismiss()
                    },
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
        }
    }

    if (showRangePicker) {
        AppModalBottomSheet(onDismissRequest = { showRangePicker = false }) {
            PilihRentangTanggalSheet(
                initialStart = fromDate?.let { LocalDate.parse(it, backendFormatter) },
                initialEnd = toDate?.let { LocalDate.parse(it, backendFormatter) },
                onConfirm = { start, end ->
                    fromDate = start.format(backendFormatter)
                    toDate = end.format(backendFormatter)
                    selectedQuickRange = null
                    showRangePicker = false
                },
            )
        }
    }
}

@Composable
private fun FilterChoiceChip(label: String, isSelected: Boolean, onTap: () -> Unit) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(100.dp))
            .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.extendedColors.inputBackground)
            .clickable { onTap() }
            .padding(horizontal = 16.dp, vertical = 10.dp),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleSmall,
            color = if (isSelected) MaterialTheme.extendedColors.onSuccess else MaterialTheme.extendedColors.textSecondary,
        )
    }
}