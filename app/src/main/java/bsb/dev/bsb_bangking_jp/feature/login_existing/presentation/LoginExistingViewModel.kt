// feature/login_existing/presentation/LoginExistingViewModel.kt
package bsb.dev.bsb_bangking_jp.feature.login_existing.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bsb.dev.bsb_bangking_jp.core.network.ApiException
import bsb.dev.bsb_bangking_jp.feature.login_existing.domain.ConfirmMpinUseCase
import bsb.dev.bsb_bangking_jp.feature.login_existing.domain.LoginInitUseCase
import bsb.dev.bsb_bangking_jp.feature.login_existing.domain.ResendOtpUseCase
import bsb.dev.bsb_bangking_jp.feature.login_existing.domain.VerifyDeviceUseCase
import bsb.dev.bsb_bangking_jp.feature.login_existing.domain.VerifyOtpUseCase
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

private const val INLINE_ERROR_CODE = "0626"

// 🔹 Dipindahkan ke sini (satu file dengan pemakainya), supaya "private" berlaku benar.
private enum class FailureContext { PHONE, OTP, CONFIRM_PIN }

class LoginExistingViewModel(
    private val loginInitUseCase: LoginInitUseCase,
    private val verifyOtpUseCase: VerifyOtpUseCase,
    private val resendOtpUseCase: ResendOtpUseCase,
    private val verifyDeviceUseCase: VerifyDeviceUseCase,
    private val confirmMpinUseCase: ConfirmMpinUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginExistingUiState())
    val uiState: StateFlow<LoginExistingUiState> = _uiState.asStateFlow()

    private val _navEvent = Channel<LoginExistingNavEvent>(Channel.BUFFERED)
    val navEvent: Flow<LoginExistingNavEvent> = _navEvent.receiveAsFlow()

    private val _uiEvent = MutableSharedFlow<LoginExistingUiEvent>(replay = 0)
    val uiEvent: SharedFlow<LoginExistingUiEvent> = _uiEvent.asSharedFlow()

    fun loginInit(phoneNumber: String) {
        if (!isValidPhone(phoneNumber)) {
            _uiState.update { it.copy(phoneInlineError = "Nomor HP harus 10–15 digit.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, phoneInlineError = null, phoneNumber = phoneNumber) }

            loginInitUseCase(phoneNumber)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false) }
                    _navEvent.send(LoginExistingNavEvent.ToOtpPage)
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false) }
                    handleFailure(error, FailureContext.PHONE)
                }
        }
    }

    fun verifyOtp(otp: String) {
        val phoneNumber = _uiState.value.phoneNumber
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, otpErrorMessage = null) }

            verifyOtpUseCase(phoneNumber, otp)
                .onSuccess { challengeToken ->
                    verifyDeviceUseCase(challengeToken, phoneNumber)
                        .onSuccess {
                            _uiState.update { it.copy(isLoading = false) }
                            _navEvent.send(LoginExistingNavEvent.ToPinPage)
                        }
                        .onFailure { error ->
                            _uiState.update { it.copy(isLoading = false) }
                            handleFailure(error, FailureContext.OTP)
                        }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false) }
                    handleFailure(error, FailureContext.OTP)
                }
        }
    }

    /** 🔹 Suspend + return Boolean -- dipanggil dari OtpForm, yang baru restart countdown kalau true. */
    suspend fun resendOtp(): Boolean {
        val phoneNumber = _uiState.value.phoneNumber
            _uiState.update { it.copy(isLoading = true, otpErrorMessage = null) }

        val result = resendOtpUseCase(phoneNumber)
        _uiState.update { it.copy(isLoading = false) }

        return result.fold(
            onSuccess = {
                _uiEvent.emit(LoginExistingUiEvent.ShowOtpResentToast)
                true
            },
            onFailure = { error ->
                handleFailure(error, FailureContext.OTP)
                false
            },
        )
    }

    fun confirmMpin(pin: String) {
        val phoneNumber = _uiState.value.phoneNumber
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, confirmPinError = null) }

            confirmMpinUseCase(phoneNumber, pin)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false) }
                    _navEvent.send(LoginExistingNavEvent.ToPortal)
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false) }
                    handleFailure(error, FailureContext.CONFIRM_PIN)
                }
        }
    }

    fun clearPhoneError() {
        _uiState.update { it.copy(phoneInlineError = null) }
    }

    private suspend fun handleFailure(error: Throwable, context: FailureContext) {
        val message = error.message ?: "Terjadi kesalahan, silakan coba lagi."
        val respCode = (error as? ApiException)?.respCode

        if (respCode == INLINE_ERROR_CODE && context == FailureContext.PHONE) {
            _uiState.update { it.copy(phoneInlineError = message) }
            return
        }

        when (context) {
            FailureContext.PHONE -> _uiEvent.emit(LoginExistingUiEvent.ShowToastError(message))
            FailureContext.OTP -> _uiState.update { it.copy(otpErrorMessage = message) }
            FailureContext.CONFIRM_PIN -> _uiState.update { it.copy(confirmPinError = message) }
        }

        if (context != FailureContext.PHONE) {
            _uiEvent.emit(LoginExistingUiEvent.ShowToastError(message))
        }
    }

    private fun isValidPhone(phone: String): Boolean = Regex("^[0-9]{10,15}$").matches(phone)
}