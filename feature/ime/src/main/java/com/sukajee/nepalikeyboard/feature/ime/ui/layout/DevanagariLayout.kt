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
 * Direct Devanagari keyboard layout.
 *
 * Layout design decisions:
 * - Row 1: Vowels (स्वर) — अ आ इ ई उ ऊ ए ऐ ओ औ
 * - Row 2: Common consonants — क ख ग घ ङ च छ ज झ ञ
 * - Row 3: More consonants  — ट ठ ड ढ ण त थ द ध न
 * - Row 4: More consonants  — प फ ब भ म य र ल व श
 * - Row 5: Remaining + matras — ष स ह क्ष त्र ज्ञ
 * - Row 6: Matras + special   — ा ि ी ु ू े ै ो ौ ं ः ्
 * - Row 7: Action row         — mode toggle, space, backspace, enter
 *
 * Secondary characters (long-press) add numerals and less-common variants.
 */
@Composable
fun DevanagariLayout(
    state: KeyboardState,
    onKeyEvent: (KeyEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 4.dp)
    ) {
        // Row 1 — Vowels
        KeyboardRow(
            keys = vowelRow,
            onKeyEvent = onKeyEvent,
        )

        // Row 2 — Consonants ka-kha group
        KeyboardRow(
            keys = consonantRow1,
            onKeyEvent = onKeyEvent,
        )

        // Row 3 — Consonants ta-tha group (retroflex + dental)
        KeyboardRow(
            keys = consonantRow2,
            onKeyEvent = onKeyEvent,
        )

        // Row 4 — Consonants pa-pha group
        KeyboardRow(
            keys = consonantRow3,
            onKeyEvent = onKeyEvent,
        )

        // Row 5 — Remaining consonants + conjuncts
        KeyboardRow(
            keys = conjunctRow,
            onKeyEvent = onKeyEvent,
        )

        // Row 6 — Matras (vowel signs) + anusvara + visarga + halanta
        KeyboardRow(
            keys = matraRow,
            onKeyEvent = onKeyEvent,
        )

        // Row 7 — Action row
        KeyboardRow(
            keys = actionRow,
            onKeyEvent = onKeyEvent,
        )
    }
}

// ── Key definitions ───────────────────────────────────────────────────────

private val vowelRow = listOf(
    KeyDefinition("अ", "१"),
    KeyDefinition("आ", "२"),
    KeyDefinition("इ", "३"),
    KeyDefinition("ई", "४"),
    KeyDefinition("उ", "५"),
    KeyDefinition("ऊ", "६"),
    KeyDefinition("ए", "७"),
    KeyDefinition("ऐ", "८"),
    KeyDefinition("ओ", "९"),
    KeyDefinition("औ", "०"),
)

private val consonantRow1 = listOf(
    KeyDefinition("क", "क्"),
    KeyDefinition("ख", "ख्"),
    KeyDefinition("ग", "ग्"),
    KeyDefinition("घ", "घ्"),
    KeyDefinition("ङ"),
    KeyDefinition("च", "च्"),
    KeyDefinition("छ"),
    KeyDefinition("ज", "ज्"),
    KeyDefinition("झ"),
    KeyDefinition("ञ"),
)

private val consonantRow2 = listOf(
    KeyDefinition("ट"),
    KeyDefinition("ठ"),
    KeyDefinition("ड", "ड़"),
    KeyDefinition("ढ", "ढ़"),
    KeyDefinition("ण"),
    KeyDefinition("त", "त्"),
    KeyDefinition("थ"),
    KeyDefinition("द", "द्"),
    KeyDefinition("ध"),
    KeyDefinition("न", "न्"),
)

private val consonantRow3 = listOf(
    KeyDefinition("प"),
    KeyDefinition("फ", "फ्"),
    KeyDefinition("ब", "ब्"),
    KeyDefinition("भ"),
    KeyDefinition("म", "म्"),
    KeyDefinition("य"),
    KeyDefinition("र", "र्"),
    KeyDefinition("ल", "ल्"),
    KeyDefinition("व"),
    KeyDefinition("श"),
)

private val conjunctRow = listOf(
    KeyDefinition("ष"),
    KeyDefinition("स", "स्"),
    KeyDefinition("ह", "ह्"),
    KeyDefinition("क्ष"),
    KeyDefinition("त्र"),
    KeyDefinition("ज्ञ"),
    KeyDefinition("श्र"),
    KeyDefinition("।", "॥"),   // Danda (Nepali full stop) — long press for double danda
    KeyDefinition("ऋ"),
    KeyDefinition("ँ"),        // Chandrabindu
)

private val matraRow = listOf(
    KeyDefinition("ा"),   // aa matra
    KeyDefinition("ि"),   // i matra
    KeyDefinition("ी"),   // ii matra
    KeyDefinition("ु"),   // u matra
    KeyDefinition("ू"),   // uu matra
    KeyDefinition("े"),   // e matra
    KeyDefinition("ै"),   // ai matra
    KeyDefinition("ो"),   // o matra
    KeyDefinition("ौ"),   // au matra
    KeyDefinition("ं"),   // Anusvara
    KeyDefinition("ः"),   // Visarga
    KeyDefinition("्"),   // Halanta (virama)
)

private val actionRow = listOf(
    KeyDefinition("नेपा\nEN", type = KeyType.MODE_TOGGLE, widthWeight = 1.5f),
    KeyDefinition("!#1", type = KeyType.SYMBOL_TOGGLE, widthWeight = 1.2f),
    KeyDefinition("Space", type = KeyType.SPACE, widthWeight = 4f),
    KeyDefinition("⌫", type = KeyType.BACKSPACE, widthWeight = 1.5f),
    KeyDefinition("↵", type = KeyType.ENTER, widthWeight = 1.5f),
)