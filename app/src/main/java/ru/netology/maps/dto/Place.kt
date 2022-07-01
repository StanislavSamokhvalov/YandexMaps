package ru.netology.maps.dto

data class Place(
    val id: Int,
    val name: String,
    val description: String,
    val latitude: Double,
    val longitude: Double,
) {
    companion object {
        val empty = Place(
            id = 0,
            name = "",
            description = "",
            latitude = 0.0,
            longitude = 0.0,
        )
    }
}