package bsb.dev.bsb_bangking_jp.feature.underconstruction

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import bsb.dev.bsb_bangking_jp.R
import bsb.dev.bsb_bangking_jp.core.components.AppButton
import bsb.dev.bsb_bangking_jp.core.theme.extendedColors

@Composable
fun UnderConstructionPage(
    onBackClick: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.weight(1f))

        // Judul
        Text(
            text = "Oops, fitur ini masih kita bangun.Nantikan segera ya",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.displaySmall,
        )

        Spacer(modifier = Modifier.height(60.dp))
        Image(
            painter = painterResource(id = R.drawable.asset_404),
            contentDescription = null,
            modifier = Modifier.height(250.dp),
            contentScale = ContentScale.Fit,
        )

        Spacer(modifier = Modifier.height(40.dp))

        // Deskripsi
        Text(
            text = "Fitur ini masih kami kembangkan supaya makin nyaman digunakan.\n Terima kasih sudah menunggu \uD83E\uDDDA\u200D\u2640\uFE0F.",
            textAlign = TextAlign.Center,
            fontSize = 14.sp,
            lineHeight = 21.sp,
            color = MaterialTheme.extendedColors.textSecondary,
        )

        Spacer(modifier = Modifier.weight(1f))

        // Tombol
        AppButton(
            text = "Kembali",
            onClick = onBackClick,
        )

        Spacer(modifier = Modifier.height(20.dp))
    }
}