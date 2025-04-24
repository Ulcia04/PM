package com.example.findit

data class Treasure(
    val id: Int? = null,
    val name: String,
    val description: String,
    val lat: Double,
    val lng: Double,
    val photoPath: String? = null
)
