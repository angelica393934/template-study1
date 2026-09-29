package bsb.dev.bsb_bangking_jp.shared.session

import bsb.dev.bsb_bangking_jp.core.device.SecureStorageService
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * Jembatan Network layer -> SessionGuard. Dipanggil saat refresh token LOGIN ditolak server.
 * Hanya menembak event kalau memang masih ada sesi login (token login masih tersimpan),
 * supaya request "sisa" setelah logout manual tidak memunculkan dialog.
 */
class SessionExpiredNotifier(private val secureStorage: SecureStorageService) {
    private val _events = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val events: SharedFlow<Unit> = _events.asSharedFlow()

    fun notifyExpired() {
        if (secureStorage.getLoginAccessToken() == null) return
        _events.tryEmit(Unit)
    }
}