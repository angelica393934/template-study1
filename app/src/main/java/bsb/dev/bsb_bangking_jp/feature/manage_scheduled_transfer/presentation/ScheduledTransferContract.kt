package bsb.dev.bsb_bangking_jp.feature.manage_scheduled_transfer.presentation

import bsb.dev.bsb_bangking_jp.feature.manage_scheduled_transfer.domain.ScheduledTransferDetail
import bsb.dev.bsb_bangking_jp.feature.manage_scheduled_transfer.domain.ScheduledTransferItem

/**
 * Padanan ScheduledTransferState (Dart, freezed) -- satu state machine untuk
 * List page & Detail page, sama seperti ScheduledTransferBloc aslinya.
 */
sealed interface ScheduledTransferUiState {
    data object Initial : ScheduledTransferUiState
    data object Loading : ScheduledTransferUiState
    data class ListSuccess(val items: List<ScheduledTransferItem>) : ScheduledTransferUiState
    data class DetailSuccess(val detail: ScheduledTransferDetail) : ScheduledTransferUiState

    /** action: "toggle" atau "delete". */
    data class ActionSuccess(val action: String) : ScheduledTransferUiState
    data class Failure(val respCode: String?, val respMessage: String) : ScheduledTransferUiState
}