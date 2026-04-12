package com.sukajee.nepalikeyboard.feature.ime.di

import com.sukajee.nepalikeyboard.feature.ime.viewmodel.KeyboardViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module


val imeModule = module {
    viewModel {
        KeyboardViewModel(
            transliterationEngine = get(),
            dictionaryRepository = get(),
            preferences = get(),
        )
    }
}