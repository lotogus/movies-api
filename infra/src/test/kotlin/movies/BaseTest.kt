package movies

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.springframework.test.web.reactive.server.WebTestClient
import wiremock.com.google.common.io.Resources
import java.io.IOException
import java.util.*

abstract class BaseTest {
    protected val client = WebTestClient.bindToServer().baseUrl("http://localhost:8080").build()

    lateinit var wireMockServer: WireMockServer

    @BeforeAll
    open fun before() {
        wireMockServer = WireMockServer(WireMockConfiguration.wireMockConfig().port(9999))
        wireMockServer.start()
    }

    @BeforeEach
    open fun each() {
        wireMockServer.resetAll()
    }

    @AfterAll
    open fun after() {
        wireMockServer.stop()
    }

    /*
    lateinit var wireMockServer: WireMockServer

    @BeforeAll
    fun beforeAll() {
        wireMockServer = WireMockServer(WireMockConfiguration.wireMockConfig().dynamicPort())
        wireMockServer.start()
        WireMock.configureFor("localhost", wireMockServer.port())
        System.setProperty("mock.port", wireMockServer.port().toString())
        System.setProperty("spring.profiles.active", "test")
    }

    @AfterAll
    fun afterAll() {
        wireMockServer.stop()
    }

    @BeforeEach
    fun beforeEach() {
        wireMockServer.resetAll()
    }
*/
    fun getFileContent(jsonFile: String): String {
        return try {
            val inputStream = Resources.getResource(jsonFile).openStream()
            val scanner = Scanner(inputStream, "UTF-8").useDelimiter("\\A")
            if (scanner.hasNext()) scanner.next() else ""
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }
}