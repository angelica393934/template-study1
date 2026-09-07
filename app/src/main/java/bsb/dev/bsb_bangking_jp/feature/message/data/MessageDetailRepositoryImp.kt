package bsb.dev.bsb_bangking_jp.feature.message.data

import bsb.dev.bsb_bangking_jp.core.network.ApiException
import bsb.dev.bsb_bangking_jp.core.network.GetWithBodyApiHelper
import bsb.dev.bsb_bangking_jp.feature.message.domain.MessageDetailItem
import bsb.dev.bsb_bangking_jp.feature.message.domain.MessageDetailRepository

private const val SUCCESS_CODE = "0000"

class MessageDetailRepositoryImpl(
    private val apiHelper: GetWithBodyApiHelper,
) : MessageDetailRepository {

    override suspend fun getMessageDetail(id: Int): MessageDetailItem {
        val response = apiHelper.execute(
            path = "v1/dashboard/getmessagebyid",
            body = GetMessageByIdRequest(id = id),
            responseType = MessageDetailResponse::class.java,
        )

        if (response.respCode != SUCCESS_CODE) {
            throw ApiException(response.respCode, response.respMessage ?: "Gagal memuat detail message.")
        }

        return response.data.toDomain()
    }
}