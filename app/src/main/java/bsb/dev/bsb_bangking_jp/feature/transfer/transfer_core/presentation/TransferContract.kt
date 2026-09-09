// feature/transfer/presentation/TransferContract.kt
package bsb.dev.bsb_bangking_jp.feature.transfer.transfer_core.presentation

import bsb.dev.bsb_bangking_jp.feature.transfer.transfer_core.domain.ConfirmTransferResultItem
import bsb.dev.bsb_bangking_jp.feature.transfer.transfer_core.domain.TransferInquiry
import bsb.dev.bsb_bangking_jp.feature.transfer.transfer_core.domain.TransferPurpose

data class TransferUiState(
    val isInquiryLoading: Boolean = false,
    val inquiryError: String? = null, // dipakai untuk field rekening (respCode "0602")

    val isSavingRecipient: Boolean = false,
    val saveRecipientError: String? = null,

    val isSubmittingTransfer: Boolean = false,
    // 🔹 transferError DIHAPUS -- semua error transfer() sekarang lewat
    // TransferUiEvent.ShowToastError (toast app, bukan Toast.makeText manual di sheet).

    val isConfirming: Boolean = false,
    val confirmError: String? = null,

    val transferPurposes: List<TransferPurpose> = emptyList(),
)

sealed class TransferNavEvent {
    data class ToDetailRekening(val inquiry: TransferInquiry) : TransferNavEvent()
    object RecipientSaved : TransferNavEvent()
    object TransferSubmitted : TransferNavEvent() // lanjut ke halaman PIN

    /** Padanan respCode "0688" di failure handler transfer() (fase 3, PeriksaKembaliSheet). */
    object TransferSessionExpired : TransferNavEvent()

    data class ConfirmSuccess(
        val result: ConfirmTransferResultItem,
    ) : TransferNavEvent()

    /** Padanan respCode "0732"/"0465" di failure handler confirmTransfer() (fase 4, PinTfPage). */
    object ConfirmSessionExpired : TransferNavEvent()
}

sealed class TransferUiEvent {
    data class ShowToastError(val message: String) : TransferUiEvent()
}