package movies.clients

import com.github.tomakehurst.wiremock.client.WireMock
import kotlinx.coroutines.runBlocking
import movies.BaseTest
import movies.model.NotFoundError
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
class OpenMovieClientHttpTest : BaseTest() {

    @Autowired
    lateinit var openMovieClientHttp: OpenMovieClientHttp

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

            val r = openMovieClientHttp.getById(id)

            assertThat(r.isRight()).isTrue()
            //TODO: validate each attribute
        }
    }

    @Test
    fun `given another existing movie in OpenMovie should get a Movie`() {
        runBlocking {
            val id = "tt2325001"
            wireMockServer.stubFor(
                WireMock.get(WireMock.urlEqualTo("/?apikey=12345&i=$id"))
                    .willReturn(
                        WireMock.aResponse()
                            .withStatus(200)
                            .withHeader("content-type", "application/json")
                            .withBody(getFileContent("samples/openMovieSample2.json"))
                    )
            )

            val r = openMovieClientHttp.getById(id)

            assertThat(r.isRight()).isTrue()
            //TODO: validate each attribute
        }
    }

    @Test
    fun `given a missing movie in OpenMovie should get an NotFound error`() {
        runBlocking {
            val id = "5555"
            wireMockServer.stubFor(
                WireMock.get(WireMock.urlEqualTo("/?apikey=12345&i=$id"))
                    .willReturn(
                        WireMock.aResponse()
                            .withStatus(200)
                            .withHeader("content-type", "application/json")
                            .withBody(getFileContent("samples/openMovieSampleNotFound.json"))
                    )
            )

            val r = openMovieClientHttp.getById(id)

            assertThat(r.isLeft()).isTrue()
            assertThat(r.swap().orNull()!!).isInstanceOf(NotFoundError::class.java)

        }
    }
}