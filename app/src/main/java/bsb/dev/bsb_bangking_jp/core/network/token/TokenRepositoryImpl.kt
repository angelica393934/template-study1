package bsb.dev.bsb_bangking_jp.core.network.token

import bsb.dev.bsb_bangking_jp.core.crypto.SignatureUtils
import bsb.dev.bsb_bangking_jp.core.device.SecureStorageService
import bsb.dev.bsb_bangking_jp.core.network.header.ApiHeaders
import bsb.dev.bsb_bangking_jp.shared.session.SessionExpiredNotifier
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import retrofit2.Response
import kotlin.coroutines.cancellation.CancellationException

class TokenRepositoryImpl(
    private val secureStorage: SecureStorageService,
    private val refreshApiService: () -> RefreshTokenApiService,
    private val sessionExpiredNotifier: SessionExpiredNotifier,
) : TokenRepository {

    // Satu refresh pada satu waktu, supaya 3 request yang kena 0465 bersamaan
    // tidak memanggil endpoint refresh 3x (refresh token bisa hangus setelah dipakai sekali).
    private val mutex = Mutex()

    /** Semua perbedaan antar fase dirangkum di sini, jadi tidak ada 6 fungsi copy-paste. */
    private class PhaseConfig(
        val getAccess: () -> String?,
        val getRefresh: () -> String?,
        val saveAccess: (String) -> Unit,
        val saveRefresh: (String) -> Unit,
        val needsSignature: Boolean,
        val call: suspend (Map<String, String>) -> Response<RefreshTokenResponse>,
    )

    private fun configFor(phase: TokenPhase): PhaseConfig? = when (phase) {
        TokenPhase.INIT -> PhaseConfig(
            secureStorage::getInitAccessToken, secureStorage::getInitRefreshToken,
            secureStorage::saveInitAccessToken, secureStorage::saveInitRefreshToken,
            needsSignature = true,
        ) { refreshApiService().refreshInitToken(it) }

        TokenPhase.REGIST -> PhaseConfig(
            secureStorage::getRegistAccessToken, secureStorage::getRegistRefreshToken,
            secureStorage::saveRegistAccessToken, secureStorage::saveRegistRefreshToken,
            needsSignature = false,
        ) { refreshApiService().refreshRegistToken(it) }

        TokenPhase.ACTIVATION -> PhaseConfig(
            secureStorage::getActivationAccessToken, secureStorage::getActivationRefreshToken,
            secureStorage::saveActivationAccessToken, secureStorage::saveActivationRefreshToken,
            needsSignature = false,
        ) { refreshApiService().refreshActivationToken(it) }

        TokenPhase.LOGIN -> PhaseConfig(
            secureStorage::getLoginAccessToken, secureStorage::getLoginRefreshToken,
            secureStorage::saveLoginAccessToken, secureStorage::saveLoginRefreshToken,
            needsSignature = true,
        ) { refreshApiService().refreshLoginToken(it) }

        TokenPhase.FORGET_ID_USER -> PhaseConfig(
            secureStorage::getForgetIdUserAccessToken, secureStorage::getForgetIdUserRefreshToken,
            secureStorage::saveForgetIdUserAccessToken, secureStorage::saveForgetIdUserRefreshToken,
            needsSignature = false,
        ) { refreshApiService().refreshForgetIdUserToken(it) }

        TokenPhase.FORGET_PW_USER -> PhaseConfig(
            secureStorage::getForgetPwUserAccessToken, secureStorage::getForgetPwUserRefreshToken,
            secureStorage::saveForgetPwUserAccessToken, secureStorage::saveForgetPwUserRefreshToken,
            needsSignature = false,
        ) { refreshApiService().refreshForgetPwUserToken(it) }

        TokenPhase.TRANSFER -> null // token transfer sekali pakai, tidak bisa di-refresh
    }

    override fun getAccessToken(phase: TokenPhase): String? = configFor(phase)?.getAccess?.invoke()
        ?: if (phase == TokenPhase.TRANSFER) secureStorage.getTransferAccessToken() else null

    override suspend fun refresh(phase: TokenPhase, failedToken: String?): String? {
        val config = configFor(phase) ?: return null

        return mutex.withLock {
            // Request lain mungkin sudah refresh saat kita menunggu giliran -> pakai hasilnya.
            val current = config.getAccess()
            if (current != null && current != failedToken) return@withLock current

            try {
                doRefresh(phase, config)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                null
            }
        }
    }

    private suspend fun doRefresh(phase: TokenPhase, config: PhaseConfig): String? {
        val isLogin = phase == TokenPhase.LOGIN

        val refreshToken = config.getRefresh()
        if (refreshToken == null) {
            if (isLogin) sessionExpiredNotifier.notifyExpired()
            return null
        }

        val headers = mutableMapOf("Authorization" to "Bearer $refreshToken")
        if (config.needsSignature) {
            val privateKey = secureStorage.getPrivateKey()
            if (privateKey == null) {
                if (isLogin) sessionExpiredNotifier.notifyExpired()
                return null
            }
            val timestamp = ApiHeaders.currentTimestamp()
            val signature = SignatureUtils.sign(emptyMap<String, String>(), timestamp, privateKey)
            headers += ApiHeaders.withSignature(signature, ApiHeaders.full(timestamp))
        }

        val response = config.call(headers)
        if (!response.isSuccessful) {
            if (isLogin && response.code() in 400..499) sessionExpiredNotifier.notifyExpired()
            return null
        }

        val data = response.body()?.data
        val newAccess = data?.accessToken
        if (newAccess == null) {
            if (isLogin) sessionExpiredNotifier.notifyExpired()
            return null
        }

        config.saveAccess(newAccess)
        data.refreshToken?.let(config::saveRefresh)
        return newAccess
    }
}