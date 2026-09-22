package bsb.dev.bsb_bangking_jp.feature.transfer.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import bsb.dev.bsb_bangking_jp.R
import bsb.dev.bsb_bangking_jp.core.components.AppButton
import bsb.dev.bsb_bangking_jp.core.components.RekeningLainnyaSheet
import bsb.dev.bsb_bangking_jp.core.components.RekeningSheetMode
import bsb.dev.bsb_bangking_jp.core.components.SaldoCardEmpty
import bsb.dev.bsb_bangking_jp.core.skeleton.SkeletonRekeningCard
import bsb.dev.bsb_bangking_jp.core.theme.extendedColors
import bsb.dev.bsb_bangking_jp.core.util.RupiahFormat
import bsb.dev.bsb_bangking_jp.shared.rekening_lainnya.data.RekeningItem
import bsb.dev.bsb_bangking_jp.shared.rekening_lainnya.data.cashBalanceValue

/** Padanan RekeningLainnyaState (initial/loading/refreshing/success/error) di . */
sealed interface RekeningSumberUiState {
    data object Loading : RekeningSumberUiState
    data class Success(val data: List<RekeningItem>) : RekeningSumberUiState
    data class Error(val message: String) : RekeningSumberUiState
}

@Composable
fun RekeningSumberCard(
    state: RekeningSumberUiState,
    activeAccountNumber: String?,
    onAccountChanged: (RekeningItem) -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(text = "Rekening Sumber", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        when (state) {
            is RekeningSumberUiState.Loading -> {
                SkeletonRekeningCard()
            }

            is RekeningSumberUiState.Error -> {
                SaldoCardEmpty(onRetry = onRetry)
            }

            is RekeningSumberUiState.Success -> {
                RekeningSumberContent(
                    daftarRekening = state.data,
                    activeAccountNumber = activeAccountNumber,
                    onAccountChanged = onAccountChanged,
                )
            }
        }
    }
}

@Composable
private fun RekeningSumberContent(
    daftarRekening: List<RekeningItem>,
    activeAccountNumber: String?,
    onAccountChanged: (RekeningItem) -> Unit,
) {
    if (daftarRekening.isEmpty()) return

    var showSheet by remember { mutableStateOf(false) }

    val aktif = daftarRekening.firstOrNull { it.number == activeAccountNumber }
        ?: daftarRekening.firstOrNull { it.isPrimary }
        ?: daftarRekening.first()

    LaunchedEffect(aktif.number) {
        if (activeAccountNumber != aktif.number) onAccountChanged(aktif)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(25.dp))
            .background(MaterialTheme.colorScheme.background)
            .border(1.dp, MaterialTheme.extendedColors.textDisabled, RoundedCornerShape(25.dp)),
    ) {
        Image(
            painter = painterResource(id = R.drawable.bg_card),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize()
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text(
                    text = aktif.accountTypeName.ifEmpty { "Tabungan Sekarang" },
                    style = MaterialTheme.typography.bodyMedium,
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = RupiahFormat(aktif.cashBalanceValue().toInt()),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = aktif.number,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.extendedColors.textSecondary,
                )
            }

            AppButton(
                text = "Ubah",
                onClick = { showSheet = true },
                backgroundColor = MaterialTheme.colorScheme.inverseSurface,
                textColor = MaterialTheme.colorScheme.inverseOnSurface,
                smallWidth = true,
                textStyle = MaterialTheme.typography.titleSmall,
            )
        }
    }

    if (showSheet) {
        val sortedForSheet = daftarRekening
            .filter { it.visible }
            .sortedByDescending { it.isPrimary }

        RekeningLainnyaSheet(
            daftarRekening = sortedForSheet,
            mode = RekeningSheetMode.REKENING_SUMBER,
            rekeningAktif = aktif.number,
            title = "Pilih Rekening Sumber",
            onDismiss = { showSheet = false },
            onSelected = { selected ->
                showSheet = false
                onAccountChanged(selected)
            },
        )
    }
}