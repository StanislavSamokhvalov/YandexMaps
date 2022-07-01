package ru.netology.maps.error
import java.sql.SQLException

sealed class AppError(message: String) : Exception(message) {
    companion object {
        fun from(e: Throwable) = when (e) {
            is SQLException -> DbError
            else -> UnknownError
        }
    }
}
object DbError : AppError("db_error")
object UnknownError : AppError("unknown_error")