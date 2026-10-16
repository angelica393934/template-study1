package bsb.dev.bsb_bangking_jp.core.network

import bsb.dev.bsb_bangking_jp.core.crypto.SignatureUtils
import bsb.dev.bsb_bangking_jp.core.device.SecureStorageService
import bsb.dev.bsb_bangking_jp.core.network.header.ApiHeaders
import bsb.dev.bsb_bangking_jp.core.network.token.TokenPhase
import bsb.dev.bsb_bangking_jp.core.network.token.domain.TokenRepository
import com.google.gson.Gson

private const val EXPIRED_RESP_CODE = "0465" // sama dengan TokenRefreshInterceptor

/**
 * Helper untuk endpoint yang mengharuskan GET membawa body. Karena bypass OkHttp,
 * TokenRefreshInterceptor ikut ter-bypass, jadi retry-setelah-refresh ditangani di sini.
 */
class GetWithBodyApiHelper(
    private val secureStorage: SecureStorageService,
    private val tokenRepository: TokenRepository,
) {
    private val gson = Gson()

    suspend fun <T : BaseRespCodeResponse> execute(
        path: String,
        body: Any,
        responseType: Class<T>,
        useLoginPhaseToken: Boolean = true,
    ): T {
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

/** Kontrak minimal supaya helper bisa cek respCode generik apapun modelnya. */
interface BaseRespCodeResponse {
    val respCode: String?
}