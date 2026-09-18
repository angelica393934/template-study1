package bsb.dev.bsb_bangking_jp.feature.pengaturan.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import bsb.dev.bsb_bangking_jp.core.component.AppButton
import bsb.dev.bsb_bangking_jp.core.component.AppModalBottomSheet
import bsb.dev.bsb_bangking_jp.core.theme.Primary8
import bsb.dev.bsb_bangking_jp.core.theme.extendedColors

/**
 * Kode bahasa dijaga tetap "id" / "en" -- PERSIS sama dengan kode yang sudah dipakai
 * [bsb.dev.bsb_bangking_jp.core.data.datastore.AppPreferenceRepository.saveLanguage]
 * dan [bsb.dev.bsb_bangking_jp.viewmodel.SettingsViewModel.saveLanguage], supaya
 * pilihan di sheet ini langsung kompatibel dengan penyimpanan (DataStore, key
 * `LANGUAGE`) yang sudah ada di project -- TIDAK perlu bikin storage baru.
 *
 * Pemanggil (mis. PengaturanPage) yang bertanggung jawab memanggil
 * `settingsViewModel.saveLanguage(lang)` di [onLanguageSelected], sama seperti pola
 * `onThemeChange` untuk dark mode yang sudah berjalan di MainActivity/AppNavigation.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BahasaSheet(
    currentLanguage: String?,
    onDismiss: () -> Unit,
    onLanguageSelected: (String) -> Unit,
) {
    var selectedLanguage by remember { mutableStateOf(currentLanguage) }

    AppModalBottomSheet(onDismissRequest = onDismiss) {
        Column {
            Text(
                text = "Atur bahasa tampilan sesuai kebutuhan Anda.",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(30.dp))

            LanguageOption(
                label = "Bahasa Indonesia",
                selected = selectedLanguage == "id",
                onTap = { selectedLanguage = "id" },
            )
            Spacer(modifier = Modifier.height(10.dp))
            LanguageOption(
                label = "Bahasa Inggris",
                selected = selectedLanguage == "en",
                onTap = { selectedLanguage = "en" },
            )

            Spacer(modifier = Modifier.height(30.dp))

            AppButton(
                text = "Pilih Bahasa",
                enabled = selectedLanguage != null,
                onClick = {
                    // 🔹 Simpan dulu ke ViewModel/DataStore (dilakukan pemanggil di
                    // onLanguageSelected), baru tutup sheet -- persis urutan di Flutter
                    // (Navigator.pop dulu, tapi di sini kita balik: save -> dismiss,
                    // supaya state tersimpan sebelum sheet hilang dari komposisi).
                    selectedLanguage?.let { onLanguageSelected(it) }
                    onDismiss()
                },
            )

            Spacer(modifier = Modifier.height(6.dp))
        }
    }
}

@Composable
private fun LanguageOption(
    label: String,
    selected: Boolean,
    onTap: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(25.dp))
            .background(if (selected) Primary8 else MaterialTheme.colorScheme.background)
            .border(
                width = 1.5.dp,
                color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.extendedColors.divider,
                shape = RoundedCornerShape(25.dp),
            )
            .clickable { onTap() }
            .padding(vertical = 18.dp),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium,
            color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.extendedColors.textPrimary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}