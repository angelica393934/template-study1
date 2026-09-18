package bsb.dev.bsb_bangking_jp.feature.pengaturan

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import bsb.dev.bsb_bangking_jp.R
import bsb.dev.bsb_bangking_jp.core.theme.appLayout
import bsb.dev.bsb_bangking_jp.core.theme.appSpacing
import bsb.dev.bsb_bangking_jp.feature.pengaturan.dummy.DummyPengaturanData
import bsb.dev.bsb_bangking_jp.feature.pengaturan.dummy.DummySyaratKetentuanSection

/** Padanan SyaratKetentuanPage.dart. Sumber data [DummyPengaturanData.syaratKetentuanList]. */
@Composable
fun SyaratKetentuanPage(
    onBackClick: () -> Unit = {},
    sections: List<DummySyaratKetentuanSection> = DummyPengaturanData.syaratKetentuanList,
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(R.drawable.bg_1),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillWidth,
            alignment = Alignment.TopCenter
        )}
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = appLayout.defaultPadding)
                ) {
                    IconButton(
                        onClick = onBackClick
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBackIosNew,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.scrim
                        )
                    }}


        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(
                    all= appLayout.defaultPadding),
            verticalArrangement = Arrangement.spacedBy(appSpacing.xxxs)
        ) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.logo_bsb),
                    contentDescription = null,
                    modifier = Modifier.width(150.dp)
                )
            }
            Text(
                text = "SYARAT DAN KETENTUAN\nBANK SUMSEL BABEL MOBILE BANKING",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.fillMaxWidth(),
            )
            Text(
                text = "PT Bank Pembangunan Daerah Sumatera Selatan dan Bangka Belitung",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(appSpacing.xxxxs))

            sections.forEach { section ->
                Text(text = section.title, style = MaterialTheme.typography.titleLarge)
                SyaratKetentuanBody(content = section.content)
                Spacer(modifier = Modifier.height(appSpacing.xxxxs))
            }
        }
    }
}

/** Parser baris -- padanan `.split("\n")...map<Widget>(...)` di Dart (numbered / bullet "•" / paragraf). */
@Composable
private fun SyaratKetentuanBody(content: String) {
    val lines = remember(content) { content.split("\n").map { it.trim() }.filter { it.isNotEmpty() } }
    val numberedRegex = remember { Regex("^(\\d+\\.)\\s*(.*)$") }

    Column {
        lines.forEach { line ->
            val match = numberedRegex.find(line)
            when {
                match != null -> {
                    val (nomor, isi) = match.destructured
                    Row(modifier = Modifier.padding(bottom = 10.dp)) {
                        Text(text = nomor, style = MaterialTheme.typography.bodyMedium)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = isi,
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Justify,
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
                line.startsWith("•") -> {
                    Row(modifier = Modifier.padding(start = 24.dp, bottom = 6.dp)) {
                        Text(text = "• ", style = MaterialTheme.typography.bodyMedium)
                        Text(
                            text = line.substring(1).trim(),
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Justify,
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
                else -> {
                    Text(
                        text = line,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Justify,
                        modifier = Modifier.padding(start = 8.dp, bottom = 10.dp),
                    )
                }
            }
        }
    }
}