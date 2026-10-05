package bsb.dev.bsb_bangking_jp.core.skeleton

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import bsb.dev.bsb_bangking_jp.core.theme.extendedColors

enum class SkeletonTrailingType { NONE, COLUMN, BOX }

enum class SkeletonListLayout { AVATAR, FULL_ROW }

@Composable
fun SkeletonList(
    modifier: Modifier = Modifier,
    itemCount: Int = 8,
    avatarSize: Int = 48,
    itemverticalPadding16: Int = 14,
    showDateHeader: Boolean = true,
    trailingType: SkeletonTrailingType = SkeletonTrailingType.COLUMN,
    layout: SkeletonListLayout = SkeletonListLayout.AVATAR,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        if (showDateHeader) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                SkeletonBox(height = 18.dp, width = 110.dp)
            }
        }

        for (index in 0 until itemCount) {
            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                Column(modifier = Modifier.padding(vertical = itemverticalPadding16.dp)) {
                    when (layout) {
                        SkeletonListLayout.AVATAR -> AvatarItem(
                            avatarSize = avatarSize,
                            trailingType = trailingType,
                        )
                        SkeletonListLayout.FULL_ROW -> FullRowItem()
                    }
                }
                if (index != itemCount - 1) {
                    HorizontalDivider(thickness = 1.dp, color = MaterialTheme.extendedColors.strip)
                }
            }
        }
    }
}

@Composable
private fun AvatarItem(
    avatarSize: Int,
    trailingType: SkeletonTrailingType,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
    ) {
        SkeletonAvatar(size = avatarSize.dp)
        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            SkeletonBox(height = 16.dp, width = 120.dp)
            Spacer(modifier = Modifier.height(8.dp))
            SkeletonBox(height = 12.dp, width = 160.dp)
        }

        Trailing(trailingType)
    }
}

@Composable
private fun FullRowItem() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            SkeletonBox(height = 14.dp, width = 120.dp) // tanggal
            Spacer(modifier = Modifier.height(10.dp))
            SkeletonBox(height = 16.dp, width = 200.dp) // nama
            Spacer(modifier = Modifier.height(8.dp))
            SkeletonBox(height = 12.dp, width = 240.dp) // bank + rekening
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(horizontalAlignment = Alignment.End) {
            SkeletonBox(height = 16.dp, width = 90.dp) // nominal
            Spacer(modifier = Modifier.height(10.dp))
            SkeletonBox(height = 28.dp, width = 64.dp, borderRadius = 14.dp) // status badge
        }
    }
}

@Composable
private fun Trailing(type: SkeletonTrailingType) {
    when (type) {
        SkeletonTrailingType.NONE -> Unit
        SkeletonTrailingType.BOX -> SkeletonBox(height = 50.dp, width = 50.dp)
        SkeletonTrailingType.COLUMN -> Column(horizontalAlignment = Alignment.End) {
            SkeletonBox(height = 16.dp, width = 80.dp)
            Spacer(modifier = Modifier.height(8.dp))
            SkeletonBox(height = 12.dp, width = 50.dp)
        }
    }
}
