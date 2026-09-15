package bsb.dev.bsb_bangking_jp.feature.manage_scheduled_transfer.data

import bsb.dev.bsb_bangking_jp.feature.manage_scheduled_transfer.domain.ScheduledTransferDetail
import bsb.dev.bsb_bangking_jp.feature.manage_scheduled_transfer.domain.ScheduledTransferItem
import com.google.gson.annotations.SerializedName
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

// ============================================================
// 1) GET LIST
// ============================================================

data class ScheduledTransferListResponse(
    @SerializedName("respCode") val respCode: String? = null,
    @SerializedName("respMessage") val respMessage: String? = null,
    @SerializedName("data") val data: List<ScheduledTransferItemDto> = emptyList(),
)

data class ScheduledTransferItemDto(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("beneficiary_account_name") val beneficiaryAccountName: String = "",
    @SerializedName("beneficiary_account_no") val beneficiaryAccountNo: String = "",
    @SerializedName("beneficiary_bank_name") val beneficiaryBankName: String = "",
    @SerializedName("next_run_date") val nextRunDate: String? = null,
    @SerializedName("amount") val amount: Int = 0,
    @SerializedName("status") val status: String = "",
    @SerializedName("can_toggle_pause") val canTogglePause: Boolean = false,
    @SerializedName("can_delete") val canDelete: Boolean = false,
    @SerializedName("source_account_no") val sourceAccountNo: String = "",
)

fun ScheduledTransferItemDto.toDomain(): ScheduledTransferItem = ScheduledTransferItem(
    id = id,
    beneficiaryName = beneficiaryAccountName,
    beneficiaryAccountNo = beneficiaryAccountNo,
    beneficiaryBankName = beneficiaryBankName,
    nextRunDate = parseScheduledDate(nextRunDate) ?: Date(0),
    amount = amount,
    status = status,
    canTogglePause = canTogglePause,
    canDelete = canDelete,
    sourceAccountNo = sourceAccountNo,
)

// ============================================================
// 2) GET DETAIL
// ============================================================

data class ScheduledTransferIdRequest(
    @SerializedName("id") val id: Int,
)

data class ScheduledTransferDetailResponse(
    @SerializedName("respCode") val respCode: String? = null,
    @SerializedName("respMessage") val respMessage: String? = null,
    @SerializedName("data") val data: ScheduledTransferDetailDto? = null,
)

data class ScheduledTransferDetailDto(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("source_account_no") val sourceAccountNo: String = "",
    @SerializedName("beneficiary_account_name") val beneficiaryAccountName: String = "",
    @SerializedName("beneficiary_account_no") val beneficiaryAccountNo: String = "",
    @SerializedName("beneficiary_bank_name") val beneficiaryBankName: String = "",
    @SerializedName("next_run_date") val nextRunDate: String? = null,
    @SerializedName("schedule_type") val scheduleType: String = "ONCE",
    @SerializedName("amount") val amount: Int = 0,
    @SerializedName("remark") val remark: String? = null,
    @SerializedName("status") val status: String = "",
    @SerializedName("startdate") val startDate: String? = null,
    @SerializedName("enddate") val endDate: String? = null,
    @SerializedName("scheduled") val scheduled: String? = null,
    @SerializedName("can_toggle_pause") val canTogglePause: Boolean = false,
    @SerializedName("can_delete") val canDelete: Boolean = false,
)

fun ScheduledTransferDetailDto.toDomain(): ScheduledTransferDetail = ScheduledTransferDetail(
    id = id,
    sourceAccountNo = sourceAccountNo,
    beneficiaryName = beneficiaryAccountName,
    beneficiaryAccountNo = beneficiaryAccountNo,
    beneficiaryBankName = beneficiaryBankName,
    nextRunDate = parseScheduledDate(nextRunDate),
    scheduleType = scheduleType,
    amount = amount,
    remark = remark.orEmpty(),
    status = status,
    startDate = startDate,
    endDate = endDate,
    scheduled = scheduled,
    canTogglePause = canTogglePause,
    canDelete = canDelete,
)

// ============================================================
// 3) TOGGLE PAUSE / 4) DELETE -- response sama, cuma respCode/respMessage
// ============================================================

data class ScheduledTransferActionResponse(
    @SerializedName("respCode") val respCode: String? = null,
    @SerializedName("respMessage") val respMessage: String? = null,
)

data class DeleteScheduledTransferRequest(
    // selalu array, padanan Dio `data: {"id": ids}`
    @SerializedName("id") val id: List<Int>,
)

// ============================================================
// Helper parsing tanggal -- toleran terhadap beberapa format ISO 8601
// ============================================================

private val isoWithMillisUtc = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.US).apply {
    timeZone = TimeZone.getTimeZone("UTC")
}
private val isoNoMillisUtc = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US).apply {
    timeZone = TimeZone.getTimeZone("UTC")
}
private val isoDateOnly = SimpleDateFormat("yyyy-MM-dd", Locale.US)

private fun parseScheduledDate(raw: String?): Date? {
    if (raw.isNullOrBlank()) return null

    val withoutZ = raw.removeSuffix("Z")
    val dotIndex = withoutZ.indexOf('.')
    val normalized = if (dotIndex != -1) {
        val millis = withoutZ.substring(dotIndex + 1).take(3).padEnd(3, '0')
        withoutZ.substring(0, dotIndex) + "." + millis
    } else {
        withoutZ
    }

    return runCatching { isoWithMillisUtc.parse(normalized) }.getOrNull()
        ?: runCatching { isoNoMillisUtc.parse(withoutZ.take(19)) }.getOrNull()
        ?: runCatching { isoDateOnly.parse(withoutZ.take(10)) }.getOrNull()
}