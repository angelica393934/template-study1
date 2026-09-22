package bsb.dev.bsb_bangking_jp.feature.change_mpin

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
import bsb.dev.bsb_bangking_jp.core.util.PinValidator
import bsb.dev.bsb_bangking_jp.feature.change_mpin.presentation.ChangeMpinNavEvent
import bsb.dev.bsb_bangking_jp.feature.change_mpin.presentation.ChangeMpinViewModel

private enum class ChangeMpinStep { OLD, NEW, CONFIRM }

/**
 * Gabungan PinLamaPage.dart + PinBaruPage.dart + PinKonfirmasiPage.dart --
 * satu flow composable, padanan pola MasukPinFlow.kt yang sudah ada di project ini.
 */
@Composable
fun ChangeMpinFlow(
    viewModel: ChangeMpinViewModel,
    onBackClick: () -> Unit,
    onNavigateToOtp: () -> Unit,
) {
    var step by remember { mutableStateOf(ChangeMpinStep.OLD) }
    var newPin by remember { mutableStateOf("") }
    var confirmMismatchError by remember { mutableStateOf<String?>(null) }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val loadingOverlay = LocalLoadingOverlay.current

    LaunchedEffect(uiState.isLoading) {
        if (uiState.isLoading) loadingOverlay.show() else loadingOverlay.hide()
    }

    LaunchedEffect(Unit) {
        viewModel.navEvent.collect { event ->
            when (event) {
                is ChangeMpinNavEvent.ToNewPinPage -> step = ChangeMpinStep.NEW
                is ChangeMpinNavEvent.ToOtpPage -> onNavigateToOtp()
                else -> Unit
            }
        }
    }

    when (step) {
        ChangeMpinStep.OLD -> InputPinPage(
            title = "Masukkan M-PIN Lama",
            usePolaHeader = true,
            customHeader = { AppHeader(title = "Masukkan M-PIN Lama", onBackClick = onBackClick) },
            showTopBackground = false,
            onBackClick = onBackClick,
            externalError = uiState.oldMpinError,
            validator = null, // padanan: validasi cuma dari backend, bukan format
            onPinComplete = { pin -> viewModel.validateOldMpin(pin) },
        )

        ChangeMpinStep.NEW -> InputPinPage(
            title = "Masukkan M-PIN Baru Yang Ingin Digunakan",
            usePolaHeader = true,
            centerTitleWithBackButton = true,
            customHeader = { AppHeader(title = "Masukkan M-PIN Baru", onBackClick = { step = ChangeMpinStep.OLD }) },
            showTopBackground = false,
            onBackClick = { step = ChangeMpinStep.OLD },
            validator = { pin -> PinValidator.validateNewPin(pin) },
            onPinComplete = { pin ->
                newPin = pin
                confirmMismatchError = null
                step = ChangeMpinStep.CONFIRM
            },
        )

        ChangeMpinStep.CONFIRM -> InputPinPage(
            title = "Konfirmasi M-PIN Baru Anda",
            usePolaHeader = true,
            centerTitleWithBackButton = true,
            customHeader = { AppHeader(title = "Konfirmasi M-PIN Baru", onBackClick = { step = ChangeMpinStep.NEW }) },
            showTopBackground = false,
            onBackClick = { step = ChangeMpinStep.NEW },
            externalError = confirmMismatchError ?: uiState.confirmMpinError,
            validator = { pin ->
                if (pin != newPin) "M-PIN konfirmasi yang Anda masukkan berbeda" else null
            },
            onPinComplete = { pin -> viewModel.changeMpin(newMpin = pin, confirmMpin = pin) },
        )
    }
}