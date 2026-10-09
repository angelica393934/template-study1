package bsb.dev.bsb_bangking_jp.feature.transfer.saved_recipient.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bsb.dev.bsb_bangking_jp.core.network.ApiException
import bsb.dev.bsb_bangking_jp.feature.transfer.saved_recipient.domain.SavedRecipientRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

class SavedRecipientViewModel(
    private val repository: SavedRecipientRepository,
) : ViewModel() {
    private var loadJob: Job? = null

    private val _uiState = MutableStateFlow(SavedRecipientUiState())
    val uiState: StateFlow<SavedRecipientUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<SavedRecipientUiEvent>(replay = 0)
    val uiEvent: SharedFlow<SavedRecipientUiEvent> = _uiEvent.asSharedFlow()

    init {
        // 🔹 Padanan `context.read<SavedRecipientBloc>().add(getSavedRecipients())` di initState.
        getSavedRecipients()
    }

    fun getSavedRecipients(forceRefresh: Boolean = false) {
        // Sudah ada fetch berjalan dan bukan paksa refresh -> tidak perlu dobel.
        if (!forceRefresh && loadJob?.isActive == true) return
        // Paksa refresh -> batalkan fetch lama supaya hasilnya tidak menimpa data baru.
        if (forceRefresh) loadJob?.cancel()

        loadJob = viewModelScope.launch {
            val hasExisting = _uiState.value.list != null

            if (hasExisting && forceRefresh) {
                _uiState.update { it.copy(isRefreshing = true, error = null) }
            } else {
                _uiState.update { it.copy(isLoading = true, error = null) }
            }

            try {
                val result = repository.getSavedRecipients(forceRefresh)
                _uiState.update {
                    it.copy(isLoading = false, isRefreshing = false, list = result, error = null)
                }
            } catch (e: CancellationException) {
                throw e // jangan ditelan sebagai error
            } catch (e: Exception) {
                val message = (e as? ApiException)?.respMessage
                    ?: e.message
                    ?: "Gagal memuat daftar rekening tersimpan."

                if (hasExisting) {
                    _uiState.update { it.copy(isLoading = false, isRefreshing = false) }
                } else {
                    _uiState.update { it.copy(isLoading = false, isRefreshing = false, error = message) }
                }
            }
        }
    }

    fun updateAlias(id: String, alias: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isUpdating = true, updateError = null) }

            repository.updateSavedRecipient(id, alias)
                .onSuccess {
                    _uiState.update { it.copy(isUpdating = false) }
                    _uiEvent.emit(SavedRecipientUiEvent.AliasUpdated)
                    getSavedRecipients(forceRefresh = true)
                }
                .onFailure { error ->
                    val message = (error as? ApiException)?.respMessage ?: error.message ?: "Gagal mengubah alias."
                    _uiState.update { it.copy(isUpdating = false, updateError = message) }
                    _uiEvent.emit(SavedRecipientUiEvent.ShowToastError(message)) // 🔹 baru
                }
        }
    }

    fun clearUpdateError() {
        _uiState.update { it.copy(updateError = null) }
    }

    fun deleteSavedRecipients(ids: List<String>) {
        viewModelScope.launch {
            _uiState.update { it.copy(isDeleting = true) }

            repository.deleteSavedRecipient(ids)
                .onSuccess {
                    _uiState.update { it.copy(isDeleting = false) }
                    _uiEvent.emit(SavedRecipientUiEvent.ShowToastSuccess("Rekening tersimpan berhasil dihapus"))
                    getSavedRecipients(forceRefresh = true)
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isDeleting = false) }
                    val message = (error as? ApiException)?.respMessage
                        ?: error.message
                        ?: "Gagal menghapus rekening tersimpan."
                    _uiEvent.emit(SavedRecipientUiEvent.ShowToastError(message))
                }
        }
    }
}