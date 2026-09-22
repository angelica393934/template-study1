package bsb.dev.bsb_bangking_jp.feature.pajak_pendidikan

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
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import bsb.dev.bsb_bangking_jp.core.components.AppHeader
import bsb.dev.bsb_bangking_jp.core.components.CardHistory
import bsb.dev.bsb_bangking_jp.core.components.AppMenu
import bsb.dev.bsb_bangking_jp.core.dummy.DummyData
import bsb.dev.bsb_bangking_jp.core.theme.extendedColors

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PajakPendidikanPage(
    onBackClick: () -> Unit = {},
    onNavigateToRoute: (String) -> Unit = {},
    onNavigateToUnavailable: () -> Unit = {},
) {
    Scaffold(
        topBar = {
            AppHeader(
                title = "Pajak dan Pendidikan",
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
            // MENU GRID -- padanan Wrap + LayoutBuilder (itemPerRow = 4)
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp, top = 30.dp, end = 10.dp, bottom = 30.dp),
            ) {
                val itemPerRow = 4
                val itemWidth = maxWidth / itemPerRow
                val rows = DummyData.pajakMenuItems.chunked(itemPerRow)
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    rows.forEach { rowItems ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            rowItems.forEach { m ->
                                Box(modifier = Modifier.width(itemWidth)) {
                                    AppMenu(
                                        label = m.label,
                                        iconImg = m.iconRes,
                                        scale = m.scale,
                                        width = itemWidth,
                                        onTap = {
                                            val route = m.route
                                            if (route != null) {
                                                onNavigateToRoute(route)
                                            } else {
                                                onNavigateToUnavailable()
                                            }
                                        }
                                    )
                                }
                            }
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

            // LIST PEMBAYARAN -- padanan Wrap + LayoutBuilder (2 kolom, 3 kalau lebar > 600dp)
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
                    DummyData.pajakPaymentList.forEach { item ->
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