import java.io.File
import java.util.ArrayDeque

typealias Coord = Pair<Long, Long>

val file = if(args.contains("-i")) args[1 + args.indexOf("-i")] else ""

val space = File(file).readLines()

var rOff = 0L
var cOff = 0L
val offset = 1000000L-1L
val rowOffset = mutableListOf<Long>()
val colOffset = mutableListOf<Long>()
val galaxies = mutableListOf<Coord>()
space.forEachIndexed { index, row -> 
    if(row.all { it == '.' }) rOff += offset
    rowOffset.add(rOff)
}
for (col in 0 until space.first().length) {
    if (space.all { it[col] == '.' }) cOff+= offset
    colOffset.add(cOff)
}

space.forEachIndexed { row, sRow ->
    sRow.forEachIndexed { col, spot ->
        if (spot == '#') galaxies.add(Coord(row.toLong(), col.toLong()))
    }
}

var sumOfShortest = 0L

for (g1 in 0 until galaxies.size - 1) {
    for (g2 in g1 + 1 until galaxies.size) {
        val galaxy1 = galaxies[g1].let { it + Coord(rowOffset[it.first.toInt()], colOffset[it.second.toInt()]) }
        val galaxy2 = galaxies[g2].let { it + Coord(rowOffset[it.first.toInt()], colOffset[it.second.toInt()]) }
        sumOfShortest += galaxy1.shortest(galaxy2)
    }
}

println(sumOfShortest)

fun Coord.shortest(other: Coord): Long {
    return Math.abs(this.first - other.first) + Math.abs(this.second - other.second)
}

operator fun Coord.plus(other: Coord): Coord {
    return Coord(this.first + other.first, this.second + other.second)
}