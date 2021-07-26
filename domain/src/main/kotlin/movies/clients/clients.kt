package movies.clients

import arrow.core.Either
import movies.model.AppError
import movies.model.Movie

interface OpenMovieClient {
    suspend fun getById(id: String): Either<AppError, Movie>
}