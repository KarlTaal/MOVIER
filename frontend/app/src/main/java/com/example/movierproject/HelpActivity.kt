package com.example.movierproject

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.help.*
import kotlinx.android.synthetic.main.match.*

class HelpActivity : AppCompatActivity() {
    companion object {
        var TAG = HelpActivity::class.java.name
    }
    var lastTheme = -10000 //inital value, -10000 means unset
    lateinit var preferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferences = getSharedPreferences(getString(R.string.preferences_file), Context.MODE_PRIVATE)
        updateTheme() //has to be called between onCreate and setContent
        setContentView(R.layout.help)
        updateLanguage()
    }

    fun updateLanguage() {
        val prefLang = preferences.getString(getString(R.string.preferences_language_key), getString(R.string.preferences_language_english_value))

        //labels
        if (prefLang == getString(R.string.preferences_language_english_value)) {
            dev_info.text = getString(R.string.english_dev_info)
        }
        if (prefLang == getString(R.string.preferences_language_russian_value)) {
            dev_info.text = getString(R.string.russian_dev_info)
        }
        if (prefLang == getString(R.string.preferences_language_finnish_value)) {
            dev_info.text = getString(R.string.finnish_dev_info)
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


}
