package com.sukajee.nepalikeyboard.feature.ime.ui.layout

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sukajee.nepalikeyboard.feature.ime.state.KeyDefinition
import com.sukajee.nepalikeyboard.feature.ime.state.KeyEvent
import com.sukajee.nepalikeyboard.feature.ime.state.KeyType
import com.sukajee.nepalikeyboard.feature.ime.state.KeyboardState
import com.sukajee.nepalikeyboard.feature.ime.ui.components.KeyboardRow

/**
 * Symbol layout — numbers, punctuation, and Nepali-specific special characters.
 *
 * Row 1: Devanagari numerals (primary) / Arabic numerals (long-press)
 * Row 2: Common punctuation
 * Row 3: Math / currency / brackets
 * Row 4: Action row
 */
@Composable
fun SymbolLayout(
    state: KeyboardState,
    onKeyEvent: (KeyEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 4.dp)
    ) {
        KeyboardRow(keys = numeralRow, onKeyEvent = onKeyEvent)
        KeyboardRow(keys = punctuationRow1, onKeyEvent = onKeyEvent)
        KeyboardRow(keys = punctuationRow2, onKeyEvent = onKeyEvent)
        KeyboardRow(keys = symbolActionRow, onKeyEvent = onKeyEvent)
    }
}

private val numeralRow = listOf(
    KeyDefinition("१", "1"),
    KeyDefinition("२", "2"),
    KeyDefinition("३", "3"),
    KeyDefinition("४", "4"),
    KeyDefinition("५", "5"),
    KeyDefinition("६", "6"),
    KeyDefinition("७", "7"),
    KeyDefinition("८", "8"),
    KeyDefinition("९", "9"),
    KeyDefinition("०", "0"),
)

private val punctuationRow1 = listOf(
    KeyDefinition("।", type = KeyType.PUNCTUATION),   // Danda
    KeyDefinition("॥", type = KeyType.PUNCTUATION),   // Double danda
    KeyDefinition("?", type = KeyType.PUNCTUATION),
    KeyDefinition("!", type = KeyType.PUNCTUATION),
    KeyDefinition(".", type = KeyType.PUNCTUATION),
    KeyDefinition(",", type = KeyType.PUNCTUATION),
    KeyDefinition("'", type = KeyType.PUNCTUATION),
    KeyDefinition("\"", type = KeyType.PUNCTUATION),
    KeyDefinition("-", type = KeyType.PUNCTUATION),
    KeyDefinition("_", type = KeyType.PUNCTUATION),
)

private val punctuationRow2 = listOf(
    KeyDefinition("@"),
    KeyDefinition("#"),
    KeyDefinition("रु", type = KeyType.CHARACTER),     // Nepali Rupee sign
    KeyDefinition("%"),
    KeyDefinition("&"),
    KeyDefinition("("),
    KeyDefinition(")"),
    KeyDefinition("/"),
    KeyDefinition(":"),
    KeyDefinition(";"),
)

private val symbolActionRow = listOf(
    KeyDefinition("नेपा\nEN", type = KeyType.MODE_TOGGLE, widthWeight = 1.5f),
    KeyDefinition("ABC", type = KeyType.SYMBOL_TOGGLE, widthWeight = 1.2f),
    KeyDefinition("Space", type = KeyType.SPACE, widthWeight = 4f),
    KeyDefinition("⌫", type = KeyType.BACKSPACE, widthWeight = 1.5f),
    KeyDefinition("↵", type = KeyType.ENTER, widthWeight = 1.5f),
)
