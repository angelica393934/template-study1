package bsb.dev.bsb_bangking_jp.feature.underconstruction

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import bsb.dev.bsb_bangking_jp.R
import bsb.dev.bsb_bangking_jp.core.components.AppButton
import bsb.dev.bsb_bangking_jp.core.theme.appLayout
import bsb.dev.bsb_bangking_jp.core.theme.appSpacing
import bsb.dev.bsb_bangking_jp.core.theme.extendedColors

@Composable
fun UnderConstructionPage(
    onBackClick: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        // ===== AREA KONTEN: pas 1 layar, scroll kalau tidak muat =====
        BoxWithConstraints(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
        ) {
            val areaHeight = maxHeight

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())   // scroll dulu...
                    .heightIn(min = areaHeight)              // ...baru tinggi minimal = area tersedia
                    .padding(horizontal = appLayout.defaultPadding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(
                    space = appSpacing.xs,
                    alignment = Alignment.CenterVertically,  // konten terpusat vertikal
                ),
            ) {
                Text(
                    text = "Oops, fitur ini masih kita bangun. Nantikan segera ya",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.displaySmall,
                )

                Image(
                    painter = painterResource(id = R.drawable.asset_404),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 250.dp),             // mengecil otomatis di layar pendek
                    contentScale = ContentScale.Fit,
                )

                Text(
                    text = "Fitur ini masih kami kembangkan supaya makin nyaman digunakan.\nTerima kasih sudah menunggu \uD83E\uDDDA\u200D\u2640\uFE0F.",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium,
                    color = extendedColors.textSecondary,
                )
            }
        }

        // ===== TOMBOL: di luar scroll, selalu menempel di bawah =====
        AppButton(
            text = "Kembali",
            onClick = onBackClick,
            modifier = Modifier.padding(
                start = appLayout.defaultPadding,
                end = appLayout.defaultPadding,
                top = appSpacing.xxxs,
                bottom = appLayout.defaultPadding,
            ),
        )
    }
}