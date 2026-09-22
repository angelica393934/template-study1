package bsb.dev.bsb_bangking_jp.feature.change_pw

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import bsb.dev.bsb_bangking_jp.core.components.AppButton
import bsb.dev.bsb_bangking_jp.core.components.AppHeader
import bsb.dev.bsb_bangking_jp.core.components.AppTextField
import bsb.dev.bsb_bangking_jp.core.components.LocalLoadingOverlay
import bsb.dev.bsb_bangking_jp.core.theme.appLayout
import bsb.dev.bsb_bangking_jp.core.theme.appSpacing
import bsb.dev.bsb_bangking_jp.core.theme.extendedColors
import bsb.dev.bsb_bangking_jp.feature.change_pw.presentation.ChangePwNavEvent
import bsb.dev.bsb_bangking_jp.feature.change_pw.presentation.ChangePwViewModel

@Composable
fun OldPasswordPage(
    viewModel: ChangePwViewModel,
    onBackClick: () -> Unit,
    onNavigateToNewPassword: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val loadingOverlay = LocalLoadingOverlay.current

    var oldPasswordInput by remember { mutableStateOf("") }

    LaunchedEffect(uiState.isLoading) {
        if (uiState.isLoading) loadingOverlay.show() else loadingOverlay.hide()
    }
    LaunchedEffect(Unit) {
        viewModel.navEvent.collect { event ->
            if (event is ChangePwNavEvent.ToNewPasswordPage) onNavigateToNewPassword()
        }
    }

    Column(modifier = Modifier.fillMaxSize()
    ) {
        AppHeader(title = "Ganti Kata Sandi", onBackClick = onBackClick)
        Column(
            verticalArrangement = Arrangement.spacedBy(appSpacing.xs),
            modifier = Modifier.padding(appLayout.defaultPadding)
        ){
            Column(
                verticalArrangement = Arrangement.spacedBy(appSpacing.xxxs),
            ) {
                Text(text = "Masukkan Kata Sandi Lama", style = MaterialTheme.typography.titleLarge)

                Text(
                    text = "Sebelum mengubah kata sandi, masukkan kata sandi lama Anda.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.extendedColors.textSecondary,
                )

                AppTextField(
                    value = oldPasswordInput,
                    onValueChange = { oldPasswordInput = it },
                    hintText = "Masukkan Kata Sandi",
                    icon = Icons.Default.Lock,
                    obscureText = true,
                    errorText = uiState.oldPasscodeError,
                    showError = uiState.oldPasscodeError != null,
                    onClearError = { viewModel.clearOldPasscodeError() },
                    enableFocusBackground = true,
                )
            }
            AppButton(
                text = "Lanjutkan",
                onClick = { viewModel.validateOldPw(oldPasswordInput) },
            )
        }
    }
}