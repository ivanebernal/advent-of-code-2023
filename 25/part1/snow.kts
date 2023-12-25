import java.io.File
import kotlin.random.Random
import java.util.ArrayDeque

typealias Edge = Pair<String, String>

val file = if(args.contains("-i")) args[1 + args.indexOf("-i")] else ""

val connections = mutableMapOf<String, MutableSet<String>>()
val seenEdges = mutableMapOf<Edge, Int>()

val trails = File(file).forEachLine { line ->
    val fromTo = line.split(": ")
    val from = fromTo[0]
    val to = fromTo[1].split(" ")

    to.forEach { destination ->
        connections.getOrPut(destination) { mutableSetOf() }.add(from)
        connections.getOrPut(from) { mutableSetOf() }.add(destination)
    }
}

val keys = connections.keys.toList()

repeat(1000) {
    val start = keys[Random.nextInt(keys.size)]
    val end = keys[Random.nextInt(keys.size)]
    getPath(start, end)?.let { path ->
        for (i in 0 until path.size - 1) {
            val first = maxOf(path[i], path[i + 1])
            val second = minOf(path[i], path[i + 1])
            val edge = first to second
            seenEdges[edge] = (seenEdges[edge] ?: 0) + 1
        }
    }
}

val sortedEdges = seenEdges.map { (edge, count) -> edge to count }.sortedBy { it.second }
val edge1 = sortedEdges[sortedEdges.size - 1].first
val edge2 = sortedEdges[sortedEdges.size - 2].first
val edge3 = sortedEdges[sortedEdges.size - 3].first

println("$edge1 $edge2 $edge3")

// remove edges
listOf(edge1, edge2, edge3).forEach { edge ->
    connections[edge.first]!!.remove(edge.second)
    connections[edge.second]!!.remove(edge.first)
}

val seen = mutableSetOf<String>()
val start = keys.first()
val queue = ArrayDeque<String>()
queue.add(start)
while(!queue.isEmpty()) {
    val v = queue.poll()
    if (seen.contains(v)) continue
    seen.add(v)
    connections[v]?.forEach { queue.add(it) }
}

println(seen.size * (keys.size - seen.size))


fun getPath(start: String, end: String): List<String>? {
    val prev = mutableMapOf<String, String>()
    var nodes = listOf<String>(start)
    val seen = mutableSetOf<String>()
    while(!nodes.isEmpty()) {
        val newNodes = mutableListOf<String>()
        for (node in nodes) {
            for (neighbor in connections[node]!!) {
                if(seen.contains(neighbor)) continue
                seen.add(neighbor)
                prev[neighbor] = node
                newNodes.add(neighbor)
            }
        }
        nodes = newNodes
    }
    if (!prev.contains(end)) return null
    val path = mutableListOf<String>()
    var node = end
    while (node != start) {
        path.add(node)
        node = prev[node]!!
    }
    path.add(start)
    return path.reversed()
}
