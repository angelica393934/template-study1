// feature/transfer/presentation/TransferViewModel.kt
package bsb.dev.bsb_bangking_jp.feature.transfer.transfer_core.presentation

import bsb.dev.bsb_bangking_jp.core.network.ApiException
import bsb.dev.bsb_bangking_jp.feature.activity.presentation.ActivityHistoryViewModel
import bsb.dev.bsb_bangking_jp.feature.message.presentation.MessageHistoryViewModel
import bsb.dev.bsb_bangking_jp.feature.transfer.last_transfer.presentation.LastTransferViewModel
import bsb.dev.bsb_bangking_jp.feature.transfer.transfer_core.domain.TransferRepository
import bsb.dev.bsb_bangking_jp.feature.transfer.transfer_core.domain.TransferRequestPayload
import bsb.dev.bsb_bangking_jp.shared.rekening_lainnya.presentation.RekeningLainnyaViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val INQUIRY_INLINE_ERROR_CODE = "0602"
private const val INQUIRY_SILENT_CODE = "0683" // gagal tapi tidak perlu toast (padanan )

// 🔹 Padanan respCode "0688" di failure handler transfer() (fase 3).
private const val TRANSFER_SESSION_EXPIRED_CODE = "0688"

// 🔹 Padanan respCode "0732"/"0465" di failure handler confirmTransfer() (fase 4).
private const val CONFIRM_SESSION_EXPIRED_CODE_1 = "0732"
private const val CONFIRM_SESSION_EXPIRED_CODE_2 = "0465"

private const val SESSION_EXPIRED_MESSAGE =
    "Sesi transaksi transfer Anda telah berakhir. Silakan ajukan permintaan transfer kembali."

