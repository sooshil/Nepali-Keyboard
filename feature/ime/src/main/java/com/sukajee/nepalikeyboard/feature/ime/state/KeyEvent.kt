package com.sukajee.nepalikeyboard.feature.ime.state

/**
 * Sealed hierarchy of every event a key press can generate.
 * KeyButton composables emit these; KeyboardViewModel processes them.
 */
sealed interface KeyEvent {

    /** A character was tapped — could be Devanagari, Roman, or symbol. */
    data class CharacterKey(val character: String) : KeyEvent

    /** Backspace was tapped — delete one character / matra. */
    data object Backspace : KeyEvent

    /** Backspace was held — delete a word at a time. */
    data object BackspaceLong : KeyEvent

    /** Space was tapped. */
    data object Space : KeyEvent

    /** Enter / return was tapped. */
    data object Enter : KeyEvent

    /** Shift was tapped once. */
    data object Shift : KeyEvent

    /** Shift was double-tapped (caps lock). */
    data object ShiftDoubleTap : KeyEvent

    /** User toggled between Devanagari and Roman mode. */
    data object ModeToggle : KeyEvent

    /** User tapped the symbol toggle key. */
    data object SymbolToggle : KeyEvent

    /** User selected a suggestion from the suggestion bar. */
    data class SuggestionSelected(val word: String) : KeyEvent

    /** User long-pressed a key and selected a secondary character. */
    data class SecondaryCharacter(val character: String) : KeyEvent
}
