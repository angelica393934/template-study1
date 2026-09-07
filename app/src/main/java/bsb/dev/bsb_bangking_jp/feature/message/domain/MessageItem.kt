package bsb.dev.bsb_bangking_jp.feature.message.domain

import java.util.Date

data class MessageItem(
    val id: Int,
    val createdDate: Date,
    val accountDestination: String,
    val accountDestinationName: String,
    val accountSourceName: String,
    val note: String,
    val amount: Long,
    val totalAmount: Long,
    val adminFee: Long,
    val status: String,
    val jenisTransaksi: String,
    val kategori: String,
    val type: String,
    val bankName: String,
    val bankCode: String,
    val transactionId: Int,
    val scheduledTransferId: Int?,
    val scheduledType: String,
    val selectTransaction: String,
)