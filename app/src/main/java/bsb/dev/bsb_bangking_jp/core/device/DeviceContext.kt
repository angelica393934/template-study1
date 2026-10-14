package bsb.dev.bsb_bangking_jp.core.device

import android.content.Context
import android.os.Build
import java.util.UUID

object DeviceContext {
    private var initialized = false

    var deviceId: String = ""
        private set
    var deviceName: String = "Unknown"
        private set
    var os: String = "Android"
        private set
    var osVersion: String = "Unknown"
        private set
    var appVersion: String = "Unknown"
        private set

    /** Panggil sekali di BsbApplication.onCreate(), setelah Koin start. */
    fun init(context: Context, secureStorage: SecureStorageService) {
        if (initialized) return

        deviceId = secureStorage.getDeviceId() ?: UUID.randomUUID().toString().also {
            secureStorage.saveDeviceId(it)
        }
        deviceName = "${Build.MANUFACTURER} ${Build.MODEL}"
        os = "Android"
        osVersion = Build.VERSION.RELEASE ?: "Unknown"

        appVersion = try {
            context.packageManager.getPackageInfo(context.packageName, 0).versionName ?: "Unknown"
        } catch (e: Exception) {
            "Unknown"
        }
        initialized = true
    }
}