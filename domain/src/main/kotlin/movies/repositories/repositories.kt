package movies.repositories

import kotlinx.coroutines.flow.Flow
import movies.model.*

interface MovieRepository {
    suspend fun getById(id: String): Movie?
    suspend fun findByTitle(title: String): Flow<Movie>
    suspend fun save(movie: Movie): Movie
}

interface ScheduleRepository {
    suspend fun getById(id: String): Schedule?
    suspend fun findByMovieId(movieId: String): Flow<Schedule>
    suspend fun create(schedule: ScheduleToSave): Schedule
    suspend fun update(schedule: Schedule): Schedule
    suspend fun deleteById(id: String)
}

interface RatingRepository {
    suspend fun getById(id: String): Rating?
    suspend fun findByMovieId(movieId: String): Flow<Rating>
    suspend fun create(rating: RatingToSave): Rating
    suspend fun update(rating: Rating): Rating
    suspend fun deleteById(id: String)
}