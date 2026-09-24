package bsb.dev.bsb_bangking_jp.feature.transfer.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import bsb.dev.bsb_bangking_jp.core.components.AppCheckBox
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import bsb.dev.bsb_bangking_jp.core.components.AppButton
import bsb.dev.bsb_bangking_jp.core.components.AppModalBottomSheet
import bsb.dev.bsb_bangking_jp.core.theme.extendedColors

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun PilihHariSheet(
    selectedDate: String?,
    onDismiss: () -> Unit,
    onSelected: (date: String, isEndOfMonth: Boolean) -> Unit,
) {
    var selected by remember { mutableStateOf(selectedDate) }
    var isEndOfMonth by remember { mutableStateOf(false) }

    AppModalBottomSheet(
        onDismissRequest = onDismiss,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Tanggal Transaksi",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
            }
            Spacer(modifier = Modifier.height(20.dp))

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                (1..28).forEach { day ->
                    val dayStr = day.toString()
                    val isSelected = selected == dayStr
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.background,
                            )
                            .border(
                                1.dp,
                                if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.extendedColors.divider,
                                CircleShape,
                            )
                            .clickable {
                                selected = dayStr
                                isEndOfMonth = false
                            },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = dayStr,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isSelected) MaterialTheme.extendedColors.onSuccess else MaterialTheme.extendedColors.textPrimary,
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            HorizontalDivider(color = MaterialTheme.extendedColors.divider)
            Spacer(modifier = Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                AppCheckBox(
                    value = isEndOfMonth,
                    onChanged ={
                        isEndOfMonth = it
                        if (it) selected = null
                    },
                )
                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    text = "Setiap Akhir Bulan",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.extendedColors.textSecondary,
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            AppButton(
                text = "Pilih",
                enabled = selected != null || isEndOfMonth,
                onClick = {
                    onSelected(selected ?: "Akhir Bulan", isEndOfMonth)
                    onDismiss()
                },
            )
        }
    }
}