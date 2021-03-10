package cz.bedla.countries.roads.algorithms

import cz.bedla.countries.roads.SearchAlgorithm
import org.jgrapht.Graph
import org.jgrapht.graph.DefaultEdge
import org.springframework.stereotype.Component

@Component
class MyCustomSearchAlgorithm : SearchAlgorithm {
    override fun getIdentifier(): String = "my-custom"

    override fun findRoute(fromId: String, toId: String, graph: Graph<String, DefaultEdge>): List<String> =
        error("I am not implemented")
}
