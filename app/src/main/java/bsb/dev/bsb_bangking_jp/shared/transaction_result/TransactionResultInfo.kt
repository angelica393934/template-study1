package bsb.dev.bsb_bangking_jp.shared.transaction_result

import java.util.Date

enum class TransactionResultStatus { SUCCESS, FAILED, PENDING, SCHEDULED }

/**
 * Model generik hasil transaksi -- dipakai bareng oleh alur confirmTransfer (Transfer)
 * dan detail message (getmessagebyid), supaya UI-nya reusable dan tidak terikat
 * ke satu fitur saja.
 */
data class TransactionResultInfo(
    val status: TransactionResultStatus,
    val transactionDate: Date,
    val referenceNumber: String,
    val beneficiaryName: String,
    val beneficiaryBankName: String,
    val beneficiaryAccountNo: String,
    val senderName: String? = null,
    val senderAccountNo: String? = null,
    val amount: Long,
    val adminFee: Long,
    val totalDebit: Long,
    val remark: String? = null,
    /** Label layanan, mis. "Transfer Sesama" / "TRANSFER_ONUS" -- ditampilkan apa adanya. */
    val serviceLabel: String? = null,
)