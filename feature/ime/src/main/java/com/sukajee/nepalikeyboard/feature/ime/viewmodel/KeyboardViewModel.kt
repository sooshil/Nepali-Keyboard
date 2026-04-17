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

class KeyboardViewModel(
    private val transliterationEngine: TransliterationEngine,
    private val dictionaryRepository: DictionaryRepository,
    private val preferences: UserPreferencesDataStore,
) : ViewModel() {

    private val _state = MutableStateFlow(KeyboardState())
    val state: StateFlow<KeyboardState> = _state.asStateFlow()

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
            InputMode.SYMBOL -> emit(CommitEvent.CommitText(character))
        }
        if (current.shiftState == ShiftState.ON) {
            _state.update { it.copy(shiftState = ShiftState.OFF) }
        }
    }

    private fun handleDevanagariCharacter(character: String) {
        // Direct Devanagari — commit immediately, no buffering needed
        emit(CommitEvent.CommitText(character))
    }

    private fun handleRomanCharacter(char: String) {
        // Append to the raw roman buffer
        val newBuffer = _state.value.romanBuffer + char

        // Show the raw roman text inline as composing text (underlined in the field)
        // setComposingText replaces whatever composing text was there before
        emit(CommitEvent.SetComposingText(newBuffer))

        // Update state
        _state.update { it.copy(romanBuffer = newBuffer) }

        // Update suggestion bar with Devanagari candidates for this roman word
        updateSuggestions(newBuffer)
    }

    private fun handleBackspace() {
        val current = _state.value
        if (current.inputMode == InputMode.ROMAN && current.romanBuffer.isNotEmpty()) {
            // Remove last character from roman buffer
            val newBuffer = current.romanBuffer.dropLast(1)
            if (newBuffer.isEmpty()) {
                // Buffer is now empty — clear composing text and suggestions
                emit(CommitEvent.FinishComposing)
                clearSuggestions()
            } else {
                // Update composing text with shorter buffer
                emit(CommitEvent.SetComposingText(newBuffer))
                updateSuggestions(newBuffer)
            }
            _state.update { it.copy(romanBuffer = newBuffer) }
        } else {
            // No composing text — delete one char from the app normally
            emit(CommitEvent.DeleteBackward(1))
            clearSuggestions()
        }
    }

    private fun handleBackspaceLong() {
        cancelComposing()
        emit(CommitEvent.DeleteWordBackward)
    }

    private fun handleSpace() {
        if (_state.value.inputMode == InputMode.ROMAN && _state.value.romanBuffer.isNotEmpty()) {
            commitRomanBuffer(appendSpace = true)
        } else {
            emit(CommitEvent.CommitText(" "))
        }
        clearSuggestions()
    }

    private fun handleEnter() {
        if (_state.value.inputMode == InputMode.ROMAN && _state.value.romanBuffer.isNotEmpty()) {
            commitRomanBuffer(appendSpace = false)
        }
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
        cancelComposing()
        _state.update { state ->
            val newMode = when (state.inputMode) {
                InputMode.DEVANAGARI -> InputMode.ROMAN
                InputMode.ROMAN -> InputMode.DEVANAGARI
                InputMode.SYMBOL -> InputMode.DEVANAGARI
            }
            state.copy(inputMode = newMode, romanBuffer = "")
        }
        viewModelScope.launch {
            preferences.setInputMode(_state.value.inputMode.name)
        }
    }

    private fun handleSymbolToggle() {
        cancelComposing()
        _state.update { state ->
            val newMode = if (state.inputMode == InputMode.SYMBOL) InputMode.DEVANAGARI
            else InputMode.SYMBOL
            state.copy(romanBuffer = "")
        }
    }

    private fun handleSuggestionSelected(word: String) {
        // Replace the composing roman text with the selected Nepali word
        emit(CommitEvent.CommitText(word))
        emit(CommitEvent.CommitText(" "))
        _state.update { it.copy(romanBuffer = "") }
        clearSuggestions()
        viewModelScope.launch {
            dictionaryRepository.recordWordUsage(word)
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────

    /**
     * Transliterate the current roman buffer and commit it.
     * If no dictionary match exists the raw roman is committed as-is.
     * Called on space and enter.
     */
    private fun commitRomanBuffer(appendSpace: Boolean) {
        val buffer = _state.value.romanBuffer
        if (buffer.isEmpty()) return

        // Use top suggestion if available, otherwise force-transliterate,
        // and if that somehow fails, fall back to the raw roman string.
        val topSuggestion = _state.value.suggestions.firstOrNull()
        val textToCommit = topSuggestion
            ?: transliterationEngine.forceCommit(buffer).takeIf { it.isNotEmpty() }
            ?: buffer

        val finalText = if (appendSpace) "$textToCommit " else textToCommit
        emit(CommitEvent.CommitText(finalText))
        _state.update { it.copy(romanBuffer = "") }
    }

    /**
     * Abandon the current composing session without committing anything.
     * Used on mode toggle, symbol toggle, long backspace.
     */
    private fun cancelComposing() {
        if (_state.value.romanBuffer.isNotEmpty()) {
            emit(CommitEvent.FinishComposing)
            _state.update { it.copy(romanBuffer = "") }
            clearSuggestions()
        }
    }

    private fun emit(event: CommitEvent) {
        viewModelScope.launch { _commitEvents.emit(event) }
    }

    private fun updateSuggestions(romanBuffer: String) {
        if (!_state.value.showSuggestions) return
        suggestionJob?.cancel()
        suggestionJob = viewModelScope.launch {
            // Transliterate the buffer to get a Devanagari prefix for DB lookup
            val devanagariPrefix = transliterationEngine.forceCommit(romanBuffer)
            if (devanagariPrefix.isBlank()) {
                clearSuggestions()
                return@launch
            }
            dictionaryRepository.getSuggestions(devanagariPrefix).collectLatest { words ->
                _state.update { it.copy(suggestions = words) }
            }
        }
    }

    private fun clearSuggestions() {
        _state.update { it.copy(suggestions = emptyList()) }
    }
}

sealed interface CommitEvent {
    /** Replace composing text in the field with this string (shown underlined) */
    data class SetComposingText(val text: String) : CommitEvent
    /** Finalize composing — commit this text permanently */
    data class CommitText(val text: String) : CommitEvent
    /** Clear composing text without committing anything */
    data object FinishComposing : CommitEvent
    data class DeleteBackward(val count: Int) : CommitEvent
    data object DeleteWordBackward : CommitEvent
    data object PerformEditorAction : CommitEvent
}