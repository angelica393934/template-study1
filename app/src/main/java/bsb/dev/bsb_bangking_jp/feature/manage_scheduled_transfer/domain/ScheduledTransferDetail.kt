package bsb.dev.bsb_bangking_jp.feature.manage_scheduled_transfer.domain

import bsb.dev.bsb_bangking_jp.core.util.RupiahFormat
import bsb.dev.bsb_bangking_jp.feature.manage_scheduled_transfer.util.MonthTranslator
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** Padanan ScheduledTransferDetailModel (Dart) -- detail 1 transfer terjadwal. */
data class ScheduledTransferDetail(
    val id: Int,
    val sourceAccountNo: String,
    val beneficiaryName: String,
    val beneficiaryAccountNo: String,
    val beneficiaryBankName: String,
    val nextRunDate: Date?,
    val scheduleType: String, // "ONCE" / "MONTHLY"
    val amount: Int,
    val remark: String,
    val status: String,
    val startDate: String?,
    val endDate: String?,
    val scheduled: String?,
    val canTogglePause: Boolean,
    val canDelete: Boolean,
) {
    // ===============================
    // UI HELPER
    // ===============================

    val nominalFormatted: String
        get() = RupiahFormat(amount)

    val tanggalFormatted: String
        get() = nextRunDate?.let { SimpleDateFormat("d MMMM yyyy", Locale("id", "ID")).format(it) } ?: "-"

    val statusLabel: String
        get() = when (status.uppercase()) {
            "ACTIVE" -> "Aktif"
            "PAUSED" -> "Dijeda"
            "RETRY_PENDING" -> "Memproses"
            else -> status
        }

    /** Logic status -- JANGAN pakai statusLabel untuk pengecekan kondisi. */
    val isActive: Boolean
        get() = status.uppercase() == "ACTIVE"

    val scheduleTypeLabel: String
        get() = when (scheduleType) {
            "MONTHLY" -> "Setiap Bulan"
            "ONCE" -> "Sekali"
            else -> scheduleType
        }

    val startDateFormatted: String
        get() = startDate?.takeIf { it.isNotEmpty() }?.let { MonthTranslator.toIndonesian(it) } ?: "-"

    val endDateFormatted: String
        get() = endDate?.takeIf { it.isNotEmpty() }?.let { MonthTranslator.toIndonesian(it) } ?: "-"

    val scheduledLabel: String
        get() = when (scheduled) {
            "Tanggal Akhir Bulan" -> "Setiap Akhir Bulan"
            else -> scheduled ?: "-"
        }
}