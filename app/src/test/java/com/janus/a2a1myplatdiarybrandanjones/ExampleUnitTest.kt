package com.janus.a2a1myplatdiarybrandanjones

import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun addTwoAndThree_equalsFive(){
        assertEquals(5,2+3)
    }
    @Test
    fun addThreeAndThree_equalsSix(){
        assertEquals(6,3+3)
    }
    @Test
    fun addFourAndThree_equalsSeven(){
        assertEquals(7,4+3)
    }

    @Test
    fun addFiveAndThree_equalsEight(){
        assertEquals(8,4+4)
    }
}
