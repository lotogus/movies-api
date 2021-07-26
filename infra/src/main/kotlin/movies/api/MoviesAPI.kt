package movies.api

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import movies.action.MovieAction
import movies.action.RatingAction
import movies.action.ScheduleAction
import movies.model.*
import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.DayOfWeek
import java.time.LocalTime

typealias Movies = List<Movie>

@Api("Movies API", description = "Movies API")
@RestController
class MoviesAPI(private val movieAction: MovieAction,
                private val scheduleAction: ScheduleAction,
                private val ratingAction: RatingAction) {

    protected val logger = KotlinLogging.logger {}

    @ApiOperation("get movies by title", response = Movie::class)
    @GetMapping("/movies")
    suspend fun findMoviesByTitle(@RequestParam("title", required = true) title: String) =
        movieAction.findByTitle(title)
            .fold({
                  when(it) {
                      is NotFoundError -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(it)
                      else -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(it)
                  }
            }, {
                ResponseEntity.ok().body(it)
            })

    @ApiOperation("get movies by ID", response = Movie::class)
    @GetMapping("/movies/{id}")
    suspend fun getMovie(@PathVariable("id", required = true) id: String) =
        movieAction.getMovie(id)
            .fold({
                when(it) {
                    is NotFoundError -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(it)
                    else -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(it)
                }
            }, {
                ResponseEntity.ok().body(it)
            })

    @ApiOperation("get critics by movie id")
    @GetMapping("/movies/{id}/critics")
    suspend fun findCritics(@PathVariable("id", required = true) id: String) =
        movieAction.findCritics(id)
            .fold({
                when(it) {
                    is NotFoundError -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(it)
                    else -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(it)
                }
            }, {
                ResponseEntity.ok().body(it)
            })

    @ApiOperation("get schedules by movie id")
    @GetMapping("/movies/{id}/schedules")
    suspend fun findSchedulesByMovieId(@PathVariable("id", required = true) id: String) =
        scheduleAction.findByMovieId(id)
            .fold({
                when(it) {
                    is NotFoundError -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(it)
                    else -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(it)
                }
            }, {
                ResponseEntity.ok().body(it)
            })

    @ApiOperation("get schedule by schedule id", response = Schedule::class)
    @GetMapping("/movies/{id}/schedules/{scheduleId}")
    suspend fun getScheduleByScheduleId(@PathVariable("id", required = true) id: String,
                                        @PathVariable("scheduleId", required = true) scheduleId: String) =
        scheduleAction.getSchedule(scheduleId)
            .fold({
                when(it) {
                    is NotFoundError -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(it)
                    else -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(it)
                }
            }, {
                ResponseEntity.ok().body(it)
            })

    data class ScheduleToSaveDTO(
        val dayOfWeek: DayOfWeek,
        val startTime: LocalTime,
        val ticketPrice: Price
    )
    @ApiOperation("create schedule", response = Schedule::class)
    @PostMapping("/movies/{id}/schedules")
    suspend fun createSchedule(@PathVariable("id", required = true) id: String,
                               @RequestBody dto: ScheduleToSaveDTO) =
        scheduleAction.createSchedule(ScheduleToSave(id, dto.dayOfWeek, dto.startTime, dto.ticketPrice))
            .fold({
                when(it) {
                    is NotFoundError -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(it)
                    else -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(it)
                }
            }, {
                ResponseEntity.ok().body(it)
            })

    @ApiOperation("update schedule by schedule id", response = Schedule::class)
    @PutMapping("/movies/{id}/schedules/{scheduleId}")
    suspend fun updateSchedule(@PathVariable("id", required = true) id: String,
                               @PathVariable("scheduleId", required = true) scheduleId: String,
                               @RequestBody schedule: Schedule) =
        scheduleAction.updateSchedule(schedule)
            .fold({
                when(it) {
                    is NotFoundError -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(it)
                    else -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(it)
                }
            }, {
                ResponseEntity.ok().body(it)
            })

    @ApiOperation("delete schedule by schedule id", response = Schedule::class)
    @DeleteMapping("/movies/{id}/schedules/{scheduleId}")
    suspend fun deleteScheduleByScheduleId(@PathVariable("id", required = true) id: String,
                                           @PathVariable("scheduleId", required = true) scheduleId: String) =
        scheduleAction.deleteById(scheduleId)
            .fold({
                when(it) {
                    is NotFoundError -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(it)
                    else -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(it)
                }
            }, {
                ResponseEntity.ok().body(it)
            })

    @ApiOperation("get ratings by movie id")
    @GetMapping("/movies/{id}/ratings")
    suspend fun findRatingsByMovieId(@PathVariable("id", required = true) id: String) =
        ratingAction.findByMovieId(id)
            .fold({
                when(it) {
                    is NotFoundError -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(it)
                    else -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(it)
                }
            }, {
                ResponseEntity.ok().body(it)
            })

    @ApiOperation("get rating by rating id", response = Rating::class)
    @GetMapping("/movies/{id}/ratings/{ratingId}")
    suspend fun getRatingsByScheduleId(@PathVariable("id", required = true) id: String,
                                        @PathVariable("ratingId", required = true) ratingId: String) =
        ratingAction.getRating(ratingId)
            .fold({
                when(it) {
                    is NotFoundError -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(it)
                    else -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(it)
                }
            }, {
                ResponseEntity.ok().body(it)
            })

    data class RatingToSaveDTO(
        val value: Int,
        val userId: String
    )
    @ApiOperation("create rating", response = Rating::class)
    @PostMapping("/movies/{id}/ratings")
    suspend fun createRating(@PathVariable("id", required = true) id: String,
                             @RequestBody dto: RatingToSaveDTO) =
        ratingAction.createRating(RatingToSave(id, dto.value, dto.userId))
            .fold({
                when(it) {
                    is NotFoundError -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(it)
                    else -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(it)
                }
            }, {
                ResponseEntity.ok().body(it)
            })

    @ApiOperation("update rating by rating id", response = Rating::class)
    @PutMapping("/movies/{id}/ratings/{ratingId}")
    suspend fun updateRating(@PathVariable("id", required = true) id: String,
                               @PathVariable("ratingId", required = true) ratingId: String,
                               @RequestBody rating: Rating) =
        ratingAction.updateRating(rating)
            .fold({
                when(it) {
                    is NotFoundError -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(it)
                    else -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(it)
                }
            }, {
                ResponseEntity.ok().body(it)
            })

    @ApiOperation("delete rating by rating id", response = Rating::class)
    @DeleteMapping("/movies/{id}/ratings/{ratingId}")
    suspend fun deleteRatingByScheduleId(@PathVariable("id", required = true) id: String,
                                         @PathVariable("ratingId", required = true) ratingId: String) =
        ratingAction.deleteById(ratingId)
            .fold({
                when(it) {
                    is NotFoundError -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(it)
                    else -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(it)
                }
            }, {
                ResponseEntity.ok().body(it)
            })
}

