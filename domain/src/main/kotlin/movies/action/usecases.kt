package movies.action

import arrow.core.Either
import arrow.core.Left
import arrow.core.Right
import arrow.core.flatMap
import kotlinx.coroutines.flow.Flow
import movies.clients.OpenMovieClient
import movies.model.*
import movies.repositories.MovieRepository
import movies.repositories.RatingRepository
import movies.repositories.ScheduleRepository
import mu.KotlinLogging

class MovieAction(private val movieRepository: MovieRepository, private val openMovieClient: OpenMovieClient) {

    private val logger = KotlinLogging.logger {}

    suspend fun createMovieFromOpenMovie(id: String) = when {
        getMovie(id).isRight() -> Left(AlreadyFoundError("Movie with id $id already exists"))
        else -> openMovieClient.getById(id)
            .flatMap {
                Right(movieRepository.save(it))
            }
    }

    suspend fun getMovie(id: String): Either<AppError, Movie> {
        return movieRepository.getById(id)
            ?.let { Right(it) }
            ?: Left(NotFoundError("Movie with id $id not found"))
    }

    suspend fun getMovieWithCritics(id: String): Either<AppError, Movie> {
        return movieRepository.getById(id)
            ?.let { Right(it) }
            ?.flatMap { movie -> findCritics(id).map { movie.copy(critics = it) } }
            ?: Left(NotFoundError("Movie with id $id not found"))
    }

    suspend fun findCritics(id: String): Either<AppError, List<Critic>> {
        return openMovieClient.getById(id).map { it.critics }
    }

    suspend fun findByTitle(title: String): Either<AppError, Flow<Movie>> {
        return Right(movieRepository.findByTitle(title))
    }
}

class ScheduleAction(private val movieRepository: MovieRepository, private val scheduleRepository: ScheduleRepository) {

    private val logger = KotlinLogging.logger {}

    suspend fun createSchedule(schedule: ScheduleToSave): Either<AppError, Schedule> {
        return movieRepository.getById(schedule.movieId)
            .let { if(it==null) Left(NotFoundError("Movie with id ${schedule.movieId} not found")) else Right(it) }
            .flatMap { Right(scheduleRepository.create(schedule)) }
    }

    suspend fun updateSchedule(schedule: Schedule): Either<AppError, Schedule> {
        return movieRepository.getById(schedule.movieId)
            .let { if(it==null) Left(NotFoundError("Movie with id ${schedule.movieId} not found")) else Right(it) }
            .flatMap {
                scheduleRepository.getById(schedule.id)
                    .let { if(it==null) Left(NotFoundError("Schedule with id ${schedule.id} not found")) else Right(it) }
            }
            .flatMap { Right(scheduleRepository.update(schedule)) }
    }

    suspend fun getSchedule(scheduleId: String): Either<AppError, Schedule> {
        return scheduleRepository.getById(scheduleId)
            .let { if(it==null) Left(NotFoundError("Schedule with id $scheduleId not found")) else Right(it) }
    }

    suspend fun findByMovieId(movieId: MovieId): Either<AppError, Flow<Schedule>> {
        return Right(scheduleRepository.findByMovieId(movieId))
    }

    suspend fun deleteById(scheduleId: String): Either<AppError, Any> {
        scheduleRepository.deleteById(scheduleId)
        return Right(Any())
    }
}

class RatingAction(private val movieRepository: MovieRepository, private val ratingRepository: RatingRepository) {

    private val logger = KotlinLogging.logger {}

    suspend fun createRating(rating: RatingToSave): Either<AppError, Rating> {
        return movieRepository.getById(rating.movieId)
            .let { if(it==null) Left(NotFoundError("Movie with id ${rating.movieId} not found")) else Right(it) }
            .flatMap { Right(ratingRepository.create(rating)) }
    }

    suspend fun updateRating(rating: Rating): Either<AppError, Rating> {
        return movieRepository.getById(rating.movieId)
            .let { if(it==null) Left(NotFoundError("Movie with id ${rating.movieId} not found")) else Right(it) }
            .flatMap {
                ratingRepository.getById(rating.id)
                    .let { if(it==null) Left(NotFoundError("Rating with id ${rating.id} not found")) else Right(it) }
            }
            .flatMap { Right(ratingRepository.update(rating)) }
    }

    suspend fun getRating(ratingId: String): Either<AppError, Rating> {
        return ratingRepository.getById(ratingId)
            .let { if(it==null) Left(NotFoundError("Rating with id $ratingId not found")) else Right(it) }
    }

    suspend fun findByMovieId(movieId: MovieId): Either<AppError, Flow<Rating>> {
        return Right(ratingRepository.findByMovieId(movieId))
    }

    suspend fun deleteById(ratingId: String): Either<AppError, Any> {
        ratingRepository.deleteById(ratingId)
        return Right(Any())
    }
}