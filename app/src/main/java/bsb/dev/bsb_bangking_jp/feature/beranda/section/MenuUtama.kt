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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.School
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
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
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
                AppMenu(
                    label = stringResource(R.string.menu_transfer),
                    iconImg = R.drawable.ic_transfer,
                    width = itemWidth,
                    scale = 0.42f,
                    useThemeStyle = true,
                    onTap = onTransferClick,
                )
                AppMenu(
                    label = stringResource(R.string.menu_top_up),
                    icon = Icons.Filled.AccountBalanceWallet,
                    width = itemWidth,
                    scale = 0.4f,
                    useThemeStyle = true,
                    onTap = onTopUpClick,
                )
                AppMenu(
                    label = stringResource(R.string.menu_virtual_account),
                    iconImg = R.drawable.ic_va,
                    scale = 0.54f,
                    width = itemWidth,
                    useThemeStyle = true,
                    onTap = onVirtualAccountClick,
                )
                AppMenu(
                    label = stringResource(R.string.menu_bsb_cash),
                    icon = Icons.Filled.CreditCard,
                    width = itemWidth,
                    useThemeStyle = true,
                    onTap = onBsbCashClick,
                )
                AppMenu(
                    label = stringResource(R.string.menu_pajak_pendidikan),
                    icon = Icons.Filled.School,
                    width = itemWidth,
                    useThemeStyle = true,
                    onTap = onPajakPendidikanClick,
                )
                AppMenu(
                    label = stringResource(R.string.menu_tagihan),
                    icon = Icons.AutoMirrored.Filled.ReceiptLong,
                    width = itemWidth,
                    useThemeStyle = true,
                    onTap = onTagihanClick,
                )
                AppMenu(
                    label = stringResource(R.string.menu_cardless),
                    iconImg = R.drawable.ic_cardless,
                    width = itemWidth,
                    scale = 0.50f,
                    useThemeStyle = true,
                    onTap = onCardlessClick,
                )
                AppMenu(
                    label = stringResource(R.string.menu_lainnya),
                    iconImg = R.drawable.ic_lainnya,
                    width = itemWidth,
                    scale = 0.45f,
                    useThemeStyle = true,
                    onTap = onLainnyaClick,
                )
            }
        }
    }
}