package com.sukajee.nepalikeyboard.feature.ime.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.sukajee.nepalikeyboard.core.ui.theme.NepaliKeyboardThemeTokens

@Composable
fun SuggestionBar(
    suggestions: List<String>,
    onSuggestionClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = NepaliKeyboardThemeTokens.keyboardColors

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(40.dp)
            .background(colors.suggestionBarBackground)
    ) {
        if (suggestions.isEmpty()) {
            // Empty state — bar is still present but blank
            return@Box
        }

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            items(suggestions) { word ->
                SuggestionChip(
                    word = word,
                    onClick = { onSuggestionClick(word) },
                )
                // Divider between chips
                Divider(
                    modifier = Modifier
                        .height(20.dp)
                        .width(1.dp),
                    color = colors.keyText.copy(alpha = 0.15f),
                )
            }
        }
    }
}

@Composable
private fun SuggestionChip(
    word: String,
    onClick: () -> Unit,
) {
    val colors = NepaliKeyboardThemeTokens.keyboardColors

    Text(
        text = word,
        style = MaterialTheme.typography.bodyMedium,
        color = colors.suggestionText,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp),
    )
}