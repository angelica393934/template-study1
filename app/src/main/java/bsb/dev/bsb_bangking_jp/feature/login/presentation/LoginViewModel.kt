package bsb.dev.bsb_bangking_jp.feature.login.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bsb.dev.bsb_bangking_jp.core.device.AppPreferences
import bsb.dev.bsb_bangking_jp.core.network.ApiException
import bsb.dev.bsb_bangking_jp.feature.login.domain.LoginRepository
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

// Padanan respCode di listener LoginSheet -mu
private const val PASSCODE_ERROR_CODE = "0601"
private const val USERID_ERROR_CODE = "0610"

class LoginViewModel(
    private val repository: LoginRepository,
    private val appPreferences: AppPreferences,
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        LoginUiState(
            rememberMe = appPreferences.isRememberMeEnabled(),
            useridLogin = appPreferences.getRememberedUserId().orEmpty(),
        )
    )
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val _navEvent = Channel<LoginNavEvent>(Channel.BUFFERED)
    val navEvent: Flow<LoginNavEvent> = _navEvent.receiveAsFlow()

    private val _uiEvent = MutableSharedFlow<LoginUiEvent>(replay = 0)
    val uiEvent: SharedFlow<LoginUiEvent> = _uiEvent.asSharedFlow()

    fun onRememberMeChanged(value: Boolean) {
        _uiState.update { it.copy(rememberMe = value) }
    }

    fun clearUseridError() {
        _uiState.update { it.copy(useridError = null) }
    }

    fun clearPasscodeError() {
        _uiState.update { it.copy(passcodeError = null) }
    }

    /** Padanan `_validateAndLogin`. */
    fun login(useridLogin: String, passcode: String) {
        viewModelScope.launch {
            // 1. Cek isLoginAllowed dulu sebelum hit API.
            if (!appPreferences.isLoginAllowed()) {
                _uiEvent.emit(
                    LoginUiEvent.ShowToastError(
                        "Anda belum melakukan aktivasi Akun.\nLakukan aktivasi terlebih dahulu untuk masuk."
                    )
                )
                return@launch
            }

            // 2. Validasi field kosong.
            val useridEmptyError = if (useridLogin.isBlank()) "ID Pengguna wajib diisi" else null
            val passcodeEmptyError = if (passcode.isBlank()) "Kata Sandi wajib diisi" else null

            if (useridEmptyError != null || passcodeEmptyError != null) {
                _uiState.update {
                    it.copy(useridError = useridEmptyError, passcodeError = passcodeEmptyError)
                }
                return@launch
            }

            _uiState.update {
                it.copy(isLoading = true, useridError = null, passcodeError = null, useridLogin = useridLogin)
            }

            repository.login(useridLogin, passcode)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false) }

                    if (_uiState.value.rememberMe) {
                        appPreferences.saveRememberedLogin(useridLogin)
                    } else {
                        appPreferences.clearRememberedLogin()
                    }

                    _navEvent.send(LoginNavEvent.ToNavbar)
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false) }
                    handleFailure(error)
                }
        }
    }

    private suspend fun handleFailure(error: Throwable) {
        val message = error.message ?: "Login gagal, silakan coba lagi."
        val respCode = (error as? ApiException)?.respCode

        when (respCode) {
            PASSCODE_ERROR_CODE -> _uiState.update { it.copy(passcodeError = message) }
            USERID_ERROR_CODE -> _uiState.update { it.copy(useridError = message) }
            else -> _uiEvent.emit(LoginUiEvent.ShowToastError(message))
        }
    }
}