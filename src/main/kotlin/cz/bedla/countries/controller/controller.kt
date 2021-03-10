package cz.bedla.countries.controller

import cz.bedla.countries.ApplicationProperties
import cz.bedla.countries.service.SearchService
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@Validated
class RouteController(
    private val properties: ApplicationProperties,
    private val searchService: SearchService
) {
    @GetMapping("/routing/{from}/{to}")
    fun findRoute(
        @PathVariable @CountryConstraint from: String,
        @PathVariable @CountryConstraint to: String,
        @RequestParam(required = false) @AlgorithmConstraint algorithm: String?
    ): ResponseEntity<RouteResult> {
        val route = searchService.searchRoute(from, to, algorithm ?: properties.defaultAlgorithm)
        return if (route.isNotEmpty()) {
            ResponseEntity.ok(RouteResult(route))
        } else {
            ResponseEntity.badRequest().build()
        }
    }
}

data class RouteResult(
    val route: List<String>
)
