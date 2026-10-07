package bsb.dev.bsb_bangking_jp.core.components

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
import bsb.dev.bsb_bangking_jp.core.theme.appLayout
import bsb.dev.bsb_bangking_jp.core.theme.appSpacing
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
        AdaptiveScrollColumn(
            modifier = Modifier
                .weight(1f)
                .imePadding(),                       // supaya tidak tertutup keyboard
        ) {
            Column(
                modifier = Modifier
                    .padding(all = appLayout.defaultPadding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(appSpacing.xxxs),
                ) {
                Image(
                    painter = painterResource(id = R.drawable.asset_message),
                    contentDescription = null,
                    modifier = Modifier.size(100.dp),
                    contentScale = ContentScale.Fit,
                )
                Text(
                    text = buildAnnotatedString {
                        append("Masukkan 6 digit kode OTP yang telah dikirimkan ke nomor ")
                        withStyle(
                            SpanStyle(
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        ) {
                            append(phoneNumber)
                        }
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                )

                if (displayedError != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = displayedError,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.offset(x = otpShakeOffset.value.dp),
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))

                OtpInput(
                    value = otpValue,
                    onValueChange = { newValue ->
                        otpValue = newValue
                        if (newValue.length == OTP_LENGTH && !isProcessing) {
                            onVerify(newValue)
                        }
                    },
                    enabled = !isExpired && !isProcessing,
                    isError = isExpired || errorMessage != null,
                    modifier = Modifier.offset(x = otpShakeOffset.value.dp),
                )

                Text(
                    text = formatTime(remainingSeconds),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                )

                FlowRow(
                    modifier = Modifier.offset(x = resendShakeOffset.value.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalArrangement = Arrangement.Center,
                    itemVerticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Tidak menerima kode OTP?",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                    )

                    Text(
                        text = if (isResending) "Mengirim..." else "Kirim Ulang OTP",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        textAlign = TextAlign.Center,
                        color = if (isExpired && !isResending) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .then(
                                if (isExpired && !isResending) {
                                    Modifier.clickable {
                                        scope.launch {
                                            isResending = true

                                            val success = onResend()

                                            isResending = false

                                            if (success) {
                                                restartCountdown()
                                            }
                                        }
                                    }
                                } else {
                                    Modifier
                                }
                            ),
                    )
                }
            }
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