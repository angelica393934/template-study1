package bsb.dev.bsb_bangking_jp.feature.change_pw

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import bsb.dev.bsb_bangking_jp.core.component.AppButton
import bsb.dev.bsb_bangking_jp.core.component.AppHeader
import bsb.dev.bsb_bangking_jp.core.component.AppTextField
import bsb.dev.bsb_bangking_jp.core.component.LocalLoadingOverlay
import bsb.dev.bsb_bangking_jp.core.component.RuleBullet
import bsb.dev.bsb_bangking_jp.core.theme.extendedColors
import bsb.dev.bsb_bangking_jp.feature.change_pw.presentation.ChangePwNavEvent
import bsb.dev.bsb_bangking_jp.feature.change_pw.presentation.ChangePwViewModel

@Composable
fun NewPasswordPage(
    viewModel: ChangePwViewModel,
    onBackClick: () -> Unit,
    onNavigateToOtp: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val loadingOverlay = LocalLoadingOverlay.current

    var newPasscode by remember { mutableStateOf("") }
    var confirmPasscode by remember { mutableStateOf("") }
    var confirmError by remember { mutableStateOf<String?>(null) }

    val has8Chars = newPasscode.length == 8
    val hasUpperLower = Regex("(?=.*[a-z])(?=.*[A-Z])").containsMatchIn(newPasscode)
    val hasNumber = Regex("[0-9]").containsMatchIn(newPasscode)
    val isPasswordValid = has8Chars && hasUpperLower && hasNumber

    LaunchedEffect(uiState.isLoading) {
        if (uiState.isLoading) loadingOverlay.show() else loadingOverlay.hide()
    }
    LaunchedEffect(Unit) {
        viewModel.navEvent.collect { event ->
            if (event is ChangePwNavEvent.ToOtpPage) onNavigateToOtp()
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        AppHeader(title = "Ganti Kata Sandi", onBackClick = onBackClick)

        Column(modifier = Modifier.padding(24.dp)) {
            Text(text = "Masukkan Kata Sandi Baru", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(25.dp))

            AppTextField(
                value = newPasscode,
                onValueChange = {
                    newPasscode = it
                    viewModel.clearNewPasscodeError()
                },
                labelText = "Kata Sandi Baru",
                hintText = "Masukkan Kata Sandi Baru",
                icon = Icons.Default.Lock,
                obscureText = true,
                errorText = uiState.newPasscodeError,
                showError = uiState.newPasscodeError != null,
                enableFocusBackground = true,
            )

            Spacer(modifier = Modifier.height(20.dp))

            AppTextField(
                value = confirmPasscode,
                onValueChange = {
                    confirmPasscode = it
                    confirmError = null
                },
                labelText = "Ulangi Kata Sandi Baru",
                hintText = "Ulangi Kata Sandi Baru",
                icon = Icons.Default.Lock,
                obscureText = true,
                errorText = confirmError,
                showError = confirmError != null,
                enableFocusBackground = true,
            )

            Spacer(modifier = Modifier.height(20.dp))
            HorizontalDivider(color = MaterialTheme.extendedColors.divider)
            Spacer(modifier = Modifier.height(20.dp))

            Text(text = "Aturan Kata Sandi", style = MaterialTheme.typography.titleSmall)
            Spacer(modifier = Modifier.height(10.dp))
            RuleBullet("Gunakan tepat 8 karakter", newPasscode.isNotEmpty(), has8Chars)
            Spacer(modifier = Modifier.height(5.dp))
            RuleBullet("Gunakan huruf besar dan kecil", newPasscode.isNotEmpty(), hasUpperLower)
            Spacer(modifier = Modifier.height(5.dp))
            RuleBullet("Gunakan minimal 1 angka", newPasscode.isNotEmpty(), hasNumber)

            Spacer(modifier = Modifier.height(40.dp))

            AppButton(
                text = "Simpan",
                enabled = isPasswordValid,
                onClick = {
                    if (confirmPasscode.isEmpty()) {
                        confirmError = "Ulangi kata sandi tidak boleh kosong"
                        return@AppButton
                    }
                    if (confirmPasscode != newPasscode) {
                        confirmError = "Kata sandi tidak sama"
                        return@AppButton
                    }
                    confirmError = null
                    viewModel.changePw(newPasscode, confirmPasscode)
                },
            )
        }
    }
}