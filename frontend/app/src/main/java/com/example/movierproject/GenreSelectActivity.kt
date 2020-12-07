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
import com.example.movierproject.entities.Genre
import com.koushikdutta.ion.Ion
import kotlinx.android.synthetic.main.activity_genre_select.*


class GenreSelectActivity : AppCompatActivity(), AdapterView.OnItemClickListener {

    lateinit var listView: ListView
    var lastTheme = -10000 //inital value, -10000 means unset
    lateinit var arrayAdapter: ArrayAdapter<Genre>
    private lateinit var roomId: String
    lateinit var preferences: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferences =
            getSharedPreferences(getString(R.string.preferences_file), Context.MODE_PRIVATE)
        updateTheme() //has to be called between onCreate and setContent
        setContentView(R.layout.activity_genre_select)

        roomId = intent.getStringExtra("roomId").toString()
        roomCode.text = getString(R.string.room_code, roomId)

        listView = findViewById(R.id.genre_select)

        setupGenreSelector()
        listView.choiceMode = ListView.CHOICE_MODE_MULTIPLE
        listView.onItemClickListener = this
        setupButtons()
    }


    override fun onResume() {
        super.onResume()
        updateTheme()
    }


    fun updateTheme() {
        val prefTheme = preferences.getString(getString(R.string.preferences_theme_key), getString(R.string.preferences_theme_light_value))
        if (lastTheme == -10000) { //when update is called from onCreate
            if (prefTheme == getString(R.string.preferences_theme_light_value)) {
                setTheme(R.style.AppThemeLight)
                lastTheme = R.style.AppThemeLight
                arrayAdapter = ArrayAdapter(applicationContext, R.layout.multi_light_row)
            }
            if (prefTheme == getString(R.string.preferences_theme_dark_value)) {
                setTheme(R.style.AppThemeDark)
                lastTheme = R.style.AppThemeDark
                arrayAdapter = ArrayAdapter(applicationContext, R.layout.multi_dark_row)
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

    private fun setupGenreSelector() {
        Ion.with(this)
            .load("GET", "https://api.themoviedb.org/3/genre/movie/list?")
            .addQuery("api_key", resources.getString(R.string.api_key))
            .addQuery("language", getString(R.string.languageQueryKey))
            .asJsonObject()
            .setCallback { e, result ->
                println("Vastus")
                println(e)
                println(result)
                val genres = result["genres"].asJsonArray
                genres.forEach { genre ->
                    val id = genre.asJsonObject["id"].toString().toInt()
                    val name = genre.asJsonObject["name"].toString()
                    arrayAdapter.add(Genre(id, name.substring(1, name.length - 1)))
                }
                listView.adapter = arrayAdapter
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
                    selectedIds += (if (selectedIds.isEmpty()) "" else "&") + "genre[]=" + arrayAdapter.getItem(
                        i
                    )?.id
                }
            }
            addGenres(selectedIds)
        }
        start_swiping.setOnClickListener {
            val intent = Intent(this, MovieSelectingActivity::class.java)
            startActivity(intent)
        }
    }

    private fun addGenres(selectedIds: String) {
        if (selectedIds == ""){
            // TODO notify that u have to choose some genres before saving
        }
        if (selectedIds != "") {
            val address = getString(R.string.address)
            val URI = getString(R.string.uri, address) + "/" + roomId + "/genres?" + selectedIds
            Ion.with(this)
                .load("POST", URI)
                .asJsonObject()
                .setCallback { e, result ->
                    println(result.asJsonObject["info"])
                    if (result.asJsonObject["info"].toString() == "\"OK\""){
                        // TODO show notification of successful save
                    } else{
                        // TODO show notification of unsuccessful save
                    }
                }
        }
    }


}
