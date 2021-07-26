package movies.action

import arrow.core.Right
import arrow.core.extensions.either.foldable.firstOrNone
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import movies.clients.OpenMovieClient
import movies.model.AlreadyFoundError
import movies.model.Critic
import movies.repositories.MovieRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class MovieActionTest {

    @Test
    fun `given a missing movie should create an existing OpenMovie Movie`() {

        val movieRepository = mockk<MovieRepository>()
        val openMovieClient = mockk<OpenMovieClient>()

        coEvery { movieRepository.getById(movie.id) } returns null
        coEvery { openMovieClient.getById(any()) } returns Right(movie)
        coEvery { movieRepository.save(any()) } returns movie

        val getMovieAction = MovieAction(movieRepository, openMovieClient)

        val movieResponse = runBlocking { getMovieAction.createMovieFromOpenMovie(movie.id) }

        assertThat(movieResponse.isRight()).isTrue
        assertThat(movieResponse.firstOrNone().orNull()!!).isEqualTo(movie)

        coVerify(exactly = 1) { movieRepository.getById(movie.id) }
        coVerify(exactly = 1) { openMovieClient.getById(movie.id) }
        coVerify(exactly = 1) { movieRepository.save(movie) }
    }

    @Test
    fun `given an existing movie should get an Already Found error when trying to create it`() {

        val movieRepository = mockk<MovieRepository>()
        val openMovieClient = mockk<OpenMovieClient>()

        coEvery { movieRepository.getById(movie.id) } returns movie

        val getMovieAction = MovieAction(movieRepository, openMovieClient)

        val movieResponse = runBlocking { getMovieAction.createMovieFromOpenMovie(movie.id) }

        assertThat(movieResponse.isLeft()).isTrue
        assertThat(movieResponse.swap().orNull()!!).isInstanceOf(AlreadyFoundError::class.java)

        coVerify(exactly = 1) { movieRepository.getById(movie.id) }
        coVerify(exactly = 0) { openMovieClient.getById(movie.id) }
        coVerify(exactly = 0) { movieRepository.save(movie) }
    }

    @Test
    fun `given an existing movie should get a Movie with new Critics from OpenMovie`() {

        val movieRepository = mockk<MovieRepository>()
        val openMovieClient = mockk<OpenMovieClient>()

        val movieCriticUpdated = movie.copy(critics = movie.critics+Critic("A", "B"))

        coEvery { movieRepository.getById(movie.id) } returns movie
        coEvery { openMovieClient.getById(any()) } returns Right(movieCriticUpdated)

        val getMovieAction = MovieAction(movieRepository, openMovieClient)

        val movieResponse = runBlocking { getMovieAction.getMovieWithCritics(movie.id) }

        assertThat(movieResponse.isRight()).isTrue
        assertThat(movieResponse.orNull()!!.critics).isEqualTo(movieCriticUpdated.critics)

        coVerify(exactly = 1) { movieRepository.getById(movie.id) }
        coVerify(exactly = 1) { openMovieClient.getById(movie.id) }
    }
}