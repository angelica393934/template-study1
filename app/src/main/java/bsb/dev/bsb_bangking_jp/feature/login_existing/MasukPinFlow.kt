// feature/login_existing/MasukPinFlow.kt
package bsb.dev.bsb_bangking_jp.feature.login_existing

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import bsb.dev.bsb_bangking_jp.core.components.AppHeader
import bsb.dev.bsb_bangking_jp.core.components.InputPinPage
import bsb.dev.bsb_bangking_jp.core.components.LocalLoadingOverlay
import bsb.dev.bsb_bangking_jp.core.components.LocalToastState
import bsb.dev.bsb_bangking_jp.core.util.PinValidator
import bsb.dev.bsb_bangking_jp.feature.login_existing.presentation.LoginExistingNavEvent
import bsb.dev.bsb_bangking_jp.feature.login_existing.presentation.LoginExistingUiEvent
import bsb.dev.bsb_bangking_jp.feature.login_existing.presentation.LoginExistingViewModel

private enum class PinStep { CREATE, CONFIRM }

@Composable
fun MasukPinFlow(
    viewModel: LoginExistingViewModel,
    onBackClick: () -> Unit,
    onCompleted: () -> Unit,
) {
    var step by remember { mutableStateOf(PinStep.CREATE) }
    var newPin by remember { mutableStateOf("") }
    var confirmMismatchError by remember { mutableStateOf<String?>(null) }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val loadingOverlay = LocalLoadingOverlay.current
    val toastState = LocalToastState.current

    LaunchedEffect(uiState.isLoading) {
        if (uiState.isLoading) loadingOverlay.show() else loadingOverlay.hide()
    }

    LaunchedEffect(Unit) {
        viewModel.navEvent.collect { event ->
            if (event is LoginExistingNavEvent.ToPortal) onCompleted()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            if (event is LoginExistingUiEvent.ShowToastError) toastState.showError(event.message)
        }
    }

    when (step) {
        PinStep.CREATE -> InputPinPage(
            title = "Masukkan M-PIN",
            usePolaHeader = true,
            customHeader = { AppHeader(title = "Masukkan M-PIN", onBackClick = onBackClick) },
            subtitle = "Buat M-PIN Baru Mobile Banking Anda!",
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
            title = "Konfirmasi M-PIN",
            usePolaHeader = true,
            centerTitleWithBackButton = true,
            customHeader = { AppHeader(title = "Konfirmasi M-PIN", onBackClick = { step = PinStep.CREATE }) },
            subtitle = "Konfirmasi M-PIN Baru Anda",
            showTopBackground = false,
            onBackClick = { step = PinStep.CREATE },
            externalError = confirmMismatchError ?: uiState.confirmPinError,
            validator = { pin ->
                if (pin != newPin) "M-PIN konfirmasi yang Anda masukkan berbeda" else null
            },
            onPinComplete = { pin -> viewModel.confirmMpin(pin) },
        )
    }
}