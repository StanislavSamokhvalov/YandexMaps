package ru.netology.maps.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.maps.dto.Place

@Entity
data class PlaceEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String,
    val description: String,
    val latitude: Double,
    val longitude: Double,

) {
    fun toDto() = Place(id, name, description, latitude, longitude)

    companion object {
        fun fromDto(dto: Place) =
            PlaceEntity(
                dto.id,
                dto.name,
                dto.description,
                dto.latitude,
                dto.longitude,
            )
    }
}

fun List<PlaceEntity>.toDto() = map(PlaceEntity::toDto)
fun List<Place>.toEntity() = map(PlaceEntity::fromDto)