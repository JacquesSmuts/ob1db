package com.jacquessmuts.ob1db.models

import com.google.gson.annotations.SerializedName

class Film {

    @SerializedName("characters")
    var characters: List<String>? = null
    @SerializedName("created")
    var created: String? = null
    @SerializedName("director")
    var director: String? = null
    @SerializedName("edited")
    var edited: String? = null
    @SerializedName("episode_id")
    var episodeId: Long? = null
    @SerializedName("opening_crawl")
    var openingCrawl: String? = null
    @SerializedName("planets")
    var planets: List<String>? = null
    @SerializedName("producer")
    var producer: String? = null
    @SerializedName("release_date")
    var releaseDate: String? = null
    @SerializedName("species")
    var species: List<String>? = null
    @SerializedName("starships")
    var starships: List<String>? = null
    @SerializedName("title")
    var title: String? = null
    @SerializedName("url")
    var url: String? = null
    @SerializedName("vehicles")
    var vehicles: List<String>? = null

}
