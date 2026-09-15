package bsb.dev.bsb_bangking_jp.feature.manage_scheduled_transfer.util

/** Padanan MonthTranslator (Dart) -- "October 2026" -> "Oktober 2026". */
object MonthTranslator {
    private val monthMap = mapOf(
        "january" to "Januari",
        "february" to "Februari",
        "march" to "Maret",
        "april" to "April",
        "may" to "Mei",
        "june" to "Juni",
        "july" to "Juli",
        "august" to "Agustus",
        "september" to "September",
        "october" to "Oktober",
        "november" to "November",
        "december" to "Desember",
    )

    fun toIndonesian(value: String): String {
        if (value.isEmpty()) return "-"

        val parts = value.trim().split(" ")
        if (parts.size != 2) return value

        val month = parts[0].lowercase()
        val year = parts[1]

        val translatedMonth = monthMap[month] ?: return value
        return "$translatedMonth $year"
    }
}