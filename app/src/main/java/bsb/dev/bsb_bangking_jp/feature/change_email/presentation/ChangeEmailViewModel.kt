package bsb.dev.bsb_bangking_jp.feature.ganti_email.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bsb.dev.bsb_bangking_jp.feature.ganti_email.domain.ConfirmGantiEmailUseCase
import bsb.dev.bsb_bangking_jp.feature.ganti_email.domain.GantiEmailUseCase
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

class GantiEmailViewModel(
    private val gantiEmailUseCase: GantiEmailUseCase,
    private val confirmGantiEmailUseCase: ConfirmGantiEmailUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(GantiEmailUiState())
    val uiState: StateFlow<GantiEmailUiState> = _uiState.asStateFlow()

    private val _navEvent = Channel<GantiEmailNavEvent>(Channel.BUFFERED)
    val navEvent: Flow<GantiEmailNavEvent> = _navEvent.receiveAsFlow()

    private val _uiEvent = MutableSharedFlow<GantiEmailUiEvent>(replay = 0)
    val uiEvent: SharedFlow<GantiEmailUiEvent> = _uiEvent.asSharedFlow()

    /** 1) GANTI EMAIL -- padanan GantiEmailEvent.gantiEmail() di GantiEmailPage.dart. */
    fun gantiEmail(newEmail: String) {
        val trimmed = newEmail.trim()
        val error = validateEmail(trimmed)
        if (error != null) {
            _uiState.update { it.copy(emailInlineError = error) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, emailInlineError = null, newEmail = trimmed) }

            gantiEmailUseCase(trimmed)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false) }
                    _navEvent.send(GantiEmailNavEvent.ToPinPage)
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false) }
                    _uiEvent.emit(
                        GantiEmailUiEvent.ShowToastError(error.message ?: "Gagal mengubah alamat email.")
                    )
                }
        }
    }

    /**
     * 2) CONFIRM EMAIL -- dipanggil LANGSUNG dari `validator` InputPinPage, padanan
     * pola `validator: (pin) async { ... await bloc.stream ... }` di GantiEmailPage.dart:
     * hasil error dikembalikan sebagai String supaya tampil inline di halaman PIN,
     * dan `onPinComplete` HANYA terpanggil kalau ini me-return null (sukses).
     */
    suspend fun confirmEmail(pin: String): String? {
        _uiState.update { it.copy(isLoading = true) }
        val result = confirmGantiEmailUseCase(pin)
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