package movies.action

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import movies.model.NotFoundError
import movies.repositories.MovieRepository
import movies.repositories.ScheduleRepository
import org.assertj.core.api.Assertions
import org.junit.Test

class ScheduleActionTest {

    @Test
    fun `given an existing Movie should save a Schedule`() {

        val movieRepository = mockk<MovieRepository>()
        val scheduleRepository = mockk<ScheduleRepository>()

        coEvery { movieRepository.getById(any()) } returns movie
        coEvery { scheduleRepository.create(scheduleToSave1) } returns schedule1

        val scheduleAction = ScheduleAction(movieRepository, scheduleRepository)

        val scheduleResponse = runBlocking { scheduleAction.createSchedule(scheduleToSave1) }

        Assertions.assertThat(scheduleResponse.isRight()).isTrue
        Assertions.assertThat(scheduleResponse.orNull()!!).isEqualTo(schedule1)

        coVerify(exactly = 1) { movieRepository.getById(scheduleToSave1.movieId) }
        coVerify(exactly = 1) { scheduleRepository.create(scheduleToSave1) }
    }

    @Test
    fun `given an missing Movie should get an error trying to save a Schedule`() {

        val movieRepository = mockk<MovieRepository>()
        val scheduleRepository = mockk<ScheduleRepository>()

        coEvery { movieRepository.getById(any()) } returns null
        coEvery { scheduleRepository.create(scheduleToSave1) } returns schedule1

        val scheduleAction = ScheduleAction(movieRepository, scheduleRepository)

        val scheduleResponse = runBlocking { scheduleAction.createSchedule(scheduleToSave1) }

        Assertions.assertThat(scheduleResponse.isLeft()).isTrue
        Assertions.assertThat(scheduleResponse.swap().orNull()!!).isInstanceOf(NotFoundError::class.java)

        coVerify(exactly = 1) { movieRepository.getById(scheduleToSave1.movieId) }
        coVerify(exactly = 0) { scheduleRepository.getById(scheduleToSave1.movieId) }
    }

    @Test
    fun `given an missing Movie should get an error trying to update a Schedule`() {

        val movieRepository = mockk<MovieRepository>()
        val scheduleRepository = mockk<ScheduleRepository>()

        coEvery { movieRepository.getById(any()) } returns null
        coEvery { scheduleRepository.update(schedule1) } returns schedule1

        val scheduleAction = ScheduleAction(movieRepository, scheduleRepository)

        val scheduleResponse = runBlocking { scheduleAction.updateSchedule(schedule1) }

        Assertions.assertThat(scheduleResponse.isLeft()).isTrue
        Assertions.assertThat(scheduleResponse.swap().orNull()!!).isInstanceOf(NotFoundError::class.java)

        coVerify(exactly = 1) { movieRepository.getById(schedule1.movieId) }
        coVerify(exactly = 0) { scheduleRepository.update(schedule1) }
    }

}