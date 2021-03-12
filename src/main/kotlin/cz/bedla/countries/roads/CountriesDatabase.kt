package cz.bedla.countries.roads

import cz.bedla.countries.loader.JsonCountry
import org.jgrapht.Graph
import org.jgrapht.graph.DefaultEdge
import org.jgrapht.graph.builder.GraphTypeBuilder
import org.springframework.stereotype.Repository

@Repository
class CountriesDatabase {
    val graph = createGraph()

    fun load(data: List<JsonCountry>) {
        data.forEach { country ->
            graph.addVertex(country.id)
            country.borders.forEach {
                graph.addVertex(it)
                if (!graph.containsEdge(country.id, it)) {
                    graph.addEdge(country.id, it)
                }
                if (!graph.containsEdge(it, country.id)) {
                    graph.addEdge(it, country.id)
                }
            }
        }
    }

    fun containsCountry(id: String?): Boolean = graph.containsVertex(id)

    fun notContainsCountry(id: String?): Boolean = !containsCountry(id)
}

class MyEdge : DefaultEdge() {
    public override fun getSource(): String {
        return super.getSource() as? String ?: error("Source vertex is not String but ${super.getSource()}")
    }

    public override fun getTarget(): String {
        return super.getTarget() as? String ?: error("Target vertex is not String but ${super.getTarget()}")
    }
}

fun createGraph(): Graph<String, MyEdge> = GraphTypeBuilder.directed<String, MyEdge>()
    .allowingMultipleEdges(false)
    .allowingSelfLoops(false)
    .edgeClass(MyEdge::class.java)
    .weighted(false)
    .buildGraph()
