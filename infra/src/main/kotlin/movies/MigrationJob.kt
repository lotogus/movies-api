package movies

import kotlinx.coroutines.runBlocking
import movies.action.MovieAction
import movies.model.AlreadyFoundError
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct

@Component
@ConditionalOnProperty(value = ["app.client.migration.enabled"], havingValue = "true")
class MigrationJob(private val movieAction: MovieAction, @Value("\${app.client.migration.ids}") val ids: List<String>) {

    protected val logger = KotlinLogging.logger {}

    @PostConstruct
    fun startupMigrate() {
        runBlocking {
            logger.debug { "running startup migration for ${ids.size} movies" }
            for(id in ids) {
                val response = movieAction.createMovieFromOpenMovie(id)
                if(response.isLeft() && (response.swap().orNull() is AlreadyFoundError)) {
                    logger.debug { "no migration needed" }
                    break
                } else {
                    logger.info { "migrated ${response.orNull()}" }
                }
            }
        }
    }
}