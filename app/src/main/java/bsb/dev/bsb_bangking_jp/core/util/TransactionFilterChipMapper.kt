package bsb.dev.bsb_bangking_jp.core.util

import bsb.dev.bsb_bangking_jp.core.components.FilterChipItem
import bsb.dev.bsb_bangking_jp.core.filter.TransactionFilterPayload

/**
 * Reusable untuk Aktivitas & message (sebelumnya TransactionFilterPayload, khusus Aktivitas).
 */
object TransactionFilterChipMapper {

    fun fromPayload(filter: TransactionFilterPayload?): List<FilterChipItem> {
        if (filter == null) return emptyList()

        if (filter.isDefaultDate && filter.isAllJenis && filter.isAllCategory && filter.quickRange == null) {
            return emptyList()
        }

        val chips = mutableListOf<FilterChipItem>()

        if (!filter.isDefaultDate && filter.quickRange == null &&
            filter.fromDate != null && filter.toDate != null
        ) {
            chips += FilterChipItem(key = "date", label = "${filter.fromDate} - ${filter.toDate}")
        }

        filter.quickRange?.let { range ->
            chips += FilterChipItem(key = "range", label = "$range Hari Terakhir")
        }

        if (!filter.isAllJenis) {
            filter.jenis?.forEach { j -> chips += FilterChipItem(key = "jenis:$j", label = pretty(j)) }
        }

        if (!filter.isAllCategory) {
            filter.category?.forEach { c -> chips += FilterChipItem(key = "category:$c", label = pretty(c)) }
        }

        return chips
    }

    fun removeChip(filter: TransactionFilterPayload, key: String): TransactionFilterPayload = when {
        key.startsWith("jenis:") -> {
            val value = key.substringAfter("jenis:")
            val updated = filter.jenis?.filterNot { it == value }
            filter.with(jenis = updated, resetJenis = updated.isNullOrEmpty(), isAllJenis = updated.isNullOrEmpty())
        }
        key.startsWith("category:") -> {
            val value = key.substringAfter("category:")
            val updated = filter.category?.filterNot { it == value }
            filter.with(category = updated, resetCategory = updated.isNullOrEmpty(), isAllCategory = updated.isNullOrEmpty())
        }
        key == "date" -> filter.with(resetFromDate = true, resetToDate = true, isDefaultDate = true)
        key == "range" -> filter.with(resetQuickRange = true, isDefaultDate = true)
        else -> filter
    }

    private fun pretty(value: String): String =
        value.replace("_", " ")
            .split(" ")
            .joinToString(" ") { word -> word.replaceFirstChar { it.uppercase() } }
}