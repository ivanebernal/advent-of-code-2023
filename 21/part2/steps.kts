import java.io.File
import java.util.ArrayDeque

typealias Coord = Pair<Int, Int>

val file = if(args.contains("-i")) args[1 + args.indexOf("-i")] else ""

val plot = File(file).readLines()

val startingRow = plot.indexOfFirst{ it.contains('S') }
val startingCol = plot[startingRow].indexOf('S')

val start = startingRow to startingCol

println("Start: $start")

val visitedTiles = mutableMapOf<Coord, Long>()

fun visitTiles(from: Coord): Map<Coord, Long> {
    var queue = ArrayDeque<Pair<Coord, Long>>()
    val directions = listOf(-1 to 0, 0 to 1, 1 to 0, 0 to -1)
    queue.add(from to 0L)

    while(!queue.isEmpty()) {
        val (currPos, stepsTaken) = queue.poll()

        if (visitedTiles.contains(currPos)) {
            continue
        }
        visitedTiles[currPos] = stepsTaken

        listOf(
            -1 to 0,
            0 to 1,
            1 to 0,
            0 to -1
        ).forEachIndexed { index, increment ->
            val next = increment + currPos
            if (next.inRange() && !visitedTiles.contains(next) && plot[next] != '#') {
                queue.addLast(next to stepsTaken + 1L)
            }
        }
    }

    return visitedTiles
}


visitTiles(start)

// the input is a special case, side size is 131, the step number 26501365 is (131/2) + 131 * 202300
val n = 202300L

val odd = (n + 1) * (n + 1)
val visitedOdd = visitedTiles.values.filter { it % 2L == 1L }.count()
val oddCorners = visitedTiles.values.filter { it % 2L == 1L && it > 65L }.count()

val even = n * n
val visitedEven = visitedTiles.values.filter { it % 2L == 0L }.count()
val evenCorners = visitedTiles.values.filter { it % 2L == 0L && it > 65L }.count()

val result = (visitedEven * even) + (visitedOdd * odd) - ((n + 1) * oddCorners) + (n * evenCorners) - n

println(result)

fun Coord.inRange() = first in plot.indices && second in plot[0].indices
// was a cool idea, but the approach was too slow :(
// operator fun List<String>.get(coord: Coord): Char {
//     val row = if (coord.first >= 0) coord.first % plot.size else ((coord.first % plot.size) + plot.size) % plot.size
//     val col = if (coord.second >= 0) coord.second % plot[0].length else ((coord.second % plot[0].length) + plot[0].length) % plot[0].length
//     return this[row][col]
// }
operator fun List<String>.get(coord: Coord): Char = (this[coord.first][coord.second])
operator fun Coord.plus(other: Coord) = Coord(first + other.first, second + other.second)