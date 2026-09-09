package bsb.dev.bsb_bangking_jp.feature.activity.domain

import bsb.dev.bsb_bangking_jp.core.filter.TransactionFilterPayload
import bsb.dev.bsb_bangking_jp.feature.activity.data.HistoryItem

interface ActivityHistoryRepository {
    val hasMore: Boolean
    val items: List<HistoryItem>

    suspend fun loadInitial(accountNumber: String, filter: TransactionFilterPayload): List<HistoryItem>
    suspend fun loadMore(accountNumber: String, filter: TransactionFilterPayload): List<HistoryItem>
}