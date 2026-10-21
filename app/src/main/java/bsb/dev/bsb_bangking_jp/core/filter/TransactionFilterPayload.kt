package bsb.dev.bsb_bangking_jp.core.filter

import bsb.dev.bsb_bangking_jp.core.util.DefaultRangeDate

/**
 * Payload filter generik untuk transaksi/message — dipakai bareng oleh Aktivitas & message.
 */
data class TransactionFilterPayload(
    val fromDate: String? = null,
    val toDate: String? = null,
    val quickRange: Int? = null,
    val jenis: List<String>? = null,
    val category: List<String>? = null,
    val isAllJenis: Boolean = true,
    val isAllCategory: Boolean = true,
    val isDefaultDate: Boolean = true,
    val label: String = "",
) {
    companion object {
        fun initial(): TransactionFilterPayload {
            val def = DefaultRangeDate.getCurrentMonth()
            return TransactionFilterPayload(
                fromDate = def.from,
                toDate = def.to,
                label = "Semua Transaksi",
            )
        }
    }

    fun with(
        fromDate: String? = this.fromDate,
        resetFromDate: Boolean = false,
        toDate: String? = this.toDate,
        resetToDate: Boolean = false,
        quickRange: Int? = this.quickRange,
        resetQuickRange: Boolean = false,
        jenis: List<String>? = this.jenis,
        resetJenis: Boolean = false,
        category: List<String>? = this.category,
        resetCategory: Boolean = false,
        isAllJenis: Boolean = this.isAllJenis,
        isAllCategory: Boolean = this.isAllCategory,
        isDefaultDate: Boolean = this.isDefaultDate,
        label: String = this.label,
    ): TransactionFilterPayload = TransactionFilterPayload(
        fromDate = if (resetFromDate) null else fromDate,
        toDate = if (resetToDate) null else toDate,
        quickRange = if (resetQuickRange) null else quickRange,
        jenis = if (resetJenis) null else jenis,
        category = if (resetCategory) null else category,
        isAllJenis = isAllJenis,
        isAllCategory = isAllCategory,
        isDefaultDate = isDefaultDate,
        label = label,
    )
}