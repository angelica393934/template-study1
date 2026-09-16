package bsb.dev.bsb_bangking_jp.feature.change_mpin

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
import bsb.dev.bsb_bangking_jp.core.component.AppModalConfirm
import bsb.dev.bsb_bangking_jp.core.component.LocalLoadingOverlay
import bsb.dev.bsb_bangking_jp.core.component.LocalToastState
import bsb.dev.bsb_bangking_jp.core.component.OtpForm
import bsb.dev.bsb_bangking_jp.feature.change_mpin.presentation.ChangeMpinNavEvent
import bsb.dev.bsb_bangking_jp.feature.change_mpin.presentation.ChangeMpinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OtpChangeMpinPage(
    viewModel: ChangeMpinViewModel,
    onBackClick: () -> Unit,
    onCompleted: () -> Unit, // 🔹 rename dari onVerified -- lebih jelas maknanya "seluruh alur selesai"
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val toastState = LocalToastState.current
    val loadingOverlay = LocalLoadingOverlay.current

    var showSuccessSheet by remember { mutableStateOf(false) } // 🔹 state LOKAL, bukan di AppNavigation

    LaunchedEffect(uiState.isLoading) {
        if (uiState.isLoading) loadingOverlay.show() else loadingOverlay.hide()
    }
    LaunchedEffect(Unit) {
        viewModel.navEvent.collect { event ->
            if (event is ChangeMpinNavEvent.ToPortalSuccess) showSuccessSheet = true
        }
    }
    LaunchedEffect(uiState.otpErrorMessage) {
        uiState.otpErrorMessage?.let { toastState.showError(it) }
    }

    OtpForm(
        title = "Masukkan OTP",
        phoneNumber = uiState.mobileNumber,
        isProcessing = uiState.isLoading,
        errorMessage = uiState.otpErrorMessage,
        onVerify = { otp -> viewModel.verifyOtp(otp) },
        onResend = {
            toastState.showSuccess("Kode OTP baru berhasil dikirim")
            true
        },
        onBackClick = onBackClick,
        modifier = Modifier.fillMaxSize(),
    )

    if (showSuccessSheet) {
        AppModalConfirm(
            onDismissRequest = {
                showSuccessSheet = false
                onCompleted()
            },
            title = "M-PIN Anda sudah diperbarui.",
            centerimage = R.drawable.asset_centang,
            description = "Silakan gunakan M-PIN baru untuk menggunakan layanan kami.",
            confirmText = "Kembali",
            onConfirm = {
                showSuccessSheet = false
                onCompleted()
            },
        )
    }
}