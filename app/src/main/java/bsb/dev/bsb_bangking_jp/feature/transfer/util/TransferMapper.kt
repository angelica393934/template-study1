package bsb.dev.bsb_bangking_jp.feature.transfer.util

import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Padanan utils/transfer_mapper.dart. Kode `service` dan format tanggal DI BAWAH
 * masih ASUMSI (tidak terlihat di source Dart yang dikirim) -- WAJIB dicek ulang
 * ke tim backend sebelum production.
 */
object TransferMapper {

    private val bulanIndonesia = listOf(
        "Januari", "Februari", "Maret", "April", "Mei", "Juni",
        "Juli", "Agustus", "September", "Oktober", "November", "Desember",
    )
    private val isoDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    /** Padanan mapScheduleType(). */
    fun mapScheduleType(isScheduled: Boolean): String =
        if (isScheduled) "SCHEDULED" else "IMMEDIATE"

    /** Padanan mapFrequency(). */
    fun mapFrequency(frekuensi: String): String =
        if (frekuensi.equals("Sekali", ignoreCase = true)) "ONCE" else "MONTHLY"


    // 3 label yang ada di TransferFormPage (Transfer Online / Transfer BI-FAST / Transfer Sesama).
    fun mapService(layananTransfer: String): String = when {
        layananTransfer.equals("Transfer Online", ignoreCase = true) -> "TRANSFER_ONLINE"
        layananTransfer.equals("Transfer BI-FAST", ignoreCase = true) -> "TRANSFER_BIFAST"
        else -> "TRANSFER_ONUS" // Transfer Sesama BSB
    }

    /**
     * Padanan mapSchedulePayload() -- balikin Pair(scheduleDate, endOfMonth).
     * - "Setiap Bulan" + "Akhir Bulan" -> endOfMonth = true, scheduleDate = null
     * - "Setiap Bulan" + angka hari ("1".."28") -> scheduleDate = angka itu
     * - "Sekali" + tanggal penuh ("17 Agustus 2026") -> scheduleDate = ISO "yyyy-MM-dd"
     */
    fun mapSchedulePayload(isScheduled: Boolean, tanggal: String): Pair<String?, Boolean?> {
        if (!isScheduled || tanggal.isBlank()) return null to null

        if (tanggal.equals("Akhir Bulan", ignoreCase = true)) return null to true

        val asDay = tanggal.toIntOrNull()
        if (asDay != null) return tanggal to false

        val parsedDate = parseIndonesianDate(tanggal) ?: return tanggal to null
        return parsedDate.format(isoDateFormatter) to false
    }
    private fun parseIndonesianDate(value: String): LocalDate? {
        val parts = value.trim().split(" ")
        if (parts.size != 3) return null
        val day = parts[0].toIntOrNull() ?: return null
        val monthIndex = bulanIndonesia.indexOfFirst { it.equals(parts[1], ignoreCase = true) }
        val year = parts[2].toIntOrNull() ?: return null
        if (monthIndex == -1) return null
        return runCatching { LocalDate.of(year, monthIndex + 1, day) }.getOrNull()
    }

    /**
     * Padanan formatMonthYearToEnglish() di Dart -- "Agustus 2026" -> "August 2026"
     * (terjemahkan nama bulan Indonesia ke Inggris; TAHUN & format tetap "Bulan tahun",
     * BUKAN dikonversi ke ISO date seperti versi lama).
     */
    private val bulanIndonesiaKeInggris = mapOf(
        "januari" to "January",
        "februari" to "February",
        "maret" to "March",
        "april" to "April",
        "mei" to "May",
        "juni" to "June",
        "juli" to "July",
        "agustus" to "August",
        "september" to "September",
        "oktober" to "October",
        "november" to "November",
        "desember" to "December",
    )

    fun formatMonthYearToEnglish(bulanTahun: String): String? {
        if (bulanTahun.isEmpty()) return null

        val parts = bulanTahun.trim().lowercase().split(" ")
        if (parts.size != 2) return bulanTahun

        val month = bulanIndonesiaKeInggris[parts[0]]
        val year = parts[1]

        if (month == null) return bulanTahun

        return "$month $year"
    }

}