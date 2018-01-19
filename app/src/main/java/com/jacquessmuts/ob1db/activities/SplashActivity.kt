package com.jacquessmuts.ob1db.activities

import android.content.ContentValues
import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jacquessmuts.ob1db.R
import com.jacquessmuts.ob1db.data.FilmContract
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

            val filmValuesArr = arrayOfNulls<ContentValues>(films.size)
            for ((i, film) in films.withIndex()){
                filmValuesArr[i] = ContentValues()
                filmValuesArr[i]!!.put(FilmContract.FilmEntry.COLUMN_FILM_ID, film.episodeId)
                //filmValuesArr[i]!!.put(FilmContract.FilmEntry.COLUMN_CHARACTERS, film.characters)
                filmValuesArr[i]!!.put(FilmContract.FilmEntry.COLUMN_CREATED, film.created)
                filmValuesArr[i]!!.put(FilmContract.FilmEntry.COLUMN_DIRECTOR, film.director)
                filmValuesArr[i]!!.put(FilmContract.FilmEntry.COLUMN_EDITED, film.edited)
                filmValuesArr[i]!!.put(FilmContract.FilmEntry.COLUMN_OPENING_CRAWL, film.openingCrawl)
                filmValuesArr[i]!!.put(FilmContract.FilmEntry.COLUMN_PRODUCER, film.producer)
                filmValuesArr[i]!!.put(FilmContract.FilmEntry.COLUMN_RELEASE_DATE, film.releaseDate)
//                filmValuesArr[i]!!.put(FilmContract.FilmEntry.COLUMN_STARSHIPS, film.starships)
                filmValuesArr[i]!!.put(FilmContract.FilmEntry.COLUMN_TITLE, film.title)
                filmValuesArr[i]!!.put(FilmContract.FilmEntry.COLUMN_URL, film.url)
//                filmValuesArr[i]!!.put(FilmContract.FilmEntry.COLUMN_VEHICLES, film.vehicles)
            }
            val contentResolver = contentResolver;
            contentResolver.bulkInsert(FilmContract.FilmEntry.CONTENT_URI, filmValuesArr)

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