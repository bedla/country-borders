package cz.bedla.countries.roads.algorithms

import cz.bedla.countries.roads.MyEdge
import cz.bedla.countries.roads.SearchAlgorithm
import org.jgrapht.Graph
import org.springframework.stereotype.Component

/**
 * Breadth-first search of route. Basically spanning tree.
 */
@Component
class MyCustomSearchAlgorithm : SearchAlgorithm {
    override fun getIdentifier(): String = "my-custom"

    override fun findRoute(fromId: String, toId: String, graph: Graph<String, MyEdge>): List<String> {
        val seen = mutableSetOf<String>()

        val verticesToProcess = ArrayDeque<String>()
        verticesToProcess.addFirst(fromId)

        val nodes = mutableMapOf<String, Node>()
        // root Node is created from from-id vertex
        var parent: Node? = nodes.computeIfAbsent(fromId) { key -> Node(key, null) }
        var foundParent: Node? = null
        main@ while (verticesToProcess.isNotEmpty()) {
            val startVertex = verticesToProcess.removeFirst()
            seen.add(startVertex)

            // current start-vertex is new Parent
            parent = nodes[startVertex] ?: error("Unable to find start vertex.id=$startVertex in node-list=$nodes")

            val outgoingEdges = graph.outgoingEdgesOf(startVertex)!!
            for (edge in outgoingEdges) {
                val endVertex = edge.target

                if (endVertex == toId) {
                    foundParent = nodes.computeIfAbsent(endVertex) { key -> Node(key, parent) }
                    break@main
                }

                if (!seen.contains(endVertex)) {
                    // link end-vertex Node with parent-vertex Node
                    nodes.computeIfAbsent(endVertex) { key -> Node(key, parent) }
                    verticesToProcess.addLast(endVertex)
                }
            }
        }

        return if (foundParent != null) {
            val route = mutableListOf<String>()
            var node = nodes[foundParent.id]
            while (node != null) {
                route.add(node.id)
                node = node.parent
            }
            return route.reversed()
        } else {
            emptyList()
        }
    }
}

data class Node(
    val id: String,
    val parent: Node?
)
