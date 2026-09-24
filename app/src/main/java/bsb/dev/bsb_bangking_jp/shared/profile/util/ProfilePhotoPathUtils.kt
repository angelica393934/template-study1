package bsb.dev.bsb_bangking_jp.shared.profile.util

object ProfilePhotoPathUtils {
    /** Ambil path relatif dari nilai mentah `photoprofile` yang dikirim backend. */
    fun extractImagePath(rawPath: String?): String {
        if (rawPath.isNullOrBlank()) return ""

        return if (rawPath.contains("://")) {
            rawPath.substringAfter("://").substringAfter("/", missingDelimiterValue = "")
        } else {
            rawPath.trim()
        }
    }
}