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

    val languageOptions = arrayListOf("English", "Estonian")
    lateinit var preferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferences =
            getSharedPreferences(getString(R.string.preferences_file), Context.MODE_PRIVATE)
        setContentView(R.layout.settings)
        setupThemeSwitch()
        setupLanguageSelector()
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

    fun setupLanguageSelector() {
        val aa = ArrayAdapter(this, android.R.layout.simple_spinner_item, languageOptions)
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        language_selector.adapter = aa

        val currentLanguage = preferences.getString("language", "english") //default value is light
        if (currentLanguage == "english")
            language_selector.setSelection(0)
        else if (currentLanguage == "estonian")
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
                    editor.putString("language", "estonian")
                editor.commit()
                //Toast.makeText(baseContext, languageOptions[id.toInt()], Toast.LENGTH_SHORT).show()
            }
        }

    }

}