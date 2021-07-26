package movies.clients

import arrow.core.Either
import arrow.core.Left
import arrow.core.Right
import com.fasterxml.jackson.annotation.JsonProperty
import io.github.resilience4j.circuitbreaker.CircuitBreaker
import io.github.resilience4j.kotlin.circuitbreaker.executeSuspendFunction
import movies.AppClientProperties
import movies.model.AppError
import movies.model.Critic
import movies.model.Movie
import movies.model.NotFoundError
import mu.KotlinLogging
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody

@Component
class OpenMovieClientHttp(val webClient: WebClient,
                          val openMovieCircuitBreaker: CircuitBreaker,
                          val appClientProperties: AppClientProperties) : OpenMovieClient {

    protected val logger = KotlinLogging.logger {}

    override suspend fun getById(id: String): Either<AppError, Movie> {
        return openMovieCircuitBreaker.executeSuspendFunction {
            getById2(id)
        }
    }

    suspend fun getById2(id: String): Either<AppError, Movie> {
        logger.info { "getting OpenMovie id $id" }
        return webClient.get()
            .uri("/?apikey=${appClientProperties.key}&i=$id")
            .accept(APPLICATION_JSON)
            .retrieve()
            .awaitBody<OpenMovie>()
            .let {
                when (it.response) {
                    "True" -> Right(it.toMovie().also { logger.debug { "got movie $it" } })
                    else -> Left(NotFoundError(it.error ?: "Unknown OpenMovie API error"))
                }
            }
    }
}

const val NA = "N/A"

data class OpenMovie(
    @JsonProperty("imdbID")
    val id: String?=null,
    @JsonProperty("Title")
    val title: String?=null,
    @JsonProperty("Year")
    val year: String?=null,
    @JsonProperty("Rated")
    val rated: String?=null,
    @JsonProperty("Released")
    val releaseDate: String?=null,
    @JsonProperty("Runtime")
    val runtime: String?=null,
    @JsonProperty("Genre")
    val genres: String?=null,
    @JsonProperty("Director")
    val director: String?=null,
    @JsonProperty("Writer")
    val writers: String?=null,
    @JsonProperty("Actors")
    val actors: String?=null,
    @JsonProperty("Plot")
    val plot: String?=null,
    @JsonProperty("Language")
    val languages: String?=null,
    @JsonProperty("Awards")
    val award: String?=null,
    @JsonProperty("Poster")
    val posterUrl: String?=null,
    @JsonProperty("Ratings")
    val critics: List<OpenMovieRating>?=null,
    @JsonProperty("Type")
    val type: String?=null,
    @JsonProperty("DVD")
    val dvdDate: String?=null,
    @JsonProperty("BoxOffice")
    val boxOffice: String?=null,
    @JsonProperty("Production")
    val production: String?=null,
    @JsonProperty("Website")
    val websiteUrl: String?=null,
    @JsonProperty("Response")
    val response: String,
    @JsonProperty("Error")
    val error: String? = null,
) {
    fun toMovie(): Movie {

        return Movie(
            id?: NA, title?: NA, year?.toInt()?:0, rated?: NA,
            releaseDate?: NA,
            runtime?: NA,
            genres?.split(",")?.map { it.trim() }?: emptyList(),
            director?: NA,
            writers?.split(",")?.map { it.trim() }?: emptyList(),
            actors?.split(",")?.map { it.trim() }?: emptyList(),
            plot?: NA,
            languages?.split(",")?.map { it.trim() }?: emptyList(),
            award?: NA, posterUrl?: NA,
            critics?.map { Critic(it.source, it.value) }?: emptyList(),
            type?: NA,
            dvdDate?: NA,
            boxOffice?: NA, production?: NA, websiteUrl?: NA
        )
    }
}

data class OpenMovieRating(
    @JsonProperty("Source")
    val source: String,
    @JsonProperty("Value")
    val value: String
)
