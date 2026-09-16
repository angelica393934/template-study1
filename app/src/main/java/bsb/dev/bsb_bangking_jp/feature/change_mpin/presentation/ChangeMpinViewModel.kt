package bsb.dev.bsb_bangking_jp.feature.change_mpin.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bsb.dev.bsb_bangking_jp.feature.change_mpin.domain.ChangeMpinUseCase
import bsb.dev.bsb_bangking_jp.feature.change_mpin.domain.ValidateOldMpinUseCase
import bsb.dev.bsb_bangking_jp.feature.change_mpin.domain.VerifyOtpChangeMpinUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ChangeMpinViewModel(
    private val validateOldMpinUseCase: ValidateOldMpinUseCase,
    private val changeMpinUseCase: ChangeMpinUseCase,
    private val verifyOtpChangeMpinUseCase: VerifyOtpChangeMpinUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChangeMpinUiState())
    val uiState: StateFlow<ChangeMpinUiState> = _uiState.asStateFlow()

    private val _navEvent = Channel<ChangeMpinNavEvent>(Channel.BUFFERED)
    val navEvent: Flow<ChangeMpinNavEvent> = _navEvent.receiveAsFlow()

    /** 1) VALIDATE OLD MPIN -- padanan PinLamaPage.dart. */
    fun validateOldMpin(oldMpin: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, oldMpinError = null) }

            validateOldMpinUseCase(oldMpin)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false) }
                    _navEvent.send(ChangeMpinNavEvent.ToNewPinPage)
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(isLoading = false, oldMpinError = error.message ?: "Validasi gagal.")
                    }
                }
        }
    }

    /** 2) CHANGE MPIN -- dipanggil dari step konfirmasi PIN, setelah pinBaru == pinKonfirmasi. */
    fun changeMpin(newMpin: String, confirmMpin: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, confirmMpinError = null) }

            changeMpinUseCase(newMpin, confirmMpin)
                .onSuccess { mobileNumber ->
                    _uiState.update { it.copy(isLoading = false, mobileNumber = mobileNumber) }
                    _navEvent.send(ChangeMpinNavEvent.ToOtpPage)
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(isLoading = false, confirmMpinError = error.message ?: "Gagal mengubah M-PIN.")
                    }
                }
        }
    }

    /** 3) VERIFY OTP. */
    fun verifyOtp(otp: String) {
        val mobileNumber = _uiState.value.mobileNumber
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, otpErrorMessage = null) }

            verifyOtpChangeMpinUseCase(mobileNumber, otp)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false) }
                    _navEvent.send(ChangeMpinNavEvent.ToPortalSuccess)
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(isLoading = false, otpErrorMessage = error.message ?: "Verifikasi OTP gagal.")
                    }
                }
        }
    }

    /**
     * Padanan `onResend` di OtpGantiPinPage.dart -- Flutter TIDAK memanggil API apapun
     * di sini, cuma langsung `showSuccessToast`. Dipertahankan apa adanya (no-op API).
     */
    fun clearOtpError() = _uiState.update { it.copy(otpErrorMessage = null) }
}