package bsb.dev.bsb_bangking_jp.shared.session

import bsb.dev.bsb_bangking_jp.shared.logout.domain.LogoutUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull

/**
 * Padanan SessionGuard.dart -- penjaga sesi otomatis: idle timeout (15 menit tanpa
 * aktivitas) & background timeout (10 menit app di-background).
 *
 * ⚠️ SELURUH LOGIC DI SINI DINONAKTIFKAN SEMENTARA. Kita masih butuh sesi tetap
 * menyala tanpa batas waktu selama development. Jangan panggil resetIdleTimer(),
 * onAppPaused(), atau onAppResumed() dari mana pun sampai ada instruksi eksplisit
 * untuk mengaktifkan kembali. Kerangkanya sudah lengkap supaya tinggal "unlock"
 * nanti (uncomment) tanpa menulis ulang dari nol.
 */
class SessionGuard(
    private val sessionManager: SessionManager,
    private val logoutUseCase: LogoutUseCase,
    sessionExpiredNotifier: SessionExpiredNotifier,
) {

    companion object {
        const val IDLE_DURATION_MILLIS = 15 * 60 * 1000L      // 15 menit
        const val BACKGROUND_LIMIT_MILLIS = 10 * 60 * 1000L   // 10 menit
        private const val LOGOUT_TIMEOUT_MILLIS = 5_000L
        }

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    // private var idleJob: Job? = null
    // private var backgroundTimestamp: Long? = null
    private var isShowingPopup = false
    private val _showSessionExpired = MutableStateFlow(false)
    val showSessionExpired: StateFlow<Boolean> = _showSessionExpired.asStateFlow()

    private var isForceLoggingOut = false

    init {
        scope.launch { sessionExpiredNotifier.events.collect { forceLogout() } }
    }

    fun forceLogout() {
        if (isForceLoggingOut) return
        isForceLoggingOut = true
        scope.launch {
            // hit API dulu, tapi jangan sampai dialog tertahan lama kalau jaringan jelek / token sudah mati
            withTimeoutOrNull(LOGOUT_TIMEOUT_MILLIS) { logoutUseCase() }
            sessionManager.clearSession() // SELALU clear lokal, apapun hasil API-nya
            _showSessionExpired.value = true
        }
    }

    /** Dipanggil tombol "Masuk Kembali" di dialog. */
    fun onLoginAgain() {
        _showSessionExpired.value = false
        isForceLoggingOut = false
    }
    /** Padanan startIdleTimer()/resetIdleTimer() -- panggil tiap ada interaksi user. */
    fun resetIdleTimer() {
        // idleJob?.cancel()
        // if (!sessionManager.isLoggedIn.value) return
        // idleJob = scope.launch {
        //     delay(IDLE_DURATION_MILLIS)
        //     forceLogout()
        // }
    }

    /** Padanan didChangeAppLifecycleState(paused). Panggil dari ON_STOP lifecycle observer. */
    fun onAppPaused() {
        // backgroundTimestamp = System.currentTimeMillis()
    }

    /** Padanan didChangeAppLifecycleState(resumed). Panggil dari ON_START lifecycle observer. */
    fun onAppResumed() {
        // val start = backgroundTimestamp ?: return
        // if (System.currentTimeMillis() - start > BACKGROUND_LIMIT_MILLIS) {
        //     // 🔹 Padanan : jalur background TIDAK hit API logout, cuma clear lokal.
        //     sessionManager.clearSession()
        //     onForceLogout()
        // }
    }


}