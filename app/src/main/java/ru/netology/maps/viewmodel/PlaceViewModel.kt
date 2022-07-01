package ru.netology.maps.viewmodel

import android.app.Application
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import ru.netology.maps.dto.Place
import kotlinx.coroutines.launch
import ru.netology.maps.db.AppDb
import ru.netology.maps.model.PlaceModel
import ru.netology.maps.model.PlaceModelState
import ru.netology.maps.repository.PlaceRepository
import ru.netology.maps.repository.PlaceRepositoryImpl

private val empty = Place(
    id = 0,
    name = "",
    description = "",
    latitude = 0.0,
    longitude = 0.0,
)

class PlaceViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: PlaceRepository =
        PlaceRepositoryImpl(AppDb.getInstance(context = application).placeDao())

    val data: LiveData<PlaceModel> = repository.data
        .map { places ->
            PlaceModel(
                places,
                places.isEmpty()
            )
        }.asLiveData(Dispatchers.Default)

    private val _dataState = MutableLiveData<PlaceModelState>()
    val dataState: LiveData<PlaceModelState>
        get() = _dataState

    val edited = MutableLiveData(empty)

    init {
        loadPlaces()
    }

    private fun loadPlaces() = viewModelScope.launch {
        try {
            _dataState.value = PlaceModelState(loading = true)
            repository.getAll()
            _dataState.value = PlaceModelState()
        } catch (e: Exception) {
            _dataState.value = PlaceModelState(error = true)
        }
    }

    fun save(latitude: Double, longitude: Double) {
        edited.value?.let {
            viewModelScope.launch {
                try {
                    repository.save(it.copy(latitude = latitude, longitude = longitude))
                    _dataState.value = PlaceModelState()
                } catch (e: Exception) {
                    _dataState.value = PlaceModelState(error = true)
                }
            }
        }
    }

    fun removeById(id: Int) = viewModelScope.launch {
        try {
            repository.removeById(id)
            _dataState.value = PlaceModelState()
        } catch (e: Exception) {
            _dataState.value =
                PlaceModelState(error = true)
        }
    }

    fun changeName(name: String) {
        val text = name.trim()
        if (edited.value?.name == text) {
            return
        }
        edited.value = edited.value?.copy(name = text)
    }

    fun changeDescription(description: String) {
        val text = description.trim()
        if (edited.value?.name == text) {
            return
        }
        edited.value = edited.value?.copy(description = text)
    }

    fun edit(place: Place) {
        edited.value = place
    }
}
