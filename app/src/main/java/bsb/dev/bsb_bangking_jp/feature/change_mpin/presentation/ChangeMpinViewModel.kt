package bsb.dev.bsb_bangking_jp.feature.change_mpin.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bsb.dev.bsb_bangking_jp.feature.change_mpin.domain.ChangeMpinRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ChangeMpinViewModel(
    private val repository: ChangeMpinRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChangeMpinUiState())
    val uiState: StateFlow<ChangeMpinUiState> = _uiState.asStateFlow()

    private val _navEvent = Channel<ChangeMpinNavEvent>(Channel.BUFFERED)
    val navEvent: Flow<ChangeMpinNavEvent> = _navEvent.receiveAsFlow()

    /** 1) VALIDATE OLD MPIN -- padanan PinLamaPage.dart. */
    fun validateOldMpin(oldMpin: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, oldMpinError = null) }

            repository.validateOldMpin(oldMpin)
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

            repository.changeMpin(newMpin, confirmMpin)
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

            repository.verifyOtpChangeMpin(mobileNumber, otp)
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
}