package com.jacquessmuts.ob1db.activities

import android.content.ContentValues
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.Log
import android.view.View
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jacquessmuts.ob1db.R
import com.jacquessmuts.ob1db.Utils
import com.jacquessmuts.ob1db.data.FilmContract
import com.jacquessmuts.ob1db.data.PeopleContract
import com.jacquessmuts.ob1db.models.Person
import com.jacquessmuts.ob1db.models.Film
import com.jacquessmuts.ob1db.network.NetworkClient
import kotlinx.android.synthetic.main.activity_splash.*
import org.json.JSONObject
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.InputStreamReader

/**
 * Created by Jacques Smuts on 1/18/2018.
 * Shows the splash screen while data loads. Should also do checks for existing data and internet connectivity
 */
class SplashActivity : AppCompatActivity() {

    private var mIsCurrentlyOpen = false //Determines whether the activity is running
    var mNextUrl = ""
    val TAG = "SplashActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mIsCurrentlyOpen = true
        setContentView(R.layout.activity_splash)

        if (!Utils.isNetworkConnected(this)) {
            //There's no internet. Check if data has been saved before
            //TODO also need to check if tables exist, in case user erases data manually and opens app, but this is a super fringe event
            if (Utils.hasEverSavedData(this)){
                navigateToNextAfterDelay()
            } else {
                //showApologyPopup()
                finish()
            }
        }

        //Only download data if it has been more than a day since last call
        if (Utils.hasBeenMoreThanADaySinceLastDataCall(this)) {
            this.GetFilmsWithAsyncTask().execute()
        } else {
            navigateToNextAfterDelay()
        }

        //show the progress bar if it takes long
        Handler().postDelayed({
            if (mIsCurrentlyOpen){
                progress_bar.visibility = View.VISIBLE
                progress_bar.animate().scaleY(5.0f).scaleX(5.0f).setDuration(12000)
            } else {
                finish() //because the user closed the app before things finished
            }
        }, 2000)
    }

    override fun onPause() {
        mIsCurrentlyOpen = false
        super.onPause()
    }

    //Loop through all the pages to get the characters. If there are too many characters,
    //just get them individually as needed and save then. For now, the list is short and this works
    fun getNextPage(){
        Log.d(TAG, "Getting next page")
        if (TextUtils.isEmpty(mNextUrl) || mNextUrl.equals("null")){
            Utils.setDataSavedTime(this)
            navigateToNextActivity()
        } else {
            GetPeopleWithAsyncTask(mNextUrl).execute()
        }
    }

    private fun navigateToNextAfterDelay(){
        Handler().postDelayed({
            if (mIsCurrentlyOpen){
                navigateToNextActivity()
            } else {
                finish() //because the user closed the app before things finished
            }
        }, 1500)
    }

    private fun navigateToNextActivity(){
        //If user closes splash screen, the app won't re-open
        if (mIsCurrentlyOpen) {
            startActivity(FilmListActivity.getIntent(this@SplashActivity))
        }
        finish()
    }

