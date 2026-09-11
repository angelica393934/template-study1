package bsb.dev.bsb_bangking_jp.feature.forget_iduser.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bsb.dev.bsb_bangking_jp.core.network.ApiException
import bsb.dev.bsb_bangking_jp.feature.forget_iduser.domain.ChangeIdUserUseCase
import bsb.dev.bsb_bangking_jp.feature.forget_iduser.domain.ForgetIdResendOtpUseCase
import bsb.dev.bsb_bangking_jp.feature.forget_iduser.domain.ForgetIdVerifyOtpUseCase
import bsb.dev.bsb_bangking_jp.feature.forget_iduser.domain.GetIdUserUseCase
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

// 🔹 Padanan pengecekan respCode di LupaIdPage.dart.
private const val ACCOUNT_ERROR_CODE_1 = "0612"
private const val ACCOUNT_ERROR_CODE_2 = "0805"
private const val PHONE_ERROR_CODE = "0611"

class ForgetIdUserViewModel(
    private val getIdUserUseCase: GetIdUserUseCase,
    private val verifyOtpUseCase: ForgetIdVerifyOtpUseCase,
    private val resendOtpUseCase: ForgetIdResendOtpUseCase,
    private val changeIdUserUseCase: ChangeIdUserUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ForgetIdUserUiState())
    val uiState: StateFlow<ForgetIdUserUiState> = _uiState.asStateFlow()

    private val _navEvent = Channel<ForgetIdUserNavEvent>(Channel.BUFFERED)
    val navEvent: Flow<ForgetIdUserNavEvent> = _navEvent.receiveAsFlow()

    private val _uiEvent = MutableSharedFlow<ForgetIdUserUiEvent>(replay = 0)
    val uiEvent: SharedFlow<ForgetIdUserUiEvent> = _uiEvent.asSharedFlow()

    /** 1) GET ID USER -- padanan _onSubmit() di LupaIdPage.dart. */
    fun getIdUser(atmCardNo: String, mobileNumber: String) {
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

            getIdUserUseCase(trimmedAccount, trimmedPhone)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false) }
                    _navEvent.send(ForgetIdUserNavEvent.ToOtpPage)
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false) }
                    val respCode = (error as? ApiException)?.respCode
                    val message = error.message ?: "Something went wrong, please try again."

                    when (respCode) {
                        ACCOUNT_ERROR_CODE_1, ACCOUNT_ERROR_CODE_2 -> _uiState.update { it.copy(atmCardError = message) }
                        PHONE_ERROR_CODE -> _uiState.update { it.copy(phoneError = message) }
                        else -> _uiEvent.emit(ForgetIdUserUiEvent.ShowToastError(message))
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
                    _navEvent.send(ForgetIdUserNavEvent.ToResetIdPage)
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
                    _uiEvent.emit(ForgetIdUserUiEvent.ShowOtpResentToast)
                    true
                 },
                onFailure = { error ->
                    _uiEvent.emit(
                        ForgetIdUserUiEvent.ShowToastError(error.message ?: "Gagal mengirim ulang OTP.")
                    )
                    false
                },
        )
    }

    /** 4) CHANGE ID USER -- padanan _submitChangeIdUser() di IdAturUlangPage.dart. */
    fun changeIdUser(newUserId: String, confirmUserId: String) {
        val mobileNumber = _uiState.value.mobileNumber
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, newUserIdError = null) }

            changeIdUserUseCase(mobileNumber, newUserId, confirmUserId)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false) }
                    _navEvent.send(ForgetIdUserNavEvent.ToPortalSuccess)
                }
                .onFailure { error ->
                    val message = error.message ?: "Failed to update User ID."
                    _uiState.update { it.copy(isLoading = false, newUserIdError = message) }
                }
        }
    }

    fun clearAccountError() = _uiState.update { it.copy(atmCardError = null) }
    fun clearPhoneError() = _uiState.update { it.copy(phoneError = null) }
    fun clearNewUserIdError() = _uiState.update { it.copy(newUserIdError = null) }

    private fun isValidPhone(phone: String): Boolean = Regex("^[0-9]{10,15}$").matches(phone)
}