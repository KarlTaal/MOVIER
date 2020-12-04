package com.example.movierproject.entities

data class Genre(val id: Int, val name: String) {
    override fun toString(): String = name
}
