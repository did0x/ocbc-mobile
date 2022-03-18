package com.putrash.ocbcmobile

import com.putrash.common.capitalize
import com.putrash.common.parseCurrency
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExtensionUnitTest {
    @Test
    fun capitalizeOneWord_isCorrect() {
        assertEquals( "Hello", "hello".capitalize())
    }

    @Test
    fun capitalizeTwoWord_isCorrect() {
        assertEquals( "Hello world", "hello world".capitalize())
    }

    @Test
    fun parseCurrencyThousand_isCorrect() {
        assertEquals(3000.25, "SGD 3,000.25".parseCurrency(), 0.00)
    }

    @Test
    fun parseCurrencyHundred_isCorrect() {
        assertEquals(300.25, "SGD 300.25".parseCurrency(), 0.00)
    }

    @Test
    fun parseCurrencyUnit_isCorrect() {
        assertEquals(0.25, "SGD 0.25".parseCurrency(), 0.00)
    }
}