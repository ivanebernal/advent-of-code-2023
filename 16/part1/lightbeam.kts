import java.io.File
import java.util.ArrayDeque

typealias Coord = Pair<Int, Int>

val file = if(args.contains("-i")) args[1 + args.indexOf("-i")] else ""

val contraption = File(file).readLines()
val NORTH = 0
val EAST = 1
val SOUTH = 2
val WEST = 3

// coord -> direction
val queue = ArrayDeque<Pair<Coord, Int>>()
val powered = mutableSetOf<Coord>()
val visited = mutableSetOf<Pair<Coord, Int>>()

queue.add(Pair(Coord(0, 0), EAST))

while(!queue.isEmpty()) {
    val (coord, dir) = queue.removeFirst()
    if (!coord.inRange() || visited.contains(coord to dir)) continue
    powered.add(coord)
    visited.add(coord to dir)
    val next = getNextCoords(coord, dir)
    next.forEach { queue.addFirst(it) }
}

println(powered.size)

fun getNextCoords(coord: Coord, direction: Int): List<Pair<Coord, Int>> {
    val char = contraption[coord]
    return when (char) {
        '.' -> {
            listOf((coord + direction.toDirection()) to direction)
        }
        '|' -> {
            if (direction % 2 == 0) listOf((coord + direction.toDirection()) to direction)
            else listOf((direction + 1) % 4, ((direction + 3) % 4)).map { (coord + it.toDirection()) to it }
        }
        '-' -> {
            if (direction % 2 == 1) listOf((coord + direction.toDirection()) to direction)
            else listOf((direction + 1) % 4, (direction + 3) % 4).map { (coord + it.toDirection()) to it }
        }
        '\\' -> {
            if (direction % 2 == 1) listOf((direction + 1) % 4).map { (coord + it.toDirection()) to it }
            else listOf((direction + 3) % 4).map { (coord + it.toDirection()) to it }
        }
        '/' -> {
            if (direction % 2 == 1) listOf((direction + 3) % 4).map { (coord + it.toDirection()) to it }
            else listOf((direction + 1) % 4).map { (coord + it.toDirection()) to it }
        }
        else -> error("Invalid char")
    }
}

fun Coord.inRange() = first in contraption.indices && second in contraption[0].indices
operator fun List<String>.get(coord: Coord) = this[coord.first][coord.second]
operator fun Coord.plus(other: Coord) = Coord(first + other.first, second + other.second)
operator fun Coord.minus(other: Coord) = Coord(first - other.first, second - other.second)

fun Int.toDirection() = when(this) {
    0 -> Coord(-1, 0) 
    1 -> Coord(0, 1)
    2 -> Coord(1, 0)
    3 -> Coord(0, -1)
    else -> error("Invalid direction")
}