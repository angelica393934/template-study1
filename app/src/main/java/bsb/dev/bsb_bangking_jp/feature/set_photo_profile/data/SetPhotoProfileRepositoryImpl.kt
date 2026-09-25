package bsb.dev.bsb_bangking_jp.feature.set_photo_profile.data

import bsb.dev.bsb_bangking_jp.core.device.SecureStorageService
import bsb.dev.bsb_bangking_jp.core.network.ApiErrorParser
import bsb.dev.bsb_bangking_jp.core.network.ApiException
import bsb.dev.bsb_bangking_jp.core.network.NetworkErrorMapper
import bsb.dev.bsb_bangking_jp.core.network.header.ApiHeaders
import bsb.dev.bsb_bangking_jp.core.network.token.TokenPhase
import bsb.dev.bsb_bangking_jp.core.network.token.TokenPhaseTag
import bsb.dev.bsb_bangking_jp.feature.set_photo_profile.domain.SetPhotoProfileRepository
import bsb.dev.bsb_bangking_jp.shared.profile.domain.ProfilePhotoRepository
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

private const val SUCCESS_CODE = "0000"

// MIME yang di-allow (sesuai backend) -- padanan `_allowedMime` di Dart.
private val ALLOWED_MIME_TYPES = setOf(
    "image/jpeg", "image/jpg", "image/png", "image/webp", "image/heic", "image/heif",
)

class SetPhotoProfileRepositoryImpl(
    private val api: SetPhotoProfileApiService,
    private val secureStorage: SecureStorageService,
    private val profilePhotoRepository: ProfilePhotoRepository, // 🔹 dari shared/profile, cuma buat invalidate cache
) : SetPhotoProfileRepository {

    override suspend fun updatePhotoProfile(imageFile: File): Result<Unit> {
        return try {
            val mimeType = guessMimeType(imageFile)
            if (mimeType == null || mimeType !in ALLOWED_MIME_TYPES) {
                return Result.failure(ApiException("9999", "Format file tidak didukung"))
            }

            val requestBody = imageFile.asRequestBody(mimeType.toMediaTypeOrNull())
            val part = MultipartBody.Part.createFormData(
                name = "photoProfile",
                filename = imageFile.name,
                body = requestBody,
            )

            val response = api.updatePhotoProfile(
                headers = ApiHeaders.fullWithoutContentType(),
                photoProfile = part,
                tokenPhase = TokenPhaseTag(TokenPhase.LOGIN),
            )

            if (!response.isSuccessful) {
                return Result.failure(ApiErrorParser.parse(response))
            }

            val body = response.body()
            if (body?.respCode != SUCCESS_CODE) {
                return Result.failure(
                    ApiException(body?.respCode, body?.respMessage ?: "Gagal update foto profil")
                )
            }

            // 🔹 Foto lama di cache shared/profile sudah tidak valid.
            profilePhotoRepository.invalidate()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(ApiException(null, NetworkErrorMapper.toUserMessage(e)))
        }
    }

    override suspend fun deletePhotoProfile(userId: String): Result<Unit> {
        return try {
            val response = api.deletePhotoProfile(
                headers = ApiHeaders.full(),
                body = DeletePhotoProfileRequest(userId = userId),
                tokenPhase = TokenPhaseTag(TokenPhase.LOGIN),
            )

            if (!response.isSuccessful) {
                return Result.failure(ApiErrorParser.parse(response))
            }

            val body = response.body()
            if (body?.respCode != SUCCESS_CODE) {
                return Result.failure(
                    ApiException(body?.respCode, body?.respMessage ?: "Gagal menghapus foto profil")
                )
            }

            profilePhotoRepository.invalidate()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(ApiException(null, NetworkErrorMapper.toUserMessage(e)))
        }
    }

    private fun guessMimeType(file: File): String? = when (file.extension.lowercase()) {
        "jpg", "jpeg" -> "image/jpeg"
        "png" -> "image/png"
        "webp" -> "image/webp"
        "heic" -> "image/heic"
        "heif" -> "image/heif"
        else -> null
    }
}