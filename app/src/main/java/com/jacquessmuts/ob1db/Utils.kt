package com.jacquessmuts.ob1db

/**
 * Created by Jacques Smuts on 1/20/2018.
 */
class Utils {

    companion object {
        var strSeparator = "__,__"
        fun convertArrayToString(array: List<String>?): String {
            var str = ""
            for (i in array!!) {
                str = str + i + strSeparator
            }
            return str.substring(0, str.length-5)
        }

        fun convertStringToArray(str: String): List<String> {
            return str.split(strSeparator.toRegex()).dropLastWhile { it.isEmpty() }.toList()
        }
    }

}