package com.jacquessmuts.ob1db.network


import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.InputStream

/**
 * Created by Jacques Smuts on 1/18/2018.
 */
class NetworkClient {

    companion object {
        const val BASE_API = "https://swapi.co/api/";
        const val GET_ALL_FILMS = BASE_API+ "films";
    }

    fun get(url: String): InputStream {
        val request = Request.Builder().url(url).build()
        val response = OkHttpClient().newCall(request).execute()
        val body = response.body()
        // body.toString() returns a string representing the object and not the body itself, probably
        // kotlins fault when using third party libraries. Use byteStream() and convert it to a String
        return body!!.byteStream()
    }

}