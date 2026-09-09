package bsb.dev.bsb_bangking_jp.feature.activity.presentation

import bsb.dev.bsb_bangking_jp.core.filter.TransactionFilterPayload
import bsb.dev.bsb_bangking_jp.feature.activity.data.HistoryItem

data class ActivityHistoryUiState(
    val accountNumber: String? = null,
    val isLoading: Boolean = false,
    val isLoadMore: Boolean = false,
    val items: List<HistoryItem> = emptyList(),
    val hasMore: Boolean = true,
    val error: String? = null,
    val activeFilter: TransactionFilterPayload? = null,
)