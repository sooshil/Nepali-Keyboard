package com.sukajee.nepalikeyboard.feature.transliteration.model

/**
 * Result of running the transliteration engine on a roman buffer.
 *
 * [committed]       — Devanagari text that is ready to be sent to the app.
 *                     This is text that has been fully resolved (no ambiguity).
 * [devanagari]      — Full Devanagari representation of the current buffer
 *                     (committed + preview of remainingBuffer). Shown in suggestion bar.
 * [remainingBuffer] — The portion of the roman input not yet committed
 *                     (still being typed, may resolve differently with more input).
 *
 * Example:
 *   Input buffer: "kaamaa"
 *   committed:       "का"   (k+aa resolved)
 *   devanagari:      "कामा"
 *   remainingBuffer: "maa"  (waiting to see if more follows)
 */
data class TransliterationResult(
    val committed: String,
    val devanagari: String,
    val remainingBuffer: String,
)