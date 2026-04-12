package com.sukajee.nepalikeyboard.feature.transliteration.di

import com.sukajee.nepalikeyboard.feature.transliteration.engine.TransliterationEngine
import org.koin.dsl.module

val transliterationModule = module {
    // Stateless engine — safe as a singleton
    single { TransliterationEngine() }
}