package bsb.dev.bsb_bangking_jp.core.network.token

import com.google.gson.Gson
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

private const val EXPIRED_RESP_CODE = "0465"
private const val PEEK_LIMIT_BYTES = 64 * 1024L

class TokenRefreshInterceptor(
    private val tokenRepository: TokenRepository,
) : Interceptor {

    private val gson = Gson()

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val phase = original.tag(TokenPhaseTag::class.java)?.phase
            ?: return chain.proceed(original)

        val usedToken = tokenRepository.getAccessToken(phase)
        val response = chain.proceed(original.withBearer(usedToken))

        if (!response.isExpired()) return response

        val newToken = runBlocking { tokenRepository.refresh(phase, usedToken) }
            ?: return response

        response.close()
        return chain.proceed(original.withBearer(newToken))
    }

    private fun Request.withBearer(token: String?): Request {
        if (token == null) return this
        return newBuilder()
            .removeHeader("Authorization")
            .addHeader("Authorization", "Bearer $token")
            .build()
    }

    /** Hanya mengintip body JSON, dan dibatasi ukurannya (gambar/file tidak ikut dibaca). */
    private fun Response.isExpired(): Boolean = try {
        if (body?.contentType()?.subtype != "json") {
            false
        } else {
            val text = peekBody(PEEK_LIMIT_BYTES).string()
            (gson.fromJson(text, Map::class.java))["respCode"] == EXPIRED_RESP_CODE
        }
    } catch (e: Exception) {
        false
    }
}