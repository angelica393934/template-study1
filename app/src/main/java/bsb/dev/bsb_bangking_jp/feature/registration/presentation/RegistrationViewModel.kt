package bsb.dev.bsb_bangking_jp.feature.registration.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bsb.dev.bsb_bangking_jp.core.network.ApiException
import bsb.dev.bsb_bangking_jp.feature.registration.domain.AddIdUserUseCase
import bsb.dev.bsb_bangking_jp.feature.registration.domain.AddPasscodeUseCase
import bsb.dev.bsb_bangking_jp.feature.registration.domain.GetAccountUseCase
import bsb.dev.bsb_bangking_jp.feature.registration.domain.RegistResendOtpUseCase
import bsb.dev.bsb_bangking_jp.feature.registration.domain.RegistVerifyDeviceUseCase
import bsb.dev.bsb_bangking_jp.feature.registration.domain.RegistVerifyOtpUseCase
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

// 🔹 Padanan pengecekan `code == "0611"` / `code == "0612"` di FindAccountPageRegistration.dart.
private const val PHONE_ERROR_CODE = "0611"
private const val ATM_CARD_ERROR_CODE = "0612"

class RegistrationViewModel(
    private val getAccountUseCase: GetAccountUseCase,
    private val resendOtpUseCase: RegistResendOtpUseCase,
    private val verifyOtpUseCase: RegistVerifyOtpUseCase,
    private val verifyDeviceUseCase: RegistVerifyDeviceUseCase,
    private val addIdUserUseCase: AddIdUserUseCase,
    private val addPasscodeUseCase: AddPasscodeUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegistrationUiState())
    val uiState: StateFlow<RegistrationUiState> = _uiState.asStateFlow()

    private val _navEvent = Channel<RegistrationNavEvent>(Channel.BUFFERED)
    val navEvent: Flow<RegistrationNavEvent> = _navEvent.receiveAsFlow()

    private val _uiEvent = MutableSharedFlow<RegistrationUiEvent>(replay = 0)
    val uiEvent: SharedFlow<RegistrationUiEvent> = _uiEvent.asSharedFlow()

    /** 1) GET ACCOUNT -- padanan `_onSubmit()` di FindAccountPageRegistration.dart. */
    fun getAccount(atmCardNo: String, mobileNumber: String) {
        if (atmCardNo.isBlank() || atmCardNo.length < 6) {
            _uiState.update { it.copy(atmCardError = "Masukkan nomor rekening yang valid.") }
            return
        }
        if (!isValidPhone(mobileNumber)) {
            _uiState.update { it.copy(phoneInlineError = "Nomor HP harus 10–15 digit.") }
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    atmCardError = null,
                    phoneInlineError = null,
                    mobileNumber = mobileNumber,
                )
            }

            getAccountUseCase(atmCardNo, mobileNumber)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false) }
                    _navEvent.send(RegistrationNavEvent.ToOtpPage)
                }
                .onFailure { error -> handleGetAccountFailure(error) }
        }
    }

    /** 2) VERIFY OTP -> lanjut VERIFY DEVICE (padanan chaining di RegisterBloc). */
    fun verifyOtp(otp: String) {
        val mobileNumber = _uiState.value.mobileNumber
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, otpErrorMessage = null) }

            verifyOtpUseCase(mobileNumber, otp)
                .onSuccess { challengeToken ->
                    verifyDeviceUseCase(challengeToken, mobileNumber)
                        .onSuccess {
                            _uiState.update { it.copy(isLoading = false) }
                            _navEvent.send(RegistrationNavEvent.ToBuatIdPage)
                        }
                        .onFailure { error ->
                            _uiState.update { it.copy(isLoading = false) }
                            _uiEvent.emit(
                                RegistrationUiEvent.ShowToastError(
                                    error.message ?: "Verifikasi device gagal."
                                )
                            )
                        }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(isLoading = false, otpErrorMessage = error.message ?: "OTP tidak valid.")
                    }
                }
        }
    }

    /** 3) RESEND OTP. */
    suspend fun resendOtp(): Boolean {
        val mobileNumber = _uiState.value.mobileNumber
        _uiState.update { it.copy(isLoading = true, otpErrorMessage = null) }

        val result = resendOtpUseCase(mobileNumber)
        _uiState.update { it.copy(isLoading = false) }

        return result.fold(
            onSuccess = {
                _uiEvent.emit(RegistrationUiEvent.ShowOtpResentToast)
                true
            },
            onFailure = { error ->
                _uiEvent.emit(
                    RegistrationUiEvent.ShowToastError(error.message ?: "Gagal mengirim ulang OTP.")
                )
                false
            },
        )
    }

    /** 4) ADD ID USER. */
    fun addIdUser(userId: String, confirmUserId: String) {
        val mobileNumber = _uiState.value.mobileNumber
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, userIdError = null) }

            addIdUserUseCase(mobileNumber, userId, confirmUserId)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false) }
                    _navEvent.send(RegistrationNavEvent.ToBuatPasswordPage)
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(isLoading = false, userIdError = error.message ?: "Gagal membuat ID Pengguna.")
                    }
                }
        }
    }

    /** 5) ADD PASSCODE. */
    fun addPasscode(passcode: String, confirmPasscode: String) {
        val mobileNumber = _uiState.value.mobileNumber
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, passcodeError = null) }

            addPasscodeUseCase(mobileNumber, passcode, confirmPasscode)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false) }
                    _navEvent.send(RegistrationNavEvent.ToPortal)
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(isLoading = false, passcodeError = error.message ?: "Gagal membuat kata sandi.")
                    }
                }
        }
    }

    fun clearAtmCardError() = _uiState.update { it.copy(atmCardError = null) }
    fun clearPhoneError() = _uiState.update { it.copy(phoneInlineError = null) }
    fun clearUserIdError() = _uiState.update { it.copy(userIdError = null) }
    fun clearPasscodeError() = _uiState.update { it.copy(passcodeError = null) }

    private suspend fun handleGetAccountFailure(error: Throwable) {
        _uiState.update { it.copy(isLoading = false) }
        val respCode = (error as? ApiException)?.respCode
        val message = error.message ?: "Terjadi kesalahan, silakan coba lagi."

        when (respCode) {
            PHONE_ERROR_CODE -> _uiState.update { it.copy(phoneInlineError = message) }
            ATM_CARD_ERROR_CODE -> _uiState.update { it.copy(atmCardError = message) }
            else -> _uiEvent.emit(RegistrationUiEvent.ShowToastError(message))
        }
    }

    private fun isValidPhone(phone: String): Boolean = Regex("^[0-9]{10,15}$").matches(phone)
}