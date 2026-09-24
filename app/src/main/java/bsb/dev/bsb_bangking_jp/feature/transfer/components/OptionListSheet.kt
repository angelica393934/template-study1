package bsb.dev.bsb_bangking_jp.feature.transfer.components

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import bsb.dev.bsb_bangking_jp.core.theme.extendedColors
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class OptionItem(
    val label: String,
    val subLabel: String? = null,
    val rightText: String? = null,

)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OptionListSheet(
    options: List<OptionItem>,
    onDismiss: () -> Unit,
    onSelected: (OptionItem) -> Unit,
    modifier: Modifier = Modifier,
    title: String? = null,
    selectedLabel: String? = null,
) {
    val scope = rememberCoroutineScope()
    _root_ide_package_.bsb.dev.bsb_bangking_jp.core.components.AppModalBottomSheet(
        onDismissRequest = onDismiss,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
        ) {

            if (!title.isNullOrEmpty()) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(modifier = Modifier.height(20.dp))
            }

            options.forEach { item ->
                OptionRow(
                    item = item,
                    isActive = item.label == selectedLabel,
                    onTap = {
                        scope.launch {
                            onSelected(item)      // ubah warna menjadi aktif
                            delay(300)            // tunggu sebentar
                            onDismiss()           // baru tutup sheet
                        }
                    },
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun OptionRow(
    item: OptionItem,
    isActive: Boolean,
    onTap: () -> Unit,
) {
    val backgroundColor = if (isActive) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.background
    val borderColor = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.extendedColors.divider
    val textColor = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.extendedColors.textPrimary
    val subtextColor = if (isActive) MaterialTheme.colorScheme.scrim else MaterialTheme.extendedColors.textSecondary
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(backgroundColor)
            .border(1.4.dp, borderColor, RoundedCornerShape(20.dp))
            .clickable { onTap() }
            .padding(horizontal = 20.dp, vertical = 15.dp),
    ) {
        if (item.subLabel != null || item.rightText != null) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.label,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = textColor,
                    )
                    item.subLabel?.let {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodySmall,
                            color = subtextColor,
                        )
                    }
                }
                item.rightText?.let {
                    Spacer(modifier = Modifier.height(0.dp))
                    Text(
                        text = it,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = textColor,
                    )
                }
            }
        } else {
            Text(
                text = item.label,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = textColor,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}