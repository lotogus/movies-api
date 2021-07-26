package movies

import io.github.resilience4j.circuitbreaker.CircuitBreaker
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry
import movies.action.MovieAction
import movies.action.RatingAction
import movies.action.ScheduleAction
import movies.clients.OpenMovieClient
import movies.repositories.MovieRepository
import movies.repositories.RatingRepository
import movies.repositories.ScheduleRepository
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration
@ConfigurationProperties("app.client")
class AppClientProperties {
    lateinit var url: String
    lateinit var key: String
}

@EnableCaching
@Configuration
class Factory {

    @Bean
    fun openMovieCircuitBreaker(circuitBreakerRegistry: CircuitBreakerRegistry) =
        circuitBreakerRegistry.circuitBreaker("openMovieCircuitBreaker")

    @Bean
    fun webClient(appClientProperties:AppClientProperties) = WebClient.builder().baseUrl(appClientProperties.url).build()

    @Bean
    fun movieAction(movieRepository: MovieRepository, openMovieClient: OpenMovieClient)
        = MovieAction(movieRepository, openMovieClient)

    @Bean
    fun scheduleAction(movieRepository: MovieRepository, scheduleRepository: ScheduleRepository)
        = ScheduleAction(movieRepository, scheduleRepository)

    @Bean
    fun ratingAction(movieRepository: MovieRepository, ratingRepository: RatingRepository)
        = RatingAction(movieRepository, ratingRepository)

}