package bsb.dev.bsb_bangking_jp.core.network

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import retrofit2.Response

data class ApiErrorResponse(
    @SerializedName("respCode") val respCode: String? = null,
    @SerializedName("respMessage") val respMessage: String? = null,
)

/** Exception yang membawa respCode & respMessage dari body error backend. */
class ApiException(
    val respCode: String?,
    val respMessage: String,
) : Exception(respMessage)

// ini untuk mengirim response saja ke depan
object ApiErrorParser {
    private val gson = Gson()

    fun parse(response: Response<*>): ApiException {
        val rawBody = response.errorBody()?.string()
        val parsed = try {
            rawBody?.let { gson.fromJson(it, ApiErrorResponse::class.java) }
        } catch (e: Exception) {
            null
        }
        return ApiException(
            respCode = parsed?.respCode,
            respMessage = parsed?.respMessage ?: "Terjadi kesalahan (${response.code()})",
        )
    }
}