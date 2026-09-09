package bsb.dev.bsb_bangking_jp.feature.transfer.transfer_core.domain

import java.util.Date

data class ConfirmTransferResultItem(
    val reffNum: String,
    val transactionDate: Date,
    val beneficiaryName: String,
    val beneficiaryBankName: String,
    val beneficiaryAccountNo: String,
    val senderName: String,
    val senderAccountNo: String,
    val amount: Int,
    val adminFee: Int,
    val totalDebit: Int,
    val remark: String?,
    /** "IMMEDIATE" atau "SCHEDULED". */
    val scheduleType: String,
    /** "ONCE" atau "MONTHLY" -- null kalau scheduleType == "IMMEDIATE". */
    val frequency: String?,
    val scheduleDate: String?,
    val startMonth: String?,
    val endMonth: String?,
)