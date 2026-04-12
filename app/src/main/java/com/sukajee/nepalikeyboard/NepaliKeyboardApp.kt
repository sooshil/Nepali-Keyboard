package com.sukajee.nepalikeyboard

import android.app.Application
import com.sukajee.nepalikeyboard.core.data.di.coreDataModule
import com.sukajee.nepalikeyboard.feature.dictionary.di.dictionaryModule
import com.sukajee.nepalikeyboard.feature.ime.di.imeModule
import com.sukajee.nepalikeyboard.feature.settings.di.settingsModule
import com.sukajee.nepalikeyboard.feature.transliteration.di.transliterationModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class NepaliKeyboardApp : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@NepaliKeyboardApp)
            modules(
                coreDataModule,
                dictionaryModule,
                imeModule,
                settingsModule,
                transliterationModule
            )
        }
    }
}