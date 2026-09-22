package bsb.dev.bsb_bangking_jp.feature.pengaturan

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import bsb.dev.bsb_bangking_jp.core.components.AppHeader
import bsb.dev.bsb_bangking_jp.core.theme.extendedColors

/**
 * Padanan halaman "Pusat Bantuan" -- SENGAJA TIDAK pakai dummy data terpisah
 * (beda dengan FAQ/Syarat Ketentuan/Tentang Aplikasi), karena kontak di bawah ini
 * bersifat tetap dan tidak akan pindah ke API, jadi cukup di-hardcode langsung di sini.
 */
private const val CALL_CENTER_NUMBER = "1500711"
private const val SUPPORT_EMAIL = "callcenter@banksumselbabel.com"
private const val KANTOR_PUSAT = "Jl. Gub. H. Bastari No.7, Jakabaring, Palembang, Sumatera Selatan"

private data class BantuanContact(
    val icon: ImageVector,
    val title: String,
    val value: String,
    val onTap: (Context) -> Unit,
)

private val bantuanContacts = listOf(
    BantuanContact(
        icon = Icons.Default.Call,
        title = "Call Center",
        value = CALL_CENTER_NUMBER,
        onTap = { ctx -> ctx.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:$CALL_CENTER_NUMBER"))) },
    ),
    BantuanContact(
        icon = Icons.Default.Email,
        title = "Email",
        value = SUPPORT_EMAIL,
        onTap = { ctx -> ctx.startActivity(Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:$SUPPORT_EMAIL"))) },
    ),
    BantuanContact(
        icon = Icons.Default.LocationOn,
        title = "Kantor Pusat",
        value = KANTOR_PUSAT,
        onTap = { ctx -> ctx.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=${Uri.encode(KANTOR_PUSAT)}"))) },
    ),
)

@Composable
fun PusatBantuanPage(onBackClick: () -> Unit = {}) {
    val context = LocalContext.current

    Scaffold(
        topBar = { AppHeader(title = "Pusat Bantuan", onBackClick = onBackClick) },
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).padding(24.dp)) {
            Text(
                text = "Butuh bantuan? Hubungi kami melalui salah satu kanal berikut.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.extendedColors.textSecondary,
            )
            Spacer(modifier = Modifier.height(20.dp))

            bantuanContacts.forEach { contact ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.extendedColors.inputBackground)
                        .clickable { contact.onTap(context) }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(imageVector = contact.icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(text = contact.title, style = MaterialTheme.typography.titleSmall)
                        Text(
                            text = contact.value,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.extendedColors.textSecondary,
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}