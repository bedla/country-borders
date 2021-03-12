package cz.bedla.countries.roads

import org.jgrapht.Graph
import org.jgrapht.graph.DefaultEdge

interface SearchAlgorithm {
    /**
     * Unique identifier of search algorithm
     */
    fun getIdentifier(): String

    fun findRoute(fromId: String, toId: String, graph: Graph<String, MyEdge>): List<String>
}
