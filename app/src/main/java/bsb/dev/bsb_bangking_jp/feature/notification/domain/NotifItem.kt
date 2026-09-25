package bsb.dev.bsb_bangking_jp.feature.notification.domain

data class NotifItem(
    val id: Int,
    val name: String,
    val description: String,
    val subtitle: String,
    val date: String,
    val pathImage: String,
)