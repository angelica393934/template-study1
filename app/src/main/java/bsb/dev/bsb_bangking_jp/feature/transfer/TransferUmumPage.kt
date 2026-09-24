package bsb.dev.bsb_bangking_jp.feature.transfer

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun TransferUmumPage(
    bank: String,
    accountNumber: String,
    name: String,
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    onLanjutkan: (TransferFormResult) -> Unit = {},
) {
    TransferFormPage(
        jenis = TransferJenis.ANTAR_BANK,
        bank = bank,
        accountNumber = accountNumber,
        name = name,
        modifier = modifier,
        onBack = onBack,
        onLanjutkan = onLanjutkan,
    )
}