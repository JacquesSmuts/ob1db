package com.jacquessmuts.ob1db.adapters

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.jacquessmuts.ob1db.FilmDetailFragment
import com.jacquessmuts.ob1db.R
import com.jacquessmuts.ob1db.activities.FilmDetailActivity
import com.jacquessmuts.ob1db.activities.FilmListActivity
import com.jacquessmuts.ob1db.dummy.DummyContent
import com.jacquessmuts.ob1db.models.Film
import kotlinx.android.synthetic.main.film_list_content.view.*

/**
 * Created by Jacques Smuts on 1/19/2018.
 */
class FilmListRecyclerViewAdapter(private val mParentActivity: FilmListActivity,
                                  private val mValues: List<Film>,
                                  private val mTwoPane: Boolean) :
        RecyclerView.Adapter<FilmListRecyclerViewAdapter.ViewHolder>() {

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