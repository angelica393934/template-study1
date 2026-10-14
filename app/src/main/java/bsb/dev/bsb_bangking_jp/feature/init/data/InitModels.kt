package bsb.dev.bsb_bangking_jp.feature.init.data

import com.google.gson.annotations.SerializedName

data class InitDeviceRequest(
    @SerializedName("devicename") val deviceName: String,
    @SerializedName("os") val os: String,
    @SerializedName("public_key") val publicKey: String,
    // Dikosongkan dulu untuk -- IMEI/IMSI/ICCID tidak bisa diakses
    @SerializedName("iccid") val iccid: String = "",
    @SerializedName("imei") val imei: String = "",
    @SerializedName("imsi") val imsi: String = "",)

data class InitDeviceResponse(
    @SerializedName("respCode") val respCode: String? = null,
    @SerializedName("respMessage") val respMessage: String? = null,
)