package com.jacquessmuts.ob1db

import android.database.Cursor
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.LoaderManager
import android.support.v4.content.CursorLoader
import android.support.v4.content.Loader
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jacquessmuts.ob1db.activities.FilmListActivity
import com.jacquessmuts.ob1db.data.FilmContract
import com.jacquessmuts.ob1db.data.FilmDbHelper
import com.jacquessmuts.ob1db.models.Film
import kotlinx.android.synthetic.main.activity_film_detail.*
import kotlinx.android.synthetic.main.film_detail.*

/**
 * A fragment representing a single Film detail screen.
 * This fragment is either contained in a [FilmListActivity]
 * in two-pane mode (on tablets) or a [FilmDetailActivity]
 * on handsets.
 */
class FilmDetailFragment : Fragment() , LoaderManager.LoaderCallbacks<Cursor>{

    /**
     * The dummy content this fragment is presenting.
     */
    private var mFilm: Film? = null
    private var mFilmId: Long = 0

    companion object {
        /**
         * The fragment argument representing the item ID that this fragment
         * represents.
         */
        const val EXTRA_FILM_ID = "film_id"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            if (it.containsKey(EXTRA_FILM_ID)) {
                // Load the dummy content specified by the fragment
                // arguments. In a real-world scenario, use a Loader
                // to load content from a content provider.
                mFilmId = it.getLong(EXTRA_FILM_ID)
                activity?.toolbar_layout?.title = mFilm?.title
            }
        }
        activity.supportLoaderManager.initLoader(FilmListActivity.ID_FILM_LIST_LOADER, null, this)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.film_detail, container, false)

        return rootView
    }

    fun showFilmDetails(){
        activity?.toolbar_layout?.title = mFilm?.title
        film_detail.text = mFilm?.openingCrawl;
    }

    /**
     * DB INTERFACE FUNCTIONS
     */
    override fun onCreateLoader(loaderId: Int, args: Bundle?): Loader<Cursor>? {
        when (loaderId) {

            FilmListActivity.ID_FILM_LIST_LOADER -> {
                val queryUri = FilmContract.FilmEntry.CONTENT_URI

                val selection = FilmContract.FilmEntry.getById(mFilmId)

                return CursorLoader(
                        this@FilmDetailFragment.context,
                        queryUri,
                        FilmDbHelper.FILM_DETAILS_PROJECTION,
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
            //use is the same as saying try{}finally{close()}
            data.use {
                while (it.moveToNext()) {
                    val film = Film()
                    film.episodeId = it.getLong(it.getColumnIndex(FilmContract.FilmEntry.COLUMN_FILM_ID))
                    film.title = it.getString(it.getColumnIndex(FilmContract.FilmEntry.COLUMN_TITLE))
                    film.releaseDate = it.getString(it.getColumnIndex(FilmContract.FilmEntry.COLUMN_RELEASE_DATE))
                    film.openingCrawl = it.getString(it.getColumnIndex(FilmContract.FilmEntry.COLUMN_OPENING_CRAWL))
                    //film.characters = it.getString(it.getColumnIndex(FilmContract.FilmEntry.COLUMN_CHARACTERS))
                    if(film.episodeId!! > 0){
                        mFilm = film;
                    }
                }
            }

            showFilmDetails();
        }
    }

    override fun onLoaderReset(loader: Loader<Cursor>?) {
        //If a cursor was not closed, now would be the time to close it.
    }
}
