package com.example.movierproject.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.movierproject.R
import com.example.movierproject.StartMenuActivity
import kotlinx.android.synthetic.main.no_more_movies.view.*

class OutOfMoviesFragment : Fragment(){
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.no_more_movies, container, false)
        view.back_to_menu_btn.setOnClickListener {
            run {
                val intent = Intent(this.context, StartMenuActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP )
                intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        }
        return view
    }
}
