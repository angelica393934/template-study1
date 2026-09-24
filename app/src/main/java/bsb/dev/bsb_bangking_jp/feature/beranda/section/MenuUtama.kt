package bsb.dev.bsb_bangking_jp.feature.beranda.section

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import bsb.dev.bsb_bangking_jp.R
import bsb.dev.bsb_bangking_jp.core.components.AppMenu


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MenuUtama(
    onTransferClick: () -> Unit = {},
    onTopUpClick: () -> Unit = {},
    onVirtualAccountClick: () -> Unit = {},
    onBsbCashClick: () -> Unit = {},
    onPajakPendidikanClick: () -> Unit = {},
    onTagihanClick: () -> Unit = {},
    onCardlessClick: () -> Unit = {},
    onLainnyaClick: () -> Unit = {},
) {
    data class MenuUtamaItem(
        val labelRes: Int,
        val iconRes: Int,
        val onClick: () -> Unit
    )

    val menuItems = listOf(
        MenuUtamaItem(
            labelRes = R.string.menu_transfer,
            iconRes = R.drawable.ic_transfer,
            onClick = onTransferClick
        ),
        MenuUtamaItem(
            labelRes = R.string.menu_top_up,
            iconRes = R.drawable.ic_topup,
            onClick = onTopUpClick
        ),
        MenuUtamaItem(
            labelRes = R.string.menu_virtual_account,
            iconRes = R.drawable.ic_va,
            onClick = onVirtualAccountClick
        ),
        MenuUtamaItem(
            labelRes = R.string.menu_bsb_cash,
            iconRes = R.drawable.ic_bsb_cash1,
            onClick = onBsbCashClick
        ),
        MenuUtamaItem(
            labelRes = R.string.menu_pajak_pendidikan,
            iconRes = R.drawable.ic_pajak_pendidikan,
            onClick = onPajakPendidikanClick
        ),
        MenuUtamaItem(
            labelRes = R.string.menu_tagihan,
            iconRes = R.drawable.ic_tagihan,
            onClick = onTagihanClick
        ),
        MenuUtamaItem(
            labelRes = R.string.menu_cardless,
            iconRes = R.drawable.ic_cardless,
            onClick = onCardlessClick
        ),
        MenuUtamaItem(
            labelRes = R.string.menu_lainnya,
            iconRes = R.drawable.ic_lainnya,
            onClick = onLainnyaClick
        )
    )
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(vertical = 16.dp),
    ) {
        Text(
            text = stringResource(R.string.label_menu_utama),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding( start = 16.dp,bottom = 20.dp),
        )

        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
            val itemWidth = maxWidth / 4

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                maxItemsInEachRow = 4,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                menuItems.forEach { item ->
                    AppMenu(
                        label = stringResource(item.labelRes),
                        iconImg = item.iconRes,
                        width = itemWidth,
                        useThemeStyle = true,
                        onTap = item.onClick,
                    )
                }
            }
        }
    }
}