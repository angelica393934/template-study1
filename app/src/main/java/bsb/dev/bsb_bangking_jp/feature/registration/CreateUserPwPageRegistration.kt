package bsb.dev.bsb_bangking_jp.feature.registration

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.ExperimentalMaterial3Api
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
import bsb.dev.bsb_bangking_jp.R
import bsb.dev.bsb_bangking_jp.core.component.AppButton
import bsb.dev.bsb_bangking_jp.core.component.AppHeader
import bsb.dev.bsb_bangking_jp.core.component.AppModalConfirm
import bsb.dev.bsb_bangking_jp.core.component.AppTextField
import bsb.dev.bsb_bangking_jp.core.component.LocalLoadingOverlay
import bsb.dev.bsb_bangking_jp.core.component.LocalToastState
import bsb.dev.bsb_bangking_jp.core.component.RuleBullet
import bsb.dev.bsb_bangking_jp.core.theme.appLayout
import bsb.dev.bsb_bangking_jp.core.theme.appSpacing
import bsb.dev.bsb_bangking_jp.core.theme.extendedColors
import bsb.dev.bsb_bangking_jp.feature.registration.presentation.RegistrationNavEvent
import bsb.dev.bsb_bangking_jp.feature.registration.presentation.RegistrationUiEvent
import bsb.dev.bsb_bangking_jp.feature.registration.presentation.RegistrationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateUserPwPageRegistration(
    viewModel: RegistrationViewModel,
    onBackClick: () -> Unit,
    onRegistrationSelesai: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val toastState = LocalToastState.current
    val loadingOverlay = LocalLoadingOverlay.current

    var passcode by remember { mutableStateOf("") }
    var confirmPasscode by remember { mutableStateOf("") }
    var confirmError by remember { mutableStateOf<String?>(null) }
    var showSuccessSheet by remember { mutableStateOf(false) }

    val has8Chars = passcode.length == 8
    val hasUpperLower = Regex("(?=.*[a-z])(?=.*[A-Z])").containsMatchIn(passcode)
    val hasNumber = Regex("[0-9]").containsMatchIn(passcode)
    val isAllValid = has8Chars && hasUpperLower && hasNumber

    LaunchedEffect(uiState.isLoading) {
        if (uiState.isLoading) loadingOverlay.show() else loadingOverlay.hide()
    }
    LaunchedEffect(Unit) {
        viewModel.navEvent.collect { event ->
            if (event is RegistrationNavEvent.ToPortal) showSuccessSheet = true
        }
    }
    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            if (event is RegistrationNavEvent.ToPortal) showSuccessSheet = true
            if (event is RegistrationUiEvent.ShowToastError) toastState.showError(event.message)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        AppHeader(title = "Buat Kata Sandi", onBackClick = onBackClick)

        Column(
            modifier = Modifier
                .padding(appLayout.defaultPadding),
                 verticalArrangement = Arrangement.spacedBy(appSpacing.xxxs)
            ) {
            Text(text = "Buat Kata Sandi yang Aman", style = MaterialTheme.typography.titleLarge)
            Text(
                text = "Kata sandi ini akan digunakan setiap kali kamu masuk ke Bank Sumsel Babel Mobile Banking. " +
                        "Pastikan sulit ditebak dan tidak digunakan di akun lain.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.extendedColors.textSecondary,
            )
            AppTextField(
                value = passcode,
                onValueChange = {
                    passcode = it
                    viewModel.clearPasscodeError()
                },
                labelText = "Kata Sandi Baru",
                hintText = "Masukkan Kata Sandi Baru",
                icon = Icons.Default.Lock,
                obscureText = true,
                errorText = uiState.passcodeError,
                showError = uiState.passcodeError != null,
                enableFocusBackground = true,
            )
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
            HorizontalDivider(color = MaterialTheme.extendedColors.divider)
            Text(
                text = "Aturan Kata Sandi",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.extendedColors.textSecondary,
            )
            RuleBullet("Gunakan tepat 8 karakter", passcode.isNotEmpty(), has8Chars)
            RuleBullet("Gunakan kombinasi huruf besar dan kecil", passcode.isNotEmpty(), hasUpperLower)
            RuleBullet("Sertakan angka", passcode.isNotEmpty(), hasNumber)

            AppButton(
                text = "Lanjutkan",
                enabled = isAllValid,
                modifier = Modifier.padding(vertical = appSpacing.xxxs),
                icon =  Icons.AutoMirrored.Filled.ArrowForward,
                onClick = {
                    if (confirmPasscode != passcode) {
                        confirmError = "Kata sandi baru tidak sama"
                        return@AppButton
                    }
                    confirmError = null
                    viewModel.addPasscode(passcode, confirmPasscode)
                },
            )
        }
    }

    if (showSuccessSheet) {
        AppModalConfirm(
            onDismissRequest = {
                showSuccessSheet = false
                onRegistrationSelesai()
            },
            title = "Registration Berhasil!",
            description = "Silakan lakukan aktivasi akun Anda terlebih dahulu, dengan menggunakan ID Pengguna " +
                    "dan kata sandi baru untuk menggunakan layanan kami.",
            centerimage = R.drawable.asset_centang,
            confirmText = "Masuk Kembali",
            onConfirm = {
                showSuccessSheet = false
                onRegistrationSelesai()
            },
        )
    }
}