package bsb.dev.bsb_bangking_jp.shared.session

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

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
    private val logoutUseCase: suspend () -> Result<Unit>, // shared/logout/domain/LogoutUseCase
    private val onForceLogout: () -> Unit, // tampilkan SessionExpiredDialog dari sisi UI
) {

//    companion object {
//        const val IDLE_DURATION_MILLIS = 15 * 60 * 1000L      // 15 menit
//        const val BACKGROUND_LIMIT_MILLIS = 10 * 60 * 1000L   // 10 menit
//    }

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    // private var idleJob: Job? = null
    // private var backgroundTimestamp: Long? = null
    private var isShowingPopup = false

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

    /** Padanan _forceLogout()/logoutFromInterceptor() -- idle & token-expired lewat jalur ini. */
    private fun forceLogout() {
        if (isShowingPopup) return
        isShowingPopup = true
        scope.launch {
        logoutUseCase() // hit API dulu (padanan LogoutBloc.add(logout()))
        sessionManager.clearSession() // SELALU clear lokal, apapun hasil API-nya
        onForceLogout()
        }
    }
}