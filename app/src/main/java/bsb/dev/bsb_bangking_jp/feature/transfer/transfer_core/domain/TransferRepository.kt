package bsb.dev.bsb_bangking_jp.feature.transfer.transfer_core.domain

import bsb.dev.bsb_bangking_jp.feature.transfer.transfer_core.domain.ConfirmTransferResultItem

interface TransferRepository {
    suspend fun getAccountDest(code: String, accountNumber: String): Result<TransferInquiry>
    suspend fun saveRecipient(alias: String): Result<Unit>
    suspend fun transfer(request: TransferRequestPayload): Result<TransferResult>
    suspend fun confirmTransfer(mobilePin: String): Result<ConfirmTransferResultItem>
    suspend fun getTransferPurpose(): Result<List<TransferPurpose>>
}

/** Padanan parameter TransferEvent.transfer(), dikumpulkan jadi satu payload. */
data class TransferRequestPayload(
    val sourceAccountNo: String,
    val amount: Double,
    val service: String,
    val scheduleType: String,
    val frequency: String? = null,
    val endOfMonth: Boolean? = null,
    val scheduleDate: String? = null,
    val startDate: String? = null,
    val endDate: String? = null,
    val remark: String? = null,
    val purpose: String? = null,
)