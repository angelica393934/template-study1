package bsb.dev.bsb_bangking_jp.feature.manage_scheduled_transfer.domain

import bsb.dev.bsb_bangking_jp.core.util.RupiahFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** Padanan ScheduledTransferItemModel (Dart) -- 1 item di daftar transfer terjadwal. */
data class ScheduledTransferItem(
    val id: Int,
    val beneficiaryName: String,
    val beneficiaryAccountNo: String,
    val beneficiaryBankName: String,
    val nextRunDate: Date,
    val amount: Int,
    val status: String,
    val canTogglePause: Boolean,
    val canDelete: Boolean,
    val sourceAccountNo: String,
) {
    // ===============================
    // UI HELPER
    // ===============================

    val bankInfo: String
        get() = "$beneficiaryBankName - $beneficiaryAccountNo"

    val tanggalFormatted: String
        get() = SimpleDateFormat("d MMMM yyyy", Locale("id", "ID")).format(nextRunDate)

    val nominalFormatted: String
        get() = RupiahFormat(amount)

    val statusLabel: String
        get() = when (status.uppercase()) {
            "ACTIVE" -> "Aktif"
            "PAUSED" -> "Dijeda"
            "RETRY_PENDING" -> "Memproses"
            else -> status
        }

    val isActive: Boolean
        get() = status.uppercase() == "ACTIVE"
}