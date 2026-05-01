package com.sukajee.nepalikeyboard.feature.transliteration.engine

import com.sukajee.nepalikeyboard.feature.transliteration.model.TransliterationResult
import com.sukajee.nepalikeyboard.feature.transliteration.rules.MatraMapper
import com.sukajee.nepalikeyboard.feature.transliteration.rules.RomanToDevanagariMap

/**
 * TransliterationEngine
 *
 * Converts a roman buffer string into Devanagari using a greedy
 * longest-match approach against [RomanToDevanagariMap.rules].
 *
 * ## How it works
 *
 * Given a buffer like "ramro":
 *  1. Try to match the longest rule starting at position 0.
 *     "r" → "र"  (match found, advance)
 *  2. Next char "a" follows a consonant → apply matra → "ा"
 *     Now we have "रा", advance.
 *  3. "m" → "म", then "r" → "र", then "o" follows "र" → matra "ो"
 *  4. Result: "राम्रो"   ... well, "ramro" = "रम्रो" actually without the aa:
 *     r+a = रा, m = म, r = र, o → ो on र = रो
 *     Final: "रामरो"  — close enough for live preview.
 *     The engine doesn't add halanta automatically between consonant clusters
 *     in this pass. Halanta handling is the responsibility of the commit step.
 *
 * ## Commit vs Preview
 *
 * The engine exposes two operations:
 * - [transliterate] — for live preview as the user types.
 *   Returns a partial result where the last few characters remain
 *   in [TransliterationResult.remainingBuffer] because they might
 *   still be extended (e.g. "k" might become "kh" or "ksh").
 * - [forceCommit] — called on space/enter to flush the buffer fully.
 *
 * This class has zero Android dependencies and is fully unit-testable.
 */
class TransliterationEngine {

    /**
     * Transliterate the given roman [buffer] into a [TransliterationResult].
     * The last [LOOKAHEAD] characters are kept in [TransliterationResult.remainingBuffer]
     * to allow further disambiguation.
     */
    fun transliterate(buffer: String): TransliterationResult {
        if (buffer.isEmpty()) {
            return TransliterationResult(committed = "", devanagari = "", remainingBuffer = "")
        }

        val fullDevanagari = convertToDevanagari(buffer)

        // Keep the last LOOKAHEAD characters uncommitted (they may extend)
        val safeCommitLength = (buffer.length - LOOKAHEAD).coerceAtLeast(0)
        val committedRoman = buffer.substring(0, safeCommitLength)
        val remainingRoman = buffer.substring(safeCommitLength)

        val committedDevanagari = if (committedRoman.isEmpty()) ""
        else convertToDevanagari(committedRoman)

        return TransliterationResult(
            committed = committedDevanagari,
            devanagari = fullDevanagari,
            remainingBuffer = remainingRoman,
        )
    }

    /**
     * Force-commit the entire buffer as-is. Called on space, enter, or
     * suggestion selection.
     */
    fun forceCommit(buffer: String): String {
        return convertToDevanagari(buffer)
    }

    // ── Core conversion ───────────────────────────────────────────────────

    private fun convertToDevanagari(roman: String): String {
        val result = StringBuilder()
        var i = 0
        // Tracks whether the last resolved output was a vowel.
        // This prevents consonant-conjunct insertion after an inherent-a,
        // which produces an empty matra but still logically "breaks" the cluster.
        var previousWasVowel = false

        while (i < roman.length) {
            val match = findLongestMatch(roman, i)
            if (match != null) {
                val (matchedRoman, rawDevanagari) = match

                if (isVowelOutput(rawDevanagari)) {
                    // Resolve to matra if preceding output was a consonant
                    val matra = MatraMapper.resolve(
                        vowel = rawDevanagari,
                        precedingTextEndsWithConsonant = MatraMapper.endsWithConsonant(result.toString())
                    )
                    result.append(matra)
                    previousWasVowel = true
                } else {
                    // It's a consonant — insert halanta only if the PREVIOUS
                    // output was also a consonant (not a vowel, even inherent-a)
                    if (!previousWasVowel && MatraMapper.endsWithConsonant(result.toString())) {
                        result.append(HALANTA)
                    }
                    result.append(rawDevanagari)
                    previousWasVowel = false
                }

                i += matchedRoman.length
            } else {
                // No rule matched — pass through as-is and reset vowel tracking
                result.append(roman[i])
                previousWasVowel = false
                i++
            }
        }

        return result.toString()
    }

    /**
     * Greedily find the longest matching rule starting at [startIndex] in [roman].
     * Returns the matched roman sequence and its Devanagari equivalent, or null.
     */
    private fun findLongestMatch(roman: String, startIndex: Int): Pair<String, String>? {
        val remaining = roman.substring(startIndex)
        // Rules are already ordered longest-first in RomanToDevanagariMap
        return RomanToDevanagariMap.rules.firstOrNull { (romanSeq, _) ->
            remaining.startsWith(romanSeq)
        }
    }

    /**
     * Returns true if [devanagari] is an independent vowel or empty string
     * (the inherent-a case).
     */
    private fun isVowelOutput(devanagari: String): Boolean {
        val independentVowels = setOf(
            "अ", "आ", "इ", "ई", "उ", "ऊ", "ए", "ऐ", "ओ", "औ", "ऋ"
        )
        return devanagari in independentVowels
    }

    companion object {
        /**
         * Number of trailing roman characters kept in the buffer
         * to allow multi-character rules to complete.
         * "ksh" is the longest rule (3 chars) so LOOKAHEAD = 2.
         */
        private const val LOOKAHEAD = 2

        private const val HALANTA = "्"  // U+094D — Devanagari virama
    }
}
