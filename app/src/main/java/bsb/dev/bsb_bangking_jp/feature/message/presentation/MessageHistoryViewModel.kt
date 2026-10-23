package bsb.dev.bsb_bangking_jp.feature.message.presentation

import bsb.dev.bsb_bangking_jp.core.filter.TransactionFilterPayload
import bsb.dev.bsb_bangking_jp.core.network.ApiException
import bsb.dev.bsb_bangking_jp.core.util.DefaultRangeDate
import bsb.dev.bsb_bangking_jp.feature.message.domain.MessageHistoryRepository
import bsb.dev.bsb_bangking_jp.shared.rekening_lainnya.presentation.RekeningLainnyaViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Koin `single` (bukan `viewModel`) -- sama seperti ActivityHistoryViewModel, supaya
 * fetch message tidak tergantung apakah messagePage sedang di-compose oleh Navbar atau tidak.
 */
class MessageHistoryViewModel(
    private val repository: MessageHistoryRepository,
    private val rekeningViewModel: RekeningLainnyaViewModel,
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    private val _uiState = MutableStateFlow(MessageHistoryUiState())
    val uiState: StateFlow<MessageHistoryUiState> = _uiState.asStateFlow()

    init {
        observeRekeningUntukAutoLoad()
    }

    private fun observeRekeningUntukAutoLoad() {
        scope.launch {
            rekeningViewModel.uiState
                .map { state ->
                    val list = state.rekeningList
                    if (list.isNullOrEmpty()) null
                    else (list.firstOrNull { it.isPrimary } ?: list.first()).number
                }
                .distinctUntilChanged()
                .collect { primaryNumber ->
                    if (primaryNumber == null) return@collect
                    if (_uiState.value.accountNumber == primaryNumber) return@collect
                    getInitial(primaryNumber)
                }
        }
    }

    fun getInitial(accountNumber: String) {
        load(accountNumber, TransactionFilterPayload.initial())
    }

//    fun switchAccount(accountNumber: String) {
//        if (_uiState.value.accountNumber == accountNumber) return
//        getInitial(accountNumber)
//    }

    fun applyFilter(filter: TransactionFilterPayload) {
        val accountNumber = _uiState.value.accountNumber ?: return
        load(accountNumber, normalizeFilter(filter))
    }

    fun refresh() {
        val accountNumber = _uiState.value.accountNumber ?: return
        val filter = _uiState.value.activeFilter ?: return
        load(accountNumber, filter)
    }

    fun loadMore() {
        val state = _uiState.value
        val accountNumber = state.accountNumber ?: return
        val filter = state.activeFilter ?: return
        if (state.isLoadMore || !repository.hasMore) return

        scope.launch {
            _uiState.update { it.copy(isLoadMore = true) }
            try {
                val items = repository.loadMore(accountNumber, filter)
                _uiState.update { it.copy(isLoadMore = false, items = items, hasMore = repository.hasMore) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoadMore = false, error = errorMessage(e)) }
            }
        }
    }

    private fun load(accountNumber: String, filter: TransactionFilterPayload) {
        scope.launch {
            _uiState.update {
                it.copy(
                    accountNumber = accountNumber,
                    isLoading = true,
                    isLoadMore = false,
                    activeFilter = filter,
                    error = null,
                )
            }
            try {
                val items = repository.loadInitial(accountNumber, filter)
                _uiState.update { it.copy(isLoading = false, items = items, hasMore = repository.hasMore) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = errorMessage(e)) }
            }
        }
    }

    private fun errorMessage(e: Exception): String =
        (e as? ApiException)?.respMessage ?: "Terjadi kesalahan, silakan coba lagi."

    private fun normalizeFilter(filter: TransactionFilterPayload): TransactionFilterPayload {
        if (filter.quickRange != null) {
            return filter.with(resetFromDate = true, resetToDate = true)
        }
        if (filter.fromDate != null && filter.toDate != null) {
            return filter.with(resetQuickRange = true)
        }
        val def = DefaultRangeDate.getCurrentMonth()
        return filter.with(fromDate = def.from, toDate = def.to, resetQuickRange = true)
    }
    fun clear() {
        _uiState.value = MessageHistoryUiState()
    }
}