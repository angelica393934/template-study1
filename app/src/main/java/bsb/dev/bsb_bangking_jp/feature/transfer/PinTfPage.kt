package bsb.dev.bsb_bangking_jp.feature.transfer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import bsb.dev.bsb_bangking_jp.core.component.InputPinPage
import bsb.dev.bsb_bangking_jp.core.component.LocalLoadingOverlay
import bsb.dev.bsb_bangking_jp.core.component.LocalToastState
import bsb.dev.bsb_bangking_jp.feature.transfer.transfer_core.domain.ConfirmTransferResultItem
import bsb.dev.bsb_bangking_jp.feature.transfer.component.PeriksaKembaliData
import bsb.dev.bsb_bangking_jp.feature.transfer.transfer_core.presentation.TransferNavEvent
import bsb.dev.bsb_bangking_jp.feature.transfer.transfer_core.presentation.TransferUiEvent
import bsb.dev.bsb_bangking_jp.feature.transfer.transfer_core.presentation.TransferViewModel
import org.koin.compose.koinInject

@Composable
fun PinTfPage(
    data: PeriksaKembaliData,
    onBack: () -> Unit,
    onBerhasilSegera: (ConfirmTransferResultItem) -> Unit,
    onBerhasilDijadwalkan: (ConfirmTransferResultItem) -> Unit,
    onSessionExpired: () -> Unit = {},
    viewModel: TransferViewModel = koinInject(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val loadingOverlay = LocalLoadingOverlay.current
    val toastState = LocalToastState.current

    // 🔹 Loading overlay mengikuti proses confirmTransfer (bukan transfer biasa).
    LaunchedEffect(uiState.isConfirming) {
        if (uiState.isConfirming) loadingOverlay.show() else loadingOverlay.hide()
    }

    // 🔹 Toast untuk error non-inline (khususnya sesi transaksi berakhir, code "0732"/"0465").
    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            if (event is TransferUiEvent.ShowToastError) {
                toastState.showError(event.message)
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.navEvent.collect { event ->
            when (event) {
                is TransferNavEvent.ConfirmSuccess -> {
                    val result = event.result
                    if (result.scheduleType == "SCHEDULED") {
                        onBerhasilDijadwalkan(result)
                    } else {
                        onBerhasilSegera(result)
                    }
                }
                is TransferNavEvent.ConfirmSessionExpired -> {
                    // 🔹 Padanan Navigator.pop 2x + pushReplacement(TransferPage) di .
                    onSessionExpired()
                }
                else -> Unit
            }
        }
    }

    InputPinPage(
        title = "Masukkan M-PIN",
        onBackClick = onBack,
        centerTitleWithBackButton = true,
        validator = null,
        externalError = uiState.confirmError,
        onPinComplete = { pin ->
            viewModel.confirmTransfer(pin) // 🔥 HIT ENDPOINT 4
        },
    )
}