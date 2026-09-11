package bsb.dev.bsb_bangking_jp.core.device

import android.content.Context
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class SecureStorageService(context: Context) {

    private val appContext = context.applicationContext

    private val prefs by lazy {
        val masterKey = MasterKey.Builder(appContext)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        EncryptedSharedPreferences.create(
            appContext,
            "secure_device_prefs",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
        )
    }

    fun getDeviceId(): String? = prefs.getString(KEY_DEVICE_ID, null)
    fun saveDeviceId(id: String) = prefs.edit { putString(KEY_DEVICE_ID, id) }

    fun getPrivateKey(): String? = prefs.getString(KEY_PRIVATE_KEY, null)
    fun savePrivateKey(base64Key: String) = prefs.edit { putString(KEY_PRIVATE_KEY, base64Key) }

    //Token untuk phase "Init" -- dipakai untuk membuatx-signature untuk membuat m-pin

    fun getInitAccessToken(): String? = prefs.getString(KEY_INIT_ACCESS_TOKEN, null)
    fun saveInitAccessToken(token: String) = prefs.edit { putString(KEY_INIT_ACCESS_TOKEN, token) }

    fun getInitRefreshToken(): String? = prefs.getString(KEY_INIT_REFRESH_TOKEN, null)
    fun saveInitRefreshToken(token: String) = prefs.edit { putString(KEY_INIT_REFRESH_TOKEN, token) }

    fun clearInitTokens() {
        prefs.edit {
            remove(KEY_INIT_ACCESS_TOKEN)
            remove(KEY_INIT_REFRESH_TOKEN)
        }
    }

    // token untuk phase "regist" -- dipakai untuk addiduserlogin & addpasscode
    fun getRegistAccessToken(): String? = prefs.getString(KEY_REGIST_ACCESS_TOKEN, null)
    fun saveRegistAccessToken(token: String) = prefs.edit { putString(KEY_REGIST_ACCESS_TOKEN, token) }

    fun getRegistRefreshToken(): String? = prefs.getString(KEY_REGIST_REFRESH_TOKEN, null)
    fun saveRegistRefreshToken(token: String) = prefs.edit { putString(KEY_REGIST_REFRESH_TOKEN, token) }

    fun clearRegistTokens() {
        prefs.edit {
            remove(KEY_REGIST_ACCESS_TOKEN)
            remove(KEY_REGIST_REFRESH_TOKEN)
        }
    }

    // token untuk phase "activation" -- dipakai validationpasscodeforactivation & activation-confirmmpin
    fun getActivationAccessToken(): String? = prefs.getString(KEY_ACTIVATION_ACCESS_TOKEN, null)
    fun saveActivationAccessToken(token: String) = prefs.edit { putString(KEY_ACTIVATION_ACCESS_TOKEN, token) }

    fun getActivationRefreshToken(): String? = prefs.getString(KEY_ACTIVATION_REFRESH_TOKEN, null)
    fun saveActivationRefreshToken(token: String) = prefs.edit { putString(KEY_ACTIVATION_REFRESH_TOKEN, token) }

    fun clearActivationTokens() {
        prefs.edit {
            remove(KEY_ACTIVATION_ACCESS_TOKEN)
            remove(KEY_ACTIVATION_REFRESH_TOKEN)
        }
    }

    //Token untuk phase "login" -- dipakai untuk semua endpoint setelah user login
    fun getLoginAccessToken(): String? = prefs.getString(KEY_LOGIN_ACCESS_TOKEN, null)
    fun saveLoginAccessToken(token: String) = prefs.edit { putString(KEY_LOGIN_ACCESS_TOKEN, token) }

    fun getLoginRefreshToken(): String? = prefs.getString(KEY_LOGIN_REFRESH_TOKEN, null)
    fun saveLoginRefreshToken(token: String) = prefs.edit { putString(KEY_LOGIN_REFRESH_TOKEN, token) }

    fun clearLoginTokens() {
        prefs.edit {
            remove(KEY_LOGIN_ACCESS_TOKEN)
            remove(KEY_LOGIN_REFRESH_TOKEN)
        }
    }
    // token untuk phase "forget_iduser" -- dipakai khusus endpoint changeiduser
    fun getForgetIdUserAccessToken(): String? = prefs.getString(KEY_FORGET_IDUSER_ACCESS_TOKEN, null)
    fun saveForgetIdUserAccessToken(token: String) = prefs.edit { putString(KEY_FORGET_IDUSER_ACCESS_TOKEN, token) }

    fun getForgetIdUserRefreshToken(): String? = prefs.getString(KEY_FORGET_IDUSER_REFRESH_TOKEN, null)
    fun saveForgetIdUserRefreshToken(token: String) = prefs.edit { putString(KEY_FORGET_IDUSER_REFRESH_TOKEN, token) }

    fun clearForgetIdUserTokens() {
        prefs.edit {
            remove(KEY_FORGET_IDUSER_ACCESS_TOKEN)
            remove(KEY_FORGET_IDUSER_REFRESH_TOKEN)
        }
    }
    // token phase transfer
    fun getTransferAccessToken(): String? = prefs.getString(KEY_TRANSFER_ACCESS_TOKEN, null)
    fun saveTransferAccessToken(token: String) = prefs.edit { putString(KEY_TRANSFER_ACCESS_TOKEN, token) }
    fun clearTransferToken() = prefs.edit { remove(KEY_TRANSFER_ACCESS_TOKEN) }

    companion object {
        private const val KEY_DEVICE_ID = "device_id"
        private const val KEY_PRIVATE_KEY = "ed25519_private_key"
        private const val KEY_INIT_ACCESS_TOKEN = "init_access_token"
        private const val KEY_INIT_REFRESH_TOKEN = "init_refresh_token"
        private const val KEY_REGIST_ACCESS_TOKEN = "regist_access_token"
        private const val KEY_REGIST_REFRESH_TOKEN = "regist_refresh_token"
        private const val KEY_LOGIN_ACCESS_TOKEN = "login_access_token"
        private const val KEY_LOGIN_REFRESH_TOKEN = "login_refresh_token"
        private const val KEY_FORGET_IDUSER_ACCESS_TOKEN = "forget_iduser_access_token"
        private const val KEY_FORGET_IDUSER_REFRESH_TOKEN = "forget_iduser_refresh_token"
        private const val KEY_TRANSFER_ACCESS_TOKEN = "transfer_access_token"
        private const val KEY_ACTIVATION_ACCESS_TOKEN = "activation_access_token"
        private const val KEY_ACTIVATION_REFRESH_TOKEN = "activation_refresh_token"

    }
}