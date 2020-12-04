package com.example.movierproject

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
    lateinit var arrayAdapter: ArrayAdapter<Genre>
    private lateinit var genreQueryLanguage: String
    private lateinit var roomId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_genre_select)

        genreQueryLanguage = intent.getStringExtra("genreQueryLanguage").toString()
        roomId = intent.getStringExtra("roomId").toString()

        listView = findViewById(R.id.genre_select)
        arrayAdapter = ArrayAdapter(
            applicationContext,
            android.R.layout.simple_list_item_multiple_choice
        )
        setupGenreSelector()
        listView.choiceMode = ListView.CHOICE_MODE_MULTIPLE
        listView.onItemClickListener = this
        setupButtons()
    }

    private fun setupGenreSelector() {
        Ion.with(this)
            .load("GET", "https://api.themoviedb.org/3/genre/movie/list?")
            .addQuery("api_key", resources.getString(R.string.api_key))
            .addQuery("language", genreQueryLanguage)
            .asJsonObject()
            .setCallback { e, result ->
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
                    selectedIds += (if (selectedIds.isEmpty()) "" else "&") + "genre[]=" + arrayAdapter.getItem(i)?.id
                }
            }
            addGenres(selectedIds)
        }
    }

    private fun addGenres(selectedIds: String) {
        val address = getString(R.string.address)
        val URI = getString(R.string.uri, address) + "/" + roomId + "/genres?" + selectedIds
        Ion.with(this)
            .load("POST", URI)
            .asJsonObject()
            .setCallback { e, result ->
                println(result.asJsonObject["info"])
            }
    }


}
