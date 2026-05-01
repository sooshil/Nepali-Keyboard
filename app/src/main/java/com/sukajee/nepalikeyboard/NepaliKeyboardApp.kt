package com.sukajee.nepalikeyboard

import android.app.Application
import com.sukajee.nepalikeyboard.core.data.di.coreDataModule
import com.sukajee.nepalikeyboard.feature.dictionary.di.dictionaryModule
import com.sukajee.nepalikeyboard.feature.dictionary.repository.DictionaryRepository
import com.sukajee.nepalikeyboard.feature.ime.di.imeModule
import com.sukajee.nepalikeyboard.feature.settings.di.settingsModule
import com.sukajee.nepalikeyboard.feature.transliteration.di.transliterationModule
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.java.KoinJavaComponent.get

class NepaliKeyboardApp : Application() {

    @OptIn(DelicateCoroutinesApi::class)
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
        // Seed the dictionary on first launch
        GlobalScope.launch(Dispatchers.IO) {
            get<DictionaryRepository>().seedIfEmpty()
        }
    }
}