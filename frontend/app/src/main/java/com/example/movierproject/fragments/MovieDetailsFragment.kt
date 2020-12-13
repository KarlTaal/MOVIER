package com.example.movierproject.fragments

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.movierproject.R
import kotlinx.android.synthetic.main.movie_details.view.*

class MovieDetailsFragment : Fragment(){
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.movie_details, container, false)
        view.movie_overview.movementMethod = ScrollingMovementMethod()
        return view
    }
}
