package com.example.movierproject


import android.content.*
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.movierproject.entities.Movie
import com.example.movierproject.fragments.OutOfMoviesFragment
import com.example.movierproject.models.MovieSelectingViewModel
import com.example.movierproject.services.MessagingService.Companion.INTENT_ACTION_SEND_MESSAGE
import com.koushikdutta.ion.Ion
import kotlinx.android.synthetic.main.movie_details.*
import kotlinx.android.synthetic.main.movie_selecting.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.URL


class MovieSelectingActivity : AppCompatActivity() {
    companion object {
        var TAG = MovieSelectingActivity::class.java.name
    }
    private lateinit var model: MovieSelectingViewModel
    var lastTheme = -10000 //inital value, -10000 means unset
    lateinit var preferences: SharedPreferences
    private lateinit var roomId: String
    private lateinit var genres: String
    private lateinit var receiver: BroadcastReceiver


    val moviesList: MutableList<Movie> = mutableListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferences = getSharedPreferences(getString(R.string.preferences_file), Context.MODE_PRIVATE)
        updateTheme() //has to be called between onCreate and setContent
        model = ViewModelProvider(this).get(MovieSelectingViewModel::class.java)

        roomId = intent.getStringExtra("roomId").toString()

        setContentView(R.layout.movie_selecting)

        getGenres()

        receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent) {
                val movieId = intent.getStringExtra("movieId")
                handleMatch(movieId!!)
            }
        }

    }


    fun updateTheme() {
        val prefTheme = preferences.getString(getString(R.string.preferences_theme_key), getString(R.string.preferences_theme_light_value))
        if (lastTheme == -10000) { //when update is called from onCreate
            if (prefTheme == getString(R.string.preferences_theme_light_value)) {
                setTheme(R.style.AppThemeLight)
                lastTheme = R.style.AppThemeLight
            }
            if (prefTheme == getString(R.string.preferences_theme_dark_value)) {
                setTheme(R.style.AppThemeDark)
                lastTheme = R.style.AppThemeDark
            }
        } else { //we need to call recreate but only when needed because otherwise it will go in infinite loop
            if (prefTheme == getString(R.string.preferences_theme_light_value) && lastTheme != R.style.AppThemeLight) {
                setTheme(R.style.AppThemeLight)
                lastTheme = R.style.AppThemeLight
                this.recreate()
            }
            if (prefTheme == getString(R.string.preferences_theme_dark_value) && lastTheme != R.style.AppThemeDark) {
                setTheme(R.style.AppThemeDark)
                lastTheme = R.style.AppThemeDark
                this.recreate()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        updateTheme()
        val filter = IntentFilter(INTENT_ACTION_SEND_MESSAGE)
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter)
    }

    override fun onPause() {
        super.onPause()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver)
    }

    fun getMovies() {
        Ion.with(this)
            .load("GET", "https://api.themoviedb.org/3/discover/movie?")
            .addQuery("api_key", resources.getString(R.string.api_key))
            .addQuery("with_genres", genres)
            .addQuery("language", getString(R.string.languageQueryKey))
            .asJsonObject()
            .setCallback { e, result ->
                val movies = result["results"].asJsonArray
                for (i in 0 until movies.size()) {
                    val movie = Movie(
                        movies[i].asJsonObject["id"].toString(),
                        movies[i].asJsonObject["title"].toString(),
                        movies[i].asJsonObject["vote_average"].toString().toDouble(),
                        movies[i].asJsonObject["overview"].toString(),
                        movies[i].asJsonObject["poster_path"].toString()
                    )
                    moviesList.add(movie)
                }
                displayMovie()
                like_button.setOnClickListener {
                    run {
                        likeClick()
                    }
                }
                dislike_button.setOnClickListener {
                    run {
                        dislikeClick()
                    }
                }
            }
    }

    fun getGenres() {
        val address = getString(R.string.address)
        val URI = getString(R.string.uri, address) + "/$roomId/genres"
        Ion.with(this)
            .load("GET", URI)
            .asJsonObject()
            .setCallback { e, result ->
                genres = result["genres"].asString
                getMovies()
            }
    }

    fun displayMovie(){
        if (model.currentMovieIndex < moviesList.size){
            val title = moviesList[model.currentMovieIndex].title
            val overview = moviesList[model.currentMovieIndex].overview
            val rating = ((moviesList[model.currentMovieIndex].voteAverage ?: 0.1) * 10).toInt() //max value is set to 100 in progress bar, so we convert it to the same scale
            movie_title.text = title.substring(1, title.length-1)
            movie_rate.max = 100
            movie_rate.isClickable = false
            movie_rate.progress = rating
            movie_overview.text = overview.substring(1, overview.length-1)
            getAndSetMoviePoster(moviesList[model.currentMovieIndex].posterPath.toString())
        } else{
            showOutOfMoviesFragment()
            dislike_button.isEnabled = false
            like_button.isEnabled = false
            dislike_button.alpha = 0.3f
            like_button.alpha = 0.3f
        }
    }

    fun showOutOfMoviesFragment(){
        val outOfMovies = OutOfMoviesFragment()
        val toReplace = R.id.fragment_container
        val fragmentManager = supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        transaction
            .replace(toReplace, outOfMovies)
            .commit()
    }

    fun getAndSetMoviePoster(posterPath: String){
        val scope = CoroutineScope( Dispatchers.Default)
        scope.launch {
            val url = URL(getString(R.string.poster_request_path, posterPath.substring(1, posterPath.length-1)))
            val fullBitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream())
            val ratio = fullBitmap.width.toDouble() / fullBitmap.height
            val scaledBitmap = Bitmap.createScaledBitmap(fullBitmap, (200*ratio).toInt(), 200, false)
            runOnUiThread {
                movie_poster.setImageBitmap(scaledBitmap)
            }
        }
    }

    fun dislikeClick(){
        model.currentMovieIndex += 1
        displayMovie()
    }

    fun likeClick() {
        val address = getString(R.string.address)
        if (model.currentMovieIndex < moviesList.size){
            val URI = getString(R.string.uri, address) + "/$roomId/like/${moviesList[model.currentMovieIndex].id}"
            model.currentMovieIndex += 1
            Ion.with(this)
                .load("POST", URI)
                .asJsonObject()
                .setCallback { e, result ->
                    if (!result["match"].asBoolean) {
                        displayMovie()
                    }
                }
        }else{
            displayMovie()
        }
    }

    fun handleMatch(movieId: String){
        val intent = Intent(this, MatchActivity::class.java)
        val film = moviesList.find {
            it.id == movieId
        }

        intent.putExtra("title", film?.title)
        intent.putExtra("rating",((film?.voteAverage ?: 0.1)* 10).toInt()) //55 means 5.5/10 in means of rating
        intent.putExtra("overview", film?.overview)
        intent.putExtra("posterPath", film?.posterPath)
        startActivity(intent)
    }

}
