package com.sukajee.nepalikeyboard.feature.ime.state

/**
 * Which input mode the keyboard is currently in.
 */
enum class InputMode {
    /** User taps Devanagari characters directly */
    DEVANAGARI,

    /** User types roman letters, engine converts to Devanagari */
    ROMAN,

    /** Number and symbol layout */
    SYMBOL,
}

/**
 * Shift / caps state for the Roman layout (mirrors a standard keyboard).
 */
enum class ShiftState {
    /** Lowercase */
    OFF,

    /** Next character uppercase, then revert to OFF */
    ON,

    /** All caps until toggled off — activated by double-tap shift */
    LOCKED,
}

/**
 * Complete snapshot of keyboard UI state.
 * The ViewModel holds and updates this; Compose reads it reactively.
 */
data class KeyboardState(
    val inputMode: InputMode = InputMode.DEVANAGARI,
    val shiftState: ShiftState = ShiftState.OFF,

    /**
     * In ROMAN mode: the raw roman characters typed so far (e.g. "mero").
     * This is shown inline in the text field as composing text.
     * Cleared on space/enter/suggestion.
     * Empty in DEVANAGARI and SYMBOL modes.
     */
    val romanBuffer: String = "",

    val suggestions: List<String> = emptyList(),
    val showSuggestions: Boolean = true,
    val longPressKey: KeyDefinition? = null,
    val isLoading: Boolean = false,
)

/**
 * Represents a single key on the keyboard.
 *
 * [primary]    — The main character/label shown on the key.
 * [secondary]  — The character shown in the top-right corner (long-press).
 * [type]       — Controls visual style and behaviour.
 * [widthWeight] — Relative width in a row (1f = standard, 1.5f = wider, etc.)
 */
data class KeyDefinition(
    val primary: String,
    val secondary: String = "",
    val type: KeyType = KeyType.CHARACTER,
    val widthWeight: Float = 1f,
)

enum class KeyType {
    /** A standard character key (Devanagari letter or roman letter) */
    CHARACTER,

    /** Shift / caps lock */
    SHIFT,

    /** Backspace / delete */
    BACKSPACE,

    /** Space bar */
    SPACE,

    /** Enter / return */
    ENTER,

    /** Switches to symbol layout */
    SYMBOL_TOGGLE,

    /** Switches between Devanagari and Roman mode */
    MODE_TOGGLE,

    /** Punctuation — treated as character but styled differently */
    PUNCTUATION,
}
