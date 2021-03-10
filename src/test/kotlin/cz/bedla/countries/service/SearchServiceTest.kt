package cz.bedla.countries.service

import cz.bedla.countries.loader.JsonCountry
import cz.bedla.countries.roads.CountriesDatabase
import cz.bedla.countries.roads.algorithms.JGraphTDijkstraSearchAlgorithm
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

class SearchServiceTest {
    private val dijkstra = JGraphTDijkstraSearchAlgorithm()

    @Test
    fun countryIslands() {
        val service = createService(
            listOf(
                JsonCountry("A1", listOf("A2", "A3")),
                JsonCountry("B1", listOf("B2", "B3"))
            )
        )

        val route1 = service.searchRoute("A1", "A2", dijkstra.getIdentifier())
        assertThat(route1)
            .hasSize(2)
            .containsExactly("A1", "A2")

        val route2 = service.searchRoute("B1", "B3", dijkstra.getIdentifier())
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
    @Test
    fun diamondRoute() {
        val service = createService(
            listOf(
                JsonCountry("A", listOf("B", "C")),
                JsonCountry("B", listOf("A", "D")),
                JsonCountry("C", listOf("A", "D")),
                JsonCountry("D", listOf("B", "C"))
            )
        )

        val route1 = service.searchRoute("A", "D", dijkstra.getIdentifier())
        assertThat(route1)
            .hasSize(3)
            .containsExactly("A", "B", "D")

        val route2 = service.searchRoute("D", "A", dijkstra.getIdentifier())
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
    @Test
    fun countriesBetweenIslands() {
        val service = createService(
            listOf(
                JsonCountry("A1", listOf("A2", "A3")),
                JsonCountry("B1", listOf("B2", "B3"))
            )
        )

        val route = service.searchRoute("A1", "B1", dijkstra.getIdentifier())
        assertThat(route)
            .isEmpty()
    }

    @Test
    fun sameFromToCountry() {
        val service = createService(listOf(JsonCountry("CZE", listOf())))
        val route = service.searchRoute("CZE", "CZE", dijkstra.getIdentifier())
        assertThat(route)
            .isEmpty()
    }

    @Test
    fun algorithmInUpperCase() {
        val service = createService(listOf(JsonCountry("CZE", listOf())))
        val route = service.searchRoute("CZE", "CZE", dijkstra.getIdentifier().toUpperCase())
        assertThat(route)
            .isEmpty()
    }

    @Test
    fun invalidFromCountry() {
        val service = createService(listOf(JsonCountry("CZE", listOf())))
        assertThatThrownBy {
            service.searchRoute("not-a-country", "CZE", dijkstra.getIdentifier())
        }
            .isInstanceOf(IllegalStateException::class.java)
            .hasMessage("Invalid from-country identifier: not-a-country")
    }

    @Test
    fun invalidToCountry() {
        val service = createService(listOf(JsonCountry("DDR", listOf())))
        assertThatThrownBy {
            service.searchRoute("DDR", "not-a-country", dijkstra.getIdentifier())
        }
            .isInstanceOf(IllegalStateException::class.java)
            .hasMessage("Invalid to-country identifier: not-a-country")
    }

    @Test
    fun invalidAlgorithm() {
        val service = createService(listOf(JsonCountry("CZE", listOf()), JsonCountry("SVK", listOf())))
        assertThatThrownBy {
            service.searchRoute("CZE", "SVK", "invalid-algorithm")
        }
            .isInstanceOf(IllegalStateException::class.java)
            .hasMessage("Invalid algorithm-id: invalid-algorithm")
    }

    @Test
    fun duplicateAlgorithmId() {
        assertThatThrownBy {
            SearchService(CountriesDatabase(), listOf(dijkstra, dijkstra)).also {
                it.afterPropertiesSet()
            }
        }
            .isInstanceOf(IllegalStateException::class.java)
            .hasMessageContaining("Duplicate algorithm key jgrapht-dijkstra of algorithms ")
    }

    private fun createService(countries: List<JsonCountry>): SearchService {
        val database = CountriesDatabase()
        database.load(countries)
        return SearchService(database, listOf(dijkstra)).also {
            it.afterPropertiesSet()
        }
    }
}
