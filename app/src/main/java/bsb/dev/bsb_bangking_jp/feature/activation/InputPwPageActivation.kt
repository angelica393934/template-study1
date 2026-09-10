package bsb.dev.bsb_bangking_jp.feature.activation

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
import bsb.dev.bsb_bangking_jp.core.component.LocalToastState
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

        Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 20.dp)) {
            Text(text = "Masukkan Password", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Gunakan Password yang sudah Anda daftarkan untuk melanjutkan aktivasi.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.extendedColors.textSecondary,
            )
            Spacer(modifier = Modifier.height(20.dp))

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

            Spacer(modifier = Modifier.height(20.dp))
            AppButton(
                text = "Lanjutkan",
                onClick = { viewModel.validatePasscode(passcodeInput) },
            )
        }
    }
}