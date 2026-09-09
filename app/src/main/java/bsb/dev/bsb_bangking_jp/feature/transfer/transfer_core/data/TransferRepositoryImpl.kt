package bsb.dev.bsb_bangking_jp.feature.transfer.transfer_core.data

import bsb.dev.bsb_bangking_jp.core.crypto.SignatureUtils
import bsb.dev.bsb_bangking_jp.core.device.SecureStorageService
import bsb.dev.bsb_bangking_jp.feature.transfer.transfer_core.domain.ConfirmTransferResultItem
import bsb.dev.bsb_bangking_jp.core.network.ApiErrorParser
import bsb.dev.bsb_bangking_jp.core.network.ApiException
import bsb.dev.bsb_bangking_jp.core.network.NetworkErrorMapper
import bsb.dev.bsb_bangking_jp.core.network.header.ApiHeaders
import bsb.dev.bsb_bangking_jp.core.network.token.TokenPhase
import bsb.dev.bsb_bangking_jp.core.network.token.TokenPhaseTag
import bsb.dev.bsb_bangking_jp.feature.transfer.transfer_core.domain.TransferInquiry
import bsb.dev.bsb_bangking_jp.feature.transfer.transfer_core.domain.TransferPurpose
import bsb.dev.bsb_bangking_jp.feature.transfer.transfer_core.domain.TransferRepository
import bsb.dev.bsb_bangking_jp.feature.transfer.transfer_core.domain.TransferRequestPayload
import bsb.dev.bsb_bangking_jp.feature.transfer.transfer_core.domain.TransferResult

private const val SUCCESS_CODE = "0000"

class TransferRepositoryImpl(
    private val api: TransferApiService,
    private val secureStorage: SecureStorageService,
) : TransferRepository {

    override suspend fun getAccountDest(code: String, accountNumber: String): Result<TransferInquiry> =
        runCatchingApi {
            val body = GetAccountDestRequest(code = code, accountNumber = accountNumber)
            val headers = signedHeaders(body)

            val response = api.getAccountDest(
                headers = headers,
                body = body,
                tokenPhase = TokenPhaseTag(TokenPhase.LOGIN),
            )
            if (!response.isSuccessful) throw ApiErrorParser.parse(response)

            val resp = response.body()
            if (resp?.respCode != SUCCESS_CODE) {
                throw ApiException(resp?.respCode, resp?.respMessage ?: "Gagal memuat data rekening tujuan.")
            }

            resp.data?.toDomain() ?: throw ApiException("9999", "Data rekening tujuan tidak ditemukan.")
        }

    override suspend fun saveRecipient(alias: String): Result<Unit> = runCatchingApi {
        val body = SaveRecipientRequest(alias = alias)
        val headers = signedHeaders(body)

        val response = api.saveRecipient(
            headers = headers,
            body = body,
            tokenPhase = TokenPhaseTag(TokenPhase.LOGIN),
        )
        if (!response.isSuccessful) throw ApiErrorParser.parse(response)

        val resp = response.body()
        if (resp?.respCode != SUCCESS_CODE) {
            throw ApiException(resp?.respCode, resp?.respMessage ?: "Gagal menyimpan penerima.")
        }
    }

    override suspend fun transfer(request: TransferRequestPayload): Result<TransferResult> = runCatchingApi {
        val body = TransferApiRequest(
            sourceAccountNo = request.sourceAccountNo,
            amount = request.amount,
            service = request.service,
            scheduleType = request.scheduleType,
            frequency = request.frequency,
            endOfMonth = request.endOfMonth,
            scheduleDate = request.scheduleDate,
            startDate = request.startDate,
            endDate = request.endDate,
            remark = request.remark,
            purpose = request.purpose,
        )
        val headers = signedHeaders(body)

        val response = api.transfer(
            headers = headers,
            body = body,
            tokenPhase = TokenPhaseTag(TokenPhase.LOGIN),
        )
        if (!response.isSuccessful) throw ApiErrorParser.parse(response)

        val resp = response.body()
        if (resp?.respCode != SUCCESS_CODE) {
            throw ApiException(resp?.respCode, resp?.respMessage ?: "Transfer gagal diproses.")
        }

        val data = resp.data ?: throw ApiException("9999", "Data transfer tidak ditemukan.")

        // 🔹 Simpan token sesi transfer -- dipakai Authorization untuk confirmTransfer.
        data.accessToken?.let { secureStorage.saveTransferAccessToken(it) }

        data.toDomain()
    }

    override suspend fun confirmTransfer(mobilePin: String): Result<ConfirmTransferResultItem> = runCatchingApi {
        val body = ConfirmTransferRequest(mobilePin = mobilePin)
        val headers = signedHeaders(body)

        val response = api.confirmTransfer(
            headers = headers,
            body = body,
            tokenPhase = TokenPhaseTag(TokenPhase.TRANSFER),
        )
        if (!response.isSuccessful) throw ApiErrorParser.parse(response)

        val resp = response.body()
        if (resp?.respCode != SUCCESS_CODE) {
            throw ApiException(resp?.respCode, resp?.respMessage ?: "Konfirmasi transfer gagal.")
        }

        val result = resp.data?.toDomain() ?: throw ApiException("9999", "Data konfirmasi tidak ditemukan.")

        // 🔹 Token sesi transfer sudah tidak dipakai lagi setelah confirm sukses.
        secureStorage.clearTransferToken()

        result
    }

    override suspend fun getTransferPurpose(): Result<List<TransferPurpose>> = runCatchingApi {
        val timestamp = ApiHeaders.currentTimestamp()
        val privateKey = secureStorage.getPrivateKey()
            ?: throw IllegalStateException("Private key tidak ditemukan, device belum ter-init.")
        val signature = SignatureUtils.sign(emptyMap<String, String>(), timestamp, privateKey)
        val headers = ApiHeaders.withSignature(signature, ApiHeaders.full(timestamp))

        val response = api.getTransferPurpose(
            headers = headers,
            tokenPhase = TokenPhaseTag(TokenPhase.LOGIN),
        )
        if (!response.isSuccessful) throw ApiErrorParser.parse(response)

        val resp = response.body()
        if (resp?.respCode != SUCCESS_CODE) {
            throw ApiException(resp?.respCode, resp?.respMessage ?: "Gagal memuat tujuan transfer.")
        }

        resp.data.map { it.toDomain() }
    }

    // ---------------------------------------------------------------
    // Helper
    // ---------------------------------------------------------------

    private fun signedHeaders(body: Any): Map<String, String> {
        val privateKey = secureStorage.getPrivateKey()
            ?: throw IllegalStateException("Private key tidak ditemukan, device belum ter-init.")
        val timestamp = ApiHeaders.currentTimestamp()
        val signature = SignatureUtils.sign(body, timestamp, privateKey)
        return ApiHeaders.withSignature(signature, ApiHeaders.full(timestamp))
    }

    private suspend fun <T> runCatchingApi(block: suspend () -> T): Result<T> = try {
        Result.success(block())
    } catch (e: ApiException) {
        Result.failure(e)
    } catch (e: Exception) {
        Result.failure(ApiException(null, NetworkErrorMapper.toUserMessage(e)))
    }
}