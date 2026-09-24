package bsb.dev.bsb_bangking_jp.shared.profile.data

import bsb.dev.bsb_bangking_jp.core.crypto.SignatureUtils
import bsb.dev.bsb_bangking_jp.core.device.SecureStorageService
import bsb.dev.bsb_bangking_jp.core.network.header.ApiHeaders
import bsb.dev.bsb_bangking_jp.core.network.token.TokenPhase
import bsb.dev.bsb_bangking_jp.core.network.token.TokenPhaseTag
import bsb.dev.bsb_bangking_jp.core.session.ClearableRepository
import bsb.dev.bsb_bangking_jp.core.util.retry
import bsb.dev.bsb_bangking_jp.shared.profile.domain.ProfilePhotoRepository
import bsb.dev.bsb_bangking_jp.shared.profile.util.ProfilePhotoPathUtils

class ProfilePhotoRepositoryImpl(
    private val api: ProfilePhotoApiService,
    private val secureStorage: SecureStorageService,
) : ProfilePhotoRepository, ClearableRepository {

    private var cachedPhoto: ByteArray? = null
    private var isEmptyFromServer = false
    private var lastPath: String? = null // 🔹 supaya cache tidak bentrok user lain

    override val hasPhoto: Boolean get() = cachedPhoto != null
    override val isEmpty: Boolean get() = isEmptyFromServer

    override suspend fun getProfilePhoto(photoProfile: String?): ByteArray? {
        val path = ProfilePhotoPathUtils.extractImagePath(photoProfile)

        if (path.isEmpty()) {
            isEmptyFromServer = true
            cachedPhoto = null
            return null
        }
        if (cachedPhoto != null && lastPath == path) return cachedPhoto
        if (isEmptyFromServer && lastPath == path) return null

        val fresh = retry { fetchProfilePhoto(path) }
        lastPath = path

        if (fresh == null) {
            cachedPhoto = null
            isEmptyFromServer = true
            return null
        }

        cachedPhoto = fresh
        isEmptyFromServer = false
        return fresh
    }

    private suspend fun fetchProfilePhoto(path: String): ByteArray? {
        return try {
            val privateKey = secureStorage.getPrivateKey() ?: return null
            val timestamp = ApiHeaders.currentTimestamp()
            val signature = SignatureUtils.sign(emptyMap<String, String>(), timestamp, privateKey)
            val headers = ApiHeaders.withSignature(signature, ApiHeaders.full(timestamp))

            val response = api.getProfilePhoto(
                headers = headers,
                path = path,
                tokenPhase = TokenPhaseTag(TokenPhase.LOGIN),
            )
            if (response.code() >= 500) return null

            val bytes = response.body()?.bytes() ?: return null
            if (bytes.isEmpty()) return null

            val looksLikeJsonError = runCatching {
                String(bytes, Charsets.UTF_8).contains("respCode")
            }.getOrDefault(false)
            if (looksLikeJsonError) return null

            bytes
        } catch (e: Exception) {
            null
        }
    }

    override fun invalidate() = clear()

    override fun clear() {
        cachedPhoto = null
        isEmptyFromServer = false
        lastPath = null
    }
}