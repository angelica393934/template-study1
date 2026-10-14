package bsb.dev.bsb_bangking_jp.core.network

import bsb.dev.bsb_bangking_jp.core.crypto.SignatureUtils
import bsb.dev.bsb_bangking_jp.core.device.SecureStorageService
import bsb.dev.bsb_bangking_jp.core.network.header.ApiHeaders
import bsb.dev.bsb_bangking_jp.core.network.token.RefreshTokenApiService
import bsb.dev.bsb_bangking_jp.core.network.token.TokenPhase
import bsb.dev.bsb_bangking_jp.core.network.token.TokenRepository
import bsb.dev.bsb_bangking_jp.shared.session.SessionExpiredNotifier
import com.google.gson.Gson

private const val EXPIRED_RESP_CODE = "0465" // sama dengan TokenRefreshInterceptor

/**
 * Helper reusable untuk SEMUA endpoint yang mengharuskan method GET membawa request body
 * (kontrak backend non-standar, tapi tidak bisa diubah). Bungkus GetWithBodyHttpClient +
 * signature + refresh-token-on-expiry manual, supaya tiap repository baru dengan kebutuhan
 * sama TIDAK PERLU menulis ulang seluruh alur ini dari nol -- cukup panggil `execute()`.
 *
 * Kenapa perlu helper terpisah (bukan cukup GetWithBodyHttpClient saja): karena bypass
 * Retrofit/OkHttp berarti TokenRefreshInterceptor juga ikut ter-bypass untuk endpoint ini,
 * jadi retry-setelah-refresh-token harus ditangani manual di satu tempat yang reusable,
 * bukan diulang copy-paste di tiap repository.
 */
class GetWithBodyApiHelper(
    private val secureStorage: SecureStorageService,
    private val tokenRepository: TokenRepository,
) {class GetWithBodyApiHelper(
    private val secureStorage: SecureStorageService,
    private val tokenRepository: TokenRepository,   // ganti RefreshTokenApiService
) {
    private val gson = Gson()

    suspend fun <T> execute(
        path: String,
        body: Any,
        responseType: Class<T>,
        useLoginPhaseToken: Boolean = true,
    ): T where T : BaseRespCodeResponse {
        val privateKey = secureStorage.getPrivateKey()
            ?: throw IllegalStateException("Private key tidak ditemukan, device belum ter-init.")

        val phase = if (useLoginPhaseToken) TokenPhase.LOGIN else TokenPhase.INIT
        val url = "${NetworkConstants.BASE_URL}$path"
        val timestamp = ApiHeaders.currentTimestamp()
        val signature = SignatureUtils.sign(body, timestamp, privateKey)
        val baseHeaders = ApiHeaders.withSignature(signature, ApiHeaders.full(timestamp))

        val usedToken = tokenRepository.getAccessToken(phase)
        var result = GetWithBodyHttpClient.getWithBody(
            url = url, headers = baseHeaders + accessTokenHeader(usedToken), body = body,
        )
        var parsed = parseOrNull(result.rawBody, responseType)

        if (parsed?.respCode == EXPIRED_RESP_CODE) {
            val refreshed = tokenRepository.refresh(phase, usedToken)
            if (refreshed != null) {
                result = GetWithBodyHttpClient.getWithBody(
                    url = url, headers = baseHeaders + accessTokenHeader(refreshed), body = body,
                )
                parsed = parseOrNull(result.rawBody, responseType)
            }
            // notifyExpired() sudah ditangani TokenRepositoryImpl, tidak perlu dobel di sini
        }

        if (result.statusCode !in 200..299) {
            val error = parseOrNull(result.rawBody, ApiErrorResponse::class.java)
            throw ApiException(error?.respCode, error?.respMessage ?: "Terjadi kesalahan (${result.statusCode})")
        }
        return parsed ?: throw ApiException(null, "Gagal memproses response dari server.")
    }

    private fun <T> parseOrNull(raw: String, type: Class<T>): T? =
        try { gson.fromJson(raw, type) } catch (e: Exception) { null }

    private fun accessTokenHeader(token: String?): Map<String, String> =
        token?.let { mapOf("Authorization" to "Bearer $it") } ?: emptyMap()
}
    }

/** Kontrak minimal supaya GetWithBodyApiHelper bisa cek respCode generik apapun modelnya. */
interface BaseRespCodeResponse {
    val respCode: String?
}