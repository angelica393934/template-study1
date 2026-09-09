package bsb.dev.bsb_bangking_jp.feature.transfer.transfer_core.data

import bsb.dev.bsb_bangking_jp.feature.transfer.transfer_core.domain.ConfirmTransferResultItem
import bsb.dev.bsb_bangking_jp.feature.transfer.transfer_core.domain.TransferAdminFee
import bsb.dev.bsb_bangking_jp.feature.transfer.transfer_core.domain.TransferInquiry
import bsb.dev.bsb_bangking_jp.feature.transfer.transfer_core.domain.TransferPurpose
import bsb.dev.bsb_bangking_jp.feature.transfer.transfer_core.domain.TransferResult
import bsb.dev.bsb_bangking_jp.feature.transfer.transfer_core.domain.TransferResultBeneficiary
import com.google.gson.annotations.SerializedName
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// ============================================================
// 1) GET ACCOUNT DEST
// ============================================================

data class GetAccountDestRequest(
    @SerializedName("code") val code: String,
    @SerializedName("accountnumber") val accountNumber: String,
)

data class GetAccountDestResponse(
    @SerializedName("respCode") val respCode: String? = null,
    @SerializedName("respMessage") val respMessage: String? = null,
    @SerializedName("data") val data: TransferInquiryData? = null,
)

// TODO: verifikasi ke backend -- struktur nested profile/external ini mengikuti
// TransferInquiryModel.fromJson()  persis.
data class TransferInquiryData(
    @SerializedName("profile") val profile: TransferInquiryProfile = TransferInquiryProfile(),
    @SerializedName("external") val external: TransferInquiryExternal = TransferInquiryExternal(),
    @SerializedName("supportedServices") val supportedServices: List<String> = emptyList(),
)

data class TransferInquiryProfile(
    @SerializedName("name") val name: String = "",
)

data class TransferInquiryExternal(
    @SerializedName("beneficiaryAccountName") val beneficiaryAccountName: String = "",
    @SerializedName("beneficiaryAccountNo") val beneficiaryAccountNo: String = "",
    @SerializedName("beneficiaryAccountType") val beneficiaryAccountType: String = "",
    @SerializedName("currency") val currency: String = "",
)

fun TransferInquiryData.toDomain(): TransferInquiry = TransferInquiry(
    bankName = profile.name,
    beneficiaryName = external.beneficiaryAccountName,
    beneficiaryAccountNo = external.beneficiaryAccountNo,
    beneficiaryAccountType = external.beneficiaryAccountType,
    currency = external.currency,
    supportedServices = supportedServices,
)

// ============================================================
// 2) SAVE RECIPIENT
// ============================================================

data class SaveRecipientRequest(
    @SerializedName("alias") val alias: String,
)

data class SaveRecipientResponse(
    @SerializedName("respCode") val respCode: String? = null,
    @SerializedName("respMessage") val respMessage: String? = null,
)

// ============================================================
// 3) TRANSFER
// ============================================================

data class TransferApiRequest(
    @SerializedName("sourceAccountNo") val sourceAccountNo: String,
    @SerializedName("amount") val amount: Double,
    @SerializedName("service") val service: String,
    @SerializedName("scheduleType") val scheduleType: String,
    @SerializedName("frequency") val frequency: String? = null,
    @SerializedName("endOfMonth") val endOfMonth: Boolean? = null,
    @SerializedName("scheduleDate") val scheduleDate: String? = null,
    @SerializedName("startDate") val startDate: String? = null,
    @SerializedName("endDate") val endDate: String? = null,
    @SerializedName("remark") val remark: String? = null,
    @SerializedName("purpose") val purpose: String? = null,
)

data class TransferApiResponse(
    @SerializedName("respCode") val respCode: String? = null,
    @SerializedName("respMessage") val respMessage: String? = null,
    @SerializedName("data") val data: TransferResultData? = null,
)

data class TransferResultData(
    @SerializedName("access_token") val accessToken: String? = null, // 🔹 token sesi utk confirmTransfer
    @SerializedName("referenceId") val referenceId: String = "",
    @SerializedName("amount") val amount: Double = 0.0,
    @SerializedName("totalDebit") val totalDebit: Double = 0.0,
    @SerializedName("schedule") val schedule: String = "",
    @SerializedName("serviceType") val serviceType: String = "",
    @SerializedName("sourceAccount") val sourceAccount: String = "",
    @SerializedName("nextRunDate") val nextRunDate: String? = null,
    @SerializedName("beneficiary") val beneficiary: TransferResultBeneficiaryDto = TransferResultBeneficiaryDto(),
    @SerializedName("adminFee") val adminFee: List<TransferAdminFeeDto> = emptyList(),
)

