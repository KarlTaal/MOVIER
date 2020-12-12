package com.example.movierproject

import android.app.Application
import androidx.lifecycle.AndroidViewModel


class MovieSelectingViewModel(application: Application) : AndroidViewModel(application) {
    var currentMovieIndex = 0
    companion object {
        var TAG = MovieSelectingViewModel::class.java.name
    }
}