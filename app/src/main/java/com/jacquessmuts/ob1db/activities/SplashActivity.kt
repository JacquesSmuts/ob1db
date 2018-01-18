package com.jacquessmuts.ob1db.activities

import android.content.ContentValues
import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.jacquessmuts.ob1db.R
import com.jacquessmuts.ob1db.models.Film
import com.jacquessmuts.ob1db.network.NetworkClient
import org.json.JSONObject
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.InputStreamReader

/**
 * Created by Jacques Smuts on 1/18/2018.
 */
class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        GetJsonWithOkHttpClient().execute();
    }

    inner class GetJsonWithOkHttpClient() : AsyncTask<Unit, Unit, String>() {

        override fun doInBackground(vararg params: Unit?): String? {
            val networkClient = NetworkClient()
            val stream = BufferedInputStream(
                    networkClient.get(NetworkClient.GET_ALL_FILMS))
            return readStream(stream)
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)

            val gson = Gson()
            val jsonObject = JSONObject(result)
            val resultsArray = jsonObject.getString("results")
            val filmsType = object : TypeToken<List<Film>>() {}.type
            val films = Gson().fromJson<List<Film>>(resultsArray, filmsType)

            val filmValues : ContentValues;
            //filmValues.put(FilmContract.FilmEntry.COLUMN_FILM_I)
            val contentResolver = contentResolver;
            //contentResolver.bulkInsert()

            //Log.e("Tag", result)

            //mInnerTextView.text = result
            startActivity(FilmListActivity.getIntent(this@SplashActivity));
        }

        fun readStream(inputStream: BufferedInputStream): String {
            val bufferedReader = BufferedReader(InputStreamReader(inputStream))
            val stringBuilder = StringBuilder()
            bufferedReader.forEachLine { stringBuilder.append(it) }
            return stringBuilder.toString()
        }
    }
}