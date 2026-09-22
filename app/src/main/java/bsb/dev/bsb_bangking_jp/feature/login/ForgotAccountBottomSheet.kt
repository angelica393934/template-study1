package bsb.dev.bsb_bangking_jp.feature.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import bsb.dev.bsb_bangking_jp.core.components.AppButton
import bsb.dev.bsb_bangking_jp.core.components.AppModalBottomSheet
import bsb.dev.bsb_bangking_jp.core.components.SelectableOptionCard
import bsb.dev.bsb_bangking_jp.core.theme.appSpacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotAccountBottomSheet(
    onDismiss: () -> Unit,
    onSelectUserId: () -> Unit,
    onSelectPassword: () -> Unit,
) {
    var selectedOption by remember { mutableStateOf<String?>(null) }

    AppModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier.fillMaxWidth(),

            verticalArrangement = Arrangement.spacedBy(appSpacing.xs)
        ) {
            SelectableOptionCard(
                title = "ID Pengguna",
                description = "Proses untuk mengatur ulang ID Pengguna akun Anda guna memulihkan akses dan memastikan keamanan profil.",
                isSelected = selectedOption == "user_id",
                onTap = { selectedOption = "user_id" },
            )
            SelectableOptionCard(
                title = "Kata Sandi",
                description = "Proses untuk mengatur ulang kata sandi akun Anda guna memulihkan akses dan menjaga keamanan data.",
                isSelected = selectedOption == "password",
                onTap = { selectedOption = "password" },
            )
            AppButton(
                text = "Lanjutkan",
                enabled = selectedOption != null,
                onClick = {
                    when (selectedOption) {
                        "user_id" -> onSelectUserId()
                        "password" -> onSelectPassword()
                    }
                },
            )
        }
    }
}