package movies.repositories

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactive.awaitFirstOrNull
import movies.model.Rating
import movies.model.RatingToSave
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux

@Repository
class RatingRepositoryMongo(private val ratingRepositoryI: RatingRepositoryI) : RatingRepository {

    override suspend fun getById(id: String): Rating? = ratingRepositoryI.findById(id)
        .map {
            it.toRating()
        }
        .awaitFirstOrNull()

    override suspend fun findByMovieId(movieId: String): Flow<Rating> {
        return ratingRepositoryI.findByMovieId(movieId)
            .map { it.toRating() }
            .asFlow()
    }

    override suspend fun create(rating: RatingToSave): Rating {
        return ratingRepositoryI.insert(RatingMongo.from(rating))
            .map { it.toRating() }
            .awaitFirst()
    }

    override suspend fun update(rating: Rating): Rating {
        return ratingRepositoryI.save(RatingMongo.fromRating(rating))
            .map { it.toRating() }
            .awaitFirst()
    }

    override suspend fun deleteById(id: String) {
        ratingRepositoryI.deleteById(id)
    }
}

@Repository
interface RatingRepositoryI : ReactiveMongoRepository<RatingMongo, String> {
    fun findByMovieId(movieId: String): Flux<RatingMongo>
}

@Document
data class RatingMongo(@Id val id: String? = null, val movieId: String, val value: Int, val userId: String) {

    companion object {
        fun fromRating(rating: Rating): RatingMongo {
            return with(rating) {
                RatingMongo(
                    id, movieId, value, userId
                )
            }
        }

        fun from(rating: RatingToSave): RatingMongo {
            return with(rating) {
                RatingMongo(
                    null, movieId, value, userId
                )
            }
        }
    }

    fun toRating(): Rating {
        return Rating(
            id ?: "", movieId, value, userId
        )
    }
}