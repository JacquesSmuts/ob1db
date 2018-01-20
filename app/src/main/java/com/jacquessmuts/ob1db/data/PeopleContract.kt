package com.jacquessmuts.ob1db.data


import android.net.Uri
import android.provider.BaseColumns

/**
 * Created by Jacques Smuts on 1/18/2018.
 */
/**
 * Defines table and column names for the film database. This class is not necessary, but keeps
 * the code organized.
 */
object PeopleContract {

    /*
     * The "Content authority" is a name for the entire content provider.
     */
    const val CONTENT_AUTHORITY = "com.jacquessmuts.starwarsdb"
    val BASE_CONTENT_URI: Uri = Uri.parse("content://" + CONTENT_AUTHORITY)
    const val PATH_PEOPLE = "people"

    /* Inner class that defines the table contents of the people table */
    class PersonEntry : BaseColumns {
        companion object {

            /* The base CONTENT_URI used to query the Weather table from the content provider */
            val CONTENT_URI: Uri = BASE_CONTENT_URI.buildUpon()
                    .appendPath(PATH_PEOPLE)
                    .build()

            /* Used internally as the name of our movies table. */
            const val TABLE_NAME = "people"

            const val COLUMN_NAME = "name"
            const val COLUMN_URL = "url"
            const val COLUMN_HEIGHT = "height"
//            const val COLUMN_MASS = "mass"
            //There are other columns, but they're unnecessary

            const val getAll = PersonEntry.COLUMN_NAME + " IS NOT NULL"

            fun getByUrl(url: String) : String {
                return PersonEntry.COLUMN_URL + " = " + url
            }

        }
    }
}