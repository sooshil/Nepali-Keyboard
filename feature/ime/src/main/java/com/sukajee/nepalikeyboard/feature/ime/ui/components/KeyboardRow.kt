package com.sukajee.nepalikeyboard.feature.ime.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.sukajee.nepalikeyboard.feature.ime.state.KeyDefinition
import com.sukajee.nepalikeyboard.feature.ime.state.KeyEvent
import com.sukajee.nepalikeyboard.feature.ime.state.KeyType

@Composable
fun KeyboardRow(
    keys: List<KeyDefinition>,
    onKeyEvent: (KeyEvent) -> Unit,
    modifier: Modifier = Modifier,
    rowHeight: Dp = 52.dp,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(rowHeight),
    ) {
        keys.forEach { key ->
            KeyButton(
                key = key,
                onClick = { onKeyEvent(resolveKeyEvent(key)) },
                onLongClick = {
                    if (key.secondary.isNotEmpty()) {
                        onKeyEvent(KeyEvent.SecondaryCharacter(key.secondary))
                    }
                },
                modifier = Modifier.weight(key.widthWeight),
            )
        }
    }
}

private fun resolveKeyEvent(key: KeyDefinition): KeyEvent {
    return when (key.type) {
        KeyType.BACKSPACE -> KeyEvent.Backspace
        KeyType.ENTER -> KeyEvent.Enter
        KeyType.SPACE -> KeyEvent.Space
        KeyType.SHIFT -> KeyEvent.Shift
        KeyType.SYMBOL_TOGGLE -> KeyEvent.SymbolToggle
        KeyType.MODE_TOGGLE -> KeyEvent.ModeToggle
        KeyType.CHARACTER,
        KeyType.PUNCTUATION -> KeyEvent.CharacterKey(key.primary)
    }
}