package bsb.dev.bsb_bangking_jp.shared.profile.util

object ProfilePhotoPathUtils {

    /**
     * Mengambil path user dari nilai photoprofile backend.
     * Contoh:
     * /v1/image/user/xxxx/profile.jpg
     * -> user/xxxx/profile.jpg
     */
    fun extractImagePath(rawPath: String?): String {
        if (rawPath.isNullOrBlank()) return ""

        val path = rawPath.trim()

        return path.substringAfter(
            "/user/",
            missingDelimiterValue = ""
        ).let { userPath ->
            if (userPath.isNotEmpty()) {
                "user/$userPath"
            } else {
                ""
            }
        }
    }
}