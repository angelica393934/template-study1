package bsb.dev.bsb_bangking_jp.shared.rekening_lainnya.presentation

import bsb.dev.bsb_bangking_jp.core.network.ApiException
import bsb.dev.bsb_bangking_jp.shared.rekening_lainnya.domain.RekeningLainnyaRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RekeningLainnyaViewModel(
    private val repository: RekeningLainnyaRepository,
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    private val _uiState = MutableStateFlow(RekeningLainnyaUiState())
    val uiState: StateFlow<RekeningLainnyaUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<RekeningLainnyaUiEvent>(replay = 0)
    val uiEvent: SharedFlow<RekeningLainnyaUiEvent> = _uiEvent.asSharedFlow()

    fun load(forceRefresh: Boolean = false) {
        scope.launch {
            val hasExisting = _uiState.value.rekeningList != null

            if (hasExisting && forceRefresh) {
                _uiState.update { it.copy(isRefreshing = true, error = null) }
            } else {
                _uiState.update { it.copy(isLoading = true, error = null) }
            }

            try {
                val result = repository.getRekeningLainnya(forceRefresh)
                _uiState.update {
                    it.copy(isLoading = false, isRefreshing = false, rekeningList = result, error = null)
                }
            } catch (e: Exception) {
                if (hasExisting) {
                    _uiState.update { it.copy(isLoading = false, isRefreshing = false) }
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isRefreshing = false,
                            error = (e as? ApiException)?.respMessage ?: e.message ?: "Gagal memuat data Rekening.",
                        )
                    }
                }
            }
        }
    }

    fun reloadFresh() {
        scope.launch {
            _uiState.update {
                it.copy(isLoading = true, isRefreshing = false, rekeningList = null, error = null)
            }
            try {
                val result = repository.getRekeningLainnya(forceRefresh = true)
                _uiState.update {
                    it.copy(isLoading = false, rekeningList = result, error = null)
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        rekeningList = null,
                        error = (e as? ApiException)?.respMessage
                            ?: e.message
                            ?: "Gagal memuat data Rekening.",
                    )
                }
            }
        }
    }

    fun setPrimaryAccount(accountNumber: String) {
        scope.launch {
            _uiState.update { it.copy(isSettingPrimaryAccount = true) }

            repository.setPrimaryAccount(accountNumber)
                .onSuccess {
                    _uiState.update { it.copy(isSettingPrimaryAccount = false) }
                    _uiEvent.emit(RekeningLainnyaUiEvent.ShowToastSuccess("Rekening Utama berhasil dirubah"))
                    load(forceRefresh = true)
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isSettingPrimaryAccount = false) }
                    val message = (error as? ApiException)?.respMessage ?: error.message ?: "Gagal merubah rekening utama."
                    _uiEvent.emit(RekeningLainnyaUiEvent.ShowToastError("Gagal merubah rekening utama. $message"))
                }
        }
    }

    fun clear() {
        _uiState.value = RekeningLainnyaUiState()
    }
}