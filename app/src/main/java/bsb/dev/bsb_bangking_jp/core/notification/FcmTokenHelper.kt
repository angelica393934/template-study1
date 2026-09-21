package bsb.dev.bsb_bangking_jp.core.notification

import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging

object FcmTokenHelper {
    /** Ambil token FCM saat ini -- panggil sekali saat app start (mis. splash / setelah login). */
    fun fetchToken(onToken: (String) -> Unit = {}) {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("FCM_TOKEN", "Gagal ambil token FCM", task.exception)
                return@addOnCompleteListener
            }
            val token = task.result
            Log.d("FCM_TOKEN", "Token: $token")
            onToken(token)
            // TODO: begitu backend siap, kirim `token` ke endpoint register-device
            // (mis. lewat SecureStorageService buat cache lokal + repository buat POST ke server)
        }
    }
}