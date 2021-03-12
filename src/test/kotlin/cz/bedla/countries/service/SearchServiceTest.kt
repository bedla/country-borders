package cz.bedla.countries.service

import cz.bedla.countries.loader.JsonCountry
import cz.bedla.countries.roads.CountriesDatabase
import cz.bedla.countries.roads.SearchAlgorithm
import cz.bedla.countries.roads.algorithms.JGraphTDijkstraSearchAlgorithm
import cz.bedla.countries.roads.algorithms.MyCustomSearchAlgorithm
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

class SearchServiceTest {
    @ParameterizedTest
    @MethodSource("algorithmsProvider")
    fun countryIslands(algorithm: SearchAlgorithm) {
        val service = createService(
            listOf(
                JsonCountry("A1", listOf("A2", "A3")),
                JsonCountry("B1", listOf("B2", "B3"))
            ),
            algorithm
        )

        val route1 = service.searchRoute("A1", "A2", algorithm.getIdentifier())
        assertThat(route1)
            .hasSize(2)
            .containsExactly("A1", "A2")

        val route2 = service.searchRoute("B1", "B3", algorithm.getIdentifier())
        assertThat(route2)
            .hasSize(2)
            .containsExactly("B1", "B3")
    }

    /**
     * ```
     *     D
     *    / \
     *   B   C
     *    \ /
     *     A
     * ```
     */
    @ParameterizedTest
    @MethodSource("algorithmsProvider")
    fun diamondRoute(algorithm: SearchAlgorithm) {
        val service = createService(
            listOf(
                JsonCountry("A", listOf("B", "C")),
                JsonCountry("B", listOf("A", "D")),
                JsonCountry("C", listOf("A", "D")),
                JsonCountry("D", listOf("B", "C"))
            ),
            algorithm
        )

        val route1 = service.searchRoute("A", "D", algorithm.getIdentifier())
        assertThat(route1)
            .hasSize(3)
            .containsExactly("A", "B", "D")

        val route2 = service.searchRoute("D", "A", algorithm.getIdentifier())
        assertThat(route2)
            .hasSize(3)
            .containsExactly("D", "B", "A")
    }

    /**
     * ```
     *     A2         B2
     *    /          /
     *   A1         B1
     *    \          \
     *     A3         B3
     * ```
     */
    @ParameterizedTest
    @MethodSource("algorithmsProvider")
    fun countriesBetweenIslands(algorithm: SearchAlgorithm) {
        val service = createService(
            listOf(
                JsonCountry("A1", listOf("A2", "A3")),
                JsonCountry("B1", listOf("B2", "B3"))
            ),
            algorithm
        )

        val route = service.searchRoute("A1", "B1", algorithm.getIdentifier())
        assertThat(route)
            .isEmpty()
    }

    @ParameterizedTest
    @MethodSource("algorithmsProvider")
    fun sameFromToCountry(algorithm: SearchAlgorithm) {
        val service = createService(listOf(JsonCountry("CZE", listOf())), algorithm)
        val route = service.searchRoute("CZE", "CZE", algorithm.getIdentifier())
        assertThat(route)
            .isEmpty()
    }

    @ParameterizedTest
    @MethodSource("algorithmsProvider")
    fun algorithmInUpperCase(algorithm: SearchAlgorithm) {
        val service = createService(listOf(JsonCountry("CZE", listOf())), algorithm)
        val route = service.searchRoute("CZE", "CZE", algorithm.getIdentifier().toUpperCase())
        assertThat(route)
            .isEmpty()
    }

    @Test
    fun invalidFromCountry() {
        val service = createService(listOf(JsonCountry("CZE", listOf())), dijkstra)
        assertThatThrownBy {
            service.searchRoute("not-a-country", "CZE", dijkstra.getIdentifier())
        }
            .isInstanceOf(IllegalStateException::class.java)
            .hasMessage("Invalid from-country identifier: not-a-country")
    }

    @Test
    fun invalidToCountry() {
        val service = createService(listOf(JsonCountry("DDR", listOf())), dijkstra)
        assertThatThrownBy {
            service.searchRoute("DDR", "not-a-country", dijkstra.getIdentifier())
        }
            .isInstanceOf(IllegalStateException::class.java)
            .hasMessage("Invalid to-country identifier: not-a-country")
    }

    @Test
    fun invalidAlgorithm() {
        val service = createService(listOf(JsonCountry("CZE", listOf()), JsonCountry("SVK", listOf())), dijkstra)
        assertThatThrownBy {
            service.searchRoute("CZE", "SVK", "invalid-algorithm")
        }
            .isInstanceOf(IllegalStateException::class.java)
            .hasMessage("Invalid algorithm-id: invalid-algorithm")
    }

    @Test
    fun duplicateAlgorithmId() {
        assertThatThrownBy {
            SearchService(
                CountriesDatabase(),
                listOf(dijkstra, dijkstra)
            ).also {
                it.afterPropertiesSet()
            }
        }
            .isInstanceOf(IllegalStateException::class.java)
            .hasMessageContaining("Duplicate algorithm key jgrapht-dijkstra of algorithms ")
    }

    companion object {
        val dijkstra = JGraphTDijkstraSearchAlgorithm()

        @JvmStatic
        fun algorithmsProvider() = Stream.of(
            Arguments.of(
                dijkstra,
                MyCustomSearchAlgorithm()
            )
        )!!

        fun createService(
            countries: List<JsonCountry>,
            algorithm: SearchAlgorithm
        ): SearchService {
            val database = CountriesDatabase()
            database.load(countries)
            return SearchService(database, listOf(algorithm)).also {
                it.afterPropertiesSet()
            }
        }
    }
}
