package com.sukajee.nepalikeyboard.feature.ime.service

import android.inputmethodservice.InputMethodService
import android.view.View
import android.view.inputmethod.EditorInfo
import com.sukajee.nepalikeyboard.feature.ime.viewmodel.CommitEvent
import com.sukajee.nepalikeyboard.feature.ime.viewmodel.KeyboardViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

/**
 * Entry point for the Input Method.
 *
 * Responsibilities:
 *  1. Create the keyboard View (via [ImeComposeWindow] which hosts ComposeView)
 *  2. Observe [KeyboardViewModel.commitEvents] and forward them to [currentInputConnection]
 *
 * We do NOT use viewModelScope here because InputMethodService is not a
 * ViewModel owner. Instead we manage a manual [serviceScope] tied to the
 * service lifecycle.
 */
class NepaliInputMethodService : InputMethodService() {

    private val viewModel: KeyboardViewModel by inject()
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var composeWindow: ImeComposeWindow? = null

    // ── Lifecycle ─────────────────────────────────────────────────────────

    override fun onCreate() {
        super.onCreate()
        observeCommitEvents()
    }

    override fun onCreateInputView(): View {
        return ImeComposeWindow(this, viewModel).also {
            composeWindow = it
        }.createView()
    }

    override fun onStartInputView(info: EditorInfo?, restarting: Boolean) {
        super.onStartInputView(info, restarting)
        // Future: adjust layout based on EditorInfo (e.g. number-only fields)
    }

    override fun onDestroy() {
        serviceScope.cancel()
        composeWindow = null
        super.onDestroy()
    }

    // ── Commit event handling ─────────────────────────────────────────────

    private fun observeCommitEvents() {
        serviceScope.launch {
            viewModel.commitEvents.collect { event ->
                handleCommitEvent(event)
            }
        }
    }

    private fun handleCommitEvent(event: CommitEvent) {
        val ic = currentInputConnection ?: return
        when (event) {
            is CommitEvent.CommitText -> {
                ic.commitText(event.text, 1)
            }
            is CommitEvent.DeleteBackward -> {
                ic.deleteSurroundingText(event.count, 0)
            }
            is CommitEvent.DeleteWordBackward -> {
                // Get text before cursor and delete back to previous space
                val before = ic.getTextBeforeCursor(50, 0)?.toString() ?: return
                val trimmed = before.trimEnd()
                val lastSpace = trimmed.lastIndexOf(' ')
                val deleteCount = if (lastSpace == -1) trimmed.length
                else trimmed.length - lastSpace
                ic.deleteSurroundingText(deleteCount, 0)
            }
            is CommitEvent.PerformEditorAction -> {
                val editorInfo = currentInputEditorInfo ?: return
                ic.performEditorAction(editorInfo.imeOptions)
            }
        }
    }
}
