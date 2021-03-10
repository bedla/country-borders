package cz.bedla.countries.controller

import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import javax.validation.ConstraintViolationException

@RestControllerAdvice
class CustomErrorHandler {
    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception): ResponseEntity<ApplicationError> {
        logger.error("Error", e)

        return ResponseEntity.status(500).body(ApplicationError(e.message ?: "Internal Server Error", null))
    }

    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolationException(e: ConstraintViolationException): ResponseEntity<ApplicationError> {
        logger.error("Constraint error: ${e.message}", e)

        val error = ApplicationError(
            e.message ?: "Invalid request",
            e.constraintViolations.map {
                ConstraintError(
                    it.propertyPath?.toString() ?: "",
                    it.invalidValue?.toString() ?: ""
                )
            }
        )
        return ResponseEntity.badRequest().body(error)
    }

    companion object {
        private val logger = LoggerFactory.getLogger(CustomErrorHandler::class.java)!!
    }
}

data class ApplicationError(
    val message: String,
    val constraintErrors: List<ConstraintError>?
)

data class ConstraintError(
    val path: String,
    val value: String
)
