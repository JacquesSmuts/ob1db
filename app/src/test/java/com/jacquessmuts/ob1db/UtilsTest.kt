package com.jacquessmuts.ob1db

import org.junit.Test

import org.junit.Assert.*

/**
 * Created by Jacques Smuts on 1/21/2018.
 */
class UtilsTest {
    @Test
    fun convertArrayToString() {
        val characterStrings = listOf<String>("One", "Two", "Three");
        val convertedString : String = Utils.convertArrayToString(characterStrings)
        assertEquals("One__,__Two__,__Three", convertedString )
    }

    @Test
    fun convertStringToArray() {
        val characterStrings = listOf<String>("One", "Two", "Three");
        val characterString = "One__,__Two__,__Three"
        val convertedString = Utils.convertStringToArray(characterString)
        assertEquals(characterStrings, convertedString)
    }

}