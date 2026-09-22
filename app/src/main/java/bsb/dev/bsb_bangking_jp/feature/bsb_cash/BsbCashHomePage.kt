package bsb.dev.bsb_bangking_jp.feature.bsb_cash


import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.nfc.NfcAdapter
import android.os.Bundle
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import bsb.dev.bsb_bangking_jp.R
import bsb.dev.bsb_bangking_jp.core.components.AppHeader
import bsb.dev.bsb_bangking_jp.core.theme.extendedColors
import kotlinx.coroutines.delay

private enum class BsbCashStep { WAITING_FOR_CARD, READING_CARD }

@Composable
fun BsbCashHomePage(
    onBackClick: () -> Unit = {},
    onFinished: () -> Unit = {},
) {
    var step by remember { mutableStateOf(BsbCashStep.WAITING_FOR_CARD) }

    val context = LocalContext.current
    val activity = remember(context) { context.findActivity() }
    val nfcAdapter = remember(context) { NfcAdapter.getDefaultAdapter(context) }

    LaunchedEffect(step) {
        if (step == BsbCashStep.READING_CARD) {
            if (activity != null) nfcAdapter?.disableReaderMode(activity)
            delay(2500)
            onFinished()
        }
    }

    // Aktifkan NFC reader mode selama halaman ini hidup (dimatikan lagi saat dispose).
    var nfcUnavailableReason by remember { mutableStateOf<String?>(null) }

    DisposableEffect(activity) {
        if (activity == null || nfcAdapter == null) {
            nfcUnavailableReason = "Perangkat ini tidak mendukung NFC."
            return@DisposableEffect onDispose { }
        }

        if (!nfcAdapter.isEnabled) {
            nfcUnavailableReason = "NFC belum aktif. Silakan aktifkan NFC di pengaturan ponsel."
            return@DisposableEffect onDispose { }
        }

        val flags = NfcAdapter.FLAG_READER_NFC_A or
                NfcAdapter.FLAG_READER_NFC_B or
                NfcAdapter.FLAG_READER_NFC_F or
                NfcAdapter.FLAG_READER_NFC_V or
                NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK

        try {
            nfcAdapter.enableReaderMode(
                activity,
                { _ ->
                    activity.runOnUiThread {
                        if (step == BsbCashStep.WAITING_FOR_CARD) {
                            step = BsbCashStep.READING_CARD
                        }
                    }
                },
                flags,
                Bundle(),
            )
        } catch (e: SecurityException) {
            // Permission NFC belum dideklarasikan di manifest.
            nfcUnavailableReason = "Aplikasi tidak memiliki izin NFC."
        }

        onDispose {
            try {
                nfcAdapter.disableReaderMode(activity)
            } catch (e: Exception) {
                // Aman diabaikan kalau reader mode memang belum aktif.
            }
        }
    }

    when (step) {
        BsbCashStep.WAITING_FOR_CARD -> TempelkanKartuContent(onBackClick = onBackClick)
        BsbCashStep.READING_CARD -> MembacaKartuContent()
    }
}

/** Halaman 1: "Tempelkan Kartu" -- kondisi awal, menunggu kartu ditempelkan. */
@Composable
private fun TempelkanKartuContent(onBackClick: () -> Unit) {
    Scaffold(
        topBar = {
            AppHeader(
                title = "Tempelkan Kartu",
                onBackClick = onBackClick,
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {

        Spacer(modifier = Modifier.height(40.dp))

        BsbCashLogo(modifier = Modifier.align(Alignment.CenterHorizontally))

        Spacer(modifier = Modifier.height(48.dp))

        Image(
            painter = painterResource(id = R.drawable.asset_nfc),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .size(220.dp),
            contentScale = ContentScale.Fit,
        )

        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "Aktifkan NFC di pengaturan ponsel.\n Tempelkan kartu di bagian belakang ponsel hingga terbaca.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.extendedColors.textSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
        )
    }
}
}
/** Halaman 2: "Sedang membaca kartu..." -- muncul begitu NFC mendeteksi kartu/e-money. */
@Composable
private fun MembacaKartuContent() {
    val infiniteTransition = rememberInfiniteTransition(label = "hourglass")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1600, easing = LinearEasing),
        ),
        label = "hourglass_rotation",
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .background(MaterialTheme.colorScheme.background),
    ) {
        Spacer(modifier = Modifier.height(56.dp))

        BsbCashLogo(modifier = Modifier.align(Alignment.CenterHorizontally))

        Spacer(modifier = Modifier.height(48.dp))

        Image(
            painter = painterResource(id = R.drawable.asset_membaca_kartu),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .size(160.dp)
                .rotate(rotation),
            contentScale = ContentScale.Fit,
        )

        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "Sedang membaca kartu…",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Pastikan NFC aktif dan kartu tetap menempel.\nJangan lepaskan kartu sampai proses selesai.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.extendedColors.textSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
        )
    }
}

/** Wordmark "BSB Cash". Ganti [R.drawable.logo_bsb_cash] sesuai nama file aset kamu. */
@Composable
private fun BsbCashLogo(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(id = R.drawable.ic_bsb_cash),
        contentDescription = "BSB Cash",
        modifier = modifier.height(60.dp) .
        size(800.dp),
        contentScale = ContentScale.Fit,
    )
}

/** Cari [Activity] dari [Context] -- diperlukan krn `enableReaderMode` butuh Activity. */
private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}