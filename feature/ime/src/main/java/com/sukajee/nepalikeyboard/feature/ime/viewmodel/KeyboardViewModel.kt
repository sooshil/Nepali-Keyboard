package com.sukajee.nepalikeyboard.feature.ime.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sukajee.nepalikeyboard.core.data.datastore.UserPreferencesDataStore
import com.sukajee.nepalikeyboard.feature.dictionary.repository.DictionaryRepository
import com.sukajee.nepalikeyboard.feature.ime.state.InputMode
import com.sukajee.nepalikeyboard.feature.ime.state.KeyEvent
import com.sukajee.nepalikeyboard.feature.ime.state.KeyboardState
import com.sukajee.nepalikeyboard.feature.ime.state.ShiftState
import com.sukajee.nepalikeyboard.feature.transliteration.engine.TransliterationEngine
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * The brain of the keyboard.
 *
 * Responsibilities:
 * - Maintain [KeyboardState] as a single source of truth
 * - Process [KeyEvent]s from the UI
 * - Coordinate with [TransliterationEngine] for Roman mode
 * - Coordinate with [DictionaryRepository] for suggestions
 * - Emit [CommitEvent]s that the IME Service observes and sends to the app
 */
class KeyboardViewModel(
    private val transliterationEngine: TransliterationEngine,
    private val dictionaryRepository: DictionaryRepository,
    private val preferences: UserPreferencesDataStore,
) : ViewModel() {

    private val _state = MutableStateFlow(KeyboardState())
    val state: StateFlow<KeyboardState> = _state.asStateFlow()

    /**
     * Events emitted to the IME service to commit text / perform editor actions.
     * The service collects this flow and calls InputConnection methods accordingly.
     */
    private val _commitEvents = MutableSharedFlow<CommitEvent>()
    val commitEvents: SharedFlow<CommitEvent> = _commitEvents.asSharedFlow()

    private var suggestionJob: Job? = null

    init {
        observePreferences()
    }

    private fun observePreferences() {
        viewModelScope.launch {
            preferences.showSuggestions.collectLatest { show ->
                _state.update { it.copy(showSuggestions = show) }
            }
        }
    }

    // ── Public entry point ────────────────────────────────────────────────

    fun onKeyEvent(event: KeyEvent) {
        when (event) {
            is KeyEvent.CharacterKey -> handleCharacter(event.character)
            is KeyEvent.Backspace -> handleBackspace()
            is KeyEvent.BackspaceLong -> handleBackspaceLong()
            is KeyEvent.Space -> handleSpace()
            is KeyEvent.Enter -> handleEnter()
            is KeyEvent.Shift -> handleShift()
            is KeyEvent.ShiftDoubleTap -> handleShiftDoubleTap()
            is KeyEvent.ModeToggle -> handleModeToggle()
            is KeyEvent.SymbolToggle -> handleSymbolToggle()
            is KeyEvent.SuggestionSelected -> handleSuggestionSelected(event.word)
            is KeyEvent.SecondaryCharacter -> handleCharacter(event.character)
        }
    }

    // ── Key handlers ──────────────────────────────────────────────────────

    private fun handleCharacter(character: String) {
        val current = _state.value
        when (current.inputMode) {
            InputMode.ROMAN -> handleRomanCharacter(character)
            InputMode.DEVANAGARI -> handleDevanagariCharacter(character)
            InputMode.SYMBOL -> commitText(character)
        }
        // After a character key, if shift was ON (not LOCKED), revert it
        if (current.shiftState == ShiftState.ON) {
            _state.update { it.copy(shiftState = ShiftState.OFF) }
        }
    }

    private fun handleDevanagariCharacter(character: String) {
        // In direct Devanagari mode, commit each character immediately.
        // Matra logic: if character is a vowel sign (matra), it should
        // attach to the preceding consonant — the Android text engine
        // handles visual rendering, we just commit the Unicode code point.
        commitText(character)
        updateSuggestions()
    }

    private fun handleRomanCharacter(char: String) {
        val currentBuffer = _state.value.pendingBuffer + char
        val result = transliterationEngine.transliterate(currentBuffer)

        _state.update { state ->
            state.copy(
                pendingBuffer = result.remainingBuffer,
                transliterationPreview = result.devanagari,
            )
        }

        if (result.committed.isNotEmpty()) {
            commitText(result.committed)
        }

        updateSuggestions()
    }

    private fun handleBackspace() {
        val current = _state.value
        when {
            // If there's a pending Roman buffer, pop last char from it
            current.inputMode == InputMode.ROMAN && current.pendingBuffer.isNotEmpty() -> {
                val newBuffer = current.pendingBuffer.dropLast(1)
                val preview = if (newBuffer.isEmpty()) ""
                else transliterationEngine.transliterate(newBuffer).devanagari

                _state.update { it.copy(pendingBuffer = newBuffer, transliterationPreview = preview) }
            }
            // Otherwise delete one character in the connected app
            else -> {
                emit(CommitEvent.DeleteBackward(1))
                updateSuggestions()
            }
        }
    }

    private fun handleBackspaceLong() {
        // Commit any pending buffer first, then delete a word
        flushBuffer()
        emit(CommitEvent.DeleteWordBackward)
    }

    private fun handleSpace() {
        flushBuffer()
        commitText(" ")
        clearSuggestions()
    }

    private fun handleEnter() {
        flushBuffer()
        emit(CommitEvent.PerformEditorAction)
    }

    private fun handleShift() {
        _state.update { state ->
            val newShift = when (state.shiftState) {
                ShiftState.OFF -> ShiftState.ON
                ShiftState.ON -> ShiftState.OFF
                ShiftState.LOCKED -> ShiftState.OFF
            }
            state.copy(shiftState = newShift)
        }
    }

    private fun handleShiftDoubleTap() {
        _state.update { it.copy(shiftState = ShiftState.LOCKED) }
    }

    private fun handleModeToggle() {
        flushBuffer()
        _state.update { state ->
            val newMode = when (state.inputMode) {
                InputMode.DEVANAGARI -> InputMode.ROMAN
                InputMode.ROMAN -> InputMode.DEVANAGARI
                InputMode.SYMBOL -> InputMode.DEVANAGARI
            }
            state.copy(inputMode = newMode, pendingBuffer = "", transliterationPreview = "")
        }
        viewModelScope.launch {
            preferences.setInputMode(_state.value.inputMode.name)
        }
    }

    private fun handleSymbolToggle() {
        flushBuffer()
        _state.update { state ->
            val newMode = if (state.inputMode == InputMode.SYMBOL) InputMode.DEVANAGARI
            else InputMode.SYMBOL
            state.copy(inputMode = newMode)
        }
    }

    private fun handleSuggestionSelected(word: String) {
        flushBuffer()
        commitText(word)
        commitText(" ")
        clearSuggestions()
        viewModelScope.launch {
            dictionaryRepository.recordWordUsage(word)
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────

    /**
     * Commit any pending Roman buffer as Devanagari and clear it.
     * Called before space, enter, suggestion select, mode toggle.
     */
    private fun flushBuffer() {
        val current = _state.value
        if (current.inputMode == InputMode.ROMAN && current.pendingBuffer.isNotEmpty()) {
            val result = transliterationEngine.forceCommit(current.pendingBuffer)
            commitText(result)
            _state.update { it.copy(pendingBuffer = "", transliterationPreview = "") }
        }
    }

    private fun commitText(text: String) {
        emit(CommitEvent.CommitText(text))
    }

    private fun emit(event: CommitEvent) {
        viewModelScope.launch { _commitEvents.emit(event) }
    }

    private fun updateSuggestions() {
        if (!_state.value.showSuggestions) return
        suggestionJob?.cancel()
        suggestionJob = viewModelScope.launch {
            val query = when (_state.value.inputMode) {
                InputMode.ROMAN -> _state.value.transliterationPreview
                InputMode.DEVANAGARI -> "" // TODO: get text before cursor from InputConnection
                InputMode.SYMBOL -> ""
            }
            if (query.isBlank()) {
                clearSuggestions()
                return@launch
            }
            dictionaryRepository.getSuggestions(query).collectLatest { words ->
                _state.update { it.copy(suggestions = words) }
            }
        }
    }

    private fun clearSuggestions() {
        _state.update { it.copy(suggestions = emptyList()) }
    }
}

/**
 * Events emitted from ViewModel → IME Service → InputConnection.
 */
sealed interface CommitEvent {
    data class CommitText(val text: String) : CommitEvent
    data class DeleteBackward(val count: Int) : CommitEvent
    data object DeleteWordBackward : CommitEvent
    data object PerformEditorAction : CommitEvent
}
