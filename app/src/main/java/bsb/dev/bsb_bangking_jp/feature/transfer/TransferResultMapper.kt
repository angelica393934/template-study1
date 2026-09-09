package bsb.dev.bsb_bangking_jp.feature.transfer

import bsb.dev.bsb_bangking_jp.feature.transfer.transfer_core.domain.ConfirmTransferResultItem
import bsb.dev.bsb_bangking_jp.shared.transaction_result.TransactionResultInfo
import bsb.dev.bsb_bangking_jp.shared.transaction_result.TransactionResultStatus

fun ConfirmTransferResultItem.toTransactionResultInfo(): TransactionResultInfo = TransactionResultInfo(
    status = TransactionResultStatus.SUCCESS,
    transactionDate = transactionDate,
    referenceNumber = reffNum,
    beneficiaryName = beneficiaryName,
    beneficiaryBankName = beneficiaryBankName,
    beneficiaryAccountNo = beneficiaryAccountNo,
    senderName = senderName,
    senderAccountNo = senderAccountNo,
    amount = amount.toLong(),
    adminFee = adminFee.toLong(),
    totalDebit = totalDebit.toLong(),
    remark = remark,
    serviceLabel = "Transfer Sesama",
)