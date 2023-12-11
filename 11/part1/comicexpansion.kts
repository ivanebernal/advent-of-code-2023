import java.io.File
import java.util.ArrayDeque

typealias Coord = Pair<Int, Int>

val file = if(args.contains("-i")) args[1 + args.indexOf("-i")] else ""

val space = File(file).readLines()

var rOff = 0
var cOff = 0
val rowOffset = mutableListOf<Int>()
val colOffset = mutableListOf<Int>()
val galaxies = mutableListOf<Coord>()
space.forEachIndexed { index, row -> 
    if(row.all { it == '.' }) rOff++
    rowOffset.add(rOff)
}
for (col in 0 until space.first().length) {
    if (space.all { it[col] == '.' }) cOff++
    colOffset.add(cOff)
}

space.forEachIndexed { row, sRow ->
    sRow.forEachIndexed { col, spot ->
        if (spot == '#') galaxies.add(Coord(row, col))
    }
}

var sumOfShortest = 0

for (g1 in 0 until galaxies.size - 1) {
    for (g2 in g1 + 1 until galaxies.size) {
        val galaxy1 = galaxies[g1].let { it + Coord(rowOffset[it.first], colOffset[it.second]) }
        val galaxy2 = galaxies[g2].let { it + Coord(rowOffset[it.first], colOffset[it.second]) }
        sumOfShortest += galaxy1.shortest(galaxy2)
    }
}

println(sumOfShortest)

fun Coord.shortest(other: Coord): Int {
    return Math.abs(this.first - other.first) + Math.abs(this.second - other.second)
}

operator fun Coord.plus(other: Coord): Coord {
    return Coord(this.first + other.first, this.second + other.second)
}