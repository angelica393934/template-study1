package bsb.dev.bsb_bangking_jp.core.util
import bsb.dev.bsb_bangking_jp.feature.activity.data.HistoryItem
import java.time.LocalDate

private val indonesianMonths = listOf(
    "Januari", "Februari", "Maret", "April", "Mei", "Juni",
    "Juli", "Agustus", "September", "Oktober", "November", "Desember",
)

/**
 * Padanan top-level function `parseYYMMDD()` di  -- parsing manual per-substring
 * (BUKAN lewat DateTimeFormatter, karena formatnya bukan pola tanggal standar).
 * "yy" diasumsikan selalu abad 2000-an (di-prefix "20").
 */
fun parseYYMMDD(value: String): LocalDate {
    val year = "20${value.substring(0, 2)}".toInt()
    val month = value.substring(2, 4).toInt()
    val day = value.substring(4, 6).toInt()
    return LocalDate.of(year, month, day)
}

/** Padanan class DateFormatterUtil di . */
object DateFormatterUtil {

    /** Padanan DateFormatterUtil.fromYYMMDD -- dipakai buat header tanggal di list transaksi. */
    fun fromYYMMDD(value: String): String {
        if (value.length != 6) return value

        return try {
            val year = "20${value.substring(0, 2)}".toInt()
            val month = value.substring(2, 4).toInt()
            val day = value.substring(4, 6).toInt()

            "$day ${indonesianMonths[month - 1]} $year"
        } catch (e: Exception) {
            value
        }
    }
}

/** Padanan groupByDateSorted() -- urutkan transaksi terbaru -> terlama, lalu group per tanggal. */
fun groupByDateSortedDesc(items: List<HistoryItem>): Map<String, List<HistoryItem>> {
    val sorted = items.sortedByDescending { parseYYMMDD(it.transactionDate) }
    return sorted.groupBy { it.transactionDate }
}