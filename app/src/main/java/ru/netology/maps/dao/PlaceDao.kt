package ru.netology.maps.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.netology.maps.entity.PlaceEntity

@Dao
interface PlaceDao {
    @Query("SELECT * FROM PlaceEntity ORDER BY id DESC")
    fun getAll(): Flow<List<PlaceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(place: PlaceEntity)

    @Query("DELETE FROM PlaceEntity WHERE id = :id")
    suspend fun removeById(id: Int)

    @Query("UPDATE PlaceEntity SET name = :name, description = :description WHERE id = :id")
    fun updateContentById(id: Int, name: String, description: String)

    suspend fun save(place: PlaceEntity) =
        if (place.id == 0) insert(place) else updateContentById(place.id, place.name, place.description)
}
