package com.example.movierproject


import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import kotlinx.android.synthetic.main.match.*
import kotlinx.android.synthetic.main.movie_details.*
import kotlinx.android.synthetic.main.movie_selecting.*

class MatchActivity : AppCompatActivity() {
    companion object {
        var TAG = MatchActivity::class.java.name
    }

    var lastTheme = -10000 //inital value, -10000 means unset
    lateinit var preferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferences = getSharedPreferences(getString(R.string.preferences_file), Context.MODE_PRIVATE)

        updateTheme() //has to be called between onCreate and setContent
        setContentView(R.layout.match)
        updateLanguage()

        setupBackToMenuClickHandler()
        setupFragmentContent()
        movie_overview.movementMethod = ScrollingMovementMethod()
    }

    fun updateLanguage() {
        val prefLang = preferences.getString(getString(R.string.preferences_language_key), getString(R.string.preferences_language_english_value))

        //labels
        if (prefLang == getString(R.string.preferences_language_english_value)) {
            match_title.text = getString(R.string.english_its_a_match)
            back_to_start_menu_btn.text = getString(R.string.english_back_to_start_menu)
        }
        if (prefLang == getString(R.string.preferences_language_russian_value)) {
            match_title.text = getString(R.string.russian_its_a_match)
            back_to_start_menu_btn.text = getString(R.string.russian_back_to_start_menu)
        }
        if (prefLang == getString(R.string.preferences_language_finnish_value)) {
            match_title.text = getString(R.string.finnish_its_a_match)
            back_to_start_menu_btn.text = getString(R.string.finnish_back_to_start_menu)
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
        updateLanguage()
        updateTheme()
    }


    fun setupFragmentContent(){
        val title = intent.getStringExtra("title") as String
        val overview = intent.getStringExtra("overview") as String
        val rating = intent.getIntExtra("rating", 0)

        movie_title.text = title.substring(1, title.length-1)
        movie_rate.max = 100
        movie_rate.isClickable = false
        movie_rate.progress = rating
        movie_overview.text = overview.substring(1, overview.length-1)

    }

    fun setupBackToMenuClickHandler(){
        back_to_start_menu_btn.setOnClickListener {
            run {
                val intent = Intent(this, StartMenuActivity::class.java)
                startActivity(intent)
            }
        }
    }
}
