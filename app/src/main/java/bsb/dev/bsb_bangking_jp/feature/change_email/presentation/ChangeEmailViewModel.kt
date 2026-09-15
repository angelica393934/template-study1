package bsb.dev.bsb_bangking_jp.feature.change_email.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bsb.dev.bsb_bangking_jp.feature.change_email.domain.ConfirmChangeEmailUseCase
import bsb.dev.bsb_bangking_jp.feature.change_email.domain.ChangeEmailUseCase
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

class ChangeEmailViewModel(
    private val changeEmailUseCase: ChangeEmailUseCase,
    private val confirmChangeEmailUseCase: ConfirmChangeEmailUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChangeEmailUiState())
    val uiState: StateFlow<ChangeEmailUiState> = _uiState.asStateFlow()

    private val _navEvent = Channel<ChangeEmailNavEvent>(Channel.BUFFERED)
    val navEvent: Flow<ChangeEmailNavEvent> = _navEvent.receiveAsFlow()

    private val _uiEvent = MutableSharedFlow<ChangeEmailUiEvent>(replay = 0)
    val uiEvent: SharedFlow<ChangeEmailUiEvent> = _uiEvent.asSharedFlow()

    /** 1) GANTI EMAIL */
    fun changeEmail(newEmail: String) {
        val trimmed = newEmail.trim()
        val error = validateEmail(trimmed)
        if (error != null) {
            _uiState.update { it.copy(emailInlineError = error) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, emailInlineError = null, newEmail = trimmed) }

            changeEmailUseCase(trimmed)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false) }
                    _navEvent.send(ChangeEmailNavEvent.ToPinPage)
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false) }
                    _uiEvent.emit(
                        ChangeEmailUiEvent.ShowToastError(error.message ?: "Gagal mengubah alamat email.")
                    )
                }
        }
    }

    /**
     * 2) CONFIRM EMAIL -- dipanggil LANGSUNG dari `validator` InputPinPage, padanan
     * pola `validator: (pin) async { ... await bloc.stream ... }` di ChangeEmailPage.dart:
     * hasil error dikembalikan sebagai String supaya tampil inline di halaman PIN,
     * dan `onPinComplete` HANYA terpanggil kalau ini me-return null (sukses).
     */
    suspend fun confirmEmail(pin: String): String? {
        _uiState.update { it.copy(isLoading = true) }
        val result = confirmChangeEmailUseCase(pin)
        _uiState.update { it.copy(isLoading = false) }

        return result.fold(
            onSuccess = { null },
            onFailure = { error -> error.message ?: "Konfirmasi email gagal." },
        )
    }

    fun clearEmailError() {
        _uiState.update { it.copy(emailInlineError = null) }
    }

    private fun validateEmail(email: String): String? {
        if (email.isEmpty()) return "Email tidak boleh kosong"
        if (!Regex("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$").matches(email)) return "Format email tidak valid"
        return null
    }
}