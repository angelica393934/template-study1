package bsb.dev.bsb_bangking_jp.core.network

import bsb.dev.bsb_bangking_jp.core.crypto.SignatureUtils
import bsb.dev.bsb_bangking_jp.core.device.SecureStorageService
import bsb.dev.bsb_bangking_jp.core.network.header.ApiHeaders
import bsb.dev.bsb_bangking_jp.core.network.token.RefreshTokenApiService
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
    private val refreshTokenApiService: RefreshTokenApiService,
) {
    private val gson = Gson()

    /**
     * @param path path relatif tanpa leading slash, mis. "v1/dashboard/gethistory"
     * @param body request body, akan di-sign DAN di-serialize sebagai JSON GET body
     * @param responseType class model response, mis. ActivityHistoryResponse::class.java
     * @param tokenPhase pilih token mana yang dipakai -- true untuk phase LOGIN, false untuk INIT
     *   (endpoint dengan body GET sejauh ini semuanya phase LOGIN, tapi parameter ini
     *   disiapkan untuk jaga-jaga kalau nanti ada yang phase INIT juga)
     */
    suspend fun <T> execute(
        path: String,
        body: Any,
        responseType: Class<T>,
        useLoginPhaseToken: Boolean = true,
    ): T where T : BaseRespCodeResponse {
        val privateKey = secureStorage.getPrivateKey()
            ?: throw IllegalStateException("Private key tidak ditemukan, device belum ter-init.")

        val url = "${NetworkConstants.BASE_URL}$path"
        val timestamp = ApiHeaders.currentTimestamp()
        val signature = SignatureUtils.sign(body, timestamp, privateKey)
        val baseHeaders = ApiHeaders.withSignature(signature, ApiHeaders.full(timestamp))

        val accessToken = getStoredAccessToken(useLoginPhaseToken)
        var headers = baseHeaders + accessTokenHeader(accessToken)

        var result = GetWithBodyHttpClient.getWithBody(url = url, headers = headers, body = body)
        var parsed = parseOrNull(result.rawBody, responseType)

        if (parsed?.respCode == EXPIRED_RESP_CODE) {
            val refreshed = tryRefresh(useLoginPhaseToken)
            if (refreshed != null) {
                headers = baseHeaders + accessTokenHeader(refreshed)
                result = GetWithBodyHttpClient.getWithBody(url = url, headers = headers, body = body)
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
        try {
            gson.fromJson(raw, type)
        } finally  {
            null
        }

    private fun accessTokenHeader(token: String?): Map<String, String> =
        token?.let { mapOf("Authorization" to "Bearer $it") } ?: emptyMap()

    private fun getStoredAccessToken(useLoginPhaseToken: Boolean): String? =
        if (useLoginPhaseToken) secureStorage.getLoginAccessToken() else secureStorage.getInitAccessToken()
    private suspend fun tryRefresh(useLoginPhaseToken: Boolean): String? {
        return try {
            val refreshToken = if (useLoginPhaseToken) secureStorage.getLoginRefreshToken() else secureStorage.getInitRefreshToken()
            val privateKey = secureStorage.getPrivateKey()
            if (refreshToken == null || privateKey == null) return null

            val timestamp = ApiHeaders.currentTimestamp()
            val baseHeaders = ApiHeaders.full(timestamp)
            val signature = SignatureUtils.sign(emptyMap<String, String>(), timestamp, privateKey)
            val headers = ApiHeaders.withSignature(signature, baseHeaders) +
                    ("Authorization" to "Bearer $refreshToken")

            val response = if (useLoginPhaseToken) {
                refreshTokenApiService.refreshLoginToken(headers = headers)
            } else {
                refreshTokenApiService.refreshInitToken(headers = headers)
            }
            if (!response.isSuccessful) return null

            val data = response.body()?.data ?: return null
            val newAccess = data.accessToken ?: return null

            if (useLoginPhaseToken) {
                secureStorage.saveLoginAccessToken(newAccess)
                data.refreshToken?.let { secureStorage.saveLoginRefreshToken(it) }
            } else {
                secureStorage.saveInitAccessToken(newAccess)
                data.refreshToken?.let { secureStorage.saveInitRefreshToken(it) }
            }
            newAccess
        } finally {
            null
        }
    }
}

/** Kontrak minimal supaya GetWithBodyApiHelper bisa cek respCode generik apapun modelnya. */
interface BaseRespCodeResponse {
    val respCode: String?
}