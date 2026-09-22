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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.PlayArrow
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

private val bulanIndonesiaRange = listOf(
    "Januari", "Februari", "Maret", "April", "Mei", "Juni",
    "Juli", "Agustus", "September", "Oktober", "November", "Desember"
)
private val hariIndonesiaRange = listOf("Min", "Sen", "Sel", "Rab", "Kam", "Jum", "Sab")

private data class RangeCalendarDay(
    val date: LocalDate,
    val inCurrentMonth: Boolean,
)

private fun buildRangeCalendarGrid(yearMonth: YearMonth): List<RangeCalendarDay> {
    val firstOfMonth = yearMonth.atDay(1)
    val leadingOffset = firstOfMonth.dayOfWeek.value % 7
    val daysInMonth = yearMonth.lengthOfMonth()

    val leadingDays = (1..leadingOffset).map { i ->
        val date = firstOfMonth.minusDays((leadingOffset - i + 1).toLong())
        RangeCalendarDay(date, inCurrentMonth = false)
    }
    val currentDays = (1..daysInMonth).map { d ->
        RangeCalendarDay(yearMonth.atDay(d), inCurrentMonth = true)
    }
    val totalSoFar = leadingDays.size + currentDays.size
    val trailingCount = (7 - totalSoFar % 7) % 7
    val trailingDays = (1..trailingCount).map { i ->
        RangeCalendarDay(yearMonth.atEndOfMonth().plusDays(i.toLong()), inCurrentMonth = false)
    }

    return leadingDays + currentDays + trailingDays
}

/**
 * Padanan DateRangePickerBottomSheet.dart -- satu kalender untuk memilih RENTANG tanggal.
 * Batas rentang yang bisa dipilih: firstDate = hari ini - 1 tahun, lastDate = hari ini
 * (padanan `firstDate: DateTime(now.year - 1)` & `lastDate: now` di ).
 * Default saat sheet dibuka pertama kali (belum ada filter aktif): start = end = hari ini.
 */
@Composable
fun PilihRentangTanggalSheet(
    initialStart: LocalDate?,
    initialEnd: LocalDate?,
    onConfirm: (start: LocalDate, end: LocalDate) -> Unit,
) {
    val today = remember { LocalDate.now() }

    // Padanan firstDate: DateTime(now.year - 1) & lastDate: now
    val firstAllowedDate = remember { today.minusYears(1) }
    val lastAllowedDate = today
    val firstAllowedMonth = remember { YearMonth.from(firstAllowedDate) }
    val lastAllowedMonth = remember { YearMonth.from(lastAllowedDate) }

    var currentMonth by remember {
        mutableStateOf(YearMonth.from(initialStart ?: initialEnd ?: today))
    }

    // Default: hari ini untuk start & end kalau belum ada filter sebelumnya.
    var startDate by remember { mutableStateOf(initialStart ?: today) }
    var endDate by remember { mutableStateOf(initialEnd ?: today) }
    var isRangeComplete by remember { mutableStateOf(true) }

    fun onDayTap(day: RangeCalendarDay) {
        val isOutOfRange = day.date.isBefore(firstAllowedDate) || day.date.isAfter(lastAllowedDate)
        if (isOutOfRange) return

        if (isRangeComplete) {
            startDate = day.date
            endDate = day.date
            isRangeComplete = false
        } else {
            if (day.date.isBefore(startDate)) {
                startDate = day.date
            } else {
                endDate = day.date
            }
            isRangeComplete = true
        }

        if (!day.inCurrentMonth) {
            currentMonth = YearMonth.from(day.date)
        }
    }

    Column {
        Text(
            text = "Pilih Rentang Tanggal",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(20.dp))

        // ===== Header "Awal" -> "Akhir" =====
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Awal",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.extendedColors.textSecondary,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${startDate.dayOfMonth} ${bulanIndonesiaRange[startDate.monthValue - 1]}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )
            }
            Icon(Icons.Default.PlayArrow, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Icon(Icons.Default.PlayArrow, contentDescription = null, tint = MaterialTheme.colorScheme.inversePrimary)
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.End,
            ) {
                Text(
                    text = "Akhir",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.extendedColors.textSecondary,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${endDate.dayOfMonth} ${bulanIndonesiaRange[endDate.monthValue - 1]}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ===== Navigasi bulan/tahun, dibatasi [firstAllowedMonth, lastAllowedMonth] =====
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "${bulanIndonesiaRange[currentMonth.monthValue - 1]} ${currentMonth.year}",
                style = MaterialTheme.typography.titleMedium,
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                val isPrevDisabled = !currentMonth.isAfter(firstAllowedMonth)
                val isNextDisabled = !currentMonth.isBefore(lastAllowedMonth)

                IconButton(
                    onClick = { currentMonth = currentMonth.minusMonths(1) },
                    enabled = !isPrevDisabled,
                ) {
                    Icon(
                        Icons.Default.ChevronLeft,
                        contentDescription = "Bulan sebelumnya",
                        tint = if (isPrevDisabled) Gray300 else MaterialTheme.extendedColors.textPrimary,
                    )
                }
                IconButton(
                    onClick = { currentMonth = currentMonth.plusMonths(1) },
                    enabled = !isNextDisabled,
                ) {
                    Icon(
                        Icons.Default.ChevronRight,
                        contentDescription = "Bulan berikutnya",
                        tint = if (isNextDisabled) Gray300 else MaterialTheme.extendedColors.textPrimary,
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ===== Header nama hari =====
        Row(modifier = Modifier.fillMaxWidth()) {
            hariIndonesiaRange.forEach { hari ->
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    Text(
                        text = hari,
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (hari == "Min") Red500 else MaterialTheme.extendedColors.textSecondary,
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // ===== Grid tanggal dengan highlight rentang =====
        val days = remember(currentMonth) { buildRangeCalendarGrid(currentMonth) }
        days.chunked(7).forEach { week ->
            Row(modifier = Modifier.fillMaxWidth()) {
                week.forEach { day ->
                    val isStart = day.date == startDate
                    val isEnd = day.date == endDate
                    val isInRange = !day.date.isBefore(startDate) && !day.date.isAfter(endDate) && startDate != endDate
                    val isSunday = day.date.dayOfWeek == DayOfWeek.SUNDAY
                    val isPastDate = day.date.isBefore(firstAllowedDate) || day.date.isAfter(lastAllowedDate)

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(vertical = 4.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        if (isInRange && !isStart && !isEnd) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(40.dp)
                                    .background(MaterialTheme.colorScheme.primaryContainer),
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isStart || isEnd) MaterialTheme.colorScheme.primary
                                    else Color.Transparent
                                )
                                .clickable(enabled = !isPastDate) { onDayTap(day) },
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = day.date.dayOfMonth.toString(),
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = if (isStart || isEnd) FontWeight.Bold else FontWeight.Normal,
                                color = when {
                                    isPastDate -> Gray300
                                    isStart || isEnd -> MaterialTheme.extendedColors.onSuccess
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
            onClick = { onConfirm(startDate, endDate) },
        )
    }
}