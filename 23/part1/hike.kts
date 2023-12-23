import java.io.File
import java.util.ArrayDeque

typealias Coord = Pair<Int, Int>

val file = if(args.contains("-i")) args[1 + args.indexOf("-i")] else ""

val trails = File(file).readLines()

val restrictedSteps = mapOf((0 to -1) to '>', (1 to 0) to '^', (0 to 1) to '<', (-1 to 0) to 'v')
val slopeDirs = mapOf('>' to (0 to 1), '^' to (-1 to 0), '<' to (0 to -1), 'v' to (1 to 0))
val dirs = listOf(-1 to 0, 0 to 1, 1 to 0, 0 to -1)

val start = 0 to 1
val end = trails.size -1 to trails[0].length - 2

val queue = ArrayDeque<Hike>()
queue.addFirst(Hike(start)) 

var maxSteps = 0
var maxPath = setOf<Coord>()

while (!queue.isEmpty()) {
    val (currPos, currPath) = queue.pollFirst()
    if (currPos == end) {
        maxSteps = maxOf(maxSteps, currPath.size)
        maxPath = currPath
        continue
    }
    val path = currPath.toMutableSet()
    path.add(currPos)
    val terrain = trails[currPos]
    if (terrain.isSlope()) {
        val nextPos = slopeDirs[terrain]!! + currPos
        queue.add(Hike(nextPos, path))
    } else {
        val nextPos = dirs.associate { it to it + currPos }
            .filter { (step, nextPos) ->
                nextPos.inRange() && trails[nextPos] != '#' && trails[nextPos] != restrictedSteps[step] && !path.contains(nextPos)
            }.forEach { (step, nextPos) ->
                queue.addLast(Hike(nextPos, path))
            }
    }
}

// trails.forEachIndexed { row, r ->
//     r.forEachIndexed { col, c ->
//         if(maxPath.contains(row to col)) print('O') else print(c)
//     }
//     println()
// }

println(maxSteps)

fun Char.isSlope() = this == '<' || this == '^' || this == '>' || this == 'v'
fun Coord.inRange() = first in trails.indices && second in trails[0].indices
operator fun List<String>.get(coord: Coord): Char = this[coord.first][coord.second]
operator fun Coord.plus(other: Coord) = Coord(first + other.first, second + other.second)

data class Hike(
    val currentPos: Coord,
    val path: Set<Coord> = setOf()
)