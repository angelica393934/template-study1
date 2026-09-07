package bsb.dev.bsb_bangking_jp.feature.message.data

import bsb.dev.bsb_bangking_jp.core.network.BaseRespCodeResponse
import bsb.dev.bsb_bangking_jp.feature.message.domain.MessageItem
import com.google.gson.annotations.SerializedName
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

data class GetMessageRequest(
    @SerializedName("accountNumber") val accountNumber: String,
    @SerializedName("limit") val limit: Int,
    @SerializedName("offset") val offset: Int,
    @SerializedName("fromDateTime") val fromDateTime: String? = null,
    @SerializedName("toDateTime") val toDateTime: String? = null,
    @SerializedName("quickRange") val quickRange: Int? = null,
    @SerializedName("jenis") val jenis: List<String>? = null,
    @SerializedName("category") val category: List<String>? = null,
)

data class MessageHistoryResponse(
    @SerializedName("respCode") override val respCode: String? = null,
    @SerializedName("respMessage") val respMessage: String? = null,
    @SerializedName("data") val data: MessageHistoryData = MessageHistoryData(),
) : BaseRespCodeResponse

data class MessageHistoryData(
    @SerializedName("number") val number: String = "",
    @SerializedName("history") val history: List<MessageHistoryItemDto> = emptyList(),
)

/**
 * Field mengikuti persis contoh response backend terbaru (getmessage).
 * Catatan penting dibanding versi sebelumnya:
 * - "createddate": sekarang ISO 8601 dengan microsecond + suffix "Z" (UTC),
 *   contoh "2026-09-04T02:35:54.513097Z" -- BUKAN lagi format tanpa timezone.
 * - Field baru dari backend yang belum ditangkap sebelumnya:
 *   accountsourcename, accountdestinationname, scheduledtype, selecttransaction.
 * - "id" & "transaction_id" di sample selalu ada (Int), dipertahankan non-null dgn default 0.
 */
data class MessageHistoryItemDto(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("createddate") val createdDate: String? = null,
    @SerializedName("accountdestination") val accountDestination: String = "",
    @SerializedName("accountdestinationname") val accountDestinationName: String = "",
    @SerializedName("accountsourcename") val accountSourceName: String = "",
    @SerializedName("note") val note: String = "",
    @SerializedName("amount") val amount: Long = 0,
    @SerializedName("total_amount") val totalAmount: Long = 0,
    @SerializedName("adminfee") val adminFee: Long = 0,
    @SerializedName("status") val status: String = "",
    @SerializedName("jenis_transaksi") val jenisTransaksi: String = "",
    @SerializedName("kategori") val kategori: String = "",
    @SerializedName("type") val type: String = "",
    @SerializedName("bankname") val bankName: String = "",
    @SerializedName("bank_code") val bankCode: String = "",
    @SerializedName("transaction_id") val transactionId: Int = 0,
    @SerializedName("ScheduledTransferID") val scheduledTransferId: Int? = null,
    @SerializedName("scheduledtype") val scheduledType: String = "",
    @SerializedName("selecttransaction") val selectTransaction: String = "",
)

// 🔹 Parser khusus utk "yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'" (microsecond + UTC).
// SimpleDateFormat Java cuma paham 3 digit milidetik ("SSS"), bukan 6 digit
// microsecond -- makanya kita potong manual ke 23 karakter ("...HH:mm:ss.SSS")
// sebelum di-parse, dan set timeZone = UTC krn suffix "Z" artinya UTC, bukan
// timezone device.
private val isoDateTimeParser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.US).apply {
    timeZone = TimeZone.getTimeZone("UTC")
}

// Fallback kalau suatu saat backend kirim tanpa microsecond, mis. "...HH:mm:ss" atau "...HH:mm:ssZ".
private val isoDateTimeParserNoMillis = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US).apply {
    timeZone = TimeZone.getTimeZone("UTC")
}

private fun parseCreatedDate(raw: String?): Date {
    if (raw.isNullOrBlank()) return Date(0)

    // Buang suffix "Z" kalau ada -- sudah kita anggap UTC lewat timeZone di formatter.
    val withoutZ = raw.removeSuffix("Z")

    // Kalau ada microsecond ("....513097"), potong jadi 3 digit ("...513") biar cocok "SSS".
    val dotIndex = withoutZ.indexOf('.')
    val normalized = if (dotIndex != -1) {
        val fraction = withoutZ.substring(dotIndex + 1)
        val millis = fraction.take(3).padEnd(3, '0')
        withoutZ.substring(0, dotIndex) + "." + millis
    } else {
        withoutZ
    }

    return runCatching { isoDateTimeParser.parse(normalized) }.getOrNull()
        ?: runCatching { isoDateTimeParserNoMillis.parse(withoutZ.take(19)) }.getOrNull()
        ?: Date(0)
}

fun MessageHistoryItemDto.toDomain(): MessageItem = MessageItem(
    id = id,
    createdDate = parseCreatedDate(createdDate),
    accountDestination = accountDestination,
    accountDestinationName = accountDestinationName,
    accountSourceName = accountSourceName,
    note = note,
    amount = amount,
    totalAmount = totalAmount,
    adminFee = adminFee,
    status = status,
    jenisTransaksi = jenisTransaksi,
    kategori = kategori,
    type = type,
    bankName = bankName,
    bankCode = bankCode,
    transactionId = transactionId,
    scheduledTransferId = scheduledTransferId,
    scheduledType = scheduledType,
    selectTransaction = selectTransaction,
)