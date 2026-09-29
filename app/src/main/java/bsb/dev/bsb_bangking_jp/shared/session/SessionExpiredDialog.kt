package bsb.dev.bsb_bangking_jp.shared.session

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import bsb.dev.bsb_bangking_jp.core.components.AppButton
import bsb.dev.bsb_bangking_jp.core.theme.extendedColors

@Composable
fun SessionExpiredDialog(onLoginAgain: () -> Unit) {
    Dialog(
        onDismissRequest = {}, // tidak bisa ditutup selain lewat tombol
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false,
        ),
    ) {
        Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 50.dp)) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(MaterialTheme.colorScheme.background)
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(
                    text = "Sesi Anda Telah Berakhir",
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    text = "Demi keamanan data dan transaksi Anda, sesi telah berakhir karena tidak ada aktivitas untuk beberapa waktu. Silakan masuk kembali untuk melanjutkan.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.extendedColors.textSecondary,
                )
                AppButton(text = "Masuk Kembali", onClick = onLoginAgain)
            }
        }
    }
}