package com.sukajee.nepalikeyboard.core.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [WordEntity::class],
    version = 1,
    exportSchema = true,
)
abstract class NepaliKeyboardDatabase : RoomDatabase() {
    abstract fun wordDao(): WordDao
}