package bsb.dev.bsb_bangking_jp.feature.transfer.component

import androidx.compose.foundation.layout.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import bsb.dev.bsb_bangking_jp.core.components.*
import bsb.dev.bsb_bangking_jp.core.theme.extendedColors
import bsb.dev.bsb_bangking_jp.feature.transfer.transfer_core.domain.TransferInquiry
import bsb.dev.bsb_bangking_jp.feature.transfer.transfer_core.presentation.TransferNavEvent
import bsb.dev.bsb_bangking_jp.feature.transfer.transfer_core.presentation.TransferViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import bsb.dev.bsb_bangking_jp.feature.transfer.transfer_core.presentation.TransferUiEvent

@Composable
fun DetailRekeningBaruModal(
    inquiry: TransferInquiry,
    viewModel: TransferViewModel,
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit = {},
    onContinue: (TransferInquiry) -> Unit,
) {
    var isSaveReceiver by remember { mutableStateOf(false) }
    var alias by remember { mutableStateOf("") }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val toastState = LocalToastState.current
    val loadingOverlay = LocalLoadingOverlay.current

    LaunchedEffect(uiState.isSavingRecipient) {
        if (uiState.isSavingRecipient) loadingOverlay.show() else loadingOverlay.hide()
    }

    LaunchedEffect(Unit) {
        viewModel.navEvent.collect { event ->
            if (event is TransferNavEvent.RecipientSaved) {
                onDismiss()
                onContinue(inquiry)
            }
        }
    }
    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            if (event is TransferUiEvent.ShowToastError) {
                toastState.showError(event.message)
            }
        }
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Text(text = "Detail Rekening", style = MaterialTheme.typography.titleLarge)
        }
        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Pastikan Nama Penerima Telah Sesuai",
            style = MaterialTheme.typography.bodyMedium,
        )

        Spacer(modifier = Modifier.height(20.dp))
        HorizontalDivider(color = MaterialTheme.extendedColors.inputBackground)
        Spacer(modifier = Modifier.height(10.dp))

        AccountTile(
            initials = inquiry.initials,
            nama = inquiry.beneficiaryName,
            bank = inquiry.bankName,
            accountNumber = inquiry.beneficiaryAccountNo,
        )
        Spacer(modifier = Modifier.height(10.dp))
        HorizontalDivider(color = MaterialTheme.extendedColors.inputBackground)
        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = "Simpan Penerima", style = MaterialTheme.typography.titleLarge)
            AppSwitch(checked = isSaveReceiver, onCheckedChange = { isSaveReceiver = it })
        }

        if (isSaveReceiver) {
            Spacer(modifier = Modifier.height(10.dp))
            AppTextField(
                value = alias,
                onValueChange = { alias = it; viewModel.clearSaveRecipientError() },
                labelText = "Simpan Sebagai",
                hintText = "Nama Inisial",
                icon = Icons.Default.Person,
                errorText = uiState.saveRecipientError,
                showError = uiState.saveRecipientError != null,
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        AppButton(
            text = "Lanjutkan",
            onClick = {
                if (!isSaveReceiver) {
                    onDismiss()
                    onContinue(inquiry)
                } else {
                    val trimmed = alias.trim()
                    if (trimmed.isEmpty()) {
                        // set error lokal cukup lewat state saveRecipientError manual jika perlu,
                        // atau tambahkan validasi lokal sebelum call API
                    } else {
                        viewModel.saveRecipient(trimmed) // 🔥 HIT ENDPOINT 2
                    }
                }
            },
        )
    }
}