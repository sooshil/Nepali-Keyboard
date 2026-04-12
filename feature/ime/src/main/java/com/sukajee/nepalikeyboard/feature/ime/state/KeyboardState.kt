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
     * The current buffer of typed characters waiting to be committed.
     * In ROMAN mode this is the raw roman string (e.g. "ramr") being
     * transliterated. In DEVANAGARI mode it's the pending Devanagari
     * string before a space/commit action.
     */
    val pendingBuffer: String = "",

    /**
     * The transliterated preview shown above the keyboard or inline.
     * Only relevant in ROMAN mode.
     */
    val transliterationPreview: String = "",

    /** Word suggestions from the dictionary shown in the suggestion bar. */
    val suggestions: List<String> = emptyList(),

    /** Whether the suggestion bar is visible. */
    val showSuggestions: Boolean = true,

    /** Whether the long-press popup is showing, and for which key. */
    val longPressKey: KeyDefinition? = null,

    /** True while the keyboard is initialising (loading dictionary etc.) */
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
