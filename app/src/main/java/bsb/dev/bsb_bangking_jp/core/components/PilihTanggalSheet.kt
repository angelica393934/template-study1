package bsb.dev.bsb_bangking_jp.core.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import bsb.dev.bsb_bangking_jp.core.theme.Gray300
import bsb.dev.bsb_bangking_jp.core.theme.Red500
import bsb.dev.bsb_bangking_jp.core.theme.extendedColors
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth

private val bulanIndonesia = listOf(
    "Januari", "Februari", "Maret", "April", "Mei", "Juni",
    "Juli", "Agustus", "September", "Oktober", "November", "Desember"
)
private val hariIndonesia = listOf("Min", "Sen", "Sel", "Rab", "Kam", "Jum", "Sab")

private data class CalendarDay(
    val date: LocalDate,
    val inCurrentMonth: Boolean,
)

private fun buildCalendarGrid(yearMonth: YearMonth): List<CalendarDay> {
    val firstOfMonth = yearMonth.atDay(1)
    val leadingOffset = firstOfMonth.dayOfWeek.value % 7
    val daysInMonth = yearMonth.lengthOfMonth()

    val leadingDays = (1..leadingOffset).map { i ->
        val date = firstOfMonth.minusDays((leadingOffset - i + 1).toLong())
        CalendarDay(date, inCurrentMonth = false)
    }

    val currentDays = (1..daysInMonth).map { d ->
        CalendarDay(yearMonth.atDay(d), inCurrentMonth = true)
    }

    val totalSoFar = leadingDays.size + currentDays.size
    val trailingCount = (7 - totalSoFar % 7) % 7
    val trailingDays = (1..trailingCount).map { i ->
        CalendarDay(yearMonth.atEndOfMonth().plusDays(i.toLong()), inCurrentMonth = false)
    }

    return leadingDays + currentDays + trailingDays
}

@Composable
fun PilihTanggalSheet(
    selectedDate: LocalDate,
    onDatePicked: (LocalDate) -> Unit,
    onConfirm: (LocalDate) -> Unit,
    onMonthYearClick: (() -> Unit)? = null,
) {
    var currentMonth by remember { mutableStateOf(YearMonth.from(selectedDate)) }
    var tanggalDipilih by remember { mutableStateOf(selectedDate) }

    val today = remember { LocalDate.now() }
    val bulanIni = remember { YearMonth.from(today) }

    // State untuk picker bulan
    var showMonthPicker by remember { mutableStateOf(false) }
    var pickerYear by remember { mutableStateOf(currentMonth.year) }

    Column {
        // Judul: tanggal yang sedang dipilih
        Text(
            text = "${tanggalDipilih.dayOfMonth} ${bulanIndonesia[tanggalDipilih.monthValue - 1]} ${tanggalDipilih.year}",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Baris navigasi bulan/tahun
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable {
                    pickerYear = currentMonth.year
                    showMonthPicker = !showMonthPicker
                    onMonthYearClick?.invoke()
                },
            ) {
                Text(
                    text = "${bulanIndonesia[currentMonth.monthValue - 1]} ${currentMonth.year}",
                    style = MaterialTheme.typography.titleMedium,
                )
                Icon(
                    imageVector = if (showMonthPicker) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                )
            }

            if (!showMonthPicker) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val isPrevMonthDisabled = !currentMonth.isAfter(bulanIni)

                    IconButton(
                        onClick = { currentMonth = currentMonth.minusMonths(1) },
                        enabled = !isPrevMonthDisabled,
                    ) {
                        Icon(
                            Icons.Default.ChevronLeft,
                            contentDescription = "Bulan sebelumnya",
                            tint = if (isPrevMonthDisabled) Gray300 else MaterialTheme.extendedColors.textPrimary,
                        )
                    }
                    IconButton(onClick = { currentMonth = currentMonth.plusMonths(1) }) {
                        Icon(Icons.Default.ChevronRight, contentDescription = "Bulan berikutnya")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (showMonthPicker) {
            // ===== Picker bulan =====
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                val isPrevYearDisabled = pickerYear <= bulanIni.year

                IconButton(
                    onClick = { pickerYear-- },
                    enabled = !isPrevYearDisabled,
                ) {
                    Icon(
                        Icons.Default.ChevronLeft,
                        contentDescription = "Tahun sebelumnya",
                        tint = if (isPrevYearDisabled) Gray300 else MaterialTheme.extendedColors.textPrimary,
                    )
                }
                Text(
                    text = pickerYear.toString(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                IconButton(onClick = { pickerYear++ }) {
                    Icon(Icons.Default.ChevronRight, contentDescription = "Tahun berikutnya")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            bulanIndonesia.chunked(3).forEachIndexed { rowIndex, rowMonths ->
                Row(modifier = Modifier.fillMaxWidth()) {
                    rowMonths.forEachIndexed { colIndex, namaBulan ->
                        val monthIndex = rowIndex * 3 + colIndex + 1
                        val monthYearMonth = YearMonth.of(pickerYear, monthIndex)
                        val isDisabled = monthYearMonth.isBefore(bulanIni)
                        val isSelectedMonth = monthYearMonth == currentMonth

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .padding(6.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (isSelectedMonth) MaterialTheme.colorScheme.primary
                                    else Color.Transparent
                                )
                                .clickable(enabled = !isDisabled) {
                                    currentMonth = monthYearMonth
                                    showMonthPicker = false
                                }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = namaBulan,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = if (isSelectedMonth) FontWeight.Bold else FontWeight.Normal,
                                color = when {
                                    isDisabled -> Gray300
                                    isSelectedMonth -> MaterialTheme.extendedColors.onSuccess
                                    else -> MaterialTheme.extendedColors.textPrimary
                                },
                            )
                        }
                    }
                }
            }
        } else {
            // ===== Header nama hari =====
            Row(modifier = Modifier.fillMaxWidth()) {
                hariIndonesia.forEach { hari ->
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = hari,
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (hari == "Min") Red500 else MaterialTheme.extendedColors.textSecondary,
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // ===== Grid tanggal =====
            val days = remember(currentMonth) { buildCalendarGrid(currentMonth) }
            days.chunked(7).forEach { week ->
                Row(modifier = Modifier.fillMaxWidth()) {
                    week.forEach { day ->
                        val isSelected = day.date == tanggalDipilih
                        val isSunday = day.date.dayOfWeek == DayOfWeek.SUNDAY
                        val isPastDate = day.date.isBefore(today)

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .padding(vertical = 4.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isSelected) MaterialTheme.colorScheme.primary
                                        else Color.Transparent
                                    )
                                    .clickable(enabled = !isPastDate) {
                                        tanggalDipilih = day.date

                                        if (!day.inCurrentMonth) {
                                            currentMonth = YearMonth.from(day.date)
                                        }

                                        onDatePicked(day.date)
                                    },
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = day.date.dayOfMonth.toString(),
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = when {
                                        isPastDate -> Gray300
                                        isSelected -> MaterialTheme.extendedColors.onSuccess
                                        !day.inCurrentMonth -> Gray300
                                        isSunday -> Red500
                                        else -> MaterialTheme.extendedColors.textPrimary
                                    },
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            AppButton(
                text = "Pilih",
                onClick = { onConfirm(tanggalDipilih) },
            )
        }
    }
}