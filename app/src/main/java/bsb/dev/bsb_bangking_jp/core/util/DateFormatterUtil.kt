package bsb.dev.bsb_bangking_jp.core.util
import bsb.dev.bsb_bangking_jp.feature.activity.data.HistoryItem
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

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

    private val indonesiaLocale = Locale("id", "ID")

    private val backendDateTimeFormatter =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    private val fullDateTimeFormatter =
        DateTimeFormatter.ofPattern(
            "dd MMMM yyyy - HH:mm",
            indonesiaLocale,
        )

    private val shortDateFormatter =
        DateTimeFormatter.ofPattern(
            "dd MMM",
            indonesiaLocale,
        )

    /** Padanan DateFormatterUtil.fromYYMMDD -- dipakai buat header tanggal di list transaksi.
         "260119" -> "19 Januari 2026" **/
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
    /** Penggunaan di detail notif
      "2026-01-19 10:14:26"  -> "19 Januari 2026 - 10:14 WIB"**/
    fun toFullDateTime(value: String): String {
        return try {
            val dateTime = LocalDateTime.parse(
                value,
                backendDateTimeFormatter,
            )

            "${dateTime.format(fullDateTimeFormatter)} WIB"
        } catch (e: Exception) {
            value
        }
    }
    /** Penggunaan di notif
      "2026-01-19 10:14:26" -> "19 Jan"" **/
    fun toShortDate(value: String): String {
        return try {
            val dateTime = LocalDateTime.parse(
                value,
                backendDateTimeFormatter,
            )

            dateTime.format(shortDateFormatter)
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