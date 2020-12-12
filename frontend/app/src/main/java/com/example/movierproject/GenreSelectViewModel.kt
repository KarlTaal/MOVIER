package com.example.movierproject

import android.app.Application
import android.util.Log
import android.widget.ArrayAdapter
import androidx.lifecycle.AndroidViewModel
import com.example.movierproject.entities.Genre
import com.koushikdutta.ion.Ion


class GenreSelectViewModel(application: Application) : AndroidViewModel(application) {
    companion object {
        var TAG = GenreSelectViewModel::class.java.name
    }


    var lastTheme = -10000 //inital value, -10000 means unset
    var proceedButtonIsEnabled = false
    lateinit var arrayAdapter: ArrayAdapter<Genre>


    init {
        Log.d(TAG, "ViewModel initialized")
        println("tere")
        Ion.with(application.baseContext)
            .load("GET", "https://api.themoviedb.org/3/genre/movie/list?")
            .addQuery("api_key", application.resources.getString(R.string.api_key))
            .addQuery("language", application.getString(R.string.languageQueryKey))
            .asJsonObject()
            .setCallback { e, result ->
                val genres = result["genres"].asJsonArray
                genres.forEach { genre ->
                    val id = genre.asJsonObject["id"].toString().toInt()
                    val name = genre.asJsonObject["name"].toString()
                    arrayAdapter.add(Genre(id, name.substring(1, name.length - 1)))
                }
            }
    }

}