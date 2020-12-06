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
        languageUpdate()
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

    fun languageUpdate() {
        //genre selections
        val prefLang = preferences.getString(getString(R.string.preferences_language_key), getString(R.string.preferences_language_english_value))

        //labels
        if (prefLang == getString(R.string.preferences_language_english_value)){
            dark_mode_header.text = getString(R.string.english_dark_mode)
            dark_mode_desc.text = getString(R.string.english_switch_to_dark_mode_if_you_want)
            lang_mode_desc.text = getString(R.string.english_language)
            lang_mode_header.text = getString(R.string.english_select_language_from_dropdown)
        }
        if (prefLang == getString(R.string.preferences_language_russian_value)){
            dark_mode_header.text = getString(R.string.russian_dark_mode)
            dark_mode_desc.text = getString(R.string.russian_switch_to_dark_mode_if_you_want)
            lang_mode_desc.text = getString(R.string.russian_language)
            lang_mode_header.text = getString(R.string.russian_select_language_from_dropdown)
        }
        if (prefLang == getString(R.string.preferences_language_finnish_value)){
            dark_mode_header.text = getString(R.string.finnish_dark_mode)
            dark_mode_desc.text = getString(R.string.finnish_switch_to_dark_mode_if_you_want)
            lang_mode_desc.text = getString(R.string.finnish_language)
            lang_mode_header.text = getString(R.string.finnish_select_language_from_dropdown)
        }
        setupLanguageSelector()
    }

    fun setupLanguageSelector() {
        val prefLang = preferences.getString(getString(R.string.preferences_language_key), getString(R.string.preferences_language_english_value))
        var options = arrayListOf<String>()
        if (prefLang == getString(R.string.preferences_language_english_value))
            options = arrayListOf(getString(R.string.english_english), getString(R.string.english_russian), getString(R.string.english_finnish))
        if (prefLang == getString(R.string.preferences_language_russian_value))
            options = arrayListOf(getString(R.string.russian_english), getString(R.string.russian_russian), getString(R.string.russian_finnish))
        if (prefLang == getString(R.string.preferences_language_finnish_value))
            options = arrayListOf(getString(R.string.finnish_english), getString(R.string.finnish_russian), getString(R.string.finnish_finnish))

        val aa = ArrayAdapter(this, android.R.layout.simple_spinner_item, options)
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        language_selector.adapter = aa

        val currentLanguage = preferences.getString(getString(R.string.preferences_language_key), getString(R.string.preferences_language_english_value)) //default value is light
        if (currentLanguage == getString(R.string.preferences_language_english_value))
            language_selector.setSelection(0)
        else if (currentLanguage == getString(R.string.preferences_language_russian_value))
            language_selector.setSelection(1)
        else if (currentLanguage == getString(R.string.preferences_language_finnish_value))
            language_selector.setSelection(2)

        language_selector.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val editor: SharedPreferences.Editor = preferences.edit()
                if (id.toInt() == 0)
                    editor.putString(getString(R.string.preferences_language_key), getString(R.string.preferences_language_english_value))
                else if (id.toInt() == 1)
                    editor.putString(getString(R.string.preferences_language_key), getString(R.string.preferences_language_russian_value))
                else if (id.toInt() == 2)
                    editor.putString(getString(R.string.preferences_language_key), getString(R.string.preferences_language_finnish_value))
                editor.commit()
                languageUpdate()
                //Toast.makeText(baseContext, languageOptions[id.toInt()], Toast.LENGTH_SHORT).show()
            }
        }

    }

}
