package com.example.movierproject

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import kotlinx.android.synthetic.main.settings.*
import kotlinx.android.synthetic.main.start_menu.*

class SettingsActivity : AppCompatActivity() {
    companion object {
        var TAG = SettingsActivity::class.java.name
    }

    lateinit var preferences: SharedPreferences
    var lastTheme = -10000 //inital value, -10000 means unset


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferences = getSharedPreferences(getString(R.string.preferences_file), Context.MODE_PRIVATE)
        updateTheme() //has to be called between onCreate and setContent
        setContentView(R.layout.settings)
        setupThemeSwitch()
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
        } else{ //we need to call recreate but only when needed because otherwise it will go in infinite loop
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


    fun setupThemeSwitch() {
        val currentTheme = preferences.getString(getString(R.string.preferences_theme_key), getString(R.string.preferences_theme_light_value)) //default value is light
        theme_switch.isChecked = currentTheme == getString(R.string.preferences_theme_dark_value)
        theme_switch.setOnClickListener {
            run {
                val isDarkSelected = theme_switch.isChecked
                val editor: SharedPreferences.Editor = preferences.edit()
                if (isDarkSelected)
                    editor.putString(getString(R.string.preferences_theme_key), getString(R.string.preferences_theme_dark_value))
                else
                    editor.putString(getString(R.string.preferences_theme_key), getString(R.string.preferences_theme_light_value))
                editor.commit()
                updateTheme()
                //Toast.makeText(this, theme_switch.isChecked.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }

}
