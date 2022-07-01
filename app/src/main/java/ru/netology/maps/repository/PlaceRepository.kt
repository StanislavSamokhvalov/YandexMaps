package ru.netology.maps.repository

import androidx.lifecycle.LiveData
import kotlinx.coroutines.flow.Flow
import ru.netology.maps.dto.Place

interface PlaceRepository {
    val data: Flow<List<Place>>

    suspend fun getAll()
    suspend fun save(place: Place)
    suspend fun removeById(id: Int)
}