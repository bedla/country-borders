package cz.bedla.countries.controller

import cz.bedla.countries.roads.CountriesDatabase
import cz.bedla.countries.roads.SearchAlgorithm
import java.lang.annotation.Documented
import java.lang.annotation.ElementType
import java.lang.annotation.RetentionPolicy
import javax.validation.Constraint
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext
import javax.validation.Payload
import kotlin.reflect.KClass

@Documented
@Constraint(validatedBy = [CountryValidator::class])
@java.lang.annotation.Target(value = [ElementType.METHOD, ElementType.FIELD])
@java.lang.annotation.Retention(RetentionPolicy.RUNTIME)
annotation class CountryConstraint(
    val message: String = "Invalid country identifier",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)

class CountryValidator(
    private val database: CountriesDatabase
) : ConstraintValidator<CountryConstraint?, String?> {
    override fun initialize(constraint: CountryConstraint?) {
    }

    override fun isValid(value: String?, context: ConstraintValidatorContext): Boolean {
        return database.containsCountry(value)
    }
}

@Documented
@Constraint(validatedBy = [AlgorithmValidator::class])
@java.lang.annotation.Target(value = [ElementType.METHOD, ElementType.FIELD])
@java.lang.annotation.Retention(RetentionPolicy.RUNTIME)
annotation class AlgorithmConstraint(
    val message: String = "Invalid algorithm identifier",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)

class AlgorithmValidator(
    algorithmBeans: List<SearchAlgorithm>
) : ConstraintValidator<AlgorithmConstraint?, String?> {
    private val algorithms = algorithmBeans.map { it.getIdentifier() }.toSet()

    override fun initialize(constraint: AlgorithmConstraint?) {
    }

    override fun isValid(value: String?, context: ConstraintValidatorContext): Boolean {
        return value == null || algorithms.contains(value)
    }
}
