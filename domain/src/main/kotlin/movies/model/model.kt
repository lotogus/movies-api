package movies.model

import java.math.BigDecimal
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.util.*

data class Movie(
    val id: MovieId,
    val title: String,
    val year: Int,
    val rated: String,
    val releaseDate: String,
    val runtime: String,
    val genres: List<String>,
    val director: String,
    val writers: List<String>,
    val actors: List<String>,
    val plot: String,
    val languages: List<String>,
    val award: String,
    val posterUrl: String,
    val critics: List<Critic>,
    val type: String,
    val dvdDate: String,
    val boxOffice: String,
    val production: String,
    val websiteUrl: String,
)

data class Critic(val source: String, val value: String, val votes: String? = null)

typealias MovieId = String

data class Schedule(
    val id: String,
    val movieId: MovieId,
    val dayOfWeek: DayOfWeek,
    val startTime: LocalTime,
    val ticketPrice: Price
)
data class ScheduleToSave(
    val movieId: MovieId,
    val dayOfWeek: DayOfWeek,
    val startTime: LocalTime,
    val ticketPrice: Price
)
data class Price(
    val amount: BigDecimal,
    val currency: Currency
)

data class Rating(
    val id: String,
    val movieId: MovieId,
    val value: Int,
    val userId: String
)
data class RatingToSave(
    val movieId: MovieId,
    val value: Int,
    val userId: String
)
