package bsb.dev.bsb_bangking_jp.feature.activation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import bsb.dev.bsb_bangking_jp.core.components.AdaptiveScrollColumn
import bsb.dev.bsb_bangking_jp.core.components.AppButton
import bsb.dev.bsb_bangking_jp.core.components.AppHeader
import bsb.dev.bsb_bangking_jp.core.components.AppTextField
import bsb.dev.bsb_bangking_jp.core.components.LocalLoadingOverlay
import bsb.dev.bsb_bangking_jp.core.components.LocalToastState
import bsb.dev.bsb_bangking_jp.core.theme.appLayout
import bsb.dev.bsb_bangking_jp.core.theme.appSpacing
import bsb.dev.bsb_bangking_jp.core.theme.extendedColors
import bsb.dev.bsb_bangking_jp.feature.activation.presentation.ActivationNavEvent
import bsb.dev.bsb_bangking_jp.feature.activation.presentation.ActivationUiEvent
import bsb.dev.bsb_bangking_jp.feature.activation.presentation.ActivationViewModel

@Composable
fun InputPwPageActivation(
    viewModel: ActivationViewModel,
    onBackClick: () -> Unit,
    onNavigateToPin: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val toastState = LocalToastState.current
    val loadingOverlay = LocalLoadingOverlay.current

    var passcodeInput by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.navEvent.collect { event ->
            if (event is ActivationNavEvent.ToPinPage) onNavigateToPin()
        }
    }
    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            if (event is ActivationUiEvent.ShowToastError) toastState.showError(event.message)
        }
    }
    LaunchedEffect(uiState.isLoading) {
        if (uiState.isLoading) loadingOverlay.show() else loadingOverlay.hide()
    }

    Column(modifier = Modifier.fillMaxSize()) {
        AppHeader(title = "Aktivasi Akun", onBackClick = onBackClick)
        AdaptiveScrollColumn(
            modifier = Modifier
                .weight(1f)
                .imePadding(),                       // supaya tidak tertutup keyboard
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(all = appLayout.defaultPadding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(appSpacing.xxxs),
            ) {
                Text(text = "Masukkan Password", style = MaterialTheme.typography.titleLarge)
                Text(
                    text = "Gunakan Password yang sudah Anda daftarkan untuk melanjutkan aktivasi.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = extendedColors.textSecondary,
                )
                AppTextField(
                    value = passcodeInput,
                    onValueChange = { passcodeInput = it },
                    labelText = "Kata Sandi",
                    hintText = "Masukkan Kata Sandi",
                    icon = Icons.Default.Lock,
                    obscureText = true,
                    errorText = uiState.passcodeError,
                    showError = uiState.passcodeError != null,
                    enableFocusBackground = true,
                )
                Spacer(modifier = Modifier.height(appSpacing.ss))
                AppButton(
                    text = "Lanjutkan",
                    onClick = { viewModel.validatePasscode(passcodeInput) },
                )
            }
        }
    }
}