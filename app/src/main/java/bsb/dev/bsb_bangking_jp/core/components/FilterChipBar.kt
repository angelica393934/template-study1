package bsb.dev.bsb_bangking_jp.core.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import bsb.dev.bsb_bangking_jp.core.theme.extendedColors


data class FilterChipItem(
    val key: String,
    val label: String,
    val removable: Boolean = true,
)

@Composable
fun FilterChipBar(
    items: List<FilterChipItem>,
    modifier: Modifier = Modifier,
    onClearAll: (() -> Unit)? = null,
    onRemove: ((FilterChipItem) -> Unit)? = null,
) {
    if (items.isEmpty()) return

    Row(
        modifier = modifier.padding(start = 24.dp, end = 16.dp, top = 10.dp, bottom = 15.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (onClearAll != null) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .border(1.dp, MaterialTheme.extendedColors.textSecondary, CircleShape)
                    .clickable { onClearAll() },
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Hapus semua filter",
                    tint = MaterialTheme.extendedColors.textSecondary,
                    modifier = Modifier.size(18.dp),
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
            items.forEach { item ->
                Box(modifier = Modifier.padding(end = 8.dp)) {
                    FilterChipPill(
                        label = item.label,
                        onRemove = if (onRemove != null && item.removable) {
                            { onRemove(item) }
                        } else null,
                    )
                }
            }
        }
    }
}

@Composable
private fun FilterChipPill(
    label: String,
    onRemove: (() -> Unit)?,
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(100.dp))
            .background(MaterialTheme.colorScheme.primaryContainer)
            .border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(100.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary,
        )
        if (onRemove != null) {
            Spacer(modifier = Modifier.width(6.dp))
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Hapus filter",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .size(14.dp)
                    .clickable { onRemove() },
            )
        }
    }
}