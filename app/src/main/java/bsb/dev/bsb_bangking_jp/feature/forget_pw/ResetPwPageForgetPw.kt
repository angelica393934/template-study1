package bsb.dev.bsb_bangking_jp.feature.forget_pw

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import bsb.dev.bsb_bangking_jp.R
import bsb.dev.bsb_bangking_jp.core.components.AppButton
import bsb.dev.bsb_bangking_jp.core.components.AppHeader
import bsb.dev.bsb_bangking_jp.core.components.AppModalConfirm
import bsb.dev.bsb_bangking_jp.core.components.AppTextField
import bsb.dev.bsb_bangking_jp.core.components.LocalLoadingOverlay
import bsb.dev.bsb_bangking_jp.core.components.LocalToastState
import bsb.dev.bsb_bangking_jp.core.components.RuleBullet
import bsb.dev.bsb_bangking_jp.core.theme.appLayout
import bsb.dev.bsb_bangking_jp.core.theme.appSpacing
import bsb.dev.bsb_bangking_jp.core.theme.extendedColors
import bsb.dev.bsb_bangking_jp.feature.forget_pw.presentation.ForgetPwNavEvent
import bsb.dev.bsb_bangking_jp.feature.forget_pw.presentation.ForgetPwUiEvent
import bsb.dev.bsb_bangking_jp.feature.forget_pw.presentation.ForgetPwViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResetPwPageForgetPw(
    viewModel: ForgetPwViewModel,
    onBackClick: () -> Unit,
    onResetSuccessComplete: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val toastState = LocalToastState.current
    val loadingOverlay = LocalLoadingOverlay.current

    var newPasscode by remember { mutableStateOf("") }
    var confirmPasscode by remember { mutableStateOf("") }
    var confirmError by remember { mutableStateOf<String?>(null) }
    var showSuccessSheet by remember { mutableStateOf(false) }

    val has8Chars = newPasscode.length == 8
    val hasUpperLower = Regex("(?=.*[a-z])(?=.*[A-Z])").containsMatchIn(newPasscode)
    val hasNumber = Regex("[0-9]").containsMatchIn(newPasscode)
    val isAllValid = has8Chars && hasUpperLower && hasNumber

    LaunchedEffect(uiState.isLoading) {
        if (uiState.isLoading) loadingOverlay.show() else loadingOverlay.hide()
    }
    LaunchedEffect(Unit) {
        viewModel.navEvent.collect { event ->
            if (event is ForgetPwNavEvent.ToPortalSuccess) showSuccessSheet = true
        }
    }
    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            if (event is ForgetPwUiEvent.ShowToastError) toastState.showError(event.message)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        AppHeader(title = "Atur Ulang Kata Sandi", onBackClick = onBackClick)

        Column(
            modifier = Modifier
                .padding(appLayout.defaultPadding),
            verticalArrangement = Arrangement.spacedBy(appSpacing.xxxs)
        ) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.asset_pw),
                    contentDescription = null,
                    modifier = Modifier.height(100.dp),
                )
            }
            Text(
                textAlign = TextAlign.Center,
                text = "Buat Kata Sandi baru untuk pengalaman masuk lebih nyaman.",
                style = MaterialTheme.typography.titleLarge,
            )
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
            AppTextField(
                value = confirmPasscode,
                onValueChange = {
                    confirmPasscode = it
                    confirmError = null
                },
                labelText = "Ulangi Kata Sandi",
                hintText = "Ulangi Kata Sandi Baru",
                icon = Icons.Default.Lock,
                obscureText = true,
                errorText = confirmError,
                showError = confirmError != null,
                enableFocusBackground = true,
            )
            Text(
                text = "Aturan Password Pengguna",
                style = MaterialTheme.typography.titleSmall,
                textAlign = TextAlign.Left,
                color = MaterialTheme.extendedColors.textSecondary,
            )
            RuleBullet("Gunakan maksimal 8 karakter", newPasscode.isNotEmpty(), has8Chars)
            RuleBullet("Gunakan huruf besar dan kecil", newPasscode.isNotEmpty(), hasUpperLower)
            RuleBullet("Sertakan angka", newPasscode.isNotEmpty(), hasNumber)
            AppButton(
                text = "Lanjutkan",
                icon =  Icons.AutoMirrored.Filled.ArrowForward,
                modifier = Modifier.padding(vertical = appSpacing.xxxs),
                enabled = isAllValid,
                onClick = {
                    if (confirmPasscode.isEmpty()) {
                        confirmError = "Ulangi password pengguna tidak boleh kosong"
                        return@AppButton
                    }
                    if (confirmPasscode != newPasscode) {
                        confirmError = "Password pengguna baru tidak sama"
                        return@AppButton
                    }
                    confirmError = null
                    viewModel.changePw(newPasscode, confirmPasscode)
                },
            )
        }
    }

    if (showSuccessSheet) {
         AppModalConfirm(
            onDismissRequest = {
                showSuccessSheet = false
                onResetSuccessComplete()
            },
            title = "Kata Sandi berhasil di perbarui",
            centerimage = R.drawable.asset_centang,
            description = "Silahkan gunakan kata sandi baru untuk menggunakan layanan kami.",
            confirmText = "Masuk Kembali",
            onConfirm = {
                showSuccessSheet = false
                onResetSuccessComplete()
            },
        )
    }
}