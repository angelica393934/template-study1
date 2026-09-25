package bsb.dev.bsb_bangking_jp.feature.set_photo_profile.data

import bsb.dev.bsb_bangking_jp.core.network.token.TokenPhaseTag
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.HTTP
import retrofit2.http.HeaderMap
import retrofit2.http.Multipart
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Tag

interface SetPhotoProfileApiService {

    @Multipart
    @PUT("v2/updatephotoprofilexm")
    suspend fun updatePhotoProfile(
        @HeaderMap headers: Map<String, String>,
        @Part photoProfile: MultipartBody.Part,
        @Tag tokenPhase: TokenPhaseTag,
    ): Response<UpdatePhotoProfileResponse>

    // Retrofit tidak punya @DELETE dengan body -> @HTTP manual (hasBody = true),
    // sama seperti pola SavedRecipientApiService.deleteSavedRecipient.
    @HTTP(method = "DELETE", path = "v2/deletephotoprofile", hasBody = true)
    suspend fun deletePhotoProfile(
        @HeaderMap headers: Map<String, String>,
        @Body body: DeletePhotoProfileRequest,
        @Tag tokenPhase: TokenPhaseTag,
    ): Response<DeletePhotoProfileResponse>
}