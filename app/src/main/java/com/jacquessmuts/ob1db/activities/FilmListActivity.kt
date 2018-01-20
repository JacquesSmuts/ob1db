package com.jacquessmuts.ob1db.activities

import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v4.app.LoaderManager
import android.support.v4.content.CursorLoader
import android.support.v4.content.Loader
import android.support.v7.widget.LinearLayoutManager
import android.util.AttributeSet
import android.view.View
import com.jacquessmuts.ob1db.R
import com.jacquessmuts.ob1db.adapters.FilmListRecyclerViewAdapter
import com.jacquessmuts.ob1db.data.FilmContract
import com.jacquessmuts.ob1db.data.FilmDbHelper

import com.jacquessmuts.ob1db.models.Film
import kotlinx.android.synthetic.main.activity_film_list.*

import kotlinx.android.synthetic.main.film_list.*

/**
 * An activity representing a list of Films. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a [FilmDetailActivity] representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
class FilmListActivity : AppCompatActivity(), LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private var mTwoPane: Boolean = false
    private var films: MutableList<Film> = mutableListOf()

    companion object {
        const val ID_FILM_LIST_LOADER = 94 //we are located at docking bay 94

        /**
         * Get the intent for this class and add the appropriate values and references
         */
        fun getIntent(context: Context) : Intent{
            val intent = Intent(context, FilmListActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            //intent.putExtra("key", value)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_film_list)

        setSupportActionBar(toolbar)
        toolbar.title = title

        if (film_detail_container != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true
        }

        recycler_film_list.layoutManager = LinearLayoutManager(this)
        supportLoaderManager.initLoader(ID_FILM_LIST_LOADER, null, this)
    }

    /**
     * Called whenever data has finished refreshing or updating
     */
    private fun setupRecyclerAdapter() {
        recycler_film_list.adapter = FilmListRecyclerViewAdapter(this, films, mTwoPane)
    }

    /**
     * DB INTERFACE FUNCTIONS
     */
    override fun onCreateLoader(loaderId: Int, args: Bundle?): Loader<Cursor>? {
        when (loaderId) {

             ID_FILM_LIST_LOADER -> {
                val queryUri = FilmContract.FilmEntry.CONTENT_URI

                val selection = FilmContract.FilmEntry.getAll

                return CursorLoader(
                        this@FilmListActivity,
                        queryUri,
                        FilmDbHelper.FILMS_LIST_PROJECTION,
                        selection,
                        null,
                        null
                )
            }

            else -> throw RuntimeException("Loader Not Implemented: " + loaderId)
        }
    }

    override fun onLoadFinished(loader: Loader<Cursor>?, data: Cursor?) {
        if (data != null) {
            /**
             *  Iterate over the entire database and load the [Film] list as objects in memory
             *
             *  if the database is expected to be larger than 1000 entries, consider removing
             *  this part and passing the cursor to the adapter instead, using the adapter to
             *  iterate over the cursor for visible items
             */
            films = mutableListOf()
            //use is the same as saying try{}finally{close()}
            data.use {
                while (it.moveToNext()) {
                    val film = Film()
                    film.episodeId = it.getLong(it.getColumnIndex(FilmContract.FilmEntry.COLUMN_FILM_ID))
                    film.title = it.getString(it.getColumnIndex(FilmContract.FilmEntry.COLUMN_TITLE))
                    film.releaseDate = it.getString(it.getColumnIndex(FilmContract.FilmEntry.COLUMN_RELEASE_DATE))
                    film.director = it.getString(it.getColumnIndex(FilmContract.FilmEntry.COLUMN_DIRECTOR))
                    film.producer = it.getString(it.getColumnIndex(FilmContract.FilmEntry.COLUMN_PRODUCER))
                    films.add(film)
                }
            }

            setupRecyclerAdapter()
        }
    }

    override fun onLoaderReset(loader: Loader<Cursor>?) {
        //If a cursor was not closed, now would be the time to close it.
    }
}
