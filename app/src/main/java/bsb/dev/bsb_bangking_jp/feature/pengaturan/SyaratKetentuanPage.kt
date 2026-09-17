package bsb.dev.bsb_bangking_jp.feature.pengaturan

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import bsb.dev.bsb_bangking_jp.core.component.AppHeader
import bsb.dev.bsb_bangking_jp.feature.pengaturan.dummy.DummyPengaturanData
import bsb.dev.bsb_bangking_jp.feature.pengaturan.dummy.DummySyaratKetentuanSection

/** Padanan SyaratKetentuanPage.dart. Sumber data [DummyPengaturanData.syaratKetentuanList]. */
@Composable
fun SyaratKetentuanPage(
    onBackClick: () -> Unit = {},
    sections: List<DummySyaratKetentuanSection> = DummyPengaturanData.syaratKetentuanList,
) {
    Scaffold(
        topBar = { AppHeader(title = "Syarat dan Ketentuan", onBackClick = onBackClick) },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
        ) {
            Text(
                text = "SYARAT DAN KETENTUAN\nBANK SUMSEL BABEL MOBILE BANKING",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "PT Bank Pembangunan Daerah Sumatera Selatan dan Bangka Belitung",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(32.dp))

            sections.forEach { section ->
                Text(text = section.title, style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(16.dp))
                SyaratKetentuanBody(content = section.content)
                Spacer(modifier = Modifier.height(20.dp))
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