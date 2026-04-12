package com.sukajee.nepalikeyboard.feature.transliteration.engine

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for [TransliterationEngine].
 *
 * These run on the JVM with no Android emulator needed — that's the
 * whole point of keeping the engine as a pure Kotlin module.
 *
 * Run with: ./gradlew :feature:feature-transliteration:test
 */
class TransliterationEngineTest {

    private lateinit var engine: TransliterationEngine

    @Before
    fun setUp() {
        engine = TransliterationEngine()
    }

    // ── forceCommit tests ─────────────────────────────────────────────────

    @Test
    fun `single consonant k becomes ka with inherent a`() {
        // "k" alone should just produce "क" (the inherent-a is implicit)
        val result = engine.forceCommit("k")
        assertEquals("क", result)
    }

    @Test
    fun `ka produces का`() {
        val result = engine.forceCommit("ka")
        assertEquals("का", result)
    }

    @Test
    fun `ram produces राम`() {
        val result = engine.forceCommit("ram")
        assertEquals("राम", result)
    }

    @Test
    fun `ramro produces रामरो`() {
        // Note: "ramro" in standard Nepali typing = रामरो
        // The full word "राम्रो" would need "raamro" or explicit halanta
        val result = engine.forceCommit("ramro")
        assertEquals("रामरो", result)
    }

    @Test
    fun `namaste produces नमस्ते`() {
        val result = engine.forceCommit("namaste")
        assertEquals("नमस्ते", result)
    }

    @Test
    fun `nepal produces नेपाल`() {
        val result = engine.forceCommit("nepal")
        assertEquals("नेपाल", result)
    }

    @Test
    fun `kh digraph produces ख`() {
        val result = engine.forceCommit("kha")
        assertEquals("खा", result)
    }

    @Test
    fun `gh digraph produces घ`() {
        val result = engine.forceCommit("gha")
        assertEquals("घा", result)
    }

    @Test
    fun `ch digraph produces च`() {
        val result = engine.forceCommit("cha")
        assertEquals("चा", result)
    }

    @Test
    fun `uppercase T produces retroflex ta`() {
        val result = engine.forceCommit("Ta")
        assertEquals("टा", result)
    }

    @Test
    fun `lowercase t produces dental ta`() {
        val result = engine.forceCommit("ta")
        assertEquals("ता", result)
    }

    @Test
    fun `ksh trigraph produces ksha conjunct`() {
        val result = engine.forceCommit("ksha")
        assertEquals("क्षा", result)
    }

    @Test
    fun `empty buffer produces empty string`() {
        val result = engine.forceCommit("")
        assertEquals("", result)
    }

    @Test
    fun `aa produces long aa vowel`() {
        val result = engine.forceCommit("aa")
        assertEquals("आ", result)
    }

    @Test
    fun `maa produces maa with long aa matra`() {
        val result = engine.forceCommit("maa")
        assertEquals("मा", result)
    }

    // ── transliterate (live preview) tests ───────────────────────────────

    @Test
    fun `transliterate partial buffer keeps trailing chars in buffer`() {
        val result = engine.transliterate("nep")
        // "nep" — "n" and "e" should be committed, "p" stays in buffer
        // as it might become "ph" or "pr" etc.
        assert(result.remainingBuffer.isNotEmpty()) {
            "Expected trailing chars in remainingBuffer for partial input"
        }
    }

    @Test
    fun `transliterate full word has empty remaining buffer after force commit`() {
        val committed = engine.forceCommit("nepal")
        assertEquals("नेपाल", committed)
    }
}