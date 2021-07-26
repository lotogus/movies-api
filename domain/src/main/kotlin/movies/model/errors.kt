package movies.model

//@JsonIgnoreProperties("stackTrace", "localizedMessage")
sealed class AppError(open val message: String, open val cause: Throwable? = null)
class ServerError(override val message: String, override val cause: Throwable? = null) : AppError(message, cause)
class ClientError(override val message: String, override val cause: Throwable? = null) : AppError(message, cause)
class NotFoundError(override val message: String) : AppError(message)
class AlreadyFoundError(override val message: String) : AppError(message)