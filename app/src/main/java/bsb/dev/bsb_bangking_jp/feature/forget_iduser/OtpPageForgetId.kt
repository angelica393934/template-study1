package bsb.dev.bsb_bangking_jp.feature.forget_iduser

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import bsb.dev.bsb_bangking_jp.core.component.LocalLoadingOverlay
import bsb.dev.bsb_bangking_jp.core.component.LocalToastState
import bsb.dev.bsb_bangking_jp.core.component.OtpForm
import bsb.dev.bsb_bangking_jp.feature.forget_iduser.presentation.ForgetIdUserNavEvent
import bsb.dev.bsb_bangking_jp.feature.forget_iduser.presentation.ForgetIdUserUiEvent
import bsb.dev.bsb_bangking_jp.feature.forget_iduser.presentation.ForgetIdUserViewModel

@Composable
fun OtpPageForgetId(
    viewModel: ForgetIdUserViewModel,
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
            if (event is ForgetIdUserNavEvent.ToResetIdPage) onVerified()
        }
    }
    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is ForgetIdUserUiEvent.ShowToastError -> toastState.showError(event.message)
                ForgetIdUserUiEvent.ShowOtpResentToast -> toastState.showSuccess("New OTP code has been sent")
            }
        }
    }

    OtpForm(
        title = "Enter OTP",
        phoneNumber = uiState.mobileNumber,
        isProcessing = uiState.isLoading,
        errorMessage = uiState.otpErrorMessage,
        onVerify = { otp -> viewModel.verifyOtp(otp) },
        onResend = { viewModel.resendOtp() },
        onBackClick = onBackClick,
        modifier = Modifier.fillMaxSize(),
    )
}