package bsb.dev.bsb_bangking_jp.feature.intro

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import bsb.dev.bsb_bangking_jp.R
import bsb.dev.bsb_bangking_jp.core.component.AppButton
import bsb.dev.bsb_bangking_jp.core.theme.extendedColors

@Composable
fun IntroPage4(
    navController: NavController
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primaryContainer)
    ) {

        // Background atas
        Image(
            painter = painterResource(R.drawable.bg),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.3f),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier.fillMaxSize()
        ) {

            // Area gambar
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.BottomCenter
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Spacer(modifier = Modifier.weight(1f))

                    Image(
                    painter = painterResource(R.drawable.intro4),
                    contentDescription = null,
                        modifier = Modifier.size(250.dp)
                )
                    Spacer(modifier = Modifier.weight(1f))
                }
            }

            // Bottom sheet
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(328.dp),
                shape = RoundedCornerShape(
                    topStart = 30.dp,
                    topEnd = 30.dp
                ),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(
                            horizontal = 24.dp,
                            vertical = 30.dp
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Text(
                        text = "Selamat datang di\nBSB Mobile App",
                        style = MaterialTheme.typography.displaySmall,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Text(
                        text = "Mengelola ekosistem keuangan daerah & mitra bisnis secara terintegrasi dan berkelanjutan.",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.extendedColors.textDisabled,
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    AppButton(
                        text = "Masuk ke Akun",

                        textColor = MaterialTheme.colorScheme.primary,
                        backgroundColor = MaterialTheme.colorScheme.primaryContainer,
                        onClick = {
                            navController.navigate("registration")
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    AppButton(
                        text = "Daftar Sekarang",
                        onClick = {
                            navController.navigate("login_existing")
                        }
                    )
                }
            }
        }
    }
}