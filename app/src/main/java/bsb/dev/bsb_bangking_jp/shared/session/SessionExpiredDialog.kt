package bsb.dev.bsb_bangking_jp.shared.session

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import bsb.dev.bsb_bangking_jp.core.components.AppButton
import bsb.dev.bsb_bangking_jp.core.theme.extendedColors

@Composable
fun SessionExpiredDialog(onLoginAgain: () -> Unit) {
    val maxDialogHeight = LocalConfiguration.current.screenHeightDp.dp * 0.8f

    Dialog(
        onDismissRequest = {}, // tidak bisa ditutup selain lewat tombol
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false,
        ),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 50.dp),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = maxDialogHeight)   // batas atas saja, bukan tinggi tetap
                    .clip(RoundedCornerShape(20.dp))
                    .background(MaterialTheme.colorScheme.background)
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                // fill = false: ambil tinggi sesuai konten, hanya menyusut
                // (dan scroll) kalau konten melebihi sisa ruang.
                Column(
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Text(
                        text = "Sesi Anda Telah Berakhir",
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Text(
                        text = "Demi keamanan data dan transaksi Anda, sesi telah berakhir karena tidak ada aktivitas untuk beberapa waktu. Silakan masuk kembali untuk melanjutkan.",
                        style = MaterialTheme.typography.bodySmall,
                        color = extendedColors.textSecondary,
                    )
                }
                AppButton(text = "Masuk Kembali", onClick = onLoginAgain)
            }
        }
    }
}