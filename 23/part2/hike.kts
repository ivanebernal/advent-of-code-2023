import java.io.File
import java.util.ArrayDeque

typealias Coord = Pair<Int, Int>

val file = if(args.contains("-i")) args[1 + args.indexOf("-i")] else ""
val trails = File(file).readLines()

val dirs = listOf(-1 to 0, 0 to 1, 1 to 0, 0 to -1)

val start = 0 to 1
val end = trails.size -1 to trails[0].length - 2

val queue = ArrayDeque<Hike>()
queue.addFirst(Hike(start, 2)) 

var maxSteps = 0
var maxPath = setOf<Coord>()

while (!queue.isEmpty()) {
    val currHike = queue.pollFirst()
    val (currPos, currDir, currPath) = currHike
    if (currPos == end) {
        maxSteps = maxOf(maxSteps, currPath.size)
        maxPath = if (maxPath.size > currPath.size) maxPath else currPath
        continue
    }
    currPath.add(currPos)
    val terrain = trails[currPos]
        val possibleDirs = dirs.filter { dir -> currHike.canTakeStepInDir(dirs.indexOf(dir)) }
        if (possibleDirs.count() == 1) {
            currHike.currDir = dirs.indexOf(possibleDirs.first())
            currHike.currentPos = currHike.currentPos + possibleDirs.first()
            queue.addFirst(currHike)
        } else {
            possibleDirs.forEach { dir ->
                val index = dirs.indexOf(dir)
                if (index != currDir) {
                    val anotherHike = Hike(
                        currDir = index,
                        currentPos = currHike.currentPos + dir,
                        path = currPath.toMutableSet()
                    )
                    queue.addFirst(anotherHike)
                }
            }
            if (possibleDirs.any { dirs[currDir] == it }) {
                currHike.currentPos = currHike.currentPos + dirs[currDir]
                queue.addFirst(currHike)
            }
        }
}

fun printPath(path: Set<Coord>) {
    trails.forEachIndexed { row, r ->
        r.forEachIndexed { col, c ->
            if(path.contains(row to col)) print('O') else print(c)
        }
        println()
    }
}

printPath(maxPath)

println(maxSteps)

fun Coord.inRange() = first in trails.indices && second in trails[0].indices
operator fun List<String>.get(coord: Coord): Char = this[coord.first][coord.second]
operator fun Coord.plus(other: Coord) = Coord(first + other.first, second + other.second)
fun Hike.canTakeStepInDir(dir: Int): Boolean { 
    val nextPos = dirs[dir] + currentPos
    return nextPos.inRange() && trails[nextPos] != '#' && !path.contains(nextPos)
}

data class Hike(
    var currentPos: Coord,
    var currDir: Int,
    val path: MutableSet<Coord> = mutableSetOf()
)