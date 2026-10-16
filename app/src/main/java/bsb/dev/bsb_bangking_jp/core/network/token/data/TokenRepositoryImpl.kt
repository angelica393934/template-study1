package bsb.dev.bsb_bangking_jp.core.network.token.data

import bsb.dev.bsb_bangking_jp.core.crypto.SignatureUtils
import bsb.dev.bsb_bangking_jp.core.device.SecureStorageService
import bsb.dev.bsb_bangking_jp.core.network.header.ApiHeaders
import bsb.dev.bsb_bangking_jp.core.network.token.TokenPhase
import bsb.dev.bsb_bangking_jp.core.network.token.domain.TokenRepository
import bsb.dev.bsb_bangking_jp.shared.session.SessionExpiredNotifier
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import retrofit2.Response
import kotlin.coroutines.cancellation.CancellationException

class TokenRepositoryImpl(
    private val secureStorage: SecureStorageService,
    // lambda supaya tidak circular dependency saat Koin resolve
    private val refreshApiService: () -> RefreshTokenApiService,
    private val sessionExpiredNotifier: SessionExpiredNotifier,
) : TokenRepository {

    // Satu refresh pada satu waktu: refresh token bisa hangus setelah dipakai sekali.
    private val mutex = Mutex()

    private class PhaseConfig(
        val getAccess: () -> String?,
        val getRefresh: () -> String?,
        val saveAccess: (String) -> Unit,
        val saveRefresh: (String) -> Unit,
        val needsSignature: Boolean = false,
        /** null = fase ini tidak bisa di-refresh (mis. TRANSFER, token sekali pakai). */
        val refreshCall: (suspend (Map<String, String>) -> Response<RefreshTokenResponse>)? = null,
    )

    /** Semua perbedaan antar fase dirangkum di sini -- mencakup SEMUA nilai [TokenPhase]. */
    private val configs: Map<TokenPhase, PhaseConfig> by lazy {
        mapOf(
            TokenPhase.INIT to PhaseConfig(
                secureStorage::getInitAccessToken, secureStorage::getInitRefreshToken,
                secureStorage::saveInitAccessToken, secureStorage::saveInitRefreshToken,
                needsSignature = true,
            ) { refreshApiService().refreshInitToken(it) },

            TokenPhase.REGIST to PhaseConfig(
                secureStorage::getRegistAccessToken, secureStorage::getRegistRefreshToken,
                secureStorage::saveRegistAccessToken, secureStorage::saveRegistRefreshToken,
            ) { refreshApiService().refreshRegistToken(it) },

            TokenPhase.ACTIVATION to PhaseConfig(
                secureStorage::getActivationAccessToken, secureStorage::getActivationRefreshToken,
                secureStorage::saveActivationAccessToken, secureStorage::saveActivationRefreshToken,
            ) { refreshApiService().refreshActivationToken(it) },

            TokenPhase.LOGIN to PhaseConfig(
                secureStorage::getLoginAccessToken, secureStorage::getLoginRefreshToken,
                secureStorage::saveLoginAccessToken, secureStorage::saveLoginRefreshToken,
                needsSignature = true,
            ) { refreshApiService().refreshLoginToken(it) },

            TokenPhase.FORGET_ID_USER to PhaseConfig(
                secureStorage::getForgetIdUserAccessToken, secureStorage::getForgetIdUserRefreshToken,
                secureStorage::saveForgetIdUserAccessToken, secureStorage::saveForgetIdUserRefreshToken,
            ) { refreshApiService().refreshForgetIdUserToken(it) },

            TokenPhase.FORGET_PW_USER to PhaseConfig(
                secureStorage::getForgetPwUserAccessToken, secureStorage::getForgetPwUserRefreshToken,
                secureStorage::saveForgetPwUserAccessToken, secureStorage::saveForgetPwUserRefreshToken,
            ) { refreshApiService().refreshForgetPwUserToken(it) },

            TokenPhase.TRANSFER to PhaseConfig(
                secureStorage::getTransferAccessToken, { null },
                secureStorage::saveTransferAccessToken, { },
                refreshCall = null,
            ),
        )
    }

    override fun getAccessToken(phase: TokenPhase): String? = configs.getValue(phase).getAccess()

    override suspend fun refresh(phase: TokenPhase, failedToken: String?): String? {
        val config = configs.getValue(phase)
        val call = config.refreshCall ?: return null

        return mutex.withLock {
            // Request lain mungkin sudah refresh saat kita antre -> pakai hasilnya.
            val current = config.getAccess()
            if (current != null && current != failedToken) return@withLock current

            try {
                doRefresh(phase, config, call)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                null // error jaringan: jangan logout paksa
            }
        }
    }

    private suspend fun doRefresh(
        phase: TokenPhase,
        config: PhaseConfig,
        call: suspend (Map<String, String>) -> Response<RefreshTokenResponse>,
    ): String? {
        val isLogin = phase == TokenPhase.LOGIN
        fun expired(): String? {
            if (isLogin) sessionExpiredNotifier.notifyExpired()
            return null
        }

        val refreshToken = config.getRefresh() ?: return expired()

        val headers = mutableMapOf("Authorization" to "Bearer $refreshToken")
        if (config.needsSignature) {
            val privateKey = secureStorage.getPrivateKey() ?: return expired()
            val timestamp = ApiHeaders.currentTimestamp()
            val signature = SignatureUtils.sign(emptyMap<String, String>(), timestamp, privateKey)
            headers += ApiHeaders.withSignature(signature, ApiHeaders.full(timestamp))
        }

        val response = call(headers)
        if (!response.isSuccessful) {
            // 4xx = refresh token ditolak (sesi benar-benar habis); 5xx = masalah server, jangan logout.
            return if (isLogin && response.code() in 400..499) expired() else null
        }

        val data = response.body()?.data
        val newAccess = data?.accessToken ?: return expired()

        config.saveAccess(newAccess)
        data.refreshToken?.let(config.saveRefresh)
        return newAccess
    }
}