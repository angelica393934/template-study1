// feature/login_existing/OtpMasukAkunPage.kt
package bsb.dev.bsb_bangking_jp.feature.login_existing

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import bsb.dev.bsb_bangking_jp.core.component.LocalLoadingOverlay
import bsb.dev.bsb_bangking_jp.core.component.LocalToastState
import bsb.dev.bsb_bangking_jp.feature.login_existing.presentation.LoginExistingViewModel
import bsb.dev.bsb_bangking_jp.core.component.OtpForm
import bsb.dev.bsb_bangking_jp.feature.login_existing.presentation.LoginExistingNavEvent
import bsb.dev.bsb_bangking_jp.feature.login_existing.presentation.LoginExistingUiEvent

@Composable
fun OtpMasukAkunPage(
    viewModel: LoginExistingViewModel,
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
            if (event is LoginExistingNavEvent.ToPinPage) onVerified()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is LoginExistingUiEvent.ShowToastError -> toastState.showError(event.message)
                LoginExistingUiEvent.ShowOtpResentToast -> toastState.showSuccess("Kode OTP baru berhasil dikirim")
            }
        }
    }

    OtpForm(
        title = "Masukkan OTP",
        phoneNumber = uiState.phoneNumber,
        isProcessing = uiState.isLoading,
        errorMessage = uiState.otpErrorMessage,
        onVerify = { otp -> viewModel.verifyOtp(otp) },
        onResend = { viewModel.resendOtp() },
        onBackClick = onBackClick,
        modifier = Modifier.fillMaxSize(),
    )
}