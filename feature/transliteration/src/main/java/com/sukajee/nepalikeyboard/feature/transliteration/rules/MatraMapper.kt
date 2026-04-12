package com.sukajee.nepalikeyboard.feature.transliteration.rules

/**
 * Maps independent vowels to their matra (vowel sign) equivalents.
 *
 * In Devanagari, when a vowel follows a consonant it takes a different
 * form called a "matra". For example:
 *   क + आ = का  (not कआ)
 *   क + इ = कि  (not कइ)
 *
 * The engine checks if the previous output ended with a consonant
 * and if so uses the matra form instead of the independent vowel form.
 *
 * The special case is "अ" (a): in Devanagari, every consonant carries
 * an inherent "a" sound. So "क" is already "ka". When we explicitly
 * want "ka" there is nothing to add. When another vowel follows
 * we need to first strip the inherent-a using a halanta (्) — but in
 * practice for a keyboard this is handled by the conjunct logic.
 */
object MatraMapper {

    /**
     * Maps independent vowel → matra (vowel sign).
     * "अ" maps to "" because consonants already carry inherent-a.
     */
    private val vowelToMatra: Map<String, String> = mapOf(
        "अ"  to "",     // inherent — no matra needed
        "आ"  to "ा",
        "इ"  to "ि",
        "ई"  to "ी",
        "उ"  to "ु",
        "ऊ"  to "ू",
        "ए"  to "े",
        "ऐ"  to "ै",
        "ओ"  to "ो",
        "औ"  to "ौ",
        "ऋ"  to "ृ",
    )

    private val devanagariConsonantRange = '\u0915'..'\u0939' // क to ह
    private val additionalConsonants = setOf(
        '\u0958', '\u0959', '\u095A', '\u095B', // क़ ख़ ग़ ज़
        '\u095C', '\u095D', '\u095E', '\u095F', // ड़ ढ़ फ़ य़
    )

    /**
     * Returns true if the string ends with a Devanagari consonant
     * (meaning the next vowel should be written as a matra).
     */
    fun endsWithConsonant(text: String): Boolean {
        if (text.isEmpty()) return false
        val last = text.last()
        return last in devanagariConsonantRange || last in additionalConsonants
    }

    /**
     * Given an independent vowel string, returns the matra form
     * if [precedingTextEndsWithConsonant] is true, otherwise returns
     * the original independent vowel.
     */
    fun resolve(vowel: String, precedingTextEndsWithConsonant: Boolean): String {
        if (!precedingTextEndsWithConsonant) return vowel
        return vowelToMatra[vowel] ?: vowel
    }
}
