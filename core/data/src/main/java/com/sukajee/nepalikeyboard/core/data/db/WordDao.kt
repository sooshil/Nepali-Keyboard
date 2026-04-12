package com.sukajee.nepalikeyboard.core.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WordDao {

    /**
     * Find suggestions that start with the given Devanagari prefix.
     * Ordered by frequency descending so most common words appear first.
     */
    @Query("""
        SELECT * FROM words 
        WHERE word LIKE :prefix || '%' 
        ORDER BY frequency DESC 
        LIMIT :limit
    """)
    fun getSuggestionsByPrefix(prefix: String, limit: Int = 5): Flow<List<WordEntity>>

    /**
     * Find suggestions by romanized prefix — used in romanized input mode.
     */
    @Query("""
        SELECT * FROM words 
        WHERE romanized LIKE :romanizedPrefix || '%' 
        ORDER BY frequency DESC 
        LIMIT :limit
    """)
    fun getSuggestionsByRomanizedPrefix(
        romanizedPrefix: String,
        limit: Int = 5
    ): Flow<List<WordEntity>>

    /**
     * Increment frequency when a word is selected by the user.
     * This gives the keyboard a basic learning capability.
     */
    @Query("UPDATE words SET frequency = frequency + 1 WHERE word = :word")
    suspend fun incrementFrequency(word: String)

    @Insert(onConflict = OnConflictStrategy.Companion.IGNORE)
    suspend fun insertAll(words: List<WordEntity>)

    @Query("SELECT COUNT(*) FROM words")
    suspend fun getWordCount(): Int
}