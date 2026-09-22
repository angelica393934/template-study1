package bsb.dev.bsb_bangking_jp.feature.pajak_pendidikan

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import bsb.dev.bsb_bangking_jp.core.components.AppHeader
import bsb.dev.bsb_bangking_jp.core.components.AppMenuHorizontal
import bsb.dev.bsb_bangking_jp.core.components.EmptyState
import bsb.dev.bsb_bangking_jp.core.components.SearchTextField
import bsb.dev.bsb_bangking_jp.core.dummy.DummyData

@Composable
fun LainnyaPajakPage(
    onBackClick: () -> Unit = {},
    onItemClick: (String) -> Unit = {},
) {
    var searchQuery by remember { mutableStateOf("") }

    val filteredItems by remember {
        derivedStateOf {
            val query = searchQuery.trim().lowercase()
            if (query.isEmpty()) {
                DummyData.pajakLainnyaMenuItems
            } else {
                DummyData.pajakLainnyaMenuItems.filter { it.label.lowercase().contains(query) }
            }
        }
    }
    Scaffold(
        topBar = {
            AppHeader(
                title = "Pajak dan Pendidikan",
                onBackClick = onBackClick,
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {

            SearchTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                hintText = "Cari",
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 20.dp),
            )

            // DAFTAR MENU ATAU EMPTY STATE
            if (filteredItems.isEmpty()) {
                EmptyState(modifier = Modifier.weight(1f))
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 12.dp),
                ) {
                    itemsIndexed(filteredItems) { index, item ->
                        AppMenuHorizontal(
                            label = item.label,
                            iconImg = item.iconRes,
                            scale = item.scale,
                            showDivider = index != filteredItems.lastIndex,
                            onTap = { onItemClick(item.label) },
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}