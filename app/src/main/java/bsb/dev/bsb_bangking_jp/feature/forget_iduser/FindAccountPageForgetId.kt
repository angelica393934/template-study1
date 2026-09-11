package bsb.dev.bsb_bangking_jp.feature.forget_iduser

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.PhoneAndroid
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import bsb.dev.bsb_bangking_jp.R
import bsb.dev.bsb_bangking_jp.core.component.AppButton
import bsb.dev.bsb_bangking_jp.core.component.AppHeader
import bsb.dev.bsb_bangking_jp.core.component.AppTextField
import bsb.dev.bsb_bangking_jp.core.component.LocalLoadingOverlay
import bsb.dev.bsb_bangking_jp.core.component.LocalToastState
import bsb.dev.bsb_bangking_jp.core.theme.extendedColors
import bsb.dev.bsb_bangking_jp.feature.forget_iduser.presentation.ForgetIdUserNavEvent
import bsb.dev.bsb_bangking_jp.feature.forget_iduser.presentation.ForgetIdUserUiEvent
import bsb.dev.bsb_bangking_jp.feature.forget_iduser.presentation.ForgetIdUserViewModel

@Composable
fun FindAccountPageForgetId(
    viewModel: ForgetIdUserViewModel,
    onBackClick: () -> Unit,
    onNavigateToOtp: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val toastState = LocalToastState.current
    val loadingOverlay = LocalLoadingOverlay.current

    var accountInput by remember { mutableStateOf("") }
    var phoneInput by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.navEvent.collect { event ->
            if (event is ForgetIdUserNavEvent.ToOtpPage) onNavigateToOtp()
        }
    }
    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            if (event is ForgetIdUserUiEvent.ShowToastError) toastState.showError(event.message)
        }
    }
    LaunchedEffect(uiState.isLoading) {
        if (uiState.isLoading) loadingOverlay.show() else loadingOverlay.hide()
    }

    Column(modifier = Modifier.fillMaxSize()) {
        AppHeader(title = "Atur ulang ID Pengguna ", onBackClick = onBackClick)

        Column(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                painter = painterResource(id = R.drawable.asset_404),
                contentDescription = null,
                modifier = Modifier.height(100.dp),
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Atur ulang ID Pengguna untuk akses akun",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge,
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Atur ulang ID Pengguna untuk memulihkan akses, dan kelancaran penggunaan layanan.",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.extendedColors.textSecondary,
            )
            Spacer(modifier = Modifier.height(30.dp))

            AppTextField(
                value = accountInput,
                onValueChange = {
                    accountInput = it
                    viewModel.clearAccountError()
                },
                labelText = "Rekening atau ATM",
                hintText = "Masukkan Nomor Rekening/ ATM",
                icon = Icons.Default.CreditCard,
                isNumberOnly = true,
                errorText = uiState.atmCardError,
                showError = uiState.atmCardError != null,
                enableFocusBackground = true,
            )
            Spacer(modifier = Modifier.height(15.dp))

            AppTextField(
                value = phoneInput,
                onValueChange = {
                    phoneInput = it
                    viewModel.clearPhoneError()
                },
                labelText = "Nomor Handphone",
                hintText = "Masukkan Nomor Handphone",
                icon = Icons.Default.PhoneAndroid,
                keyboardType = KeyboardType.Number,
                isNumberOnly = true,
                errorText = uiState.phoneError,
                showError = uiState.phoneError != null,
                enableFocusBackground = true,
            )

            Spacer(modifier = Modifier.height(20.dp))
            AppButton(
                text = "Continue",
                onClick = { viewModel.getIdUser(accountInput, phoneInput) },
            )
        }
    }
}