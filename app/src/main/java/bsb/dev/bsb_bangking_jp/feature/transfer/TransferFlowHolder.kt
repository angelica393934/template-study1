package bsb.dev.bsb_bangking_jp.feature.transfer

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import bsb.dev.bsb_bangking_jp.feature.transfer.components.PeriksaKembaliData
import bsb.dev.bsb_bangking_jp.feature.transfer.transfer_core.domain.ConfirmTransferResultItem
import bsb.dev.bsb_bangking_jp.shared.rekening_lainnya.data.cashBalanceValue

/** Data yang dibawa antar halaman dalam satu siklus transfer (form -> PIN -> hasil). */
class TransferFlowHolder {
    var pendingTransfer by mutableStateOf<PeriksaKembaliData?>(null)
        private set
    var confirmResult by mutableStateOf<ConfirmTransferResultItem?>(null)
        private set
    var sumberKlasifikasi by mutableStateOf("Tabungan Sekarang")
        private set
    var sumberSaldo by mutableIntStateOf(0)
        private set

    fun onFormSubmitted(data: PeriksaKembaliData) {
        pendingTransfer = data
        sumberKlasifikasi = "Tabungan Sekarang"
        sumberSaldo = data.result.sumber.cashBalanceValue().toInt()
    }

    fun onConfirmed(result: ConfirmTransferResultItem) {
        confirmResult = result
    }

    fun clear() {
        pendingTransfer = null
        confirmResult = null
        sumberSaldo = 0
    }
}