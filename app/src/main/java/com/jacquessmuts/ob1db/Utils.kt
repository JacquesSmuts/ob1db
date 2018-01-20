package com.jacquessmuts.ob1db

import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.net.ConnectivityManager



/**
 * Created by Jacques Smuts on 1/20/2018.
 */
object Utils {

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


    fun isNetworkConnected(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return cm.activeNetworkInfo != null
    }
}