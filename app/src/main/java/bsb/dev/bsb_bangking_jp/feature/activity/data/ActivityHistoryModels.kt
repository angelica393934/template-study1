package bsb.dev.bsb_bangking_jp.feature.activity.data

import com.google.gson.annotations.SerializedName

data class GetHistoryRequest(
    @SerializedName("accountNumber") val accountNumber: String,
    @SerializedName("limit") val limit: Int,
    @SerializedName("offset") val offset: Int,
    @SerializedName("fromDateTime") val fromDateTime: String? = null,
    @SerializedName("toDateTime") val toDateTime: String? = null,
    @SerializedName("quickRange") val quickRange: Int? = null,
    @SerializedName("jenis") val jenis: List<String>? = null,
    @SerializedName("category") val category: List<String>? = null,
)
data class ActivityHistoryResponse(
    @SerializedName("respCode") override val respCode: String? = null,
    @SerializedName("respMessage") val respMessage: String? = null,
    @SerializedName("data") val data: ActivityHistoryData = ActivityHistoryData(),
) : bsb.dev.bsb_bangking_jp.core.network.BaseRespCodeResponse

data class ActivityHistoryData(
    @SerializedName("number") val number: String = "",
    @SerializedName("history") val history: List<HistoryItem> = emptyList(),
)

data class HistoryAmount(
    @SerializedName("currency") val currency: String = "",
    @SerializedName("value") val value: String = "",
)

data class HistoryDetailBalance(
    @SerializedName("Amount") val amount: HistoryAmount = HistoryAmount(),
)

data class HistoryItem(
    @SerializedName("detailBalance") val detailBalance: HistoryDetailBalance = HistoryDetailBalance(),
    @SerializedName("remark") val remark: String = "",
    @SerializedName("transactionDate") val transactionDate: String = "", // format "yyMMdd"
    @SerializedName("transactionDetailStatus") val transactionDetailStatus: String = "",
    @SerializedName("transactionId") val transactionId: String = "",
    @SerializedName("type") val type: String = "",
    @SerializedName("transactionType") val transactionType: String = "",
    @SerializedName("jenisTransaksi") val jenisTransaksi: String = "",
    @SerializedName("kategoriTransaksi") val kategoriTransaksi: String = "",
) {
    val currency: String get() = detailBalance.amount.currency

    /** Nominal mentah dari backend, mis. "10000,00" (koma sebagai desimal). */
    val amount: String get() = detailBalance.amount.value

    /**
     * 🔹 BARU: parsing aman nominal -- backend pakai format Indonesia
     * (titik = pemisah ribuan, koma = desimal), mis. "10000,00" atau "1.500.000,00".
     * Jangan pakai `amount.toDoubleOrNull()` langsung karena Kotlin cuma paham
     * titik sebagai desimal -> akan selalu null utk format backend ini.
     */
    val amountValue: Double
        get() = amount
            .replace(".", "")   // buang pemisah ribuan
            .replace(",", ".")  // koma desimal -> titik desimal
            .toDoubleOrNull() ?: 0.0

    /** Padanan getter isMasuk -- true kalau kategoriTransaksi persis "berhasil, masuk". */
    val isMasuk: Boolean get() = kategoriTransaksi.lowercase() == "berhasil, masuk"

    /** Padanan getter arahTransaksi -- ambil kata terakhir setelah koma ("masuk"/"keluar") -> "dari"/"ke". */
    val arahTransaksi: String get() {
        if (kategoriTransaksi.isEmpty()) return ""
        val parts = kategoriTransaksi.split(",")
        if (parts.size < 2) return ""
        return when (parts.last().trim().lowercase()) {
            "masuk" -> "dari"
            "keluar" -> "ke"
            else -> ""
        }
    }

    /**
     * Padanan getter rekeningTujuan -- ambil angka setelah huruf 'S' terakhir di remark,
     * sampai sebelum '-'. Contoh: "100504     313000S15001001816 - Testing senin"
     * -> "15001001816".
     */
    val rekeningTujuan: String get() {
        if (remark.isEmpty()) return ""
        val indexS = remark.lastIndexOf('S')
        if (indexS == -1) return ""
        val afterS = remark.substring(indexS + 1)
        val indexStrip = afterS.indexOf('-')
        return if (indexStrip != -1) afterS.substring(0, indexStrip).trim() else afterS.trim()
    }

    /**
     * 🔹 BARU: teks bebas setelah tanda "-" TERAKHIR di remark -- ini catatan/keterangan
     * transaksi yang diketik user, mis. "100504 313000S15001001816 - Testing senin"
     * -> "Testing senin". Kosong kalau remark tidak punya "-".
     */
    val keteranganTransaksi: String get() {
        if (remark.isEmpty()) return ""
        val indexStrip = remark.lastIndexOf('-')
        return if (indexStrip != -1) remark.substring(indexStrip + 1).trim() else ""
    }

    /** Padanan getter deskripsiTransaksi -- "Jenis arah\nrekeningTujuan". */
    val deskripsiTransaksi: String get() {
        val jenis = if (jenisTransaksi.isNotEmpty()) {
            jenisTransaksi[0].uppercase() + jenisTransaksi.substring(1).lowercase()
        } else ""
        val arah = arahTransaksi
        val rekening = rekeningTujuan
        if (jenis.isEmpty() && rekening.isEmpty()) return ""
        return "$jenis $arah\n$rekening"
    }
}