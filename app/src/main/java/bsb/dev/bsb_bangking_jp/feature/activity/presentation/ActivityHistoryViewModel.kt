package bsb.dev.bsb_bangking_jp.feature.activity.presentation

import bsb.dev.bsb_bangking_jp.core.filter.TransactionFilterPayload
import bsb.dev.bsb_bangking_jp.core.network.ApiException
import bsb.dev.bsb_bangking_jp.core.util.DefaultRangeDate
import bsb.dev.bsb_bangking_jp.feature.activity.domain.ActivityHistoryRepository
import bsb.dev.bsb_bangking_jp.shared.rekening_lainnya.presentation.RekeningLainnyaViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Koin `single` (BUKAN `viewModel`), sama seperti BerandaViewModel -- supaya 1 instance
 * dipakai di seluruh app dan proses fetch histori TIDAK tergantung apakah ActivityPage
 * sedang di-compose atau tidak (Navbar cuma compose page yang aktif via `when(currentIndex)`,
 * jadi trigger tidak boleh diletakkan di LaunchedEffect milik ActivityPage).
 *
 * Begitu class ini pertama kali di-resolve Koin, dia langsung "mengamati" state rekening
 * dari BerandaViewModel lewat coroutine sendiri. Saat rekeningList berhasil terisi untuk
 * PERTAMA KALI, otomatis pilih rekening utama & fetch histori -- tanpa perlu UI mana pun
 * memicunya secara eksplisit.
 */
    class ActivityHistoryViewModel(
    private val repository: ActivityHistoryRepository,
    private val rekeningViewModel: RekeningLainnyaViewModel,
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    private val _uiState = MutableStateFlow(ActivityHistoryUiState())
    val uiState: StateFlow<ActivityHistoryUiState> = _uiState.asStateFlow()

    init {
        android.util.Log.d("ActivityHistory", "ViewModel CREATED (Koin resolve)")
        observeRekeningUntukAutoLoad()
    }

    /**
     * Padanan `BlocListener<RekeningLainnyaBloc>` di ActivityPage.dart lama --
     * tunggu rekening lainnya berhasil dulu, baru jalankan fetch histori pertama kali.
     * Hanya trigger SEKALI (selama accountNumber belum pernah di-set) supaya tidak
     * menimpa pilihan rekening manual user setiap kali BerandaViewModel refresh.
     */
    private fun observeRekeningUntukAutoLoad() {
        scope.launch {
            rekeningViewModel.uiState
                .map { it.rekeningList }
                .distinctUntilChangedBy { it?.size to it?.firstOrNull()?.number }
                .collect { rekeningList ->
                    android.util.Log.d("ActivityHistory", "observe rekeningList changed, size=${rekeningList?.size}, current accountNumber=${_uiState.value.accountNumber}")
                    if (_uiState.value.accountNumber != null) return@collect
                    if (rekeningList.isNullOrEmpty()) return@collect

                    val primary = rekeningList.firstOrNull { it.isPrimary } ?: rekeningList.first()
                    android.util.Log.d("ActivityHistory", "auto-trigger getInitial() with account=${primary.number}")
                    getInitial(primary.number)
                }
        }
    }

    fun getInitial(accountNumber: String) {
        android.util.Log.d("ActivityHistory", "getInitial() CALLED account=$accountNumber")
        load(accountNumber, TransactionFilterPayload.initial())
    }

    /** Dipanggil saat user pilih rekening lain lewat SaldoCardSelector. */
    fun switchAccount(accountNumber: String) {
        if (_uiState.value.accountNumber == accountNumber) return
        getInitial(accountNumber)
    }

    /** Padanan ActivityHistoryEvent.applyFilter. */
    fun applyFilter(filter:TransactionFilterPayload) {
        val accountNumber = _uiState.value.accountNumber ?: return
        load(accountNumber, normalizeFilter(filter))
    }

    /** Padanan ActivityHistoryEvent.refresh -- pakai filter aktif yang sedang berlaku. */
    fun refresh() {
        val accountNumber = _uiState.value.accountNumber ?: return
        val filter = _uiState.value.activeFilter ?: return
        load(accountNumber, filter)
    }

    /** Padanan ActivityHistoryEvent.loadMore. */
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

    private fun load(accountNumber: String, filter:TransactionFilterPayload) {
        android.util.Log.d("ActivityHistory", "load() launching coroutine, account=$accountNumber")
        scope.launch {
            android.util.Log.d("ActivityHistory", "load() coroutine STARTED")
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
                android.util.Log.d("ActivityHistory", "calling repository.loadInitial()...")
                val items = repository.loadInitial(accountNumber, filter)
                android.util.Log.d("ActivityHistory", "loadInitial() SUCCESS, items=${items.size}")
                _uiState.update { it.copy(isLoading = false, items = items, hasMore = repository.hasMore) }
            } catch (e: Exception) {
                android.util.Log.e("ActivityHistory", "loadInitial() FAILED", e)
                _uiState.update { it.copy(isLoading = false, error = errorMessage(e)) }
            }
        }
    }

    private fun errorMessage(e: Exception): String =
        (e as? ApiException)?.respMessage ?: "Terjadi kesalahan, silakan coba lagi."

    /** Padanan _normalizeFilter -- quickRange dan manual date saling eksklusif. */
    private fun normalizeFilter(filter:TransactionFilterPayload):TransactionFilterPayload {
        if (filter.quickRange != null) {
            return filter.with(resetFromDate = true, resetToDate = true)
        }
        if (filter.fromDate != null && filter.toDate != null) {
            return filter.with(resetQuickRange = true)
        }
        val def = DefaultRangeDate.getCurrentMonth()
        return filter.with(fromDate = def.from, toDate = def.to, resetQuickRange = true)
    }
}