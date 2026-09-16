package bsb.dev.bsb_bangking_jp.feature.forget_pw

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
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
import bsb.dev.bsb_bangking_jp.core.theme.appLayout
import bsb.dev.bsb_bangking_jp.core.theme.appSpacing
import bsb.dev.bsb_bangking_jp.core.theme.extendedColors
import bsb.dev.bsb_bangking_jp.feature.forget_pw.presentation.ForgetPwNavEvent
import bsb.dev.bsb_bangking_jp.feature.forget_pw.presentation.ForgetPwUiEvent
import bsb.dev.bsb_bangking_jp.feature.forget_pw.presentation.ForgetPwViewModel

@Composable
fun FindAccountPageForgetPw(
    viewModel: ForgetPwViewModel,
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
            if (event is ForgetPwNavEvent.ToOtpPage) onNavigateToOtp()
        }
    }
    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            if (event is ForgetPwUiEvent.ShowToastError) toastState.showError(event.message)
        }
    }
    LaunchedEffect(uiState.isLoading) {
        if (uiState.isLoading) loadingOverlay.show() else loadingOverlay.hide()
    }

    Column(modifier = Modifier.fillMaxSize()) {
        AppHeader(title = "Atur ulang Kata Sandi", onBackClick = onBackClick)

        Column(
            modifier = Modifier.padding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(appSpacing.xxxs)
        ) {
            Image(
                painter = painterResource(id = R.drawable.asset_pw),
                contentDescription = null,
                modifier = Modifier.height(100.dp),
            )
            Text(
                text = "Atur ulang Kata Sandi untuk akses akun",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge,
            )
            Text(
                text = "Atur ulang Kata Sandi untuk memulihkan akses, dan kelancaran penggunaan layanan.",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.extendedColors.textSecondary,
            )
            AppTextField(
                value = accountInput,
                onValueChange = {
                    accountInput = it
                    viewModel.clearAccountError()
                },
                labelText = "Rekening atau ATM",
                hintText = "Masukkan Nomor Rekening / ATM",
                icon = Icons.Default.CreditCard,
                isNumberOnly = true,
                errorText = uiState.atmCardError,
                showError = uiState.atmCardError != null,
                enableFocusBackground = true,
            )
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
            AppButton(
                text = "Lanjutkan",
                modifier = Modifier.padding(vertical= appSpacing.xxxs),
                icon =  Icons.AutoMirrored.Filled.ArrowForward,
                onClick = { viewModel.getPw(accountInput, phoneInput) },
            )
        }
    }
}