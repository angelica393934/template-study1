package bsb.dev.bsb_bangking_jp.feature.forget_pw.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bsb.dev.bsb_bangking_jp.core.network.ApiException
import bsb.dev.bsb_bangking_jp.feature.forget_pw.domain.ChangePwUseCase
import bsb.dev.bsb_bangking_jp.feature.forget_pw.domain.ForgetPwResendOtpUseCase
import bsb.dev.bsb_bangking_jp.feature.forget_pw.domain.ForgetPwVerifyOtpUseCase
import bsb.dev.bsb_bangking_jp.feature.forget_pw.domain.GetPwUseCase
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

// 🔹 Padanan pengecekan respCode di LupaPwPage.dart.
private const val ACCOUNT_ERROR_CODE_1 = "0612"
private const val ACCOUNT_ERROR_CODE_2 = "0805"
private const val PHONE_ERROR_CODE = "0824" // 🔹 beda dari forget_iduser (yang pakai "0611")

class ForgetPwViewModel(
    private val getPwUseCase: GetPwUseCase,
    private val verifyOtpUseCase: ForgetPwVerifyOtpUseCase,
    private val resendOtpUseCase: ForgetPwResendOtpUseCase,
    private val changePwUseCase: ChangePwUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ForgetPwUiState())
    val uiState: StateFlow<ForgetPwUiState> = _uiState.asStateFlow()

    private val _navEvent = Channel<ForgetPwNavEvent>(Channel.BUFFERED)
    val navEvent: Flow<ForgetPwNavEvent> = _navEvent.receiveAsFlow()

    private val _uiEvent = MutableSharedFlow<ForgetPwUiEvent>(replay = 0)
    val uiEvent: SharedFlow<ForgetPwUiEvent> = _uiEvent.asSharedFlow()

    /** 1) GET PW -- padanan _onSubmit() di LupaPwPage.dart. */
    fun getPw(atmCardNo: String, mobileNumber: String) {
        val trimmedAccount = atmCardNo.trim()
        val trimmedPhone = mobileNumber.trim()

        val accountError = if (trimmedAccount.isEmpty()) "Account or ATM number cannot be empty" else null
        val phoneError = if (!isValidPhone(trimmedPhone)) "Enter a valid phone number (10–15 digits)" else null

        if (accountError != null || phoneError != null) {
            _uiState.update { it.copy(atmCardError = accountError, phoneError = phoneError) }
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    atmCardError = null,
                    phoneError = null,
                    atmCardNo = trimmedAccount,
                    mobileNumber = trimmedPhone,
                )
            }

            getPwUseCase(trimmedAccount, trimmedPhone)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false) }
                    _navEvent.send(ForgetPwNavEvent.ToOtpPage)
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false) }
                    val respCode = (error as? ApiException)?.respCode
                    val message = error.message ?: "Something went wrong, please try again."

                    when (respCode) {
                        ACCOUNT_ERROR_CODE_1, ACCOUNT_ERROR_CODE_2 -> _uiState.update { it.copy(atmCardError = message) }
                        PHONE_ERROR_CODE -> _uiState.update { it.copy(phoneError = message) }
                        else -> _uiEvent.emit(ForgetPwUiEvent.ShowToastError(message))
                    }
                }
        }
    }

    /** 2) VERIFY OTP. */
    fun verifyOtp(otp: String) {
        val mobileNumber = _uiState.value.mobileNumber
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, otpErrorMessage = null) }

            verifyOtpUseCase(mobileNumber, otp)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false) }
                    _navEvent.send(ForgetPwNavEvent.ToResetPasswordPage)
                }
                .onFailure { error ->
                    val message = error.message ?: "Invalid OTP."
                    _uiState.update { it.copy(isLoading = false, otpErrorMessage = message) }
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
                _uiEvent.emit(ForgetPwUiEvent.ShowOtpResentToast)
                true
            },
            onFailure = { error ->
                _uiEvent.emit(
                    ForgetPwUiEvent.ShowToastError(error.message ?: "Gagal mengirim ulang OTP.")
                )
                false
            },
        )
    }

    /** 4) CHANGE PW -- padanan _submit() di PwAturUlangPage.dart. */
    fun changePw(newPasscode: String, confirmPasscode: String) {
        val mobileNumber = _uiState.value.mobileNumber
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, newPasscodeError = null) }

            changePwUseCase(mobileNumber, newPasscode, confirmPasscode)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false) }
                    _navEvent.send(ForgetPwNavEvent.ToPortalSuccess)
                }
                .onFailure { error ->
                    val message = error.message ?: "Failed to update password."
                    _uiState.update { it.copy(isLoading = false, newPasscodeError = message) }
                }
        }
    }

    fun clearAccountError() = _uiState.update { it.copy(atmCardError = null) }
    fun clearPhoneError() = _uiState.update { it.copy(phoneError = null) }
    fun clearNewPasscodeError() = _uiState.update { it.copy(newPasscodeError = null) }

    private fun isValidPhone(phone: String): Boolean = Regex("^[0-9]{10,15}$").matches(phone)
}