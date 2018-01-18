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
object FilmContract {

    /*
     * The "Content authority" is a name for the entire content provider.
     */
    const val CONTENT_AUTHORITY = "com.jacquessmuts.starwarsdb"
    val BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY)

    /*
     * Possible paths that can be appended to BASE_CONTENT_URI to form valid URI's that the app
     * can handle. For instance,
     *
     *     content://com.example.android.sunshine/weather/
     *     [           BASE_CONTENT_URI         ][ PATH_FILM ]
     *
     * is a valid path for looking at weather data.
     *
     *      content://com.example.android.sunshine/givemeroot/
     *
     * will fail, as the ContentProvider hasn't been given any information on what to do with
     * "givemeroot". At least, let's hope not. Don't be that dev, reader. Don't be that dev.
     */
    const val PATH_FILM = "film"

    /* Inner class that defines the table contents of the weather table */
    class FilmEntry : BaseColumns {
        companion object {

            /* The base CONTENT_URI used to query the Weather table from the content provider */
            val CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                    .appendPath(PATH_FILM)
                    .build()

            /* Used internally as the name of our movies table. */
            const val TABLE_NAME = "films"

            const val COLUMN_FILM_ID = "episode_id"
            const val COLUMN_CHARACTERS = "characters"
            const val COLUMN_CREATED = "created"
            const val COLUMN_DIRECTOR = "director"
            const val COLUMN_EDITED = "edited"
            const val COLUMN_OPENING_CRAWL = "opening_crawl"
            const val COLUMN_PLANETS = "planets"
            const val COLUMN_PRODUCER = "producer"
            const val COLUMN_RELEASE_DATE = "release_date"
            const val COLUMN_SPECIES = "species"
            const val COLUMN_STARSHIPS = "starships"
            const val COLUMN_TITLE = "title"
            const val COLUMN_URL = "url"
            const val COLUMN_VEHICLES = "vehicles"
        }

        //        public static String getAll() {
        //            return MovieEntry.COLUMN_IS_FAVORITE + " >= 1";
        //        }

        //        public static String getById(int id){
        //            return MovieEntry.COLUMN_MOVIE_ID + " = " + id;
        //        }
    }
}