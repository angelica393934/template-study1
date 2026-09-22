package bsb.dev.bsb_bangking_jp.feature.va

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CreditCard
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
import androidx.compose.ui.unit.dp
import bsb.dev.bsb_bangking_jp.core.components.AppButton
import bsb.dev.bsb_bangking_jp.core.components.AppHeader
import bsb.dev.bsb_bangking_jp.core.components.AppTextField
import bsb.dev.bsb_bangking_jp.core.components.CardHistory
import bsb.dev.bsb_bangking_jp.core.dummy.DummyData
import bsb.dev.bsb_bangking_jp.core.theme.extendedColors

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun VaPage(
    onBackClick: () -> Unit = {},
    onNavigateToUnavailable: () -> Unit = {},
    onLanjutkanClick: () -> Unit = {},
) {
    var vaNumber by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            AppHeader(
                title = "Virtual Account",
                onBackClick = onBackClick,
            )
        },
        bottomBar = {
            AppButton(
                text = "Lanjutkan",
                onClick = onLanjutkanClick,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 20.dp)
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
        ) {
            // HEADER

            // INPUT NOMOR VA
            Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 20.dp)) {
                AppTextField(
                    value = vaNumber,
                    onValueChange = { vaNumber = it },
                    hintText = "Masukkan Nomor Virtual Account",
                    labelText = "Nomor Tujuan",
                    isNumberOnly = true,
                    icon = Icons.Default.CreditCard,
                )
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
        }
    }
}