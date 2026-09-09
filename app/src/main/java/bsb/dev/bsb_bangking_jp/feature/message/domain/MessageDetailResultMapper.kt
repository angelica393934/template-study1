package bsb.dev.bsb_bangking_jp.feature.message.domain

import bsb.dev.bsb_bangking_jp.shared.transaction_result.TransactionResultInfo
import bsb.dev.bsb_bangking_jp.shared.transaction_result.TransactionResultStatus

fun MessageDetailItem.toTransactionResultInfo(): TransactionResultInfo = TransactionResultInfo(
    status = mapMessageStatus(status),
    transactionDate = transactionDate,
    referenceNumber = referenceNumber,
    beneficiaryName = beneficiaryName,
    beneficiaryBankName = beneficiaryBankName,
    beneficiaryAccountNo = beneficiaryAccountNo,
    senderName = senderName.takeIf { it.isNotBlank() },
    senderAccountNo = null, // getmessagebyid tidak mengirim nomor rekening pengirim
    amount = amount,
    adminFee = adminFee,
    totalDebit = totalDebit,
    remark = remark,
    serviceLabel = service( jenisTransaksi.takeIf { it.isNotBlank() })
)

private fun mapMessageStatus(raw: String): TransactionResultStatus = when (raw.trim().uppercase()) {
    "SUCCESS", "BERHASIL" -> TransactionResultStatus.SUCCESS
    "FAILED", "GAGAL" -> TransactionResultStatus.FAILED
    "PENDING", "PROSES", "DIPROSES" -> TransactionResultStatus.PENDING
    else -> TransactionResultStatus.PENDING
}

fun service(serviceCode: String?): String? = when (serviceCode?.trim()?.uppercase()) {
    "TRANSFER_ONUS" -> "Transfer Sesama"
    "TRANSFER_IBFT" -> "Transfer Online"
    "TRANSFER_BI_FAST" -> "Transfer BI-FAST"
    else -> serviceCode // fallback: tampilkan kode aslinya kalau tidak dikenali
}