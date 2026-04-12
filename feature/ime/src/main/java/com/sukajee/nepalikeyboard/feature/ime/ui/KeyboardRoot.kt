package com.sukajee.nepalikeyboard.feature.ime.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.sukajee.nepalikeyboard.core.ui.theme.NepaliKeyboardThemeTokens
import com.sukajee.nepalikeyboard.feature.ime.state.InputMode
import com.sukajee.nepalikeyboard.feature.ime.state.KeyEvent
import com.sukajee.nepalikeyboard.feature.ime.state.KeyboardState
import com.sukajee.nepalikeyboard.feature.ime.ui.components.SuggestionBar
import com.sukajee.nepalikeyboard.feature.ime.ui.layout.DevanagariLayout
import com.sukajee.nepalikeyboard.feature.ime.ui.layout.RomanLayout
import com.sukajee.nepalikeyboard.feature.ime.ui.layout.SymbolLayout

/**
 * Root composable for the keyboard.
 * Selects the correct layout based on [KeyboardState.inputMode]
 * and stacks the suggestion bar on top.
 */
@Composable
fun KeyboardRoot(
    state: KeyboardState,
    onKeyEvent: (KeyEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = NepaliKeyboardThemeTokens.keyboardColors

    Column(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(colors.keyboardBackground)
    ) {
        // Suggestion bar — always visible when showSuggestions is true
        if (state.showSuggestions) {
            SuggestionBar(
                suggestions = state.suggestions,
                onSuggestionClick = { word -> onKeyEvent(KeyEvent.SuggestionSelected(word)) },
            )
        }

        // Key layout — switches based on mode
        when (state.inputMode) {
            InputMode.DEVANAGARI -> DevanagariLayout(
                state = state,
                onKeyEvent = onKeyEvent,
            )
            InputMode.ROMAN -> RomanLayout(
                state = state,
                onKeyEvent = onKeyEvent,
            )
            InputMode.SYMBOL -> SymbolLayout(
                state = state,
                onKeyEvent = onKeyEvent,
            )
        }
    }
}