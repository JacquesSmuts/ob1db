package com.jacquessmuts.ob1db

import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.net.ConnectivityManager
import java.util.*


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


    /**
     * If SharedPreferences is used anywhere else, move this to a PreferencesHelper class
     */
    val PREFS_FILENAME = "com.jacquessmuts.ob1db.prefs"
    val TIME_SAVED = "time_saved" //in epoch time
    fun setDataSavedTime(context : Context){
        val timeInMillis = Calendar.getInstance().timeInMillis //current time

        val prefs = context.getSharedPreferences(PREFS_FILENAME, 0)
        val editor = prefs!!.edit()
        editor.putLong(TIME_SAVED, timeInMillis)
        editor.apply()
    }

    fun hasBeenMoreThanADaySinceLastDataCall(context : Context) : Boolean {
        val timeInMillis = Calendar.getInstance().timeInMillis //current time
        val prefs = context.getSharedPreferences(PREFS_FILENAME, 0)
        val timeSaved = prefs!!.getLong(TIME_SAVED, 0)

        val oneday = 24*60*60*1000 //24 hours in milliseconds
        return (timeInMillis - timeSaved > oneday)
    }

    fun hasEverSavedData(context : Context) : Boolean {
        val prefs = context.getSharedPreferences(PREFS_FILENAME, 0)
        val timeSaved = prefs!!.getLong(TIME_SAVED, 0)
        return (timeSaved > 0)
    }

}