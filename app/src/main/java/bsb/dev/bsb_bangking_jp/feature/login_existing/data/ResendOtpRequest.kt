// feature/login_existing/data/ResendOtpRequest.kt
package bsb.dev.bsb_bangking_jp.feature.login_existing.data

import com.google.gson.annotations.SerializedName

data class ResendOtpRequest(@SerializedName("mobilenumber") val identifier: String)

data class ResendOtpResponse(
    @SerializedName("respCode") val respCode: String? = null,
    @SerializedName("respMessage") val respMessage: String? = null,
)