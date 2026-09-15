package bsb.dev.bsb_bangking_jp.feature.manage_scheduled_transfer.domain

interface ScheduledTransferRepository {
    suspend fun getScheduledTransfers(forceRefresh: Boolean = false): List<ScheduledTransferItem>
    suspend fun getScheduledTransferDetail(id: Int, forceRefresh: Boolean = false): ScheduledTransferDetail
    suspend fun togglePause(id: Int): Result<Unit>
    suspend fun deleteScheduledTransfer(ids: List<Int>): Result<Unit>
}