//    private fun getFilmsWithOkHttp(){
//        val client = OkHttpClient()
//        val request = Request.Builder().url(NetworkClient.GET_ALL_FILMS).build()
//        val Response = client.newCall(request).execute()
//
//        return response.body().string();
//
//    }


    //TODO: use OkHttp. I mean really. Gosh gee willikers.
    inner class GetFilmsWithAsyncTask() : AsyncTask<Unit, Unit, String>() {

        override fun doInBackground(vararg params: Unit?): String? {
            val networkClient = NetworkClient()
            val stream = BufferedInputStream(
                    networkClient.get(NetworkClient.GET_ALL_FILMS))
            return readStream(stream)
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)

            //TODO: if (TextUtils.isEmpty(result)){}

            val jsonObject = JSONObject(result)
            val resultsArray = jsonObject.getString("results")
            val filmsType = object : TypeToken<List<Film>>() {}.type
            val films = Gson().fromJson<List<Film>>(resultsArray, filmsType)

            val filmValuesArr = arrayOfNulls<ContentValues>(films.size)
            for ((i, film) in films.withIndex()){
                filmValuesArr[i] = ContentValues()
                filmValuesArr[i]!!.put(FilmContract.FilmEntry.COLUMN_FILM_ID, film.episodeId)
                val charactersString: String = Utils.convertArrayToString(film.characters)
                filmValuesArr[i]!!.put(FilmContract.FilmEntry.COLUMN_CHARACTERS, charactersString)
                filmValuesArr[i]!!.put(FilmContract.FilmEntry.COLUMN_CREATED, film.created)
                filmValuesArr[i]!!.put(FilmContract.FilmEntry.COLUMN_DIRECTOR, film.director)
                filmValuesArr[i]!!.put(FilmContract.FilmEntry.COLUMN_EDITED, film.edited)
                filmValuesArr[i]!!.put(FilmContract.FilmEntry.COLUMN_OPENING_CRAWL, film.openingCrawl)
                filmValuesArr[i]!!.put(FilmContract.FilmEntry.COLUMN_PRODUCER, film.producer)
                filmValuesArr[i]!!.put(FilmContract.FilmEntry.COLUMN_RELEASE_DATE, film.releaseDate)
//                filmValuesArr[i]!!.put(FilmContract.FilmEntry.COLUMN_STARSHIPS, film.starships)
//                filmValuesArr[i]!!.put(FilmContract.FilmEntry.COLUMN_VEHICLES, film.vehicles)
                filmValuesArr[i]!!.put(FilmContract.FilmEntry.COLUMN_TITLE, film.title)
                filmValuesArr[i]!!.put(FilmContract.FilmEntry.COLUMN_URL, film.url)
            }
            val contentResolver = contentResolver
            contentResolver.bulkInsert(FilmContract.FilmEntry.CONTENT_URI, filmValuesArr)

            //After getting the films, get the characters
            this@SplashActivity.GetPeopleWithAsyncTask(mNextUrl).execute()
        }

        private fun readStream(inputStream: BufferedInputStream): String {
            val bufferedReader = BufferedReader(InputStreamReader(inputStream))
            val stringBuilder = StringBuilder()
            bufferedReader.forEachLine { stringBuilder.append(it) }
            return stringBuilder.toString()
        }
    }

    inner class GetPeopleWithAsyncTask(url : String) : AsyncTask<Unit, Unit, String>() {

        private val streamUrl = url

        override fun doInBackground(vararg params: Unit?): String? {
            val networkClient = NetworkClient()
            var url = NetworkClient.GET_ALL_CHARACTERS
            if (!TextUtils.isEmpty(streamUrl)){
                url = streamUrl
            }
            Log.d("SplashActivity", "Getting People:" + url)
            val stream = BufferedInputStream(
                    networkClient.get(url))
            return readStream(stream)
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)

            //TODO: if (TextUtils.isEmpty(result)){}

            val jsonObject = JSONObject(result)

            mNextUrl = ""
            if (jsonObject.has("next")){
                mNextUrl = jsonObject.getString("next")
            }

            val resultsArray = jsonObject.getString("results")
            val peopleType = object : TypeToken<List<Person>>() {}.type
            val people = Gson().fromJson<List<Person>>(resultsArray, peopleType)

            val peopleValuesArr = arrayOfNulls<ContentValues>(people.size)
            for ((i, person) in people.withIndex()){
                peopleValuesArr[i] = ContentValues()
                peopleValuesArr[i]!!.put(PeopleContract.PersonEntry.COLUMN_NAME, person.name)
                peopleValuesArr[i]!!.put(PeopleContract.PersonEntry.COLUMN_URL, person.url)
                peopleValuesArr[i]!!.put(PeopleContract.PersonEntry.COLUMN_HEIGHT, person.height)
            }
            val contentResolver = contentResolver
            contentResolver.bulkInsert(PeopleContract.PersonEntry.CONTENT_URI, peopleValuesArr)

            getNextPage()
        }

        private fun readStream(inputStream: BufferedInputStream): String {
            val bufferedReader = BufferedReader(InputStreamReader(inputStream))
            val stringBuilder = StringBuilder()
            bufferedReader.forEachLine { stringBuilder.append(it) }
            return stringBuilder.toString()
        }
    }
}