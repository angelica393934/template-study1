package bsb.dev.bsb_bangking_jp.feature.init

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import bsb.dev.bsb_bangking_jp.R
import bsb.dev.bsb_bangking_jp.core.component.LocalToastState
import bsb.dev.bsb_bangking_jp.feature.init.presentation.SplashNavigationEvent
import bsb.dev.bsb_bangking_jp.feature.init.presentation.SplashUiState
import bsb.dev.bsb_bangking_jp.feature.init.presentation.InitViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun SplashScreen(
    navController: NavController,
    viewModel: InitViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val toastState = LocalToastState.current

    // Navigasi one-shot: begitu event muncul, pindah halaman & bersihkan back stack "splash".
    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collect { event ->
            val destination = when (event) {
                SplashNavigationEvent.ToIntro -> "intro"
                SplashNavigationEvent.ToPortal -> "portal"
            }
            navController.navigate(destination) {
                popUpTo("splash") { inclusive = true }
            }
        }
    }

    // Toast error, padanan showErrorToast(context, message) di listener Bloc .
    LaunchedEffect(uiState) {
        val state = uiState
        if (state is SplashUiState.Error) {
            toastState.showError(state.message)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 20.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(1f))

            Image(
                painter = painterResource(id = R.drawable.logosplash),
                contentDescription = "Logo",
                modifier = Modifier.size(150.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "PT Bank Pembangunan Daerah Sumatera Selatan dan Bangka Belitung berizin dan diawasi oleh Otoritas Jasa Keuangan (OJK) dan Bank Indonesia (BI), serta merupakan peserta penjaminan Lembaga Penjamin Simpanan (LPS).",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Bank Sumsel Babel Mobile Banking\nVersi ${viewModel.appVersion}",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleSmall
            )
        }
    }
}