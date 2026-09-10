package bsb.dev.bsb_bangking_jp.core.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import bsb.dev.bsb_bangking_jp.R
import bsb.dev.bsb_bangking_jp.core.component.AppHeader
import bsb.dev.bsb_bangking_jp.core.component.OtpPinInput
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val OTP_LENGTH = 6
private const val COUNTDOWN_SECONDS = 180

@Composable
fun OtpForm(
    title: String,
    phoneNumber: String,
    isProcessing: Boolean,
    errorMessage: String?,
    onVerify: (otp: String) -> Unit,
    onResend: suspend () -> Boolean,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var otpValue by remember { mutableStateOf("") }
    var remainingSeconds by remember { mutableIntStateOf(COUNTDOWN_SECONDS) }
    var isExpired by remember { mutableStateOf(false) }
    var isResending by remember { mutableStateOf(false) }

    val otpShakeOffset = remember { Animatable(0f) }
    val resendShakeOffset = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()

    // 🔹 Countdown, padanan Timer.periodic di .
    LaunchedEffect(Unit) {
        while (remainingSeconds > 0 && !isExpired) {
            delay(1000)
            remainingSeconds -= 1
        }
        if (remainingSeconds <= 0) isExpired = true
    }

    fun restartCountdown() {
        remainingSeconds = COUNTDOWN_SECONDS
        isExpired = false
        otpValue = ""
        scope.launch {
            while (remainingSeconds > 0 && !isExpired) {
                delay(1000)
                remainingSeconds -= 1
            }
            if (remainingSeconds <= 0) isExpired = true
        }
    }

    // 🔹 Trigger shake tiap kali ada error baru masuk dari luar (hasil verifyOtp gagal).
    LaunchedEffect(errorMessage) {
        if (errorMessage != null) {
            otpValue = ""
            otpShakeOffset.snapTo(0f)
            otpShakeOffset.animateTo(10f, tween(150))
            otpShakeOffset.animateTo(0f, tween(150))
        }
    }

    fun formatTime(seconds: Int): String {
        val m = (seconds / 60).toString().padStart(2, '0')
        val s = (seconds % 60).toString().padStart(2, '0')
        return "$m:$s"
    }

    val displayedError = errorMessage
        ?: if (isExpired) "Kode OTP sudah tidak berlaku. Silakan klik 'Kirim Ulang OTP' untuk mendapatkan kode baru." else null

    Column(modifier = modifier) {
        AppHeader(title = title, onBackClick = onBackClick)

        Column(
            modifier = Modifier.padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(30.dp))

            Image(
                painter = painterResource(id = R.drawable.asset_message), // TODO: sesuaikan nama drawable
                contentDescription = null,
                modifier = Modifier.size(100.dp),
                contentScale = ContentScale.Fit,
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = buildAnnotatedString {
                    append("Masukkan 6 digit kode OTP yang telah dikirimkan ke nomor ")
                    withStyle(SpanStyle(color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)) {
                        append(phoneNumber)
                    }
                },
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(50.dp))

            if (displayedError != null) {
                Text(
                    text = displayedError,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.offset(x = otpShakeOffset.value.dp),
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            OtpPinInput(
                value = otpValue,
                onValueChange = { newValue ->
                    otpValue = newValue
                    if (newValue.length == OTP_LENGTH && !isProcessing) {
                        onVerify(newValue)
                    }
                },
                length = OTP_LENGTH,
                enabled = !isExpired && !isProcessing,
                isError = isExpired || errorMessage != null,
                modifier = Modifier.offset(x = otpShakeOffset.value.dp),
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = formatTime(remainingSeconds),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
            )

            Spacer(modifier = Modifier.height(50.dp))

            Row(
                modifier = Modifier.offset(x = resendShakeOffset.value.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Tidak menerima kode OTP? ",
                    style = MaterialTheme.typography.bodyMedium,
                )
                Text(
                    text = if (isResending) "Mengirim..." else "Kirim Ulang OTP",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = if (isExpired && !isResending) MaterialTheme.colorScheme.primary else Color.Gray,
                    modifier = Modifier.clip(RoundedCornerShape(4.dp))
                        .then(
                            if (isExpired && !isResending) {
                                Modifier.androidx_clickable(onClick = {
                                    scope.launch {
                                        isResending = true
                                        val success = onResend()
                                        isResending = false
                                        // 🔹 KUNCI PERBAIKAN: countdown & status "aktif kembali"
                                        // HANYA direset kalau resend benar-benar sukses.
                                        // Kalau gagal, isExpired tetap true dan tombol
                                        // "Kirim Ulang OTP" tetap bisa ditekan ulang.
                                        if (success) {
                                            restartCountdown()
                                        }
                                    }
                                })
                            } else Modifier
                        ),
                )
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

// Helper kecil biar import clickable tidak bentrok nama dengan variabel lain di file ini.
@Composable
private fun Modifier.androidx_clickable(onClick: () -> Unit): Modifier =
    this.then(
        Modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = onClick,
        )
    )