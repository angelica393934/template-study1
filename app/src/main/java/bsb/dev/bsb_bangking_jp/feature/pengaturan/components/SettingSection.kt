package bsb.dev.bsb_bangking_jp.feature.pengaturan.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import bsb.dev.bsb_bangking_jp.core.theme.Primary2
import bsb.dev.bsb_bangking_jp.core.theme.appSpacing
import bsb.dev.bsb_bangking_jp.core.theme.extendedColors

data class SettingItemData(
    val icon: ImageVector,
    val title: String,
    val onClick: () -> Unit = {}
)

@Composable
fun SettingSection(
    title: String,
    items: List<SettingItemData>,
) {
    Column{
        Spacer(modifier = Modifier.height(15.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.outline
        )
        Spacer(modifier = Modifier.height(15.dp))

        HorizontalDivider(
            color = MaterialTheme.extendedColors.strip
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.background
            )
        ) {
            Column {
                items.forEachIndexed { index, item ->

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { item.onClick() }
                            .padding(vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.title,
                            tint = Primary2,
                            modifier = Modifier.size(22.dp)
                        )

                        Spacer(modifier = Modifier.width(appSpacing.xxs))

                        Text(
                            text = item.title,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.extendedColors.textPrimary,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    if (index != items.lastIndex) {
                        HorizontalDivider(
                            color = MaterialTheme.extendedColors.strip
                        )
                    }
                }
            }
        }
        HorizontalDivider(
            color = MaterialTheme.extendedColors.strip
        )
        Spacer(modifier = Modifier.height(appSpacing.xxxs))
    }
}