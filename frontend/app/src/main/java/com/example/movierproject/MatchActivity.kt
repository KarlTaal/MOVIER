package com.example.movierproject


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.match.*
import kotlinx.android.synthetic.main.movie_details.*

class MatchActivity : AppCompatActivity() {
    companion object {
        var TAG = MatchActivity::class.java.name
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.match)

        setupBackToMenuClickHandler()
        setupFragmentContent()
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