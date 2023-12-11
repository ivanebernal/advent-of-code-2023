import java.io.File
import java.util.ArrayDeque

typealias Coord = Pair<Int, Int>

val file = if(args.contains("-i")) args[1 + args.indexOf("-i")] else ""

// map that indicates which directions are accessible depending on the pipe symbol.
// the list is in the following order: n, e, s, w

val accessMap = mapOf(
    '-' to listOf(0, 1, 0, 1),
    '|' to listOf(1, 0, 1, 0),
    'L' to listOf(1, 1, 0, 0),
    'J' to listOf(1, 0, 0, 1),
    '7' to listOf(0, 0, 1, 1),
    'F' to listOf(0, 1, 1, 0),
    '.' to listOf(0, 0, 0, 0),
    'S' to listOf(1, 1, 1, 1),
)

val pipeLoop = File(file).readLines()
val visited = mutableSetOf<Coord>()

var start = Coord(-1, -1)

for (r in 0 until pipeLoop.size) {
    for (c in 0 until pipeLoop[r].length) {
        if (pipeLoop[r][c] == 'S') {
            start = Coord(r, c)
            break;
        }
    }
    if (start != Coord(-1, -1)) {
        break;
    }
}

val queue = ArrayDeque<Pair<Coord, Int>>()
val maxLoop = findMaxLoop()
val resultSteps = maxLoop

println("Farthest steps from start are $resultSteps")

fun findMaxLoop(): Int {
    queue.add(Pair(start, 0))
    var maxLoop = 0
    while(!queue.isEmpty()) {
        val (coord, steps) = queue.poll()
        visited.add(coord)
        maxLoop = Math.max(maxLoop, steps)
        findAccessibleSpaces(coord).forEach {
            queue.add(Pair(it, steps + 1))
        }
    }
    return maxLoop
}

fun findAccessibleSpaces(coord: Coord): List<Coord> {
    // n, e, s, w -> s, w, n, e from the perspective of the space we are going to
    return listOf(Coord(-1, 0), Coord(0, 1), Coord(1, 0), Coord(0, -1))
        .map { it + coord }
        .filterIndexed { index, nextCoord -> 
            accessMap[pipeLoop[coord]]!![index] == 1 && pipeLoop.inRange(nextCoord) && 
                !visited.contains(nextCoord) && accessMap[pipeLoop[nextCoord]]!![(index + 2) % 4] == 1
        }
}

operator fun Coord.plus(other: Coord): Coord {
    return Coord(this.first + other.first, this.second + other.second)
}

operator fun List<String>.get(coord: Coord): Char = this[coord.first][coord.second]

fun List<String>.inRange(coord: Coord): Boolean = 
    coord.first >= 0 && coord.second >= 0 && 
    this.size > 0 && coord.first < this.size && 
    !this[0].isEmpty() && coord.second < this[0].length