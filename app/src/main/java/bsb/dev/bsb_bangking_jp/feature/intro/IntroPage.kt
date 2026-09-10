package bsb.dev.bsb_bangking_jp.feature.intro

import androidx.compose.foundation.background
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import bsb.dev.bsb_bangking_jp.R
import bsb.dev.bsb_bangking_jp.core.component.AppButton
import bsb.dev.bsb_bangking_jp.core.component.AppModalConfirm
import bsb.dev.bsb_bangking_jp.core.component.AppSwitch
import bsb.dev.bsb_bangking_jp.core.theme.extendedColors
import kotlinx.coroutines.launch

data class IntroPageData(
    val title: String,
    val desc: String,
    val image: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IntroPage(
    navController: NavController,
    darkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit
) {
    val pages = listOf(
        IntroPageData(
            title = "Selamat datang di\nBank Sumsel Babel Mobile",
            desc = "Terimakasih telah mempercayai transaksi keuangan anda di Bank SumselBabel, pastikan anda telah mendaftarkan layanan Bank SumselBabel Mobile melalui Costumer Service di Kantor Layanan Bank SumselBabel terdekat.",
            image = R.drawable.intro1
        ),
        IntroPageData(
            title = "Kelola keuangan anda\ndengan mudah dan nyaman",
            desc = "Merupakan suatu kehormatan bagi kami dapat melayani anda, Silahkan menggunakan layanan perbankan 24 jam dengan Bank SumselBabel Mobile, dapatkan kemudahan dan kenyamanan dalam melakukan transaksi keuangan kapanpun dan dimanapun anda inginkan.",
            image = R.drawable.intro2
        ),
        IntroPageData(
            title = "Keamanan dalam bertransaksi",
            desc = "Untuk keamanan dalam bertransaksi jagalah kerahasiaan akun Bank SumselBabel Mobile anda, Petugas Bank SumselBabel tidak pernah meminta informasi sensitif mengenai akun Mobile Banking anda.",
            image = R.drawable.intro3
        )
    )

    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { pages.size }
    )

    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primaryContainer)
    ) {
        Image(
            painter = painterResource(R.drawable.bg),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth(),
            contentScale = ContentScale.Crop
        )
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()
            ) {
                Spacer(modifier = Modifier.weight(1f))
                Image(
                    painter = painterResource(pages[page].image),
                    contentDescription = null,
                    modifier = Modifier.size(270.dp)
                )
                Spacer(modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.height(200.dp))
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .align(Alignment.TopCenter)
        ) {

            Spacer(modifier = Modifier.height(20.dp))

            Row {
                repeat(pages.size) { index ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(6.dp)
                            .padding(horizontal = 4.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (pagerState.currentPage == index)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.secondary
                            )
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))


            TextButton(
                modifier = Modifier.align(Alignment.End),
                onClick = {
                    navController.navigate("intro4") {
                        popUpTo("intro") {
                            inclusive = true
                        }
                    }
                }
            ) {
                Text("Lewati",
                        color = MaterialTheme.colorScheme.primary
                )

                Icon(
                    Icons.AutoMirrored.Filled.ArrowForward,
                    tint = MaterialTheme.colorScheme.primary,
                    contentDescription = null
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (darkTheme) "Dark" else "Light",
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.width(8.dp))

                AppSwitch(
                    checked = darkTheme,
                    onCheckedChange = onThemeChange
                )
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(328.dp)
                .align(Alignment.BottomCenter),
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
                verticalArrangement = Arrangement.SpaceBetween
            ) {

                Text(
                    text = pages[pagerState.currentPage].title,
                    style = MaterialTheme.typography.displaySmall,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = pages[pagerState.currentPage].desc,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.extendedColors.textDisabled,
                    textAlign = TextAlign.Center
                )
                AppButton(
                    text = if (pagerState.currentPage == 0) {
                        "Mulai"
                    } else {
                        "Lanjutkan"
                    },
                    onClick = {
                        scope.launch {
                            if (pagerState.currentPage < pages.lastIndex) {
                                pagerState.animateScrollToPage(
                                    pagerState.currentPage + 1
                                )
                            } else {
                                navController.navigate("intro4") {
                                    popUpTo("intro") {
                                        inclusive = true
                                    }
                                }
                            }
                        }
                    }
                )
            }
        }
    }
}