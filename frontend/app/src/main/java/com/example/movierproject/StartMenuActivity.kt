package com.example.movierproject

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.animation.AnimationUtils
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.koushikdutta.ion.Ion
import kotlinx.android.synthetic.main.start_menu.*

// TODO implement some logical behaviour for the backstack. For example, when clicking back to menu from match activity the backstack should be empty after that.
class StartMenuActivity : AppCompatActivity() {
    companion object {
        var TAG = StartMenuActivity::class.java.name
    }

    lateinit var preferences: SharedPreferences

    var lastTheme = -10000 //inital value, -10000 means unset
    lateinit var menuItemName1: String
    lateinit var menuItemName2: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferences = getSharedPreferences(getString(R.string.preferences_file), Context.MODE_PRIVATE)
        preferencesSetupOnFirstRun()

        updateTheme() //has to be called between onCreate and setContent
        setContentView(R.layout.start_menu)
        updateLanguage()


        setupButtons()
        //setupGenreSelector()
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayShowTitleEnabled(false)

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

    fun updateLanguage() {
        //genre selections
        val prefLang = preferences.getString(getString(R.string.preferences_language_key), getString(R.string.preferences_language_english_value))

        //labels
        if (prefLang == getString(R.string.preferences_language_english_value)) {
            main_menu_header.text = getString(R.string.english_movier)
            start_session_btn.text = getString(R.string.english_start_new_session)
            join_session_btn.text = getString(R.string.english_join_session)
            session_key_input.hint = getString(R.string.english_session_key)
            menuItemName1 = getString(R.string.english_help)
            menuItemName2 = getString(R.string.english_settings)
        }
        if (prefLang == getString(R.string.preferences_language_russian_value)) {
            main_menu_header.text = getString(R.string.russian_movier)
            start_session_btn.text = getString(R.string.russian_start_new_session)
            join_session_btn.text = getString(R.string.russian_join_session)
            session_key_input.hint = getString(R.string.russian_session_key)
            menuItemName1 = getString(R.string.russian_help)
            menuItemName2 = getString(R.string.russian_settings)
        }
        if (prefLang == getString(R.string.preferences_language_finnish_value)) {
            main_menu_header.text = getString(R.string.finnish_movier)
            start_session_btn.text = getString(R.string.finnish_start_new_session)
            join_session_btn.text = getString(R.string.finnish_join_session)
            session_key_input.hint = getString(R.string.finnish_session_key)
            menuItemName1 = getString(R.string.finnish_help)
            menuItemName2 = getString(R.string.finnish_settings)
        }

    }

    override fun onResume() { //we have to update in onResume, because onCreate is not called when we hit back button
        super.onResume()
        updateLanguage()
        invalidateOptionsMenu() //refresh menuOptions in case of language update
        updateTheme()
    }

    fun preferencesSetupOnFirstRun() {
        val editor: SharedPreferences.Editor = preferences.edit()
        if (!preferences.contains(getString(R.string.preferences_theme_key))) {
            //Toast.makeText(this, "theme setup", Toast.LENGTH_SHORT).show()
            editor.putString(getString(R.string.preferences_theme_key), getString(R.string.preferences_theme_light_value))
        }
        if (!preferences.contains(getString(R.string.preferences_language_key))) {
            //Toast.makeText(this, "language setup", Toast.LENGTH_SHORT).show()
            editor.putString(getString(R.string.preferences_language_key), getString(R.string.preferences_language_english_value))
        }
        editor.commit()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar, menu)
        //second parameters sets the id
        menu?.add(0,0,0,menuItemName1) //id 0
        menu?.add(0,1,0,menuItemName2) //id 1
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            0 -> {
                val intent = Intent(this, HelpActivity::class.java)
                startActivity(intent)
                true
            }
            1 -> {
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
                startNewRoom()
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
        val regex = "[0-9]{5}".toRegex()
        if (!regex.matches(key)) {
            val animShake = AnimationUtils.loadAnimation(this, R.anim.shake)
            session_key_input.startAnimation(animShake)
        } else {
            val address = getString(R.string.address)
            val URI = getString(R.string.uri, address) + "/join/$key"
            Ion.with(this)
                .load("POST", URI)
                .asJsonObject()
                .withResponse()
            switchToMovieSelectingActivity(key)
        }
    }

    fun switchToMovieSelectingActivity(roomId: String) {
        val intent = Intent(this, GenreSelectActivity::class.java)
        intent.putExtra("roomId", roomId)
        startActivity(intent)
    }

    private fun startNewRoom() {
        val address = getString(R.string.address)
        val URI = getString(R.string.uri, address) + "/create"
        Ion.with(this)
            .load("POST", URI)
            .asJsonObject()
            .setCallback { e, result ->
                val roomId = result.asJsonObject["room"].asString
                switchToMovieSelectingActivity(roomId)
            }
    }

}
