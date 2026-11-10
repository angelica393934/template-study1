package bsb.dev.bsb_bangking_jp.feature.pengaturan

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import bsb.dev.bsb_bangking_jp.R
import bsb.dev.bsb_bangking_jp.core.components.AppButton
import bsb.dev.bsb_bangking_jp.core.components.AppHeader
import bsb.dev.bsb_bangking_jp.core.components.LocalToastState
import bsb.dev.bsb_bangking_jp.core.theme.appSpacing
import bsb.dev.bsb_bangking_jp.feature.pengaturan.dummy.DummyPengaturanData
import bsb.dev.bsb_bangking_jp.feature.pengaturan.dummy.DummyTentangAplikasi

/**
 * Padanan TentangAplikasiPage.dart. Sumber data [DummyPengaturanData.tentangAplikasi].
 * Tombol "Kasih Ulasan" langsung buka Play Store karena build ini Android native --
 * `appStoreUrl` tetap disimpan di data buat jaga-jaga kalau dipakai fitur share lain nanti.
 */
@Composable
fun TentangAplikasiPage(
    onBackClick: () -> Unit = {},
    data: DummyTentangAplikasi = DummyPengaturanData.tentangAplikasi,
) {
    val context = LocalContext.current
    val toastState = LocalToastState.current

    Scaffold(
        topBar = { AppHeader(title = "Tentang Aplikasi", onBackClick = onBackClick) },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(appSpacing.sm)
        ) { Spacer(modifier = Modifier.height(appSpacing.ss))

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.logo_bsb),
                    contentDescription = null,
                    modifier = Modifier.width(180.dp)
                )
            }
            Spacer(modifier = Modifier.height(appSpacing.sm))

            Text(text = data.description, style = MaterialTheme.typography.bodyMedium)

            Text(text = data.features, style = MaterialTheme.typography.bodyMedium)
            Text(text = data.closingText, style = MaterialTheme.typography.bodyMedium)

            Spacer(modifier = Modifier.height(appSpacing.xs))
            AppButton(
                text = "Kasih Ulasan",
                icon = Icons.Default.ThumbUp,
                iconBeforeText = true,
                onClick = {
                    try {
                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(data.playStoreUrl)))
                    } catch (e: Exception) {
                        toastState.showError("Tidak bisa membuka Play Store")
                    }
                },
            )
        }
    }
}