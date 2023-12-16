import java.io.File
import java.util.ArrayDeque

typealias Coord = Pair<Int, Int>

val file = if(args.contains("-i")) args[1 + args.indexOf("-i")] else ""

val contraption = File(file).readLines()
val NORTH = 0
val EAST = 1
val SOUTH = 2
val WEST = 3

var maxPower = 0

val corners = listOf(
    Coord(contraption.size - 1, contraption[0].length - 1), 
    Coord(contraption.size - 1, 0),
    Coord(0, 0), 
    Coord(0, contraption[0].length - 1), 
)
val increments = listOf(Coord(0, -1), Coord(-1, 0), Coord(0, 1), Coord(1, 0))

for (dir in NORTH..WEST) {
    var initialCoord = corners[dir]
    val increment = increments[dir]
    val size = if (dir % 2 == 0) contraption.size else contraption[0].length
    for (space in 0 until size) {
        maxPower = Math.max(maxPower, findPoweredSpaces(initialCoord, dir))
        initialCoord = initialCoord + increment
    }
}

println(maxPower)


fun findPoweredSpaces(initialCoord: Coord, initialDir: Int): Int {
    val queue = ArrayDeque<Pair<Coord, Int>>()
    val powered = mutableSetOf<Coord>()
    val visited = mutableSetOf<Pair<Coord, Int>>()

    queue.add(initialCoord to initialDir)

    while(!queue.isEmpty()) {
        val (coord, dir) = queue.removeFirst()
        if (!coord.inRange() || visited.contains(coord to dir)) continue
        powered.add(coord)
        visited.add(coord to dir)
        val next = getNextCoords(coord, dir)
        next.forEach { queue.addFirst(it) }
    }
    return powered.size
}

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