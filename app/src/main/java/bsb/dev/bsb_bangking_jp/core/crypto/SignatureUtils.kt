// core/crypto/SignatureUtils.kt
package bsb.dev.bsb_bangking_jp.core.crypto

import android.util.Base64
import com.google.gson.Gson
import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters
import org.bouncycastle.crypto.signers.Ed25519Signer

object SignatureUtils {
    private val gson = Gson()

    /** Signature normal: minify(body) + timestamp, di-sign pakai private key. */
    fun sign(
        body: Any,
        timestamp: String,
        privateKeyBase64: String
    ): String {
        val minifiedBody = gson.toJson(body)
        return signRaw("$minifiedBody$timestamp", privateKeyBase64)
    }

//    fun signJson(
//        jsonBody: String,
//        timestamp: String,
//        privateKeyBase64: String
//    ): String {
//        val payload = jsonBody + timestamp
//        return signRaw(payload, privateKeyBase64)
//    }

    /** Signature khusus verify-device: sign challenge langsung (bukan body+timestamp). */
    fun signChallenge(challenge: String, privateKeyBase64: String): String =
        signRaw(challenge, privateKeyBase64)

    private fun signRaw(message: String, privateKeyBase64: String): String {
        val privateKeyBytes = Base64.decode(privateKeyBase64, Base64.NO_WRAP)
        val privateKeyParams = Ed25519PrivateKeyParameters(privateKeyBytes, 0)
        val signer = Ed25519Signer().apply { init(true, privateKeyParams) }
        val messageBytes = message.toByteArray(Charsets.UTF_8)
        signer.update(messageBytes, 0, messageBytes.size)
        return Base64.encodeToString(signer.generateSignature(), Base64.NO_WRAP)
    }
}