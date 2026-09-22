package bsb.dev.bsb_bangking_jp.feature.top_up

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.SignalCellularAlt
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import bsb.dev.bsb_bangking_jp.R
import bsb.dev.bsb_bangking_jp.core.components.AppHeader
import bsb.dev.bsb_bangking_jp.core.components.CardHistory
import bsb.dev.bsb_bangking_jp.core.components.SearchTextField
import bsb.dev.bsb_bangking_jp.core.components.AppMenu
import bsb.dev.bsb_bangking_jp.core.dummy.DummyData
import bsb.dev.bsb_bangking_jp.core.theme.extendedColors


private data class TopUpMenuItem(
    val icon: ImageVector? = null,
    val iconImg: Int? = null,
    val scale: Float? = null,
    val label: String,
    val route: String? = null,
)

private val topUpMenuItems = listOf(
    TopUpMenuItem(icon = Icons.Default.SignalCellularAlt, scale = 0.7f, label = "Pulsa"),
    TopUpMenuItem(icon = Icons.Default.Language, scale = 0.7f, label = "Paket Data"),
    TopUpMenuItem(iconImg = R.drawable.ic_gopay, scale = 0.6f, label = "Gopay"),
    TopUpMenuItem(iconImg = R.drawable.ic_bsb_cash, scale = 0.8f, label = "BSB Cash"),
    TopUpMenuItem(iconImg = R.drawable.ic_ovo, scale = 0.6f, label = "OVO"),
    TopUpMenuItem(iconImg = R.drawable.ic_s_pay, scale = 0.7f, label = "Shopee Pay"),
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TopUpPage(
    onBackClick: () -> Unit = {},
    onNavigateToRoute: (String) -> Unit = {},
    onNavigateToUnavailable: () -> Unit = {},
) {
    var query by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            AppHeader(
                title = "Top Up",
                onBackClick = onBackClick,
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
        ) {

            // SEARCH FIELD
            SearchTextField(
                value = query,
                onValueChange = { query = it },
                hintText = "Cari",
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 20.dp),
            )

            // MENU GRID -- padanan Wrap + LayoutBuilder (itemPerRow = 3)
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp, top = 5.dp, end = 15.dp, bottom = 30.dp),
            ) {
                val itemPerRow = 3
                val itemWidth = maxWidth / itemPerRow

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    topUpMenuItems.forEach { m ->
                        Box(modifier = Modifier.width(itemWidth)) {
                            AppMenu(
                                label = m.label,
                                icon = m.icon,
                                iconImg = m.iconImg,
                                scale = m.scale,
                                width = itemWidth,
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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = "Daftar Pembayaran Terakhir",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.extendedColors.textSecondary,
                )
                Row(
                    modifier = Modifier.clickable { onNavigateToUnavailable() },
                ) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp),
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Atur",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }

            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 5.dp),
            ) {
                val columnCount = if (maxWidth > 600.dp) 3 else 2
                val spacing = 12.dp
                val itemWidth = (maxWidth - spacing * (columnCount - 1)) / columnCount

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(spacing),
                    verticalArrangement = Arrangement.spacedBy(spacing),
                ) {
                    DummyData.paymentMethodList.forEach { item ->
                        Box(modifier = Modifier.width(itemWidth)) {
                            CardHistory(
                                title = item.title,
                                subtitle = item.number,
                                imageRes = item.iconRes,
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}