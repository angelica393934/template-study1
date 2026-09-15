package bsb.dev.bsb_bangking_jp.feature.change_email

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import bsb.dev.bsb_bangking_jp.core.component.InputPinPage
import bsb.dev.bsb_bangking_jp.core.component.LocalLoadingOverlay
import bsb.dev.bsb_bangking_jp.core.component.LocalToastState
import bsb.dev.bsb_bangking_jp.feature.change_email.presentation.ChangeEmailViewModel

@Composable
fun ChangeEmailPinPage(
    viewModel: ChangeEmailViewModel,
    onBackClick: () -> Unit,
    onCompleted: () -> Unit,
) {
    val loadingOverlay = LocalLoadingOverlay.current
    val toastState = LocalToastState.current

    InputPinPage(
        title = "Masukkan M-PIN untuk melanjutkan",
        onBackClick = onBackClick,
        validator = { pin ->
            loadingOverlay.show()
            val error = viewModel.confirmEmail(pin)
            loadingOverlay.hide()
            error
        },
        onPinComplete = {
            toastState.showSuccess("Email berhasil diperbarui")
            onCompleted()
        },
    )
}