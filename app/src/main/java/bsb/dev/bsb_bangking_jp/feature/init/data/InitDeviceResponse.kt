package bsb.dev.bsb_bangking_jp.feature.init.data

import com.google.gson.annotations.SerializedName

data class InitDeviceResponse(
    @SerializedName("status") val status: String? = null,
    @SerializedName("message") val message: String? = null,
)