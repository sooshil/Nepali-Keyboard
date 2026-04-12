package com.sukajee.nepalikeyboard.core.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Represents a single Nepali word in the dictionary.
 *
 * [word]      — The Devanagari word (e.g. "राम्रो")
 * [frequency] — How commonly it appears in text. Higher = shown first in suggestions.
 * [romanized] — Romanized form (e.g. "ramro") used for romanized-mode lookup.
 */
@Entity(
    tableName = "words",
    indices = [
        Index(value = ["word"], unique = true),
        Index(value = ["romanized"]),
    ]
)
data class WordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "word")
    val word: String,

    @ColumnInfo(name = "frequency")
    val frequency: Int = 1,

    @ColumnInfo(name = "romanized")
    val romanized: String = "",
)