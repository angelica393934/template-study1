package bsb.dev.bsb_bangking_jp.feature.activity.data

import bsb.dev.bsb_bangking_jp.core.filter.TransactionFilterPayload
import bsb.dev.bsb_bangking_jp.core.network.ApiException
import bsb.dev.bsb_bangking_jp.core.network.GetWithBodyApiHelper
import bsb.dev.bsb_bangking_jp.core.network.NetworkErrorMapper
import bsb.dev.bsb_bangking_jp.core.session.ClearableRepository
import bsb.dev.bsb_bangking_jp.core.util.BackendDateTimeUtil
import bsb.dev.bsb_bangking_jp.core.util.retry
import bsb.dev.bsb_bangking_jp.feature.activity.domain.ActivityHistoryRepository

private const val SUCCESS_CODE = "0000"
private const val LIMIT = 10

class ActivityHistoryRepositoryImpl(
    private val apiHelper: GetWithBodyApiHelper,
) : ActivityHistoryRepository, ClearableRepository {

    private val _items = mutableListOf<HistoryItem>()
    private var offset = 0
    private var _hasMore = true

    override val hasMore: Boolean get() = _hasMore
    override val items: List<HistoryItem> get() = _items.toList()

    override suspend fun loadInitial(
        accountNumber: String,
        filter: TransactionFilterPayload,
    ): List<HistoryItem> {
        reset()
        val fresh = retry { fetchHistory(accountNumber, filter) }
        _items.addAll(fresh)
        _hasMore = fresh.size == LIMIT
        offset += LIMIT
        return items
    }

    override suspend fun loadMore(
        accountNumber: String,
        filter:TransactionFilterPayload,
    ): List<HistoryItem> {
        if (!_hasMore) return items
        val fresh = fetchHistory(accountNumber, filter)
        _items.addAll(fresh)
        _hasMore = fresh.size == LIMIT
        offset += LIMIT
        return items
    }

    private suspend fun fetchHistory(
        accountNumber: String,
        filter:TransactionFilterPayload,
    ): List<HistoryItem> {
        try{
        val body = GetHistoryRequest(
            accountNumber = accountNumber,
            limit = LIMIT,
            offset = offset,
            fromDateTime = filter.fromDate?.let { BackendDateTimeUtil.startOfDay(it) },
            toDateTime = filter.toDate?.let {
                if (filter.isDefaultDate) BackendDateTimeUtil.now() else BackendDateTimeUtil.endOfDay(it)
            },
            quickRange = filter.quickRange,
            jenis = filter.jenis?.takeIf { it.isNotEmpty() },
            category = filter.category?.takeIf { it.isNotEmpty() },
        )

        val response = apiHelper.execute(
            path = "v1/dashboard/gethistory",
            body = body,
            responseType = ActivityHistoryResponse::class.java,
        )

        if (response.respCode != SUCCESS_CODE) {
            throw ApiException(response.respCode, "Gagal memuat riwayat aktivitas.")
        }

        return response.data.history
        } catch (e: ApiException) {
            throw e
        } catch (e: Exception) {
            throw ApiException(null, NetworkErrorMapper.toUserMessage(e))
        }
    }

    override fun clear() = reset()

    private fun reset() {
        _items.clear()
        offset = 0
        _hasMore = true
    }
}