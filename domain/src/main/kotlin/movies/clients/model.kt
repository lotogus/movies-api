package movies.clients

import movies.model.Critic
import movies.model.Movie
import java.time.LocalDate

data class OpenMovie(
    val id: String,
    val title: String,
    val year: Int,
    val rated: String,
    val releaseDate: LocalDate,
    val runtime: String,
    val genres: List<String>,
    val director: String,
    val writers: List<String>,
    val actors: List<String>,
    val plot: String,
    val languages: List<String>,
    val award: String,
    val posterUrl: String,
    val critics: List<Critic>? = null,
    val type: String,
    val dvdDate: LocalDate,
    val boxOffice: String,
    val production: String,
    val websiteUrl: String,
) {
    /*fun toMovie(): Movie {
        return Movie(
            id=id,
            title=title,
            year=year,
            rated=rated,
            releaseDate=releaseDate,
            runtime=runtime,
            genres=genres,

        )
    }*/
}