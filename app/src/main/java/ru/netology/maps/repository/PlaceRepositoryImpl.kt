package ru.netology.maps.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import ru.netology.maps.dao.PlaceDao
import ru.netology.maps.dto.Place
import ru.netology.maps.entity.PlaceEntity
import ru.netology.maps.entity.toDto
import ru.netology.maps.error.DbError
import ru.netology.maps.error.UnknownError
import java.sql.SQLException

class PlaceRepositoryImpl(private val placeDao: PlaceDao) : PlaceRepository {

    override val data: Flow<List<Place>> = placeDao.getAll()
        .map { it.toDto() }.flowOn(Dispatchers.Default)

    override suspend fun getAll() {
        try {
            placeDao.getAll()
        } catch (e: SQLException) {
            throw DbError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun save(place: Place) {
        try {
            placeDao.save(PlaceEntity.fromDto(place))
        } catch (e: SQLException) {
            throw DbError
        } catch (e: Exception) {
            throw UnknownError
        }

    }

    override suspend fun removeById(id: Int) {
        try {
            placeDao.removeById(id)
        } catch (e: SQLException) {
            throw DbError
        } catch (e: Exception) {
            throw UnknownError
        }
    }
}