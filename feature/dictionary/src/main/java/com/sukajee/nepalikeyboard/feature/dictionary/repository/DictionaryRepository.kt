package com.sukajee.nepalikeyboard.feature.dictionary.repository

import com.sukajee.nepalikeyboard.core.data.db.WordDao
import com.sukajee.nepalikeyboard.core.data.db.WordEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface DictionaryRepository {
    fun getSuggestions(prefix: String, limit: Int = 5): Flow<List<String>>
    suspend fun recordWordUsage(word: String)
    suspend fun seedIfEmpty()
}

class DictionaryRepositoryImpl(
    private val wordDao: WordDao,
) : DictionaryRepository {

    override fun getSuggestions(prefix: String, limit: Int): Flow<List<String>> {
        return wordDao.getSuggestionsByPrefix(prefix, limit)
            .map { entities -> entities.map { it.word } }
    }

    override suspend fun recordWordUsage(word: String) {
        wordDao.incrementFrequency(word)
    }

    /**
     * Seed the database with a starter word list on first launch.
     * In production this would load from a bundled assets file.
     * For now we seed with a small set of common Nepali words.
     */
    override suspend fun seedIfEmpty() {
        if (wordDao.getWordCount() > 0) return
        val starterWords = listOf(
            WordEntity(word = "नमस्ते", frequency = 100, romanized = "namaste"),
            WordEntity(word = "धन्यवाद", frequency = 90, romanized = "dhanyabad"),
            WordEntity(word = "नेपाल", frequency = 95, romanized = "nepal"),
            WordEntity(word = "काठमाडौं", frequency = 80, romanized = "kathmandu"),
            WordEntity(word = "राम्रो", frequency = 85, romanized = "ramro"),
            WordEntity(word = "मान्छे", frequency = 75, romanized = "manchhe"),
            WordEntity(word = "घर", frequency = 88, romanized = "ghar"),
            WordEntity(word = "पानी", frequency = 82, romanized = "pani"),
            WordEntity(word = "खाना", frequency = 79, romanized = "khana"),
            WordEntity(word = "जान्छु", frequency = 70, romanized = "jaanchu"),
        )
        wordDao.insertAll(starterWords)
    }
}
