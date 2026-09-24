package bsb.dev.bsb_bangking_jp.feature.cardless

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import bsb.dev.bsb_bangking_jp.core.components.AppHeader
import bsb.dev.bsb_bangking_jp.core.components.AppMenu
import bsb.dev.bsb_bangking_jp.core.dummy.DummyCardlessHistory
import bsb.dev.bsb_bangking_jp.core.dummy.DummyData
import bsb.dev.bsb_bangking_jp.core.theme.extendedColors
import androidx.compose.foundation.background
import bsb.dev.bsb_bangking_jp.R


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CardlessPage(
    onBackClick: () -> Unit = {},
    onNavigateToRoute: (String) -> Unit = {},
    onNavigateToUnavailable: () -> Unit = {},
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // HEADER
        AppHeader(
            title = "Cardless",
            onBackClick = onBackClick,
        )

        // MENU GRID -- padanan Wrap + LayoutBuilder (itemPerRow = 2)
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp, top = 30.dp, end = 10.dp, bottom = 30.dp),
        ) {
            val itemPerRow = 2
            val itemWidth = maxWidth / itemPerRow

            FlowRow(modifier = Modifier.fillMaxWidth()) {
                DummyData.cardlessMenuItems.forEach { m ->
                    Box(modifier = Modifier.width(itemWidth)) {
                        AppMenu(
                            label = m.label,
                            iconImg = m.iconRes,
                            scale = m.scale,
                            width = itemWidth,
                            useThemeStyle = true,
                            onTap = {
                                val route = m.route
                                if (route != null) onNavigateToRoute(route) else onNavigateToUnavailable()
                            },
                        )
                    }
                }
            }
        }

        // TITLE SECTION
        Text(
            text = "Daftar Cardless Terakhir",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.extendedColors.textSecondary,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 20.dp),
        )

        // LIST CARDLESS -- padanan ListView.separated
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            itemsIndexed(DummyData.cardlessHistoryList) { index, item ->
                CardlessListItem(item = item)
                if (index != DummyData.cardlessHistoryList.lastIndex) {
                    HorizontalDivider(thickness = 1.dp, color = MaterialTheme.extendedColors.strip)
                }
            }
        }
    }
}

@Composable
private fun CardlessListItem(item: DummyCardlessHistory) {
    val isBerhasil = item.status.trim().equals("berhasil", ignoreCase = true)
    val statusColor = if (isBerhasil) MaterialTheme.extendedColors.success else MaterialTheme.extendedColors.danger

    // Padanan: item.date.replaceFirst('-', '-\n')
    val displayDate = if (item.date.contains("-")) item.date.replaceFirst("-", "-\n") else item.date

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Leading icon
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(MaterialTheme.colorScheme.primaryContainer)
                .padding(12.dp),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_va1),
                contentDescription = null,
                Modifier.fillMaxSize(),
                tint = Color.Unspecified,
            )
        }

        // Title + subtitle
        Column(modifier = Modifier.weight(1f).padding(start = 12.dp)) {
            Text(
                text = item.title,
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text = displayDate,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.extendedColors.textSecondary,
            )
        }

        // Trailing status badge
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(statusColor)
                .padding(horizontal = 12.dp, vertical = 6.dp),
        ) {
            Text(
                text = item.status,
                fontSize = 10.sp,
                color = Color.White,
                fontWeight = FontWeight.W500,
            )
        }
    }
}