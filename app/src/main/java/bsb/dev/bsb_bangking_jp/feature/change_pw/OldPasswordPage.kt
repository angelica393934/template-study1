package bsb.dev.bsb_bangking_jp.feature.change_pw

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import bsb.dev.bsb_bangking_jp.core.component.AppButton
import bsb.dev.bsb_bangking_jp.core.component.AppHeader
import bsb.dev.bsb_bangking_jp.core.component.AppTextField
import bsb.dev.bsb_bangking_jp.core.component.LocalLoadingOverlay
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

    Column(modifier = Modifier.fillMaxSize()) {
        AppHeader(title = "Ganti Kata Sandi", onBackClick = onBackClick)

        Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 24.dp)) {
            Text(text = "Masukkan Kata Sandi Lama", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Sebelum mengubah kata sandi, masukkan kata sandi lama Anda.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.extendedColors.textSecondary,
            )
            Spacer(modifier = Modifier.height(25.dp))

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

            Spacer(modifier = Modifier.height(40.dp))

            AppButton(
                text = "Lanjutkan",
                onClick = { viewModel.validateOldPw(oldPasswordInput) },
            )
        }
    }
}