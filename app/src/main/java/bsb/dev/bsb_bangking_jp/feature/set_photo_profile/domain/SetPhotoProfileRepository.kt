package bsb.dev.bsb_bangking_jp.feature.set_photo_profile.domain

import java.io.File

interface SetPhotoProfileRepository {
    /** Padanan PhotoProfileService.updatePhotoProfile() -- PUT multipart. */
    suspend fun updatePhotoProfile(imageFile: File): Result<Unit>

    /** Padanan PhotoProfileService.deletePhotoProfile() -- DELETE dengan body userid. */
    suspend fun deletePhotoProfile(userId: String): Result<Unit>
}