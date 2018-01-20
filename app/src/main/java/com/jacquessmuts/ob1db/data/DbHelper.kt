package com.jacquessmuts.ob1db.data

/**
 * Created by Jacques Smuts on 1/18/2018.
 */


import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * Manages a local database for star wars data.
 */
class DbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {


    companion object {

        val DATABASE_NAME = "swapifilm.db"

        /*
         * If you change the database schema, you must increment the database version or the onUpgrade
         * method will not be called.
         */
        private val DATABASE_VERSION = 3

        /*
        * The columns of data that we are interested in displaying within the list
        */
        val FILMS_LIST_PROJECTION: Array<String>
            get() = arrayOf(
                    FilmContract.FilmEntry.COLUMN_FILM_ID,
                    FilmContract.FilmEntry.COLUMN_TITLE,
                    FilmContract.FilmEntry.COLUMN_RELEASE_DATE,
                    FilmContract.FilmEntry.COLUMN_DIRECTOR,
                    FilmContract.FilmEntry.COLUMN_PRODUCER)


        val FILM_DETAILS_PROJECTION: Array<String>
            get() = arrayOf(
                    FilmContract.FilmEntry.COLUMN_FILM_ID,
                    FilmContract.FilmEntry.COLUMN_TITLE,
                    FilmContract.FilmEntry.COLUMN_RELEASE_DATE,
                    FilmContract.FilmEntry.COLUMN_CHARACTERS,
                    FilmContract.FilmEntry.COLUMN_OPENING_CRAWL)

        val PEOPLE_PROJECTION: Array<String>
            get() = arrayOf(
                    PeopleContract.PersonEntry.COLUMN_NAME,
                    PeopleContract.PersonEntry.COLUMN_URL)
    }

    /**
     * @param sqLiteDatabase The database.
     */
    override fun onCreate(sqLiteDatabase: SQLiteDatabase) {

        /*
         * This String will contain a simple SQL statement that will create a table that will
         * cache our weather data.
         */
        val SQL_CREATE_FILMS_TABLE =

                "CREATE TABLE " + FilmContract.FilmEntry.TABLE_NAME + " (" +
                        FilmContract.FilmEntry.COLUMN_FILM_ID + " INTEGER PRIMARY KEY NOT NULL ON CONFLICT REPLACE, " +
                        FilmContract.FilmEntry.COLUMN_CHARACTERS + " TEXT, " +
                        FilmContract.FilmEntry.COLUMN_CREATED + " TEXT," +
                        FilmContract.FilmEntry.COLUMN_DIRECTOR + " TEXT, " +
                        FilmContract.FilmEntry.COLUMN_EDITED + " TEXT, " +
                        FilmContract.FilmEntry.COLUMN_OPENING_CRAWL + " TEXT, " +
                        FilmContract.FilmEntry.COLUMN_PLANETS + " TEXT, " +
                        FilmContract.FilmEntry.COLUMN_PRODUCER + " TEXT, " +
                        FilmContract.FilmEntry.COLUMN_RELEASE_DATE + " TEXT," +
                        FilmContract.FilmEntry.COLUMN_SPECIES + " TEXT," +
                        FilmContract.FilmEntry.COLUMN_STARSHIPS + " TEXT, " +
                        FilmContract.FilmEntry.COLUMN_TITLE + " TEXT, " +
                        FilmContract.FilmEntry.COLUMN_URL + " TEXT, " +
                        FilmContract.FilmEntry.COLUMN_VEHICLES + " TEXT);"

        val SQL_CREATE_PEOPLE_TABLE =
                "CREATE TABLE " + PeopleContract.PersonEntry.TABLE_NAME + " (" +
                        PeopleContract.PersonEntry.COLUMN_URL + " TEXT PRIMARY KEY NOT NULL ON CONFLICT REPLACE, " +
                        PeopleContract.PersonEntry.COLUMN_NAME + " TEXT, " +
                        PeopleContract.PersonEntry.COLUMN_HEIGHT + " TEXT);"

        sqLiteDatabase.execSQL(SQL_CREATE_FILMS_TABLE)
        sqLiteDatabase.execSQL(SQL_CREATE_PEOPLE_TABLE)
    }

    /**
     * This database is only a cache for online data, so its upgrade policy is simply to discard
     * the data and call through to onCreate to recreate the table. Note that this only fires if
     * you change the version number for your database (in our case, DATABASE_VERSION). It does NOT
     * depend on the version number for your application found in your app/build.gradle file. If
     * you want to update the schema without wiping data, commenting out the current body of this
     * method should be your top priority before modifying this method.
     *
     * @param sqLiteDatabase Database that is being upgraded
     * @param oldVersion     The old database version
     * @param newVersion     The new database version
     */
    override fun onUpgrade(sqLiteDatabase: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FilmContract.FilmEntry.TABLE_NAME)
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + PeopleContract.PersonEntry.TABLE_NAME)
        onCreate(sqLiteDatabase)
    }
}
