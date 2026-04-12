package com.sukajee.nepalikeyboard.core.data.di

import androidx.room.Room
import com.sukajee.nepalikeyboard.core.data.datastore.UserPreferencesDataStore
import com.sukajee.nepalikeyboard.core.data.db.NepaliKeyboardDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import kotlin.jvm.java

val coreDataModule = module {

    // Room database — singleton
    single {
        Room.databaseBuilder(
            androidContext(),
            NepaliKeyboardDatabase::class.java,
            "nepali_keyboard.db"
        )
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()
    }

    // DAOs
    single { get<NepaliKeyboardDatabase>().wordDao() }

    // DataStore
    single { UserPreferencesDataStore(androidContext()) }
}