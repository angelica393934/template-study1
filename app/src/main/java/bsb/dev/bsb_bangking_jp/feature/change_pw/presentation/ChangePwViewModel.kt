package bsb.dev.bsb_bangking_jp.feature.change_pw.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bsb.dev.bsb_bangking_jp.feature.change_pw.domain.ChangePwUseCase
import bsb.dev.bsb_bangking_jp.feature.change_pw.domain.ResendOtpChangePwUseCase
import bsb.dev.bsb_bangking_jp.feature.change_pw.domain.ValidateOldPwUseCase
import bsb.dev.bsb_bangking_jp.feature.change_pw.domain.VerifyOtpChangePwUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ChangePwViewModel(
    private val validateOldPwUseCase: ValidateOldPwUseCase,
    private val changePwUseCase: ChangePwUseCase,
    private val verifyOtpChangePwUseCase: VerifyOtpChangePwUseCase,
    private val resendOtpChangePwUseCase: ResendOtpChangePwUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChangePwUiState())
    val uiState: StateFlow<ChangePwUiState> = _uiState.asStateFlow()

    private val _navEvent = Channel<ChangePwNavEvent>(Channel.BUFFERED)
    val navEvent: Flow<ChangePwNavEvent> = _navEvent.receiveAsFlow()

    private val _uiEvent = MutableSharedFlow<ChangePwUiEvent>(replay = 0)
    val uiEvent: SharedFlow<ChangePwUiEvent> = _uiEvent.asSharedFlow()

    /** 1) VALIDATE OLD PASSWORD -- padanan UbahKataSandiLamaPage.dart. */
    fun validateOldPw(oldPasscode: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, oldPasscodeError = null) }

            validateOldPwUseCase(oldPasscode)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false) }
                    _navEvent.send(ChangePwNavEvent.ToNewPasswordPage)
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(isLoading = false, oldPasscodeError = error.message ?: "Validasi gagal.")
                    }
                }
        }
    }

    fun clearOldPasscodeError() = _uiState.update { it.copy(oldPasscodeError = null) }

    /** 2) CHANGE PASSWORD -- sukses simpan mobileNumber, lanjut ke halaman OTP. */
    fun changePw(newPasscode: String, confirmPasscode: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, newPasscodeError = null) }

            changePwUseCase(newPasscode, confirmPasscode)
                .onSuccess { mobileNumber ->
                    _uiState.update { it.copy(isLoading = false, mobileNumber = mobileNumber) }
                    _navEvent.send(ChangePwNavEvent.ToOtpPage)
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(isLoading = false, newPasscodeError = error.message ?: "Gagal mengubah kata sandi.")
                    }
                }
        }
    }

    fun clearNewPasscodeError() = _uiState.update { it.copy(newPasscodeError = null) }

    /** 3) VERIFY OTP. */
    fun verifyOtp(otp: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, otpErrorMessage = null) }

            verifyOtpChangePwUseCase(otp)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false) }
                    _navEvent.send(ChangePwNavEvent.ToPortalSuccess)
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(isLoading = false, otpErrorMessage = error.message ?: "Verifikasi OTP gagal.")
                    }
                }
        }
    }

    /** 4) RESEND OTP -- suspend + Boolean, dipanggil dari OtpForm.onResend. */
    suspend fun resendOtp(): Boolean {
        val mobileNumber = _uiState.value.mobileNumber
        _uiState.update { it.copy(isLoading = true, otpErrorMessage = null) }

        val result = resendOtpChangePwUseCase(mobileNumber)
        _uiState.update { it.copy(isLoading = false) }

        return result.fold(
            onSuccess = {
                _uiEvent.emit(ChangePwUiEvent.ShowOtpResentToast)
                true
            },
            onFailure = { error ->
                _uiEvent.emit(ChangePwUiEvent.ShowToastError(error.message ?: "Gagal mengirim ulang OTP."))
                false
            },
        )
    }
}