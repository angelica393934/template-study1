package bsb.dev.bsb_bangking_jp.feature.activation

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import bsb.dev.bsb_bangking_jp.R
import bsb.dev.bsb_bangking_jp.core.component.AppHeader
import bsb.dev.bsb_bangking_jp.core.component.AppModalConfirm
import bsb.dev.bsb_bangking_jp.core.component.InputPinPage
import bsb.dev.bsb_bangking_jp.core.component.LocalLoadingOverlay
import bsb.dev.bsb_bangking_jp.core.component.LocalToastState
import bsb.dev.bsb_bangking_jp.core.util.PinValidator
import bsb.dev.bsb_bangking_jp.feature.activation.presentation.ActivationNavEvent
import bsb.dev.bsb_bangking_jp.feature.activation.presentation.ActivationUiEvent
import bsb.dev.bsb_bangking_jp.feature.activation.presentation.ActivationViewModel

private enum class PinStep { CREATE, CONFIRM }

/** Padanan PinAktivasiPage.dart + KonfirmasiPinAktivasiPage.dart + CustomModalSuccess (digabung 1 flow). */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivationPinFlow(
    viewModel: ActivationViewModel,
    onBackClick: () -> Unit,
    onCompleted: () -> Unit,
) {
    var step by remember { mutableStateOf(PinStep.CREATE) }
    var newPin by remember { mutableStateOf("") }
    var confirmMismatchError by remember { mutableStateOf<String?>(null) }
    var showSuccessSheet by remember { mutableStateOf(false) }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val loadingOverlay = LocalLoadingOverlay.current
    val toastState = LocalToastState.current

    LaunchedEffect(uiState.isLoading) {
        if (uiState.isLoading) loadingOverlay.show() else loadingOverlay.hide()
    }

    LaunchedEffect(Unit) {
        viewModel.navEvent.collect { event ->
            if (event is ActivationNavEvent.ToPortal) showSuccessSheet = true
        }
    }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            if (event is ActivationUiEvent.ShowToastError) toastState.showError(event.message)
        }
    }

    when (step) {
        PinStep.CREATE -> InputPinPage(
            title = "Masukkan M-PIN Anda",
            usePolaHeader = true,
            customHeader = { AppHeader(title = "Masukkan M-PIN Anda", onBackClick = onBackClick) },
            showTopBackground = false,
            onBackClick = onBackClick,
            validator = { pin -> PinValidator.validateNewPin(pin) },
            onPinComplete = { pin ->
                newPin = pin
                confirmMismatchError = null
                step = PinStep.CONFIRM
            },
        )

        PinStep.CONFIRM -> InputPinPage(
            title = "Konfirmasi M-PIN Anda",
            usePolaHeader = true,
            centerTitleWithBackButton = true,
            customHeader = { AppHeader(title = "Konfirmasi M-PIN Anda", onBackClick = { step = PinStep.CREATE }) },
            showTopBackground = false,
            onBackClick = { step = PinStep.CREATE },
            externalError = confirmMismatchError ?: uiState.confirmPinError,
            validator = { pin ->
                if (pin != newPin) "M-PIN konfirmasi yang Anda masukkan berbeda" else null
            },
            onPinComplete = { pin -> viewModel.confirmMpin(pin) },
        )
    }

    // 🔹 Padanan CustomModalSuccess -- muncul setelah confirmMpinActivation sukses,
    // baru navigasi ke portal & bersihkan seluruh back stack saat sheet ditutup.
    if (showSuccessSheet) {
        AppModalConfirm(
            onDismissRequest = {
                showSuccessSheet = false
                onCompleted()
            },
            title = "Aktivasi Berhasil",
            centerimage = R.drawable.asset_centang,
            description = "Untuk menggunakan semua fitur, silakan kunjungi cabang Bank Sumsel Babel terdekat dan selesaikan proses verifikasi.",
            confirmText = "Kembali",
            onConfirm = {
                showSuccessSheet = false
                onCompleted()
            },
        )
    }
}