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
import com.sukajee.nepalikeyboard.feature.ime.state.ShiftState
import com.sukajee.nepalikeyboard.feature.ime.ui.components.KeyboardRow

/**
 * QWERTY layout for romanized Nepali input.
 * Keys are standard Latin letters — the transliteration engine
 * converts them to Devanagari in real time.
 *
 * Shift state is respected — uppercase letters trigger different
 * transliteration mappings (e.g. 'T' vs 't' → ट vs त).
 */
@Composable
fun RomanLayout(
    state: KeyboardState,
    onKeyEvent: (KeyEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val isUpperCase = state.shiftState != ShiftState.OFF

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 4.dp)
    ) {
        KeyboardRow(
            keys = buildRow(row1Lower, row1Upper, isUpperCase),
            onKeyEvent = onKeyEvent,
        )
        KeyboardRow(
            keys = buildRow(row2Lower, row2Upper, isUpperCase),
            onKeyEvent = onKeyEvent,
        )
        KeyboardRow(
            keys = buildShiftRow(row3Lower, row3Upper, isUpperCase, onKeyEvent),
            onKeyEvent = onKeyEvent,
        )
        KeyboardRow(
            keys = bottomRow,
            onKeyEvent = onKeyEvent,
        )
    }
}

// ── Row data ──────────────────────────────────────────────────────────────

// Row 1 — QWERTYUIOP
private val row1Lower = listOf("q", "w", "e", "r", "t", "y", "u", "i", "o", "p")
private val row1Upper = listOf("Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P")

// Row 2 — ASDFGHJKL
private val row2Lower = listOf("a", "s", "d", "f", "g", "h", "j", "k", "l")
private val row2Upper = listOf("A", "S", "D", "F", "G", "H", "J", "K", "L")

// Row 3 — ZXCVBNM (with shift key on sides)
private val row3Lower = listOf("z", "x", "c", "v", "b", "n", "m")
private val row3Upper = listOf("Z", "X", "C", "V", "B", "N", "M")

private val bottomRow = listOf(
    KeyDefinition("नेपा\nEN", type = KeyType.MODE_TOGGLE, widthWeight = 1.5f),
    KeyDefinition("!#1", type = KeyType.SYMBOL_TOGGLE, widthWeight = 1.2f),
    KeyDefinition("Space", type = KeyType.SPACE, widthWeight = 4f),
    KeyDefinition("⌫", type = KeyType.BACKSPACE, widthWeight = 1.5f),
    KeyDefinition("↵", type = KeyType.ENTER, widthWeight = 1.5f),
)

// ── Helpers ───────────────────────────────────────────────────────────────

private fun buildRow(
    lower: List<String>,
    upper: List<String>,
    isUpperCase: Boolean,
): List<KeyDefinition> {
    return lower.zip(upper).map { (lo, up) ->
        val primary = if (isUpperCase) up else lo
        val secondary = if (isUpperCase) lo else up
        KeyDefinition(primary = primary, secondary = secondary)
    }
}

/**
 * Row 3 includes a Shift key on the left and Backspace on the right,
 * matching the standard QWERTY layout.
 */
private fun buildShiftRow(
    lower: List<String>,
    upper: List<String>,
    isUpperCase: Boolean,
    onKeyEvent: (KeyEvent) -> Unit,
): List<KeyDefinition> {
    val shiftLabel = when {
        isUpperCase -> "⇧" // could be filled arrow for caps
        else -> "⇧"
    }
    val letterKeys = buildRow(lower, upper, isUpperCase)
    return listOf(
        KeyDefinition(shiftLabel, type = KeyType.SHIFT, widthWeight = 1.4f)
    ) + letterKeys + listOf(
        KeyDefinition("⌫", type = KeyType.BACKSPACE, widthWeight = 1.4f)
    )
}
