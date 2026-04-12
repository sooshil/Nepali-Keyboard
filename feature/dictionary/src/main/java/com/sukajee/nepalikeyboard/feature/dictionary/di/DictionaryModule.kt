package com.sukajee.nepalikeyboard.feature.dictionary.di

import com.sukajee.nepalikeyboard.feature.dictionary.repository.DictionaryRepository
import com.sukajee.nepalikeyboard.feature.dictionary.repository.DictionaryRepositoryImpl

import org.koin.dsl.module

val dictionaryModule = module {
    single<DictionaryRepository> {
        DictionaryRepositoryImpl(wordDao = get())
    }
}
