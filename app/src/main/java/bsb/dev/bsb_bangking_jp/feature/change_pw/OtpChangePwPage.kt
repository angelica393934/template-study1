package bsb.dev.bsb_bangking_jp.feature.change_pw

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import bsb.dev.bsb_bangking_jp.R
import bsb.dev.bsb_bangking_jp.core.components.AppModalConfirm
import bsb.dev.bsb_bangking_jp.core.components.LocalLoadingOverlay
import bsb.dev.bsb_bangking_jp.core.components.LocalToastState
import bsb.dev.bsb_bangking_jp.core.components.OtpForm
import bsb.dev.bsb_bangking_jp.feature.change_pw.presentation.ChangePwNavEvent
import bsb.dev.bsb_bangking_jp.feature.change_pw.presentation.ChangePwUiEvent
import bsb.dev.bsb_bangking_jp.feature.change_pw.presentation.ChangePwViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OtpChangePwPage(
    viewModel: ChangePwViewModel,
    onBackClick: () -> Unit,
    onCompleted: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val toastState = LocalToastState.current
    val loadingOverlay = LocalLoadingOverlay.current
    var showChangePwSuccessSheet by remember { mutableStateOf(false) } // 🔹 state LOKAL, bukan di AppNavigation

    LaunchedEffect(uiState.isLoading) {
        if (uiState.isLoading) loadingOverlay.show() else loadingOverlay.hide()
    }
    LaunchedEffect(Unit) {
        viewModel.navEvent.collect { event ->
            if (event is ChangePwNavEvent.ToPortalSuccess) showChangePwSuccessSheet = true
        }
    }
    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is ChangePwUiEvent.ShowToastError -> toastState.showError(event.message)
                ChangePwUiEvent.ShowOtpResentToast -> toastState.showSuccess("Kode OTP baru berhasil dikirim")
            }
        }
    }

    OtpForm(
        title = "Masukkan OTP",
        phoneNumber = uiState.mobileNumber,
        isProcessing = uiState.isLoading,
        errorMessage = uiState.otpErrorMessage,
        onVerify = { otp -> viewModel.verifyOtp(otp) },
        onResend = { viewModel.resendOtp() },
        onBackClick = onBackClick,
        modifier = Modifier.fillMaxSize(),
    )


    if (showChangePwSuccessSheet) {
        AppModalConfirm(
            onDismissRequest = {
                showChangePwSuccessSheet = false
                onCompleted()
            },
            title = "Kata sandi Anda sudah diperbarui.",
            centerimage = R.drawable.asset_centang,
            description = "Silahkan gunakan kata sandi baru untuk menggunakan layanan kami.",
            confirmText = "Kembali",
            onConfirm = {
                showChangePwSuccessSheet = false
                onCompleted()
             },
        )
    }
}