package cz.bedla.countries.roads

import cz.bedla.countries.loader.JsonCountry
import org.jgrapht.Graph
import org.jgrapht.graph.DefaultEdge
import org.jgrapht.graph.builder.GraphTypeBuilder
import org.springframework.stereotype.Component

@Component
class CountriesDatabase {
    val graph: Graph<String, DefaultEdge> = GraphTypeBuilder.undirected<String, DefaultEdge>()
        .allowingMultipleEdges(false)
        .allowingSelfLoops(false)
        .edgeClass(DefaultEdge::class.java)
        .weighted(false)
        .buildGraph()

    fun load(data: List<JsonCountry>) {
        data.forEach { country ->
            graph.addVertex(country.id)
            country.borders.forEach {
                graph.addVertex(it)
                graph.addEdge(country.id, it)
            }
        }
    }

    fun containsCountry(id: String?): Boolean = graph.containsVertex(id)

    fun notContainsCountry(id: String?): Boolean = !containsCountry(id)
}
