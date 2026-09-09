// core/network/token/TokenRefreshInterceptor.kt
package bsb.dev.bsb_bangking_jp.core.network.token

import bsb.dev.bsb_bangking_jp.core.crypto.SignatureUtils
import bsb.dev.bsb_bangking_jp.core.device.SecureStorageService
import bsb.dev.bsb_bangking_jp.core.network.header.ApiHeaders
import com.google.gson.Gson
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

private const val EXPIRED_RESP_CODE = "0465"

class TokenRefreshInterceptor(
    private val secureStorage: SecureStorageService,
    private val refreshApiService: () -> RefreshTokenApiService,
) : Interceptor {

    private val gson = Gson()

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val phaseTag = originalRequest.tag(TokenPhaseTag::class.java)
            ?: return chain.proceed(originalRequest)

        val requestWithAuth = attachAuthorization(originalRequest, phaseTag.phase)
        val response = chain.proceed(requestWithAuth)

        if (peekRespCode(response) != EXPIRED_RESP_CODE) return response

        val newAccessToken = runBlocking { tryRefresh(phaseTag.phase) } ?: return response
        response.close()

        val retryRequest = requestWithAuth.newBuilder()
            .removeHeader("Authorization")
            .addHeader("Authorization", "Bearer $newAccessToken")
            .build()

        return chain.proceed(retryRequest)
    }

    private fun attachAuthorization(request: Request, phase: TokenPhase): Request {
        val token = getStoredAccessToken(phase) ?: return request
        return request.newBuilder()
            .removeHeader("Authorization")
            .addHeader("Authorization", "Bearer $token")
            .build()
    }

    private fun getStoredAccessToken(phase: TokenPhase): String? = when (phase) {
        TokenPhase.INIT -> secureStorage.getInitAccessToken()
        TokenPhase.REGIST -> secureStorage.getRegistAccessToken()
        TokenPhase.LOGIN -> secureStorage.getLoginAccessToken()
        TokenPhase.TRANSFER -> secureStorage.getTransferAccessToken()
    }

    private suspend fun tryRefresh(phase: TokenPhase): String? = try {
        when (phase) {
            TokenPhase.INIT -> refreshInitToken()
            TokenPhase.REGIST ->  refreshRegistToken()
            TokenPhase.LOGIN -> refreshLoginToken()
            TokenPhase.TRANSFER -> null
        }
    } catch (e: Exception) {
        null
    }

    private suspend fun refreshInitToken(): String? {
        val refreshToken = secureStorage.getInitRefreshToken() ?: return null
        val privateKey = secureStorage.getPrivateKey() ?: return null

        val timestamp = ApiHeaders.currentTimestamp()
        val baseHeaders = ApiHeaders.full(timestamp)
        val signature = SignatureUtils.sign(emptyMap<String, String>(), timestamp, privateKey)
        val headers = ApiHeaders.withSignature(signature, baseHeaders) +
                ("Authorization" to "Bearer $refreshToken")

        val response = refreshApiService().refreshInitToken(headers = headers)
        if (!response.isSuccessful) return null

        val data = response.body()?.data ?: return null
        val newAccess = data.accessToken ?: return null

        secureStorage.saveInitAccessToken(newAccess)
        data.refreshToken?.let { secureStorage.saveInitRefreshToken(it) }
        return newAccess
    }

    private suspend fun refreshRegistToken(): String? {
        val refreshToken = secureStorage.getRegistRefreshToken() ?: return null

        val headers = mapOf("Authorization" to "Bearer $refreshToken")

        val response = refreshApiService().refreshRegistToken(headers = headers)
        if (!response.isSuccessful) return null

        val data = response.body()?.data ?: return null
        val newAccess = data.accessToken ?: return null

        secureStorage.saveRegistAccessToken(newAccess)
        data.refreshToken?.let { secureStorage.saveRegistRefreshToken(it) }
        return newAccess
    }

    private suspend fun refreshLoginToken(): String? {
        val refreshToken = secureStorage.getLoginRefreshToken() ?: return null
        val privateKey = secureStorage.getPrivateKey() ?: return null

        val timestamp = ApiHeaders.currentTimestamp()
        val baseHeaders = ApiHeaders.full(timestamp)
        val signature = SignatureUtils.sign(emptyMap<String, String>(), timestamp, privateKey)
        val headers = ApiHeaders.withSignature(signature, baseHeaders) +
                ("Authorization" to "Bearer $refreshToken")

        val response = refreshApiService().refreshLoginToken(headers = headers)
        if (!response.isSuccessful) return null

        val data = response.body()?.data ?: return null
        val newAccess = data.accessToken ?: return null

        secureStorage.saveLoginAccessToken(newAccess)
        data.refreshToken?.let { secureStorage.saveLoginRefreshToken(it) }
        return newAccess
    }

    private fun peekRespCode(response: Response): String? = try {
        val bodyString = response.peekBody(Long.MAX_VALUE).string()
        (gson.fromJson(bodyString, Map::class.java))["respCode"] as? String
    } catch (e: Exception) {
        null
    }
}