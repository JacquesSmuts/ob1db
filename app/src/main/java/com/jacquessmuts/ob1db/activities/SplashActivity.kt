package com.jacquessmuts.ob1db.activities

import android.content.ContentValues
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
import com.jacquessmuts.ob1db.models.Film
import com.jacquessmuts.ob1db.models.Person
import com.jacquessmuts.ob1db.network.NetworkClient
import kotlinx.android.synthetic.main.activity_splash.*
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.DialogAction
import com.jacquessmuts.ob1db.testing.SimpleIdlingResource
import android.support.test.espresso.IdlingResource
import android.support.annotation.NonNull
import android.support.annotation.VisibleForTesting



/**
 * Created by Jacques Smuts on 1/18/2018.
 * Shows the splash screen while data loads. Should also do checks for existing data and internet connectivity
 */
class SplashActivity : AppCompatActivity() {

    private var mIsCurrentlyOpen = false //Determines whether the activity is running
    private val okHttpClient = OkHttpClient()
    private var mNextUrl = ""
    private val TAG = "splashActivity"
    private var mIdlingResource : SimpleIdlingResource? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        if (mIdlingResource != null) mIdlingResource!!.setIdleState(false) //testing must wait
        super.onCreate(savedInstanceState)
        mIsCurrentlyOpen = true
        setContentView(R.layout.activity_splash)

        if (!Utils.isNetworkConnected(this)) {
            //There's no internet. Check if data has been saved before
            //TODO also need to check if tables exist, in case user erases data manually and opens app, but this is a super fringe event
            handleApiFailure()
            return
        }

        //Only download data if it has been more than a day since last call
        if (Utils.hasBeenMoreThanADaySinceLastDataCall(this)) {
            this.getFilmsWithOkHttp()
        } else {
            navigateToNextAfterDelay()
        }

        //show the progress bar if it takes long
        Handler().postDelayed({
            if (mIsCurrentlyOpen){
                progress_bar.visibility = View.VISIBLE
                progress_bar.animate().scaleY(5.0f).scaleX(5.0f).duration = 12000
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
    private fun getNextPage(){
        Log.d(TAG, "Getting next page")
        if (TextUtils.isEmpty(mNextUrl) || mNextUrl == "null"){
            Utils.setDataSavedTime(this)
            navigateToNextActivity()
        } else {
            getPeopleWithOkHttp(mNextUrl)
        }
    }

    private fun handleApiFailure(){
        if (mIdlingResource != null) mIdlingResource!!.setIdleState(true) //Testing can continue
        if (Utils.hasEverSavedData(this)){
            showApologyPopup(getString(R.string.error_api_with_data))
        } else {
            showApologyPopup(getString(R.string.error_api))
        }
    }

    private fun showApologyPopup(bodyText : String){
        MaterialDialog.Builder(this)
                .title(R.string.error_title)
                .content(bodyText)
                .positiveText(R.string.ok_button)
                .onAny(PopupCallback())
                .show()
    }

    private inner class PopupCallback : MaterialDialog.SingleButtonCallback {
        override fun onClick(dialog: MaterialDialog, which: DialogAction) {
            if (Utils.hasEverSavedData(this@SplashActivity)){
                navigateToNextActivity()
            } else {
                finish()
            }
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
        if (mIdlingResource != null) mIdlingResource!!.setIdleState(true) //Testing can continue
        //If user closes splash screen, the app won't re-open
        if (mIsCurrentlyOpen) {
            startActivity(FilmListActivity.getIntent(this@SplashActivity))
        }
        finish()
    }

    private fun getFilmsWithOkHttp(){
        val request = Request.Builder()
                .url(NetworkClient.GET_ALL_FILMS)
                .build()

        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                handleApiFailure()
            }

            override fun onResponse(call: Call, response: Response) {
                response.body().use({ responseBody ->
                    if (!response.isSuccessful){
                        handleApiFailure()
                        return
                    }

                    handleFilmResults(responseBody?.string())
                })
            }
        })
    }

    /**
     * [result] full server response body
     */
    private fun handleFilmResults(result: String?){

        if (result == null){
            handleApiFailure()
            return
        }

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
        getPeopleWithOkHttp(mNextUrl)
    }

    private fun getPeopleWithOkHttp(url : String){
        var streamUrl = NetworkClient.GET_ALL_CHARACTERS
        if (!TextUtils.isEmpty(url)){
            streamUrl = url
        }
        val request = Request.Builder()
                .url(streamUrl)
                .build()
        Log.d("SplashActivity", "Getting People:" + streamUrl)

        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                handleApiFailure()
            }

            override fun onResponse(call: Call, response: Response) {
                response.body().use({ responseBody ->
                    if (!response.isSuccessful){
                        handleApiFailure()
                        return
                    }

                    handlePeopleResults(responseBody?.string())
                })
            }
        })
    }

    private fun handlePeopleResults(result : String?){
        if (result == null){
            handleApiFailure()
            return
        }

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

    /**
     * Only called from test, creates and returns a new [SimpleIdlingResource].
     */
    @VisibleForTesting
    fun getIdlingResource(): IdlingResource {
        if (mIdlingResource == null) {
            mIdlingResource = SimpleIdlingResource()
        }
        return mIdlingResource as SimpleIdlingResource
    }
}