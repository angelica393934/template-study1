package bsb.dev.bsb_bangking_jp.feature.pengaturan

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import bsb.dev.bsb_bangking_jp.core.components.AppHeader
import bsb.dev.bsb_bangking_jp.core.theme.appLayout
import bsb.dev.bsb_bangking_jp.core.theme.appSpacing
import bsb.dev.bsb_bangking_jp.core.theme.extendedColors
import bsb.dev.bsb_bangking_jp.feature.pengaturan.dummy.DummyFaqAnswer
import bsb.dev.bsb_bangking_jp.feature.pengaturan.dummy.DummyFaqBullet
import bsb.dev.bsb_bangking_jp.feature.pengaturan.dummy.DummyFaqItem
import bsb.dev.bsb_bangking_jp.feature.pengaturan.dummy.DummyPengaturanData

@Composable
fun FaqPage(
    onBackClick: () -> Unit = {},
    faqList: List<DummyFaqItem> = DummyPengaturanData.faqList,
) {
    Scaffold(
        topBar = { AppHeader(title = "FAQ", onBackClick = onBackClick) },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.padding(innerPadding).fillMaxSize(),
            contentPadding = PaddingValues(vertical = appLayout.defaultPadding),
        ) {
            items(faqList) { item ->
                FaqCard(item = item, modifier = Modifier.padding(horizontal = appLayout.defaultPadding, vertical = appSpacing.xxxs))
            }
        }
    }
}

@Composable
private fun FaqCard(item: DummyFaqItem, modifier: Modifier = Modifier) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(0.5.dp, MaterialTheme.extendedColors.textDisabled,
                RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.background),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
                .padding(all= appSpacing.xxxs),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = item.question,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                tint = MaterialTheme.extendedColors.textSecondary,
            )
        }

        AnimatedVisibility(visible = expanded) {
            Column {
                HorizontalDivider(thickness = 0.5.dp ,color = MaterialTheme.extendedColors.textDisabled)
                Column(modifier = Modifier.padding( appSpacing.xxxs)) {
                    FaqAnswerContent(answer = item.answer)
                }
            }
        }
    }
}

@Composable
private fun FaqAnswerContent(answer: DummyFaqAnswer) {
    answer.text?.let {
        Text(text = it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.extendedColors.textSecondary)
        if (answer.bullets.isNotEmpty()) Spacer(modifier = Modifier.height(6.dp))
    }
    if (answer.bullets.isNotEmpty()) {
        FaqBulletList(bullets = answer.bullets)
    }
}

/** Padanan BulletList.dart -- mendukung 1 level sub-bullet (indent bertambah). */
@Composable
private fun FaqBulletList(bullets: List<DummyFaqBullet>, indent: Dp = 0.dp) {
    Column {
        bullets.forEach { bullet ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(start = indent, bottom = 4.dp),
                verticalAlignment = Alignment.Top,
            ) {
                Icon(
                    imageVector = Icons.Default.Circle,
                    contentDescription = null,
                    tint = MaterialTheme.extendedColors.textSecondary,
                    modifier = Modifier.padding(top = 6.dp).size(if (indent == 0.dp) 6.dp else 5.dp),
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = bullet.text,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.extendedColors.textSecondary,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.weight(1f),
                )
            }
            if (bullet.subItems.isNotEmpty()) {
                FaqBulletList(
                    bullets = bullet.subItems.map { DummyFaqBullet(it) },
                    indent = indent + 20.dp,
                )
            }
        }
    }
}