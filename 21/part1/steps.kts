import java.io.File
import java.util.ArrayDeque

typealias Coord = Pair<Int, Int>

val file = if(args.contains("-i")) args[1 + args.indexOf("-i")] else ""

val plot = File(file).readLines()

val startingRow = plot.indexOfFirst{ it.contains('S') }
val startingCol = plot[startingRow].indexOf('S')

val start = startingRow to startingCol

println("Start: $start")

fun countPossibilities(from: Coord, steps: Int): Int {
    var queue = ArrayDeque<Pair<Coord, Int>>()
    val endPossitions = mutableSetOf<Coord>()
    val directions = listOf(-1 to 0, 0 to 1, 1 to 0, 0 to -1)
    if (steps % 2 == 1) {
        directions.forEach { queue.addLast(it + from to 1) }
    } else {
        queue.addFirst(from to 0)
    }

    while(!queue.isEmpty()) {
        val (currPos, stepsTaken) = queue.poll()

        if (
            endPossitions.contains(currPos) || 
            !currPos.inRange() || 
            plot[currPos] == '#' || 
            stepsTaken > steps
        ) {
            continue
        }
        endPossitions.add(currPos)

        listOf(
            -2 to 0,
            0 to 2,
            2 to 0,
            0 to -2
        ).forEachIndexed { index, increment ->
            val immediateNext = currPos + directions[index]
            if (immediateNext.inRange() && plot[immediateNext] != '#') {
                queue.addLast(increment + currPos to stepsTaken + 2)
            }
        }

        listOf(
            -1 to 1,
            1 to 1,
            1 to -1,
            -1 to -1, 
        ).forEachIndexed { index, increment ->
            val immediateNext = currPos + directions[index]
            val nextImmediateNext = currPos + directions[(index + 1) % 4]
            if ((immediateNext.inRange() && plot[immediateNext] != '#') || (nextImmediateNext.inRange() && plot[nextImmediateNext] != '#')) {
                queue.addLast(increment + currPos to stepsTaken + 2)   
            }
        }
    }
    plot.forEachIndexed { row, r ->
        r.forEachIndexed { col, char ->
            print(if(endPossitions.contains(row to col)) 'O' else plot[row to col])
        }
        println()
    }
    return endPossitions.size
}

println(countPossibilities(start, 64))

fun Coord.inRange() = first in plot.indices && second in plot[0].indices
operator fun List<String>.get(coord: Coord): Char = (this[coord.first][coord.second])
operator fun Coord.plus(other: Coord) = Coord(first + other.first, second + other.second)