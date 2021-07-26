package movies.action

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import movies.model.NotFoundError
import movies.repositories.MovieRepository
import movies.repositories.RatingRepository
import org.assertj.core.api.Assertions
import org.junit.Test

class RatingActionTest {

    @Test
    fun `given an existing Movie should save a Rating`() {

        val movieRepository = mockk<MovieRepository>()
        val ratingRepository = mockk<RatingRepository>()

        coEvery { movieRepository.getById(any()) } returns movie
        coEvery { ratingRepository.create(ratingToSave1) } returns rating1

        val ratingAction = RatingAction(movieRepository, ratingRepository)

        val ratingResponse = runBlocking { ratingAction.createRating(ratingToSave1) }

        Assertions.assertThat(ratingResponse.isRight()).isTrue
        Assertions.assertThat(ratingResponse.orNull()!!).isEqualTo(rating1)

        coVerify(exactly = 1) { movieRepository.getById(ratingToSave1.movieId) }
        coVerify(exactly = 1) { ratingRepository.create(ratingToSave1) }
    }

    @Test
    fun `given an missing Movie should get an error trying to save a Rating`() {

        val movieRepository = mockk<MovieRepository>()
        val ratingRepository = mockk<RatingRepository>()

        coEvery { movieRepository.getById(any()) } returns null
        coEvery { ratingRepository.create(ratingToSave1) } returns rating1

        val ratingAction = RatingAction(movieRepository, ratingRepository)

        val ratingResponse = runBlocking { ratingAction.createRating(ratingToSave1) }

        Assertions.assertThat(ratingResponse.isLeft()).isTrue
        Assertions.assertThat(ratingResponse.swap().orNull()!!).isInstanceOf(NotFoundError::class.java)

        coVerify(exactly = 1) { movieRepository.getById(ratingToSave1.movieId) }
        coVerify(exactly = 0) { ratingRepository.getById(ratingToSave1.movieId) }
    }

    @Test
    fun `given an missing Movie should get an error trying to update a Rating`() {

        val movieRepository = mockk<MovieRepository>()
        val ratingRepository = mockk<RatingRepository>()

        coEvery { movieRepository.getById(any()) } returns null
        coEvery { ratingRepository.update(rating1) } returns rating1

        val ratingAction = RatingAction(movieRepository, ratingRepository)

        val ratingResponse = runBlocking { ratingAction.updateRating(rating1) }

        Assertions.assertThat(ratingResponse.isLeft()).isTrue
        Assertions.assertThat(ratingResponse.swap().orNull()!!).isInstanceOf(NotFoundError::class.java)

        coVerify(exactly = 1) { movieRepository.getById(rating1.movieId) }
        coVerify(exactly = 0) { ratingRepository.update(rating1) }
    }

}