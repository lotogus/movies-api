package movies.repositories

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactive.awaitFirstOrNull
import movies.model.Price
import movies.model.Schedule
import movies.model.ScheduleToSave
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import java.time.DayOfWeek
import java.time.LocalTime

@Repository
class ScheduleRepositoryMongo(private val scheduleRepositoryI: ScheduleRepositoryI) : ScheduleRepository {

    override suspend fun getById(id: String): Schedule? = scheduleRepositoryI.findById(id)
        .map {
            it.toSchedule()
        }
        .awaitFirstOrNull()

    override suspend fun findByMovieId(movieId: String): Flow<Schedule> {
        return scheduleRepositoryI.findByMovieId(movieId)
            .map { it.toSchedule() }
            .asFlow()
    }

    override suspend fun create(schedule: ScheduleToSave): Schedule {
        return scheduleRepositoryI.insert(ScheduleMongo.from(schedule))
            .map { it.toSchedule() }
            .awaitFirst()
    }

    override suspend fun update(schedule: Schedule): Schedule {
        return scheduleRepositoryI.save(ScheduleMongo.fromSchedule(schedule))
            .map { it.toSchedule() }
            .awaitFirst()
    }

    override suspend fun deleteById(id: String) {
        scheduleRepositoryI.deleteById(id)
    }
}

@Repository
interface ScheduleRepositoryI : ReactiveMongoRepository<ScheduleMongo, String> {
    fun findByMovieId(movieId: String): Flux<ScheduleMongo>
}

@Document
data class ScheduleMongo(@Id val id: String? = null, val movieId: String, val dayOfWeek: DayOfWeek, val startTime: LocalTime, val ticketPrice: Price) {

    companion object {
        fun fromSchedule(schedule: Schedule): ScheduleMongo {
            return with(schedule) {
                ScheduleMongo(
                    id, movieId, dayOfWeek, startTime, ticketPrice
                )
            }
        }

        fun from(schedule: ScheduleToSave): ScheduleMongo {
            return with(schedule) {
                ScheduleMongo(
                    null, movieId, dayOfWeek, startTime, ticketPrice
                )
            }
        }
    }

    fun toSchedule(): Schedule {
        return Schedule(
            id ?: "", movieId, dayOfWeek, startTime, ticketPrice
        )
    }
}