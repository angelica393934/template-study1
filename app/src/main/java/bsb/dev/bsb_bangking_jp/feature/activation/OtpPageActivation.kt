package bsb.dev.bsb_bangking_jp.feature.activation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import bsb.dev.bsb_bangking_jp.core.component.LocalLoadingOverlay
import bsb.dev.bsb_bangking_jp.core.component.LocalToastState
import bsb.dev.bsb_bangking_jp.core.util.maskPhoneNumber
import bsb.dev.bsb_bangking_jp.feature.activation.presentation.ActivationNavEvent
import bsb.dev.bsb_bangking_jp.feature.activation.presentation.ActivationUiEvent
import bsb.dev.bsb_bangking_jp.feature.activation.presentation.ActivationViewModel
import bsb.dev.bsb_bangking_jp.core.component.OtpForm

@Composable
fun OtpPageActivation(
    viewModel: ActivationViewModel,
    onBackClick: () -> Unit,
    onVerified: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val toastState = LocalToastState.current
    val loadingOverlay = LocalLoadingOverlay.current

    LaunchedEffect(uiState.isLoading) {
        if (uiState.isLoading) loadingOverlay.show() else loadingOverlay.hide()
    }
    LaunchedEffect(Unit) {
        viewModel.navEvent.collect { event ->
            if (event is ActivationNavEvent.ToPasswordPage) onVerified()
        }
    }
    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is ActivationUiEvent.ShowToastError -> toastState.showError(event.message)
                ActivationUiEvent.ShowOtpResentToast -> toastState.showSuccess("Kode OTP baru berhasil dikirim")
            }
        }
    }

    // 🔹 Padanan maskPhoneNumber() -- OTP page Flutter menampilkan nomor tersamar.
    OtpForm(
        title = "Masukkan OTP",
        phoneNumber = maskPhoneNumber(uiState.mobileNumber),
        isProcessing = uiState.isLoading,
        errorMessage = uiState.otpErrorMessage,
        onVerify = { otp -> viewModel.verifyOtp(otp) },
        onResend = { viewModel.resendOtp() },
        onBackClick = onBackClick,
        modifier = Modifier.fillMaxSize(),
    )
}