package com.jacquessmuts.ob1db.activities

import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.support.design.widget.Snackbar
import android.support.v4.app.LoaderManager
import android.support.v4.content.CursorLoader
import android.support.v4.content.Loader
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.jacquessmuts.ob1db.FilmDetailFragment
import com.jacquessmuts.ob1db.R
import com.jacquessmuts.ob1db.data.FilmContract

import com.jacquessmuts.ob1db.dummy.DummyContent
import com.jacquessmuts.ob1db.models.Film
import kotlinx.android.synthetic.main.activity_film_list.*
import kotlinx.android.synthetic.main.film_list_content.view.*

import kotlinx.android.synthetic.main.film_list.*

/**
 * An activity representing a list of Pings. This activity
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
        val ID_FILM_LIST_LOADER = 94

        /*
    * The columns of data that we are interested in displaying within the list
    */
        val MAIN_MOVIES_PROJECTION = arrayOf<String>(
                FilmContract.FilmEntry.COLUMN_FILM_ID,
                FilmContract.FilmEntry.COLUMN_TITLE,
                FilmContract.FilmEntry.COLUMN_RELEASE_DATE,
                FilmContract.FilmEntry.COLUMN_DIRECTOR,
                FilmContract.FilmEntry.COLUMN_PRODUCER)

        fun getIntent(context: Context) : Intent{
            var intent = Intent(context, FilmListActivity::class.java)
            //intent.putExtra("key", value)
            return intent;
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_film_list)

        setSupportActionBar(toolbar)
        toolbar.title = title

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }

        if (film_detail_container != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true
        }

        setupRecyclerView()
        supportLoaderManager.initLoader(ID_FILM_LIST_LOADER, null, this)
    }

    private fun setupRecyclerView() {
        film_list.adapter = SimpleItemRecyclerViewAdapter(this, films, mTwoPane)
    }

    class SimpleItemRecyclerViewAdapter(private val mParentActivity: FilmListActivity,
                                        private val mValues: List<Film>,
                                        private val mTwoPane: Boolean) :
            RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder>() {

        private val mOnClickListener: View.OnClickListener

        init {
            mOnClickListener = View.OnClickListener { v ->
                val item = v.tag as DummyContent.DummyItem
                if (mTwoPane) {
                    val fragment = FilmDetailFragment().apply {
                        arguments = Bundle().apply {
                            putString(FilmDetailFragment.ARG_ITEM_ID, item.id)
                        }
                    }
                    mParentActivity.supportFragmentManager
                            .beginTransaction()
                            .replace(R.id.film_detail_container, fragment)
                            .commit()
                } else {
                    val intent = Intent(v.context, FilmDetailActivity::class.java).apply {
                        putExtra(FilmDetailFragment.ARG_ITEM_ID, item.id)
                    }
                    v.context.startActivity(intent)
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.film_list_content, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = mValues[position]
            holder.mIdView.text = item.title
            holder.mContentView.text = item.director

            with(holder.itemView) {
                tag = item
                setOnClickListener(mOnClickListener)
            }
        }

        override fun getItemCount(): Int {
            return mValues.size
        }

        inner class ViewHolder(mView: View) : RecyclerView.ViewHolder(mView) {
            val mIdView: TextView = mView.id_text
            val mContentView: TextView = mView.content
        }
    }

    /**
     * DB FUNCTIONS
     */
    override fun onCreateLoader(loaderId: Int, args: Bundle?): Loader<Cursor>? {
        when (loaderId) {

             ID_FILM_LIST_LOADER -> {
                val queryUri = FilmContract.FilmEntry.CONTENT_URI

                val selection = FilmContract.FilmEntry.getAll

                return CursorLoader(
                        this@FilmListActivity,
                        queryUri,
                        MAIN_MOVIES_PROJECTION,
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
            films = mutableListOf()
            try {
                while (data.moveToNext()) {
                    var film = Film()
                    film.episodeId = data.getLong(data!!.getColumnIndex(FilmContract.FilmEntry.COLUMN_FILM_ID))
                    film.title = data.getString(data!!.getColumnIndex(FilmContract.FilmEntry.COLUMN_TITLE))
                    film.releaseDate = data.getString(data!!.getColumnIndex(FilmContract.FilmEntry.COLUMN_RELEASE_DATE))
                    film.director = data.getString(data!!.getColumnIndex(FilmContract.FilmEntry.COLUMN_DIRECTOR))
                    film.producer = data.getString(data!!.getColumnIndex(FilmContract.FilmEntry.COLUMN_PRODUCER))
                    films.add(film)
                }
            } finally {
                data.close()
            }

            setupRecyclerView()
        }
    }

    override fun onLoaderReset(p0: Loader<Cursor>?) {

    }
}
