package cz.bedla.countries.roads.algorithms

import cz.bedla.countries.roads.SearchAlgorithm
import org.jgrapht.Graph
import org.jgrapht.alg.shortestpath.DijkstraShortestPath
import org.jgrapht.graph.DefaultEdge
import org.springframework.stereotype.Component

@Component
class JGraphTDijkstraSearchAlgorithm : SearchAlgorithm {
    override fun getIdentifier(): String = "jgrapht-dijkstra"

    override fun findRoute(fromId: String, toId: String, graph: Graph<String, DefaultEdge>): List<String> {
        val dijkstra = DijkstraShortestPath(graph)
        return dijkstra.getPath(fromId, toId)?.vertexList ?: emptyList()
    }
}
