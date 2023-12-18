import java.io.File
// import java.util.ArrayDeque

typealias Coord = Pair<Int, Int>

val dirMap = mapOf(
    "U" to (-1 to 0),
    "R" to (0 to 1),
    "D" to (1 to 0),
    "L" to (0 to -1)
)

val file = if(args.contains("-i")) args[1 + args.indexOf("-i")] else ""
val instructions = File(file).readLines()
var currPos = 0 to 0
var minColMap = mutableMapOf<Int, Int>(0 to 0)
var maxColMap = mutableMapOf<Int, Int>(0 to 0)
var dugHoles = mutableMapOf<Coord, String>(currPos to instructions[0].split(" ")[0])

instructions.forEach { instruction ->
    val instList = instruction.split(" ")
    val direction = instList[0]
    val quantity = instList[1].toInt()
    val color = instList[2]

    repeat(quantity) {
        currPos += dirMap[direction]!!
        dugHoles[currPos] = direction
        minColMap[currPos.row] = minOf(minColMap[currPos.row] ?: Int.MAX_VALUE, currPos.col)
        maxColMap[currPos.row] = maxOf(maxColMap[currPos.row] ?: Int.MIN_VALUE, currPos.col)
    }
}

println(dugHoles.size)

var areaInside = 0
for (row in minColMap.keys) {
    var inside = false
    var intersect: String? = null
    for (col in minColMap[row]!!..maxColMap[row]!!) {
        // Enter horizontal line
        if (
            dugHoles.contains(row to col) && 
            dugHoles.contains(row to col + 1) && 
            intersect == null && 
            (dugHoles[row to col] == "L" || dugHoles[row to col] == "R" || dugHoles[row to col + 1] == "L" || dugHoles[row to col + 1] == "R")
        ) {
            intersect = if (dugHoles.contains(row - 1 to col)) "U" else "D"
        } else if (dugHoles.contains(row to col) && intersect != null && !dugHoles.contains(row to col + 1)) {
            val exitIntersect = if (dugHoles.contains(row - 1 to col)) "U" else "D"
            if (exitIntersect != intersect) {
                inside = !inside
            }
            intersect = null
        } else if (dugHoles.contains(row to col) && (dugHoles[row to col] == "U" || dugHoles[row to col] == "D")) {
            inside = !inside
        } else if (inside && intersect == null) {
            areaInside++
        }
    }
}

println(areaInside + dugHoles.size)

operator fun Coord.plus(other: Coord) = Coord(first + other.first, second + other.second)
operator fun Coord.minus(other: Coord) = Coord(first - other.first, second - other.second)
val Coord.row: Int
    get() = this.first
val Coord.col: Int
    get() = this.second

