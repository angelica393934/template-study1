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
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import bsb.dev.bsb_bangking_jp.core.components.AppModalBottomSheet
import bsb.dev.bsb_bangking_jp.core.theme.appSpacing
import bsb.dev.bsb_bangking_jp.core.theme.extendedColors

private const val CALL_CENTER_NUMBER = "1500711"
private const val SUPPORT_EMAIL = "callcenter@banksumselbabel.com"

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
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpCenterSheet(
    onDismiss: () -> Unit,
) {
    val context = LocalContext.current

    AppModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            verticalArrangement = Arrangement.spacedBy(appSpacing.xxxs),
        ) {
            Text(
                text = "Pusat Bantuan",
                style = MaterialTheme.typography.titleLarge,
            )
            Text(
                text = "Jika mengalami kendala, Anda dapat menghubungi Customer Service kami",
                style = MaterialTheme.typography.bodyMedium,
                color = extendedColors.textSecondary,
            )

            bantuanContacts.forEach { contact ->

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .clickable { contact.onTap(context) }
                        .padding(vertical = 28.dp, horizontal = 20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                imageVector = contact.icon,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(20.dp))
                            Column {
                                Text(
                                    text = contact.title,
                                    style = MaterialTheme.typography.titleSmall
                                )
                                Text(
                                    text = contact.value,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = extendedColors.textSecondary,
                                )
                            }
                        }
                        Row {
                            Spacer(modifier = Modifier.width(20.dp))
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}