package movies

import com.github.tomakehurst.wiremock.client.WireMock
import kotlinx.coroutines.runBlocking
import movies.BaseTest
import movies.action.MovieAction
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
class IntegrationTest : BaseTest() {

    @Autowired
    lateinit var movieAction: MovieAction

    @Test
    fun `given an existing movie in OpenMovie should get a Movie`() {
        runBlocking {
            val id = "tt1905041"
            wireMockServer.stubFor(
                WireMock.get(WireMock.urlEqualTo("/?apikey=12345&i=$id"))
                    .willReturn(
                        WireMock.aResponse()
                            .withStatus(200)
                            .withHeader("content-type", "application/json")
                            .withBody(getFileContent("samples/openMovieSample1.json"))
                    )
            )
            //migrate the movie
            movieAction.createMovieFromOpenMovie(id)

            client.get().uri("/movies/$id")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is2xxSuccessful
                .expectBody().jsonPath("$.title", "Furious 6")
        }
    }

    @Test
    fun `given an missing movie in OpenMovie should get 404 error`() {
        runBlocking {
            val id = "666666"
            client.get().uri("/movies/$id")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound
        }
    }
}