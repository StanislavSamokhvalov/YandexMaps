package ru.netology.maps.model

import ru.netology.maps.dto.Place

data class PlaceModel(
    val places: List<Place> = emptyList(),
    val empty: Boolean = false
)

data class PlaceModelState(
    val loading: Boolean = false,
    val error: Boolean = false,
    val refreshing: Boolean = false,
)