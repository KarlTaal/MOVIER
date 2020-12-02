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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferences =
            getSharedPreferences(getString(R.string.preferences_file), Context.MODE_PRIVATE)
        setContentView(R.layout.settings)
        setupThemeSwitch()
        languageUpdate()
    }


    fun setupThemeSwitch() {
        val currentTheme = preferences.getString("theme", "light") //default value is light
        theme_switch.isChecked = currentTheme == "dark"
        theme_switch.setOnClickListener {
            run {
                val isDarkSelected = theme_switch.isChecked
                val editor: SharedPreferences.Editor = preferences.edit()
                if (isDarkSelected)
                    editor.putString("theme", "dark")
                else
                    editor.putString("theme", "light")
                editor.commit()
                //Toast.makeText(this, theme_switch.isChecked.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun languageUpdate() {
        //genre selections
        val prefLang = preferences.getString("language", "english")

        //labels
        if (prefLang == "english"){
            dark_mode_header.text = getString(R.string.english_dark_mode)
            dark_mode_desc.text = getString(R.string.english_switch_to_dark_mode_if_you_want)
            lang_mode_desc.text = getString(R.string.english_language)
            lang_mode_header.text = getString(R.string.english_select_language_from_dropdown)
        }
        if (prefLang == "russian"){
            dark_mode_header.text = getString(R.string.russian_dark_mode)
            dark_mode_desc.text = getString(R.string.russian_switch_to_dark_mode_if_you_want)
            lang_mode_desc.text = getString(R.string.russian_language)
            lang_mode_header.text = getString(R.string.russian_select_language_from_dropdown)
        }
        setupLanguageSelector()
    }

    fun setupLanguageSelector() {
        val prefLang = preferences.getString("language", "english")
        var options = arrayListOf<String>()
        if (prefLang == "english")
            options = arrayListOf(getString(R.string.english_english), getString(R.string.english_russian))
        if (prefLang == "russian")
            options = arrayListOf(getString(R.string.russian_english), getString(R.string.russian_russian))

        val aa = ArrayAdapter(this, android.R.layout.simple_spinner_item, options)
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        language_selector.adapter = aa

        val currentLanguage = preferences.getString("language", "english") //default value is light
        if (currentLanguage == "english")
            language_selector.setSelection(0)
        else if (currentLanguage == "russian")
            language_selector.setSelection(1)

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
                    editor.putString("language", "english")
                else if (id.toInt() == 1)
                    editor.putString("language", "russian")
                editor.commit()
                languageUpdate()
                //Toast.makeText(baseContext, languageOptions[id.toInt()], Toast.LENGTH_SHORT).show()
            }
        }

    }

}