class TransferViewModel(
    private val rekeningViewModel: RekeningLainnyaViewModel,
    private val messageViewModel: MessageHistoryViewModel,
    private val activityViewModel: ActivityHistoryViewModel,
    private val lastTransferViewModel: LastTransferViewModel,
    private val repository: TransferRepository,
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    private val _uiState = MutableStateFlow(TransferUiState())
    val uiState: StateFlow<TransferUiState> = _uiState.asStateFlow()

    private val _navEvent = Channel<TransferNavEvent>(Channel.BUFFERED)
    val navEvent: Flow<TransferNavEvent> = _navEvent.receiveAsFlow()

    private val _uiEvent = MutableSharedFlow<TransferUiEvent>(replay = 0)
    val uiEvent: SharedFlow<TransferUiEvent> = _uiEvent.asSharedFlow()

    /** 1) GET ACCOUNT DEST. */
    fun getAccountDest(code: String, accountNumber: String) {
        scope.launch {
            _uiState.update { it.copy(isInquiryLoading = true, inquiryError = null) }

            repository.getAccountDest(code, accountNumber)
                .onSuccess { inquiry ->
                    _uiState.update { it.copy(isInquiryLoading = false) }
                    _navEvent.send(TransferNavEvent.ToDetailRekening(inquiry))
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isInquiryLoading = false) }
                    val respCode = (error as? ApiException)?.respCode
                    val message = error.message ?: "Gagal memuat data rekening tujuan."

                    when (respCode) {
                        INQUIRY_INLINE_ERROR_CODE -> _uiState.update { it.copy(inquiryError = message) }
                        INQUIRY_SILENT_CODE -> Unit // padanan: tidak ditampilkan sama sekali
                        else -> _uiEvent.emit(TransferUiEvent.ShowToastError(message))
                    }
                }
        }
    }

    fun clearInquiryError() {
        _uiState.update { it.copy(inquiryError = null) }
    }

    /** 2) SAVE RECIPIENT. */
    fun saveRecipient(alias: String) {
        scope.launch {
            _uiState.update { it.copy(isSavingRecipient = true, saveRecipientError = null) }

            repository.saveRecipient(alias)
                .onSuccess {
                    _uiState.update { it.copy(isSavingRecipient = false) }
                    _navEvent.send(TransferNavEvent.RecipientSaved)
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isSavingRecipient = false) }
                    val respCode = (error as? ApiException)?.respCode
                    val message = error.message ?: "Gagal menyimpan penerima."

                    // 🔹 Padanan cek code == "0676" -> sesi transaksi berakhir, tutup & toast.
                    if (respCode == "0676") {
                        _uiEvent.emit(
                            TransferUiEvent.ShowToastError(
                                "Sesi transaksi transfer Anda telah berakhir. Silakan ulangi proses transfer kembali."
                            )
                        )
                    } else {
                        _uiState.update { it.copy(saveRecipientError = message) }
                    }
                }
        }
    }

    fun clearSaveRecipientError() {
        _uiState.update { it.copy(saveRecipientError = null) }
    }

    /**
     * 3) TRANSFER.
     * Padanan `failure: (code, msg)` di listener PeriksaKembaliSheet -mu:
     * - code "0688" -> sesi berakhir: toast message tetap, lalu tutup sheet + pop 1x lagi.
     * - selain itu -> toast pakai `msg` dari server, tetap di halaman yang sama.
     */
    fun transfer(payload: TransferRequestPayload) {
        scope.launch {
            _uiState.update { it.copy(isSubmittingTransfer = true) }

            repository.transfer(payload)
                .onSuccess {
                    _uiState.update { it.copy(isSubmittingTransfer = false) }
                    _navEvent.send(TransferNavEvent.TransferSubmitted)
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isSubmittingTransfer = false) }
                    val respCode = (error as? ApiException)?.respCode
                    val message = error.message ?: "Transfer gagal diproses."

                    if (respCode == TRANSFER_SESSION_EXPIRED_CODE) {
                        _uiEvent.emit(TransferUiEvent.ShowToastError(SESSION_EXPIRED_MESSAGE))
                        _navEvent.send(TransferNavEvent.TransferSessionExpired)
                    } else {
                        _uiEvent.emit(TransferUiEvent.ShowToastError(message))
                    }
                }
        }
    }

    /**
     * 4) CONFIRM TRANSFER.
     * Padanan `failure: (code, message)` di listener PinTfPage -mu:
     * - code "0732"/"0465" -> sesi berakhir: toast, pop 2x, lalu pushReplacement ke TransferPage.
     * - selain itu -> tetap tampil inline di field PIN (confirmError, sudah ada sebelumnya).
     */
    fun confirmTransfer(mobilePin: String) {
        scope.launch {
            _uiState.update { it.copy(isConfirming = true, confirmError = null) }

            repository.confirmTransfer(mobilePin)
                .onSuccess { result ->
                    _uiState.update { it.copy(isConfirming = false) }
                    // 🔹 Cuma reload rekening vie model untuk transfer SEGERA (bukan terjadwal) -- padanan
                    if (result.scheduleType != "SCHEDULED") {
                        rekeningViewModel.load(forceRefresh = true)
                        activityViewModel.refresh()
                        messageViewModel.refresh()
                        lastTransferViewModel.load(forceRefresh = true)
                    }
                    _navEvent.send(TransferNavEvent.ConfirmSuccess(result))
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isConfirming = false) }

                    val respCode = (error as? ApiException)?.respCode
                    val message = error.message ?: "Konfirmasi transfer gagal."

                    if (respCode == CONFIRM_SESSION_EXPIRED_CODE_1 || respCode == CONFIRM_SESSION_EXPIRED_CODE_2) {
                        _uiEvent.emit(TransferUiEvent.ShowToastError(SESSION_EXPIRED_MESSAGE))
                        _navEvent.send(TransferNavEvent.ConfirmSessionExpired)
                    } else {
                        _uiState.update { it.copy(confirmError = message) }
                    }
                }
        }
    }

    fun clearConfirmError() {
        _uiState.update { it.copy(confirmError = null) }
    }

    /** 5) GET TRANSFER PURPOSE. */
    fun getTransferPurpose() {
        scope.launch {
            repository.getTransferPurpose()
                .onSuccess { purposes -> _uiState.update { it.copy(transferPurposes = purposes) } }
                .onFailure { /* opsional -- fallback ke daftar statis di UI */ }
        }
    }

    /** Reset state setelah 1 siklus transfer selesai (sukses/dibatalkan). */
    fun reset() {
        _uiState.value = TransferUiState()
    }
}