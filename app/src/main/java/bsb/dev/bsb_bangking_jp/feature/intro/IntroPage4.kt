package bsb.dev.bsb_bangking_jp.feature.intro

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import bsb.dev.bsb_bangking_jp.core.components.AppButton
import bsb.dev.bsb_bangking_jp.core.theme.appLayout
import bsb.dev.bsb_bangking_jp.core.util.getResponsiveCardHeight

@Composable
fun IntroPage4(
    navController: NavController
) {                    // card bawah ±42% layar

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primaryContainer)
    ) {
        val cardHeight = getResponsiveCardHeight(maxHeight)
        val screenHeight = maxHeight
        val screenWidth = maxWidth
        val imageSize = minOf(screenWidth * 0.65f, screenHeight * 0.31f) // gambar ikut lebar & tinggi

        // Background atas
        Image(
            painter = painterResource(R.drawable.bg),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth(),
            contentScale = ContentScale.FillWidth
        )

        Column(
            modifier = Modifier.fillMaxSize()

        ) {

            // Area gambar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                        .weight(1f),
                contentAlignment = Alignment.Center

            ) {
                    Image(
                    painter = painterResource(R.drawable.intro4),
                    contentDescription = null,
                        modifier = Modifier.size(imageSize),
                    contentScale = ContentScale.Fit

                    )
            }

            // Bottom sheet
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(cardHeight),
                shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(all= appLayout.defaultPadding),
                ) {
                    // Judul + deskripsi: area yang bisa scroll kalau teks panjang / layar kecil
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Selamat datang di\nBSB Mobile App",
                            style = MaterialTheme.typography.displaySmall,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Text(
                            text = "Mengelola ekosistem keuangan daerah & mitra bisnis secara terintegrasi dan berkelanjutan.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                        )

                        Spacer(modifier = Modifier.height(4.dp))
                    }
                    Spacer(modifier = Modifier.height(10.dp))

                    AppButton(
                        text = "Masuk ke Akun",

                        textColor = MaterialTheme.colorScheme.primary,
                        backgroundColor = MaterialTheme.colorScheme.primaryContainer,
                        onClick = {
                            navController.navigate("login_existing")
                        }
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    AppButton(
                        text = "Daftar Sekarang",
                        onClick = {
                            navController.navigate("registration")
                        }
                    )
                }
            }
        }
    }
}