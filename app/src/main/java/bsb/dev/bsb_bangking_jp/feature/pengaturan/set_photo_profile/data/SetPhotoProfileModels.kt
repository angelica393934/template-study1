package bsb.dev.bsb_bangking_jp.feature.pengaturan.set_photo_profile.data

import com.google.gson.annotations.SerializedName

data class DeletePhotoProfileRequest(
    @SerializedName("userid") val userId: String,
)

data class UpdatePhotoProfileResponse(
    @SerializedName("respCode") val respCode: String? = null,
    @SerializedName("respMessage") val respMessage: String? = null,
)

data class DeletePhotoProfileResponse(
    @SerializedName("respCode") val respCode: String? = null,
    @SerializedName("respMessage") val respMessage: String? = null,
)