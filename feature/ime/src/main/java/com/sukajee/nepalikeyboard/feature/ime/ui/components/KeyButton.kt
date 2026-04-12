package com.sukajee.nepalikeyboard.feature.ime.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sukajee.nepalikeyboard.core.ui.theme.NepaliKeyboardThemeTokens
import com.sukajee.nepalikeyboard.feature.ime.state.KeyDefinition
import com.sukajee.nepalikeyboard.feature.ime.state.KeyType

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun KeyButton(
    key: KeyDefinition,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    val colors = NepaliKeyboardThemeTokens.keyboardColors

    val backgroundColor = when (key.type) {
        KeyType.CHARACTER, KeyType.PUNCTUATION -> colors.keyBackground
        KeyType.SPACE -> colors.spaceKeyBackground
        KeyType.ENTER -> colors.enterKeyBackground
        KeyType.SHIFT, KeyType.BACKSPACE,
        KeyType.SYMBOL_TOGGLE, KeyType.MODE_TOGGLE -> colors.keyBackgroundSpecial
    }

    val textColor = when (key.type) {
        KeyType.ENTER -> colors.enterKeyText
        else -> colors.keyText
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxHeight()
            .padding(horizontal = 3.dp, vertical = 4.dp)
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(6.dp),
                ambientColor = colors.keyShadow,
                spotColor = colors.keyShadow,
            )
            .clip(RoundedCornerShape(6.dp))
            .background(backgroundColor)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick,
            )
    ) {
        // Secondary character label (top-right, smaller)
        if (key.secondary.isNotEmpty()) {
            Text(
                text = key.secondary,
                style = MaterialTheme.typography.labelSmall,
                color = textColor.copy(alpha = 0.5f),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 3.dp, end = 4.dp),
            )
        }

        // Primary label
        Text(
            text = key.primary,
            fontSize = when (key.type) {
                KeyType.CHARACTER -> 20.sp
                KeyType.SPACE, KeyType.MODE_TOGGLE -> 11.sp
                else -> 16.sp
            },
            color = textColor,
            textAlign = TextAlign.Center,
        )
    }
}
