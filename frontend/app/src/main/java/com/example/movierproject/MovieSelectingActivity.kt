package com.example.movierproject


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.koushikdutta.ion.Ion
import kotlinx.android.synthetic.main.movie_details.*
import kotlinx.android.synthetic.main.movie_selecting.*


class MovieSelectingActivity : AppCompatActivity() {
    companion object {
        var TAG = MovieSelectingActivity::class.java.name
    }

    val moviesList: MutableList<HashMap<String, String>> = mutableListOf()
    var currentMovieIndex = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.movie_selecting)
        getMovies()
    }

    fun getMovies() {
        Ion.with(this)
            .load("GET", "https://api.themoviedb.org/3/discover/movie?")
            .addQuery("api_key", resources.getString(R.string.api_key))
            .addQuery("with_genres", "28")
            .asJsonObject()
            .setCallback { e, result ->
                val movies = result["results"].asJsonArray
                for (i in 1..3) {
                    moviesList.add(hashMapOf(
                        "title" to movies[i].asJsonObject["title"].toString(),
                        "vote_average" to movies[i].asJsonObject["vote_average"].toString(),
                        "overview" to movies[i].asJsonObject["overview"].toString()
                    ))
                }
                displayMovie()
                like_button.setOnClickListener { run{handleLikeClick()} }
                dislike_button.setOnClickListener { run{handleLikeClick()} }
            }
    }

    fun displayMovie(){
        val title = moviesList[currentMovieIndex]["title"].toString()
        val overview = moviesList[currentMovieIndex]["overview"].toString()
        val rating = ((moviesList[currentMovieIndex]["vote_average"]?.toDouble() ?: 0.1) * 10).toInt() //max value is set to 100 in progress bar, so we convert it to the same scale
        Log.i(TAG, rating.toString())
        movie_title.text = title.substring(1, title.length-1)
        movie_rate.max = 100
        movie_rate.isClickable = false
        movie_rate.progress = rating
        movie_overview.text = overview.substring(1, overview.length-1)
    }

    fun handleLikeClick(){
        currentMovieIndex += 1
        if (currentMovieIndex < moviesList.size)
            displayMovie()
        else
            handleMatch()
    }

    fun handleMatch(){
        val intent = Intent(this, MatchActivity::class.java)
        intent.putExtra("title"," Dummy Name ")
        intent.putExtra("rating",55) //55 means 5.5/10 in means of rating
        intent.putExtra("overview"," There are many variations of passages of Lorem Ipsum available, but the majority have suffered alteration in some form, by injected humour, or randomised words which don't look even slightly believable. If you are going to use a passage of Lorem Ipsum, you need to be sure there isn't anything embarrassing hidden in the middle of text. All the Lorem Ipsum generators on the Internet tend to repeat predefined chunks as necessary, making this the first true generator on the Internet. It uses a dictionary of over 200 Latin words, combined with a handful of model sentence structures, to generate Lorem Ipsum which looks reasonable. The generated Lorem Ipsum is therefore always free from repetition, injected humour, or non-characteristic words etc. ")
        startActivity(intent)
    }

}