data class TransferResultBeneficiaryDto(
    @SerializedName("accountName") val accountName: String = "",
    @SerializedName("accountNo") val accountNo: String = "",
    @SerializedName("bankName") val bankName: String = "",
    @SerializedName("bank_code") val bankCode: String = "",
)

data class TransferAdminFeeDto(
    @SerializedName("amount") val amount: Double = 0.0,
    @SerializedName("trxtype") val trxType: String = "",
)

private val isoDateTimeParser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)

fun TransferResultData.toDomain(): TransferResult = TransferResult(
    referenceId = referenceId,
    amount = amount,
    totalDebit = totalDebit,
    schedule = schedule,
    serviceType = serviceType,
    sourceAccount = sourceAccount,
    nextRunDate = nextRunDate?.let { runCatching { isoDateTimeParser.parse(it) }.getOrNull() },
    beneficiary = TransferResultBeneficiary(
        accountName = beneficiary.accountName,
        accountNo = beneficiary.accountNo,
        bankName = beneficiary.bankName,
        bankCode = beneficiary.bankCode,
    ),
    adminFee = adminFee.map { TransferAdminFee(it.amount, it.trxType) },
)

// ============================================================
// 4) CONFIRM TRANSFER
// ============================================================

data class ConfirmTransferRequest(
    @SerializedName("mobilepin") val mobilePin: String,
)

data class ConfirmTransferApiResponse(
    @SerializedName("respCode") val respCode: String? = null,
    @SerializedName("respMessage") val respMessage: String? = null,
    @SerializedName("data") val data: ConfirmTransferResultItemDto? = null,
)

data class ConfirmTransferResultItemDto(
    @SerializedName("adminFee") val adminFee: Double = 0.0,
    @SerializedName("amount") val amount: Double = 0.0,
    @SerializedName("beneficiary") val beneficiary: ConfirmBeneficiaryDto = ConfirmBeneficiaryDto(),
    @SerializedName("sender") val sender: ConfirmSenderDto = ConfirmSenderDto(),
    @SerializedName("reffNum") val reffNum: String = "",
    @SerializedName("serviceType") val serviceType: String = "",
    @SerializedName("totalDebit") val totalDebit: Double = 0.0,
    @SerializedName("transactionDate") val transactionDate: String = "",
    @SerializedName("type") val type: String = "",
    @SerializedName("remark") val remark: String? = null,
    @SerializedName("scheduleType") val scheduleType: String? = null,
    @SerializedName("scheduleDate") val scheduleDate: String? = null,
    @SerializedName("frequency") val frequency: String? = null,
    @SerializedName("startMonth") val startMonth: String? = null,
    @SerializedName("endMonth") val endMonth: String? = null,
)

data class ConfirmBeneficiaryDto(
    @SerializedName("accountName") val accountName: String = "",
    @SerializedName("accountNo") val accountNo: String = "",
    @SerializedName("bankCode") val bankCode: String = "",
    @SerializedName("bankName") val bankName: String = "",
)

data class ConfirmSenderDto(
    @SerializedName("accountNumber") val accountNumber: String = "",
    @SerializedName("name") val name: String = "",
)

fun ConfirmTransferResultItemDto.toDomain(): ConfirmTransferResultItem = ConfirmTransferResultItem(
    reffNum = reffNum,
    transactionDate = runCatching { isoDateTimeParser.parse(transactionDate) }.getOrNull() ?: Date(),
    beneficiaryName = beneficiary.accountName,
    beneficiaryBankName = beneficiary.bankName,
    beneficiaryAccountNo = beneficiary.accountNo,
    senderName = sender.name,
    senderAccountNo = sender.accountNumber,
    amount = amount.toInt(),
    adminFee = adminFee.toInt(),
    totalDebit = totalDebit.toInt(),
    remark = remark,
    scheduleType = scheduleType ?: "IMMEDIATE",
    frequency = frequency,
    scheduleDate = scheduleDate,
    startMonth = startMonth,
    endMonth = endMonth,
)

// ============================================================
// 5) GET TRANSFER PURPOSE
// ============================================================

// TODO: verifikasi ke backend -- bentuk objek "purpose" belum terlihat di source Dart
// ( cuma teruskan Map mentah). Diasumsikan list of {code, name}.
data class TransferPurposeResponse(
    @SerializedName("respCode") val respCode: String? = null,
    @SerializedName("respMessage") val respMessage: String? = null,
    @SerializedName("data") val data: List<TransferPurposeDto> = emptyList(),
)

data class TransferPurposeDto(
    @SerializedName("code") val code: String = "",
    @SerializedName("name") val name: String = "",
)

fun TransferPurposeDto.toDomain(): TransferPurpose = TransferPurpose(code, name)