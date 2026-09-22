package bsb.dev.bsb_bangking_jp.core.components

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import bsb.dev.bsb_bangking_jp.R
import bsb.dev.bsb_bangking_jp.core.theme.extendedColors
import kotlinx.coroutines.launch

private const val PIN_LENGTH = 6

@Composable
fun InputPinPage(
    title: String,
    onBackClick: () -> Unit = {},
    showBack: Boolean = true,
    centerTitleWithBackButton: Boolean = false,
    onPinComplete: ((String) -> Unit)? = null,
    validator: (suspend (String) -> String?)? = null,
    externalError: String? = null,
    usePolaHeader: Boolean = false,
    customHeader: (@Composable () -> Unit)? = null,
    subtitle: String? = null,
    showTopBackground: Boolean = true,
    @DrawableRes backgroundRes: Int = R.drawable.bg,
    modifier: Modifier = Modifier,
) {
    var pin by remember { mutableStateOf("") }
    var pinSalah by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val shakeOffset = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()

    suspend fun showError(message: String) {
        pinSalah = true
        errorMessage = message
        shakeOffset.animateTo(-14f, tween(60))
        shakeOffset.animateTo(14f, tween(120))
        shakeOffset.animateTo(-14f, tween(120))
        shakeOffset.animateTo(0f, tween(60))
        pin = ""
    }

    // Padanan `didUpdateWidget`: tiap kali `externalError` baru datang dari
    // caller (mis. dari state management/ViewModel), tampilkan sebagai error.
    LaunchedEffect(externalError) {
        if (externalError != null) {
            showError(externalError)
        }
    }

    suspend fun handlePinComplete(enteredPin: String) {
        val error = validator?.invoke(enteredPin)
        if (error != null) {
            showError(error)
            return
        }

        errorMessage = null
        pinSalah = false

        onPinComplete?.invoke(enteredPin)
        pin = ""
    }

    val isUsingCustomHeader = usePolaHeader || centerTitleWithBackButton

    // Box di root = padanan `Stack` di : latar (bg.png) digambar penuh
    // satu layar dulu, baru Scaffold (header + konten + banner + keypad) di
    // atasnya dengan container transparan supaya latarnya kelihatan.
    Box(modifier = modifier.background(MaterialTheme.colorScheme.background)) {
        if (showTopBackground) {
            Image(
                painter = painterResource(id = backgroundRes),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.FillWidth,
                alignment = Alignment.TopCenter,
            )
        }

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {// ---- Header ----
                if (usePolaHeader) {
                    customHeader?.invoke()
                } else if (centerTitleWithBackButton) {
                    CenteredPinHeader(title = title, showBack = showBack, onBackClick = onBackClick)
                } else {
                    DefaultPinHeader(showBack = showBack, onBackClick = onBackClick)
                }
                     },
            bottomBar = {
                Column {
                    BannerKeamanan(
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 20.dp),
                    )
                    AppPinKeyboard(
                        onKeyTap = { key ->
                            if (pin.length < PIN_LENGTH) {
                                if (pinSalah || errorMessage != null) {
                                    pinSalah = false
                                    errorMessage = null
                                }
                                val newPin = pin + key
                                pin = newPin
                                if (newPin.length == PIN_LENGTH) {
                                    scope.launch { handlePinComplete(newPin) }
                                }
                            }
                        },
                        onBackspace = {
                            if (pin.isNotEmpty()) pin = pin.dropLast(1)
                        },
                    )
                }
            },
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            ) {


                // ---- Konten scrollable ----
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    if (!isUsingCustomHeader) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleLarge,
                            textAlign = TextAlign.Center,
                        )
                        Spacer(modifier = Modifier.height(30.dp))
                    }

                    subtitle?.let {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = it,
                            style = MaterialTheme.typography.titleLarge,
                            textAlign = TextAlign.Center,
                        )
                        Spacer(modifier = Modifier.height(30.dp))
                    }

                    Row(
                        modifier = Modifier.graphicsLayer { translationX = shakeOffset.value },
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        repeat(PIN_LENGTH) { index ->
                            val filled = index < pin.length
                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 12.dp)
                                    .size(12.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when {
                                            filled && pinSalah -> MaterialTheme.extendedColors.danger
                                            filled -> MaterialTheme.colorScheme.primary
                                            else -> MaterialTheme.extendedColors.divider
                                        }
                                    ),
                            )
                        }
                    }

                    errorMessage?.let {
                        Spacer(modifier = Modifier.height(18.dp))
                        Text(
                            text = it,
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.extendedColors.danger,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DefaultPinHeader(
    showBack: Boolean,
    onBackClick: () -> Unit,
) {
    Row(modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)) {
        if (showBack) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Default.ChevronLeft,
                    contentDescription = "Kembali",
                    modifier = Modifier.size(40.dp),
                )
            }
        }
    }
}

@Composable
private fun CenteredPinHeader(
    title: String,
    showBack: Boolean,
    onBackClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 20.dp),
        contentAlignment = Alignment.Center,
    ) {
        if (showBack) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.align(Alignment.CenterStart),
            ) {
                Icon(
                    imageVector = Icons.Default.ChevronLeft,
                    contentDescription = "Kembali",
                    modifier = Modifier.size(40.dp),
                )
            }
        }
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
        )
    }
}