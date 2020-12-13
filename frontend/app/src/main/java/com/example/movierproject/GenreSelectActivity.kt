package com.example.movierproject

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.movierproject.models.GenreSelectViewModel
import com.koushikdutta.ion.Ion
import kotlinx.android.synthetic.main.activity_genre_select.*


class GenreSelectActivity : AppCompatActivity(), AdapterView.OnItemClickListener {

    private lateinit var model: GenreSelectViewModel

    lateinit var listView: ListView
    private lateinit var roomId: String
    lateinit var preferences: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferences =
            getSharedPreferences(getString(R.string.preferences_file), Context.MODE_PRIVATE)

        // This is needed so on orientation change the theme is set correctly
        if (preferences.getString(
                getString(R.string.preferences_theme_key),
                getString(R.string.preferences_theme_light_value)
            ) == getString(R.string.preferences_theme_light_value)) {
            this.setTheme(R.style.AppThemeLight)
        }
        if (preferences.getString(
                getString(R.string.preferences_theme_key),
                getString(R.string.preferences_theme_light_value)
            ) == getString(R.string.preferences_theme_dark_value)) {
            this.setTheme(R.style.AppThemeDark)
        }

        model = ViewModelProvider(this).get(GenreSelectViewModel::class.java)
        updateTheme() //has to be called between onCreate and setContent
        setContentView(R.layout.activity_genre_select)


        roomId = intent.getStringExtra("roomId").toString()
        roomCode.text = getString(R.string.room_code, roomId)

        listView = findViewById(R.id.genre_select)

        listView.adapter = model.arrayAdapter

        listView.choiceMode = ListView.CHOICE_MODE_MULTIPLE
        listView.onItemClickListener = this
        setupButtons()
    }


    override fun onResume() {
        super.onResume()
        updateTheme()
    }


    fun updateTheme() {
        val prefTheme = preferences.getString(
            getString(R.string.preferences_theme_key),
            getString(R.string.preferences_theme_light_value)
        )
        if (model.lastTheme == -10000) { //when update is called from onCreate
            if (prefTheme == getString(R.string.preferences_theme_light_value)) {
                this.setTheme(R.style.AppThemeLight)
                model.lastTheme = R.style.AppThemeLight
                model.arrayAdapter = ArrayAdapter(this, R.layout.multi_light_row)
            }
            if (prefTheme == getString(R.string.preferences_theme_dark_value)) {
                this.setTheme(R.style.AppThemeDark)
                model.lastTheme = R.style.AppThemeDark
                model.arrayAdapter = ArrayAdapter(this, R.layout.multi_dark_row)
            }
        } else { //we need to call recreate but only when needed because otherwise it will go in infinite loop
            if (prefTheme == getString(R.string.preferences_theme_light_value) && model.lastTheme != R.style.AppThemeLight) {
                this.setTheme(R.style.AppThemeLight)
                model.lastTheme = R.style.AppThemeLight
                this.recreate()
            }
            if (prefTheme == getString(R.string.preferences_theme_dark_value) && model.lastTheme != R.style.AppThemeDark) {
                this.setTheme(R.style.AppThemeDark)
                model.lastTheme = R.style.AppThemeDark
                this.recreate()
            }
        }
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
    }

    private fun setupButtons() {
        my_genres.setOnClickListener {
            var selectedIds = ""
            val checked = listView.checkedItemPositions
            for (i in 0 until checked.size()) {
                if (checked.valueAt(i)) {
                    selectedIds += (if (selectedIds.isEmpty()) "" else "&") + "genre[]=" + model.arrayAdapter.getItem(
                        checked.keyAt(i)
                    )?.id
                }
            }
            addGenres(selectedIds)
        }
        start_swiping.setOnClickListener {
            val intent = Intent(this, MovieSelectingActivity::class.java)
            intent.putExtra("roomId", roomId)
            startActivity(intent)
        }

        updateButtonsState()
    }

    private fun updateButtonsState(){
        start_swiping.isEnabled = model.proceedButtonIsEnabled
        if (model.proceedButtonIsEnabled){
            start_swiping.alpha = 1f
            my_genres.alpha = 0.3f
        }
        else{
            start_swiping.alpha = 0.3f
            my_genres.alpha = 1f
        }
    }


    private fun addGenres(selectedIds: String) {
        if (selectedIds != "") {
            val address = getString(R.string.address)
            val URI = getString(R.string.uri, address) + "/" + roomId + "/genres?" + selectedIds
            Ion.with(this)
                .load("POST", URI)
                .asJsonObject()
                .setCallback { e, result ->
                    if (result.asJsonObject["info"].toString() == "true") {
                        model.proceedButtonIsEnabled = true
                        updateButtonsState()
                    }
                }
        }
    }


}
