package bsb.dev.bsb_bangking_jp.feature.notification.domain

interface NotifRepository {
    val hasData: Boolean
    val cachedNotif: List<NotifItem>?
    suspend fun getNotif(forceRefresh: Boolean = false): List<NotifItem>
}