package bsb.dev.bsb_bangking_jp.feature.message.data

import bsb.dev.bsb_bangking_jp.core.network.BaseRespCodeResponse
import bsb.dev.bsb_bangking_jp.feature.message.domain.MessageDetailItem
import com.google.gson.annotations.SerializedName
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class GetMessageByIdRequest(
    @SerializedName("id") val id: Int,
)

data class MessageDetailResponse(
    @SerializedName("respCode") override val respCode: String? = null,
    @SerializedName("respMessage") val respMessage: String? = null,
    @SerializedName("data") val data: MessageDetailDto = MessageDetailDto(),
) : BaseRespCodeResponse

data class MessageDetailDto(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("createddate") val createdDate: String? = null,
    @SerializedName("accountdestination") val accountDestination: String = "",
    @SerializedName("note") val note: String = "",
    @SerializedName("adminfee") val adminFee: Long = 0,
    @SerializedName("bank_code") val bankCode: String = "",
    @SerializedName("transaction_id") val transactionId: Int = 0,
    @SerializedName("status") val status: String = "",
    @SerializedName("ScheduledTransferID") val scheduledTransferId: Int? = null,
    @SerializedName("jenis_transaksi") val jenisTransaksi: String = "",
    @SerializedName("kategori") val kategori: String = "",
    @SerializedName("amount") val amount: Long = 0,
    @SerializedName("total_amount") val totalAmount: Long = 0,
    @SerializedName("type") val type: String = "",
    @SerializedName("bankname") val bankName: String = "",
    @SerializedName("accountsourcename") val accountSourceName: String = "",
    @SerializedName("accountdestinationname") val accountDestinationName: String = "",
    @SerializedName("scheduledtype") val scheduledType: String = "",
    @SerializedName("selecttransaction") val selectTransaction: String = "",
)

private val isoDateTimeParser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)

fun MessageDetailDto.toDomain(): MessageDetailItem = MessageDetailItem(
    id = id,
    // 🔹 "2026-09-04T02:35:54.513097Z" -- ambil 19 char pertama, sama pola dengan
    // NewsDetailModels/MessageHistoryModels supaya konsisten di seluruh project.
    transactionDate = createdDate?.let { runCatching { isoDateTimeParser.parse(it.take(19)) }.getOrNull() } ?: Date(0),
    status = status,
    jenisTransaksi = jenisTransaksi,
    beneficiaryName = accountDestinationName,
    beneficiaryBankName = bankName,
    beneficiaryAccountNo = accountDestination,
    senderName = accountSourceName,
    amount = amount,
    adminFee = adminFee,
    totalDebit = totalAmount,
    remark = note.takeIf { it.isNotBlank() },
    referenceNumber = transactionId.toString(),
)