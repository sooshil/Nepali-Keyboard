package com.sukajee.nepalikeyboard.feature.ime.service

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.runtime.Recomposer
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.AndroidUiDispatcher
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.compositionContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.sukajee.nepalikeyboard.core.ui.theme.NepaliKeyboardTheme
import com.sukajee.nepalikeyboard.feature.ime.ui.KeyboardRoot
import com.sukajee.nepalikeyboard.feature.ime.viewmodel.KeyboardViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class ImeComposeWindow(
    private val context: Context,
    private val viewModel: KeyboardViewModel,
) : LifecycleOwner, ViewModelStoreOwner, SavedStateRegistryOwner {

    // ── Lifecycle plumbing ────────────────────────────────────────────────

    private val lifecycleRegistry = LifecycleRegistry(this)
    override val lifecycle: Lifecycle get() = lifecycleRegistry

    override val viewModelStore = ViewModelStore()

    private val savedStateRegistryController = SavedStateRegistryController.create(this)
    override val savedStateRegistry: SavedStateRegistry
        get() = savedStateRegistryController.savedStateRegistry

    private var recomposerScope: CoroutineScope? = null

    init {
        savedStateRegistryController.performRestore(null)
        lifecycleRegistry.currentState = Lifecycle.State.CREATED
    }

    // ── View creation ─────────────────────────────────────────────────────

    fun createView(): View {
        // KEY FIX: Use a FrameLayout as the root container.
        // We set all three owners on THIS root — not on the ComposeView.
        // When Compose walks up the tree from ComposeView, it hits this
        // FrameLayout first and finds the owners it needs.
        val rootView = FrameLayout(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
            )

            // Set owners on the ROOT, not on ComposeView
            setViewTreeLifecycleOwner(this@ImeComposeWindow)
            setViewTreeViewModelStoreOwner(this@ImeComposeWindow)
            setViewTreeSavedStateRegistryOwner(this@ImeComposeWindow)
        }

        // Set up the Recomposer manually so it uses our lifecycle scope
        val coroutineContext = AndroidUiDispatcher.CurrentThread
        val recomposerJob = SupervisorJob()
        val scope = CoroutineScope(coroutineContext + recomposerJob)
        recomposerScope = scope

        val recomposer = Recomposer(coroutineContext)
        scope.launch(coroutineContext) {
            recomposer.runRecomposeAndApplyChanges()
        }

        val composeView = ComposeView(context).apply {
            // Set the recomposer on the ComposeView so it uses ours
            compositionContext = recomposer

            setContent {
                val state by viewModel.state.collectAsStateWithLifecycle()
                NepaliKeyboardTheme {
                    KeyboardRoot(
                        state = state,
                        onKeyEvent = viewModel::onKeyEvent,
                    )
                }
            }
        }

        rootView.addView(composeView)

        // Move to RESUMED after view is fully set up
        lifecycleRegistry.currentState = Lifecycle.State.RESUMED

        return rootView
    }

    fun destroy() {
        lifecycleRegistry.currentState = Lifecycle.State.DESTROYED
        recomposerScope?.cancel()
        recomposerScope = null
    }
}