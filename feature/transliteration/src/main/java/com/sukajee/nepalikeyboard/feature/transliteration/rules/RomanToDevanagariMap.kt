package com.sukajee.nepalikeyboard.feature.transliteration.rules

/**
 * Complete mapping of roman sequences → Devanagari Unicode.
 *
 * Rules:
 * - Longer sequences must come first so the engine matches greedily.
 *   e.g. "ksh" before "ks" before "k"
 * - Uppercase variants handle aspirated / retroflex disambiguation.
 *   e.g. 'T' → ट (retroflex), 't' → त (dental)
 * - Vowel matras are handled separately — see [MatraMapper].
 *
 * This follows the popular "Romanized Nepali" convention used by
 * many Nepali typing tools and is familiar to most Nepali users.
 */
object RomanToDevanagariMap {

    /**
     * Ordered list of (romanSequence → devanagari) pairs.
     * Order matters: longer/more-specific sequences first.
     */
    val rules: List<Pair<String, String>> = listOf(

        // ── Conjuncts (must come before individual consonants) ────────────
        "ksh" to "क्ष",
        "Gny" to "ज्ञ",
        "gny" to "ज्ञ",
        "shr" to "श्र",
        "str" to "स्त्र",
        "tr"  to "त्र",

        // ── Two-letter sequences ──────────────────────────────────────────
        "kh"  to "ख",
        "gh"  to "घ",
        "ch"  to "च",
        "Ch"  to "छ",
        "jh"  to "झ",
        "Th"  to "ठ",   // uppercase T = retroflex
        "Dh"  to "ढ",
        "th"  to "थ",   // lowercase t = dental
        "dh"  to "ध",
        "ph"  to "फ",
        "bh"  to "भ",
        "sh"  to "श",
        "Sh"  to "ष",
        "rr"  to "ऋ",   // rrukaar vowel
        "aa"  to "आ",
        "ii"  to "ई",
        "uu"  to "ऊ",
        "ai"  to "ऐ",
        "au"  to "औ",
        "ou"  to "औ",
        "oo"  to "ऊ",
        "ee"  to "ई",
        "ng"  to "ङ",
        "NY"  to "ञ",
        "ny"  to "ञ",
        "nn"  to "ण",

        // ── Single consonants ─────────────────────────────────────────────
        "k"   to "क",
        "g"   to "ग",
        "c"   to "च",
        "j"   to "ज",
        "T"   to "ट",   // retroflex T
        "D"   to "ड",   // retroflex D
        "N"   to "ण",   // retroflex N
        "t"   to "त",
        "d"   to "द",
        "n"   to "न",
        "p"   to "प",
        "f"   to "फ",
        "b"   to "ब",
        "m"   to "म",
        "y"   to "य",
        "r"   to "र",
        "l"   to "ल",
        "v"   to "व",
        "w"   to "व",
        "s"   to "स",
        "h"   to "ह",
        "x"   to "क्ष",
        "q"   to "क",

        // ── Single vowels ─────────────────────────────────────────────────
        "a"   to "अ",
        "i"   to "इ",
        "u"   to "उ",
        "e"   to "ए",
        "o"   to "ओ",
        "A"   to "आ",
        "I"   to "ई",
        "U"   to "ऊ",
        "E"   to "ऐ",
        "O"   to "ओ",

        // ── Special characters ────────────────────────────────────────────
        "M"   to "ं",    // Anusvara (bindu)
        "H"   to "ः",    // Visarga
        "."   to "।",    // Danda (Nepali full stop)
        ".."  to "॥",    // Double danda
    )

    /**
     * Pre-built map for O(1) lookup of exact sequences.
     * The engine uses [rules] for greedy prefix matching
     * and this for fast exact-match checks.
     */
    val exactMap: Map<String, String> = rules.toMap()
}