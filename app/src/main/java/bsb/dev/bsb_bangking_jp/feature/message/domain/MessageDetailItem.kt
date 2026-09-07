package bsb.dev.bsb_bangking_jp.feature.message.domain

import java.util.Date

/** Padanan domain model dari getmessagebyid -- dipakai untuk menampilkan hasil transaksi. */
data class MessageDetailItem(
    val id: Int,
    val transactionDate: Date,
    val status: String,
    val jenisTransaksi: String,
    val beneficiaryName: String,
    val beneficiaryBankName: String,
    val beneficiaryAccountNo: String,
    val senderName: String,
    val amount: Long,
    val adminFee: Long,
    val totalDebit: Long,
    val remark: String?,
    val referenceNumber: String,
)