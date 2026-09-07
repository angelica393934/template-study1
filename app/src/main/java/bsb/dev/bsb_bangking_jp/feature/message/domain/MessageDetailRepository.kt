package bsb.dev.bsb_bangking_jp.feature.message.domain

interface MessageDetailRepository {
    suspend fun getMessageDetail(id: Int): MessageDetailItem
}