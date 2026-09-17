package bsb.dev.bsb_bangking_jp.feature.lokasi_atm

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import bsb.dev.bsb_bangking_jp.core.component.AppHeader
import bsb.dev.bsb_bangking_jp.core.component.EmptyState
import bsb.dev.bsb_bangking_jp.core.component.SearchTextField
import bsb.dev.bsb_bangking_jp.core.theme.Gray400
import bsb.dev.bsb_bangking_jp.core.theme.Primary2
import bsb.dev.bsb_bangking_jp.core.theme.Primary8
import bsb.dev.bsb_bangking_jp.core.theme.appLayout
import kotlinx.coroutines.launch

// Data model ATM
data class Atm(
    val name: String,
    val address: String,
    val mapsUrl: String,
)

private val atmList = listOf(
    Atm(
        name = "Bank Sumsel Babel Kantor Pusat",
        address = "Jakabaring, Jl. Gub H Bastari No.7, Silaberanti, Kecamatan Seberang Ulu I, Kota Palembang, Sumatera Selatan 30967",
        mapsUrl = "https://maps.app.goo.gl/Az5pJh349c9mNbm9A",
    ),
    Atm(
        name = "Bank Sumsel Babel KC UIN RADEN Fatah",
        address = "Jl. Panca Usaha No.2085, 5 Ulu, Kecamatan Seberang Ulu I, Kota Palembang, Sumatera Selatan 30267",
        mapsUrl = "https://maps.app.goo.gl/7vhexcKfYhg15tKz5",
    ),
    Atm(
        name = "Bank Sumsel Babel Cabang Kapten A. Rivai",
        address = "Jl. Kapten A. Rivai No.21, Sungai Pangeran, Kec. Ilir Tim. I, Kota Palembang, Sumatera Selatan 30129",
        mapsUrl = "https://maps.app.goo.gl/KLQfNqXeBLMdpAGU7",
    ),
    Atm(
        name = "Bank Sumsel Babel - ATM Drive Thru",
        address = "Jl. POM IX No.1296, Lorok Pakjo, Kec. Ilir Bar. I, Kota Palembang, Sumatera Selatan 30126",
        mapsUrl = "https://maps.app.goo.gl/KZu12uj1APnAteB28",
    ),
    Atm(
        name = "ATM Bank Sumsel Babel Syariah kas RSI Siti Khadijah",
        address = "Jl. Demang Lebar Daun No.1, Lorok Pakjo, Kec. Ilir Bar. I, Kota Palembang, Sumatera Selatan",
        mapsUrl = "https://maps.app.goo.gl/Y5UbmdYk8WfsC5UY6",
    ),
    Atm(
        name = "Bank Sumsel Babel Cabang Lubuk Linggau",
        address = "Jl. Garuda No.43, Ps. Permiri, Kec. Lubuk Linggau Bar. II, Kota Lubuklinggau, Sumatera Selatan 31613",
        mapsUrl = "https://maps.app.goo.gl/tzHWeepUBu7h2zq68",
    ),
    Atm(
        name = "Bank Sumsel Babel Cabang Pangkal Pinang",
        address = "Jl. Jendral Sudirman No.8, Opas Indah, Kec. Taman Sari, Kota Pangkal Pinang, Kepulauan Bangka Belitung 33684",
        mapsUrl = "https://maps.app.goo.gl/SQ7T8ee4UHsmZs1F9",
    ),
    Atm(
        name = "Bank Sumsel Babel Cabang Batu Raja",
        address = "V5HF+74W, Jl. Dr. Setia Budi, Baturaja Lama, Kec. Baturaja Timur, Kabupaten Ogan Komering Ulu, Sumatera Selatan 32125",
        mapsUrl = "https://maps.app.goo.gl/ckGUzyouSrg1UcAd7",
    ),
)

@Composable
fun LokasiAtmPage(
    onBack: (() -> Unit)? = null,
    navController: NavController
) {
    var query by remember { mutableStateOf("") }
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val filteredList = remember(query) {
        atmList.filter { atm ->
            atm.name.contains(query, ignoreCase = true) ||
                    atm.address.contains(query, ignoreCase = true)
        }
    }

    fun launchMaps(url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            intent.setPackage("com.google.android.apps.maps")
            context.startActivity(intent)
        } catch (e: Exception) {
            try {
                // fallback: buka via browser kalau app Maps tidak ada
                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
            } catch (e2: Exception) {
                scope.launch {
                    snackbarHostState.showSnackbar("Tidak bisa membuka Google Maps")
                }
            }
        }
    }

    Scaffold(
        topBar = {
            AppHeader(
                title = "Lokasi ATM",
                onBackClick = {
                    onBack?.invoke() ?: navController.popBackStack()
                }
            )
        },
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column (
            modifier = Modifier
                .padding(all = appLayout.defaultPadding))
            {
                    SearchTextField(
                        value = query,
                        onValueChange = { query = it },
                    )
                    // 🔹 Tampilkan EmptyState jika hasil pencarian kosong
                    if (filteredList.isEmpty()) {
                        EmptyState(modifier = Modifier.fillMaxSize())
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            items(filteredList) { atm ->
                                AtmListItem(
                                    atm = atm,
                                    onClick = { launchMaps(atm.mapsUrl) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

@Composable
private fun AtmListItem(
    atm: Atm,
    onClick: () -> Unit,
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Primary8),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
        .padding( vertical = 6.dp)

    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                tint = Primary2,
                modifier = Modifier.size(25.dp)
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = atm.name,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = atm.address,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodySmall,
                    color = Gray400
                )
            }

            Icon(
                imageVector =  Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = null,
                modifier = Modifier.size(12.dp)
            )
        }
    }
}