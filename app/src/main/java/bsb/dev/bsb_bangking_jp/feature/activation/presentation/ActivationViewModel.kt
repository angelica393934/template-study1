package bsb.dev.bsb_bangking_jp.feature.activation.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bsb.dev.bsb_bangking_jp.core.network.ApiException
import bsb.dev.bsb_bangking_jp.feature.activation.domain.ActivationResendOtpUseCase
import bsb.dev.bsb_bangking_jp.feature.activation.domain.ActivationVerifyOtpUseCase
import bsb.dev.bsb_bangking_jp.feature.activation.domain.ConfirmMpinActivationUseCase
import bsb.dev.bsb_bangking_jp.feature.activation.domain.GetAccountActivationUseCase
import bsb.dev.bsb_bangking_jp.feature.activation.domain.ValidatePasscodeUseCase
import bsb.dev.bsb_bangking_jp.feature.activation.domain.ValidateUserIdUseCase
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

// 🔹 Padanan Flutter: 3 halaman awal (Rekening/ATM, ID Pengguna, Kata Sandi) SAMA-SAMA
// memeriksa respCode "0612" untuk error inline field masing-masing.
private const val INLINE_ERROR_CODE = "0612"

private enum class FailureContext { ATM_CARD, USER_ID, OTP, PASSCODE, CONFIRM_PIN }

class ActivationViewModel(
    private val getAccountActivationUseCase: GetAccountActivationUseCase,
    private val validateUserIdUseCase: ValidateUserIdUseCase,
    private val resendOtpUseCase: ActivationResendOtpUseCase,
    private val verifyOtpUseCase: ActivationVerifyOtpUseCase,
    private val validatePasscodeUseCase: ValidatePasscodeUseCase,
    private val confirmMpinUseCase: ConfirmMpinActivationUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ActivationUiState())
    val uiState: StateFlow<ActivationUiState> = _uiState.asStateFlow()

    private val _navEvent = Channel<ActivationNavEvent>(Channel.BUFFERED)
    val navEvent: Flow<ActivationNavEvent> = _navEvent.receiveAsFlow()

    private val _uiEvent = MutableSharedFlow<ActivationUiEvent>(replay = 0)
    val uiEvent: SharedFlow<ActivationUiEvent> = _uiEvent.asSharedFlow()

    /** 1) GET ACCOUNT ACTIVATION -- padanan _onSubmit() di AktivasiAkunRegistrasiPage. */
    fun getAccountActivation(atmCardNo: String) {
        if (atmCardNo.isBlank() || atmCardNo.length < 6) {
            _uiState.update { it.copy(atmCardError = "Masukkan nomor rekening yang valid.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, atmCardError = null, atmCardNo = atmCardNo) }

            getAccountActivationUseCase(atmCardNo)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false) }
                    _navEvent.send(ActivationNavEvent.ToIdPage)
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false) }
                    handleFailure(error, FailureContext.ATM_CARD)
                }
        }
    }

    /** 2) VALIDATE USER ID -- padanan _onSubmit() di AktivasiIdAkunRegistrasiPage. */
    fun validateUserId(userId: String) {
        if (userId.isBlank() || userId.length < 4) {
            _uiState.update { it.copy(userIdError = "ID Pengguna tidak valid.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, userIdError = null) }

            validateUserIdUseCase(userId)
                .onSuccess { mobileNumber ->
                    _uiState.update { it.copy(isLoading = false, mobileNumber = mobileNumber) }
                    _navEvent.send(ActivationNavEvent.ToOtpPage)
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false) }
                    handleFailure(error, FailureContext.USER_ID)
                }
        }
    }

    /** 3) VERIFY OTP -- sukses langsung simpan access/refresh token activation. */
    fun verifyOtp(otp: String) {
        val mobileNumber = _uiState.value.mobileNumber
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, otpErrorMessage = null) }

            verifyOtpUseCase(mobileNumber, otp)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false) }
                    _navEvent.send(ActivationNavEvent.ToPasswordPage)
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false) }
                    handleFailure(error, FailureContext.OTP)
                }
        }
    }

    /** 3b) RESEND OTP. */
    suspend fun resendOtp(): Boolean {
        val mobileNumber = _uiState.value.mobileNumber
        _uiState.update { it.copy(isLoading = true, otpErrorMessage = null) }

        val result = resendOtpUseCase(mobileNumber)
        _uiState.update { it.copy(isLoading = false) }

        return result.fold(
            onSuccess = {
                _uiEvent.emit(ActivationUiEvent.ShowOtpResentToast)
                true
            },
            onFailure = { error ->
                _uiEvent.emit(
                    ActivationUiEvent.ShowToastError(error.message ?: "Gagal mengirim ulang OTP.")
                )
                false
            },
        )
    }

    /** 4) VALIDATE PASSCODE. */
    fun validatePasscode(passcode: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, passcodeError = null) }

            validatePasscodeUseCase(passcode)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false) }
                    _navEvent.send(ActivationNavEvent.ToPinPage)
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false) }
                    handleFailure(error, FailureContext.PASSCODE)
                }
        }
    }

    /** 5) CONFIRM MPIN -- padanan ActivationEvent.confirmMpinActivation di KonfirmasiPinAktivasiPage. */
    fun confirmMpin(pin: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, confirmPinError = null) }

            confirmMpinUseCase(pin)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false) }
                    _navEvent.send(ActivationNavEvent.ToPortal)
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false) }
                    handleFailure(error, FailureContext.CONFIRM_PIN)
                }
        }
    }

    fun clearAtmCardError() = _uiState.update { it.copy(atmCardError = null) }
    fun clearUserIdError() = _uiState.update { it.copy(userIdError = null) }

    private suspend fun handleFailure(error: Throwable, context: FailureContext) {
        val message = error.message ?: "Terjadi kesalahan, silakan coba lagi."
        val respCode = (error as? ApiException)?.respCode

        when (context) {
            FailureContext.ATM_CARD -> {
                if (respCode == INLINE_ERROR_CODE) {
                    _uiState.update { it.copy(atmCardError = message) }
                } else {
                    _uiEvent.emit(ActivationUiEvent.ShowToastError(message))
                }
            }
            FailureContext.USER_ID -> {
                if (respCode == INLINE_ERROR_CODE) {
                    _uiState.update { it.copy(userIdError = message) }
                } else {
                    _uiEvent.emit(ActivationUiEvent.ShowToastError(message))
                }
            }
            FailureContext.PASSCODE -> {
                if (respCode == INLINE_ERROR_CODE) {
                    _uiState.update { it.copy(passcodeError = message) }
                } else {
                    _uiEvent.emit(ActivationUiEvent.ShowToastError(message))
                }
            }
            FailureContext.OTP -> {
                _uiState.update { it.copy(otpErrorMessage = message) }
                _uiEvent.emit(ActivationUiEvent.ShowToastError(message))
            }
            FailureContext.CONFIRM_PIN -> {
                _uiState.update { it.copy(confirmPinError = message) }
                _uiEvent.emit(ActivationUiEvent.ShowToastError(message))
            }
        }
    }
}