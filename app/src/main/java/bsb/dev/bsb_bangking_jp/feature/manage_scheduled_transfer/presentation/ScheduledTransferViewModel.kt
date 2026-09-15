package bsb.dev.bsb_bangking_jp.feature.manage_scheduled_transfer.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bsb.dev.bsb_bangking_jp.core.network.ApiException
import bsb.dev.bsb_bangking_jp.feature.manage_scheduled_transfer.domain.ScheduledTransferRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Padanan ScheduledTransferBloc (Dart) -- satu ViewModel menangani list & detail,
 * di-scope ke nav graph "manage_scheduled_transfer" supaya instance sama dipakai
 * List page <-> Detail page (padanan `context.read<ScheduledTransferBloc>()` yang sama).
 */
class ScheduledTransferViewModel(
    private val repository: ScheduledTransferRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<ScheduledTransferUiState>(ScheduledTransferUiState.Initial)
    val uiState: StateFlow<ScheduledTransferUiState> = _uiState.asStateFlow()

    /** 1) GET LIST -- padanan `_GetList`. */
    fun getList(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            _uiState.update { ScheduledTransferUiState.Loading }
            try {
                val items = repository.getScheduledTransfers(forceRefresh)
                _uiState.update { ScheduledTransferUiState.ListSuccess(items) }
            } catch (e: Exception) {
                _uiState.update { failureFrom(e) }
            }
        }
    }

    /** 2) GET DETAIL -- padanan `_GetDetail`. */
    fun getDetail(id: Int, forceRefresh: Boolean = false) {
        viewModelScope.launch {
            _uiState.update { ScheduledTransferUiState.Loading }
            try {
                val detail = repository.getScheduledTransferDetail(id, forceRefresh)
                _uiState.update { ScheduledTransferUiState.DetailSuccess(detail) }
            } catch (e: Exception) {
                _uiState.update { failureFrom(e) }
            }
        }
    }

    /** 3) TOGGLE PAUSE -- padanan `_TogglePause`. */
    fun togglePause(id: Int) {
        viewModelScope.launch {
            _uiState.update { ScheduledTransferUiState.Loading }
            repository.togglePause(id)
                .onSuccess {
                    _uiState.update { ScheduledTransferUiState.ActionSuccess("toggle") }
                }
                .onFailure { error ->
                    _uiState.update { failureFrom(error) }
                }
        }
    }

    /** 4) DELETE -- padanan `_Delete`. */
    fun delete(ids: List<Int>) {
        viewModelScope.launch {
            _uiState.update { ScheduledTransferUiState.Loading }
            repository.deleteScheduledTransfer(ids)
                .onSuccess {
                    _uiState.update { ScheduledTransferUiState.ActionSuccess("delete") }
                }
                .onFailure { error ->
                    _uiState.update { failureFrom(error) }
                }
        }
    }

    /** Padanan `_Reset`. */
    fun reset() {
        _uiState.update { ScheduledTransferUiState.Initial }
    }

    private fun failureFrom(error: Throwable): ScheduledTransferUiState.Failure {
        val respCode = (error as? ApiException)?.respCode
        val message = error.message ?: "Terjadi kesalahan, silakan coba lagi."
        return ScheduledTransferUiState.Failure(respCode, message)
    }
}