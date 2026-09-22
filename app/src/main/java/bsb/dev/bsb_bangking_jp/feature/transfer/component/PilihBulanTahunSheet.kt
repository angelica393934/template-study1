package bsb.dev.bsb_bangking_jp.feature.transfer.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import bsb.dev.bsb_bangking_jp.core.components.AppButton
import bsb.dev.bsb_bangking_jp.core.components.AppModalBottomSheet
import bsb.dev.bsb_bangking_jp.core.theme.extendedColors
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PilihBulanTahunSheet(
    onDismiss: () -> Unit,
    onSelected: (bulan: String, tahun: Int) -> Unit,
    modifier: Modifier = Modifier,
    initialYear: Int = Calendar.getInstance().get(Calendar.YEAR),
    selectedMonth: String? = null,
) {
    val months = remember {
        listOf(
            "Januari", "Februari", "Maret", "April", "Mei", "Juni",
            "Juli", "Agustus", "September", "Oktober", "November", "Desember",
        )
    }
    val now = remember { Calendar.getInstance() }
    val minYear = now.get(Calendar.YEAR)
    val maxYear = minYear + 5
    val currentMonth = now.get(Calendar.MONTH) + 1

    var currentYear by remember { mutableStateOf(initialYear) }
    var selected by remember { mutableStateOf(selectedMonth) }

    AppModalBottomSheet(
        onDismissRequest = onDismiss,
    ) {
        Column(modifier = Modifier.fillMaxWidth().heightIn(max = 560.dp)) {
            Column {
                Text(
                    text = "Pilih Bulan dan Tahun",
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(
                        onClick = { if (currentYear > minYear) currentYear-- },
                        enabled = currentYear > minYear,
                    ) {
                        Icon(imageVector = Icons.Default.ChevronLeft, contentDescription = "Tahun sebelumnya")
                    }
                    Text(text = "$currentYear", style = MaterialTheme.typography.titleLarge)
                    IconButton(
                        onClick = { if (currentYear < maxYear) currentYear++ },
                        enabled = currentYear < maxYear,
                    ) {
                        Icon(imageVector = Icons.Default.ChevronRight, contentDescription = "Tahun berikutnya")
                    }
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f, fill = false)
                    .verticalScroll(rememberScrollState())
            ) {
                months.forEachIndexed { index, monthName ->
                    val monthNumber = index + 1
                    val isSelected = monthName == selected
                    val isDisabled = currentYear == minYear && monthNumber < currentMonth

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                when {
                                    isSelected -> MaterialTheme.colorScheme.primaryContainer
                                    isDisabled -> MaterialTheme.extendedColors.inputBackground
                                    else -> MaterialTheme.colorScheme.background
                                },
                            )
                            .border(
                                1.dp,
                                when {
                                    isSelected -> MaterialTheme.colorScheme.primary
                                    isDisabled -> MaterialTheme.extendedColors.divider
                                    else -> MaterialTheme.extendedColors.textDisabled
                                },
                                RoundedCornerShape(25.dp),
                            )
                            .clickable(enabled = !isDisabled) { selected = monthName }
                            .padding(vertical = 14.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = monthName,
                            style = MaterialTheme.typography.titleMedium,
                            color = when {
                                isDisabled -> MaterialTheme.extendedColors.textDisabled
                                isSelected -> MaterialTheme.colorScheme.primary
                                else -> MaterialTheme.extendedColors.textPrimary
                            },
                        )
                    }
                }
            }

            Column {
                AppButton(
                    text = "Pilih",
                    enabled = selected != null,
                    onClick = {
                        selected?.let { onSelected(it, currentYear) }
                        onDismiss()
                    },
                )
            }
        }
    }
}