package com.example.movierproject

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Menu
import android.view.MenuItem
import android.view.animation.AnimationUtils
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.koushikdutta.ion.Ion
import kotlinx.android.synthetic.main.start_menu.*
import java.util.*
import kotlin.properties.Delegates


class StartMenuActivity : AppCompatActivity() {
    companion object {
        var TAG = StartMenuActivity::class.java.name
    }

    lateinit var preferences: SharedPreferences

    var roomId = 0
    var genreQueryLanguage: String = "en-US"
    var lastTheme = -10000 //inital value, -10000 means unset

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferences =
            getSharedPreferences(getString(R.string.preferences_file), Context.MODE_PRIVATE)
        preferencesSetupOnFirstRun()

        updateTheme() //has to be called between onCreate and setContent
        setContentView(R.layout.start_menu)

        setupButtons()
        setupGenreSelector()
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayShowTitleEnabled(false)

        updateLanguage()
    }

    fun updateTheme() {
        val prefTheme = preferences.getString("theme", "light")
        if (lastTheme == -10000) { //when update is called from onCreate
            if (prefTheme == "light") {
                setTheme(R.style.AppThemeLight)
                lastTheme = R.style.AppThemeLight
            }
            if (prefTheme == "dark") {
                setTheme(R.style.AppThemeDark)
                lastTheme = R.style.AppThemeDark
            }
        } else{ //we need to call recreate but only when needed because otherwise it will go in infinite loop
            if (prefTheme == "light" && lastTheme != R.style.AppThemeLight) {
                setTheme(R.style.AppThemeLight)
                lastTheme = R.style.AppThemeLight
                this.recreate()
            }
            if (prefTheme == "dark" && lastTheme != R.style.AppThemeDark) {
                setTheme(R.style.AppThemeDark)
                lastTheme = R.style.AppThemeDark
                this.recreate()
            }
        }
    }

    fun updateLanguage() {
        //genre selections
        val prefLang = preferences.getString("language", "english")
        if (prefLang == "english")
            genreQueryLanguage = "en-US"
        if (prefLang == "russian")
            genreQueryLanguage = "ru"
        setupGenreSelector()

        //labels
        if (prefLang == "english") {
            main_menu_header.text = getString(R.string.english_movier)
            start_session_btn.text = getString(R.string.english_start_new_session)
            main_menu_or.text = getString(R.string.english_or)
            join_session_btn.text = getString(R.string.english_join_session)
            session_key_input.hint = getString(R.string.english_session_key)
        }
        if (prefLang == "russian") {
            main_menu_header.text = getString(R.string.russian_movier)
            start_session_btn.text = getString(R.string.russian_start_new_session)
            main_menu_or.text = getString(R.string.russian_or)
            join_session_btn.text = getString(R.string.russian_join_session)
            session_key_input.hint = getString(R.string.russian_session_key)
        }

    }

    override fun onResume() { //we have to update in onResume, because onCreate is not called when we hit back button
        super.onResume()
        updateLanguage()
        updateTheme()
    }

    fun preferencesSetupOnFirstRun() {
        val editor: SharedPreferences.Editor = preferences.edit()
        if (!preferences.contains("theme")) {
            //Toast.makeText(this, "theme setup", Toast.LENGTH_SHORT).show()
            editor.putString("theme", "light")
        }
        if (!preferences.contains("language")) {
            //Toast.makeText(this, "language setup", Toast.LENGTH_SHORT).show()
            editor.putString("language", "english")
        }
        editor.commit()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.option_help -> {
                val intent = Intent(this, HelpActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.option_settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }


    fun setupButtons() {
        start_session_btn.setOnClickListener {
            run {
                Thread {
                    startNewRoom()
                }
                switchToMovieSelectingActivity()
            }
        }

        join_session_btn.setOnClickListener {
            run {
                handleJoinClick()
            }
        }
    }

    fun handleJoinClick() {
        val key = session_key_input.text.toString()
        val regex = "#[0-9]{5}".toRegex()
        if (!regex.matches(key)) {
            val animShake = AnimationUtils.loadAnimation(this, R.anim.shake)
            session_key_input.startAnimation(animShake)
        } else {
            switchToMovieSelectingActivity()
        }
    }

    fun setupGenreSelector() {
        Ion.with(this)
            .load("GET", "https://api.themoviedb.org/3/genre/movie/list?")
            .addQuery("api_key", resources.getString(R.string.api_key))
            .addQuery("language", genreQueryLanguage)
            .asJsonObject()
            .setCallback { e, result ->
                val genres = result["genres"].asJsonArray
                val genreArray = mutableListOf<String>()
                genres.forEach { genre ->
                    val name = genre.asJsonObject["name"].toString()
                    genreArray.add(name.substring(1, name.length - 1))
                }
                val aa = ArrayAdapter(this, android.R.layout.simple_spinner_item, genreArray)
                aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                genre_select.adapter = aa
            }
    }

    fun switchToMovieSelectingActivity() {
        val intent = Intent(this, MovieSelectingActivity::class.java)
        startActivity(intent)
    }

    @SuppressLint("StringFormatMatches")
    private fun startNewRoom() {
        val address = R.string.address
        Ion.with(this)
            .load("POST", getString(R.string.create_room, address))
            .asJsonObject()
            .setCallback { e, result ->
                roomId = result["roomId"].asInt
            }
    }

}
