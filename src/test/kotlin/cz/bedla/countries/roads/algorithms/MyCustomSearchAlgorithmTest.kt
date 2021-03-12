package cz.bedla.countries.roads.algorithms

import cz.bedla.countries.ApplicationProperties
import cz.bedla.countries.loader.CountryDataLoader
import cz.bedla.countries.loader.CountryDataParser
import cz.bedla.countries.roads.CountriesDatabase
import cz.bedla.countries.roads.createGraph
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder

class MyCustomSearchAlgorithmTest {
    private val algorithm = MyCustomSearchAlgorithm()

    /**
     * ```
     *    v1 -- v3
     *     |     |
     *    v2 -- v4 -- v5
     * ```
     */
    @Test
    fun testTraversal() {
        val graph = createGraph()
        graph.addVertex("v1")
        graph.addVertex("v2")
        graph.addVertex("v3")
        graph.addVertex("v4")
        graph.addVertex("v5")
        graph.addEdge("v1", "v2")
        graph.addEdge("v2", "v1")
        graph.addEdge("v2", "v3")
        graph.addEdge("v3", "v2")
        graph.addEdge("v2", "v4")
        graph.addEdge("v4", "v2")
        graph.addEdge("v3", "v4")
        graph.addEdge("v4", "v3")
        graph.addEdge("v4", "v5")
        graph.addEdge("v5", "v4")

        val route = algorithm.findRoute("v1", "v5", graph)
        assertThat(route)
            .hasSize(4)
            .containsExactly("v1", "v2", "v4", "v5")
    }

    /**
     * When graph was created as unordered, returned edges had swapped source-target vertices. We were able to detect
     * it only when special order of data load has been invoked (see `cze-ita.json` file).
     */
    @Test
    fun orderedCountries() {
        val database = createDatabase("cze-ita.json")
        val route = algorithm.findRoute("CZE", "ITA", database.graph)
        assertThat(route)
            .hasSize(3)
            .containsExactly("CZE", "AUT", "ITA")
    }

    private fun createDatabase(classPathResource: String): CountriesDatabase {
        val loader = CountryDataLoader(
            ApplicationProperties(
                Thread.currentThread().contextClassLoader.getResource(classPathResource)!!.toURI(),
                "xxx"
            ),
            RestTemplateBuilder()
        )
        val resource = loader.downloadData()
        val parser = CountryDataParser(Jackson2ObjectMapperBuilder.json())
        val data = parser.parseData(resource)
        val database = CountriesDatabase()
        database.load(data)
        return database
    }
}
