package bsb.dev.bsb_bangking_jp.feature.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import bsb.dev.bsb_bangking_jp.R
import bsb.dev.bsb_bangking_jp.core.component.AppButton
import androidx.compose.ui.res.stringResource
import bsb.dev.bsb_bangking_jp.core.component.AppModalBottomSheet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginPage(
    navController: NavController
) {
    val login = stringResource(R.string.login_button)
    val loginText = stringResource(R.string.portal_login_text)
    val highlightText = stringResource(R.string.portal_login_highlight)
    var showLoginSheet by remember {
        mutableStateOf(false)
    }
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    if (showLoginSheet) {
        AppModalBottomSheet(
            showIndicator = false,

            onDismissRequest = {
                showLoginSheet = false
            },
            sheetState = sheetState
        ) {
            LoginSheet(
                navController = navController
            )
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {

        // Background
        Image(
            painter = painterResource(R.drawable.latar_bg),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 10.dp)
        ) {

            // Konten utama
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Spacer(modifier = Modifier.height(10.dp))

                Image(
                    painter = painterResource(R.drawable.logo_bsb),
                    contentDescription = null,
                    modifier = Modifier
                    .size(130.dp)
                )

                Spacer(modifier = Modifier.height(25.dp))

                Image(
                    painter = painterResource(R.drawable.portal),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(25.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.30f))
                        .padding(
                            horizontal = 24.dp,
                            vertical = 16.dp
                        )
                ) {

                    Text(
                        text = buildAnnotatedString {
                            withStyle(
                                SpanStyle(
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            ) {
                                append(loginText)
                            }

                            withStyle(
                                SpanStyle(
                                    color = MaterialTheme.colorScheme.tertiary
                                )
                            ) {
                                append(highlightText)
                            }
                        },
                        style = MaterialTheme.typography.displayLarge
                    )
                }
            }
            // Tombol bawah
            Column(
                modifier = Modifier.padding(
                    horizontal = 24.dp,
                    vertical = 20.dp
                )
            ) {
                AppButton(
                    text = login,
                    onClick = {
                        showLoginSheet = true
                    }
                )
            }
        }
    }
}