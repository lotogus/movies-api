package movies

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class MoviesApiApplication

fun main(args: Array<String>) {
	runApplication<MoviesApiApplication>(*args)
}
