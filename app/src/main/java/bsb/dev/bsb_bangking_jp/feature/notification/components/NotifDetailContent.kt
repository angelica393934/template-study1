package bsb.dev.bsb_bangking_jp.feature.notification.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import bsb.dev.bsb_bangking_jp.core.components.AppButton
import bsb.dev.bsb_bangking_jp.core.theme.appSpacing
import bsb.dev.bsb_bangking_jp.core.theme.extendedColors
import bsb.dev.bsb_bangking_jp.core.util.DateFormatterUtil
import bsb.dev.bsb_bangking_jp.feature.notification.domain.NotifItem
import bsb.dev.bsb_bangking_jp.shared.get_image.NetworkImage
import bsb.dev.bsb_bangking_jp.shared.get_image.domain.ImageCategory


@Composable
fun NotifDetailContent(
    notif: NotifItem,
    onClose: () -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(appSpacing.xs),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(appSpacing.xxxs),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            NetworkImage(
                path = notif.pathImage,
                category = ImageCategory.NEWS,
                contentDescription = notif.name,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .clip(RoundedCornerShape(12.dp)),
            )

            Text(
                text = notif.name,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.displaySmall,
            )

            Text(
                text = DateFormatterUtil.toFullDateTime(notif.date),
                style = MaterialTheme.typography.bodySmall,
                color = extendedColors.textSecondary,
            )

            Text(
                text = notif.description,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium,
                color = extendedColors.textSecondary,
            )
        }
        AppButton(
            text = "Baiklah",
            onClick = onClose,
        )
    }
}