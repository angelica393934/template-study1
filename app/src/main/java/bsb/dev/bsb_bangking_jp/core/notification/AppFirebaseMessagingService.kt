package bsb.dev.bsb_bangking_jp.core.notification

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class AppFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // TODO: kirim token ini ke backend begitu endpoint register-device-token tersedia.
        // Untuk sekarang, simpan lokal dulu supaya bisa dicek manual saat testing.
        android.util.Log.d("FCM_TOKEN", "Token baru: $token")
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        // 🔹 Data-only message (payload custom dari backend nanti, TIDAK ada field "notification")
        // -- FCM TIDAK auto-render notif, jadi kita build manual di sini.
        val title = message.notification?.title
            ?: message.data["title"]
            ?: "Bank Sumsel Babel"
        val body = message.notification?.body
            ?: message.data["body"]
            ?: "Anda memiliki notifikasi baru"

        NotificationHelper.showTransaksiBerhasil(
            context = applicationContext,
            title = title,
            message = body,
        )
    }
}