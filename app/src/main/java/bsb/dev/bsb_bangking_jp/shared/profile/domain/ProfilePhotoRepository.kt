package bsb.dev.bsb_bangking_jp.shared.profile.domain

interface ProfilePhotoRepository {
    val hasPhoto: Boolean
    val isEmpty: Boolean
    suspend fun getProfilePhoto(photoProfile: String?): ByteArray?

    /** Dipanggil dari luar (mis. setelah update/delete foto sukses) untuk paksa fetch ulang. */
    fun invalidate()
}