package movies.action

import movies.model.*
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.util.*

val movie = Movie(
    "1",
    "the movie",
    2021,
    "PG-13",
    "22 Jun 2001",
    "99 min",
    listOf("action"),
    "he",
    listOf("me"),
    listOf("you"),
    "terror",
    listOf("spanish"),
    "3 wins",
    "www.themovie.com/poster.png",
    listOf(
        Critic("Metacritic", "3/100")
    ),
    "movie",
    "22 Jun 2003",
    "$123",
    "universal",
    "www.themovie.com"
)

val scheduleToSave1 = ScheduleToSave( movie.id, DayOfWeek.FRIDAY, LocalTime.of(14, 20), Price(400.toBigDecimal(), Currency.getInstance(Locale.US)))
val schedule1 = Schedule("30", movie.id, DayOfWeek.FRIDAY, LocalTime.of(14, 20), Price(400.toBigDecimal(), Currency.getInstance(Locale.US)))

val ratingToSave1 = RatingToSave(movie.id, 7, "1000")
val rating1 = Rating("55", movie.id, 7, "1000")