package movies.repositories

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactive.awaitFirstOrNull
import movies.model.Critic
import movies.model.Movie
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import java.time.LocalDate

@Repository
class MoviesRepositoryMongo(private val movieRepositoryI: MovieRepositoryI) : MovieRepository {

    /*override suspend fun getById(id: String): Either<AppError, Movie> = movieRepositoryI.findById(id)
        .map {
            val r: Either<AppError, Movie> = Either.right(it.toMovie())
            r
        }
        .onErrorResume {
            Mono.just(Either.left(ServerError(it.message ?: "unknown error", it)))
        }
        .awaitFirst()*/
    override suspend fun getById(id: String): Movie? = movieRepositoryI.findById(id)
        .map {
            it.toMovie()
        }
        .awaitFirstOrNull()

    override suspend fun findByTitle(title: String): Flow<Movie> {
        return movieRepositoryI.findByTitleRegex(".*$title.*")
            .map { it.toMovie() }
            .asFlow()
    }

    /*override suspend fun findByTitle(title: String): Either<AppError, List<Movie>> {
        return movieRepositoryI.findByTitle(title)
            .map { it.toMovie() }
            .collectList()
            .map {
                val r: Either<AppError, List<Movie>> = Either.right(it)
                r
            }
            .onErrorResume {
                Mono.just(ServerError(it.message ?: "unknown error", it))
            }
            .awaitFirst()
    }*/

    override suspend fun save(movie: Movie): Movie {
        return movieRepositoryI.save(MovieMongo.fromMovie(movie))
            .map { it.toMovie() }
            .awaitFirst()
    }

}

@Repository
interface MovieRepositoryI : ReactiveMongoRepository<MovieMongo, String> {
    suspend fun findByTitleRegex(title: String): Flux<MovieMongo>
}

@Document
data class MovieMongo(@Id val id: String, val title: String, val year: Int, val rated: String, val releaseDate: String, val runtime: String, val genres: List<String>, val director: String, val writers: List<String>, val actors: List<String>, val plot: String, val languages: List<String>, val award: String, val posterUrl: String, val critics: List<Critic>, val type: String, val dvdDate: String, val boxOffice: String, val production: String, val websiteUrl: String) {

    companion object {
        fun fromMovie(movie: Movie): MovieMongo {
            return with(movie) {
                MovieMongo(
                    id, title, year, rated, releaseDate, runtime, genres, director, writers, actors, plot, languages, award, posterUrl, critics, type, dvdDate, boxOffice, production, websiteUrl
                )
            }
        }
    }

    fun toMovie(): Movie {
        return Movie(
            id, title, year, rated, releaseDate, runtime, genres, director, writers, actors, plot, languages, award, posterUrl, critics, type, dvdDate, boxOffice, production, websiteUrl
        )
    }
}