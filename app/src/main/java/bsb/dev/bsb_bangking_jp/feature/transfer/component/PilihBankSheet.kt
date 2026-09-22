package bsb.dev.bsb_bangking_jp.feature.transfer.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import bsb.dev.bsb_bangking_jp.core.components.EmptyState
import bsb.dev.bsb_bangking_jp.core.components.InitialAvatar
import bsb.dev.bsb_bangking_jp.core.components.SearchTextField
import bsb.dev.bsb_bangking_jp.feature.transfer.daftar_bank.domain.BankItem
import bsb.dev.bsb_bangking_jp.feature.transfer.daftar_bank.presentation.DaftarBankUiState
import bsb.dev.bsb_bangking_jp.feature.transfer.daftar_bank.presentation.DaftarBankViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun PilihBankSheet(
    modifier: Modifier = Modifier,
    viewModel: DaftarBankViewModel = koinViewModel(),
    onDismiss: () -> Unit = {},
    onBankSelected: (code: String, name: String) -> Unit,
) {
    var query by remember { mutableStateOf("") }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // 🔹 Trigger fetch (no-op kalau sudah Success -- ditangani di ViewModel).
    LaunchedEffect(Unit) {
        viewModel.getDaftarBank()
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(max = 1000.dp),
    ) {
        SearchTextField(
            value = query,
            onValueChange = { query = it },
            hintText = "Cari Bank Tujuan",
        )

        Spacer(modifier = Modifier.height(20.dp))

        when (val state = uiState) {
            is DaftarBankUiState.Initial,
            is DaftarBankUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }

            is DaftarBankUiState.Error -> {
                EmptyState(
                    message = "Data bank tidak dapat dimuat",
                    subMessage = "Terjadi kesalahan saat mengambil data.\nPeriksa koneksi anda.",
                    actionText = "Coba Lagi",
                    onAction = { viewModel.retry() },
                    modifier = Modifier.height(300.dp),
                )
            }

            is DaftarBankUiState.Success -> {
                val filteredSesama = remember(query, state.sesamaBank) {
                    state.sesamaBank.filter { it.bankName.contains(query, ignoreCase = true) }
                }
                val filteredBankLain = remember(query, state.bankLain) {
                    state.bankLain.filter { it.bankName.contains(query, ignoreCase = true) }
                }

                if (filteredSesama.isEmpty() && filteredBankLain.isEmpty()) {
                    EmptyState(
                        message = "Bank tidak ditemukan",
                        subMessage = "Coba kata kunci lain",
                        actionText = null,
                        modifier = Modifier.height(300.dp),
                    )
                } else {
                    LazyColumn {
                        if (filteredSesama.isNotEmpty()) {
                            item {
                                Text("Sesama Bank", style = MaterialTheme.typography.titleLarge)
                                Spacer(modifier = Modifier.height(15.dp))
                            }
                            items(filteredSesama, key = { it.bankCode }) { bank ->
                                BankItemRow(bank = bank, onClick = { onBankSelected(bank.bankCode, bank.bankName) })
                                Spacer(modifier = Modifier.height(10.dp))
                            }
                            item { Spacer(modifier = Modifier.height(10.dp)) }
                        }

                        if (filteredBankLain.isNotEmpty()) {
                            item {
                                Text("Daftar Bank Lain", style = MaterialTheme.typography.titleLarge)
                                Spacer(modifier = Modifier.height(20.dp))
                            }
                            items(filteredBankLain, key = { it.bankCode }) { bank ->
                                BankItemRow(bank = bank, onClick = { onBankSelected(bank.bankCode, bank.bankName) })
                                Spacer(modifier = Modifier.height(10.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BankItemRow(
    bank: BankItem,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
    ) {
        // 🔹 InitialAvatar sudah otomatis fallback ke inisial kalau `imagePath`
        // null/gagal load (lihat GlideImage di dalamnya) -- padanan skeleton+fallback
        // manual yang ada di GetImageBloc versi .
        InitialAvatar(
            initials = bank.bankName,
            imagePath = bank.picture,
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = bank.bankName,
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}