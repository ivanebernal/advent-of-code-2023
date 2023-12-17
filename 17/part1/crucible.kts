import java.io.File
import java.util.PriorityQueue

typealias Coord = Pair<Int, Int>

val file = if(args.contains("-i")) args[1 + args.indexOf("-i")] else ""

data class Step(val coord: Coord, val direction: Int, val stepsTaken: Int, val heatLoss: Int): Comparable<Step> {
    override fun compareTo(other: Step) = heatLoss - other.heatLoss
}

val LEFT = 3
val RIGHT = 1
val directions = listOf<Coord>(-1 to 0, 0 to 1, 1 to 0, 0 to -1)

val heatMap = File(file).readLines()

val memo = Array(heatMap.size) { // rows
    Array(heatMap[0].length) { // cols
        Array(4) { // direction
            Array(3) { // steps
                Int.MAX_VALUE
            }
        }
    }
}

val queue = PriorityQueue<Step>()

queue.offer(Step(0 to 1, 1, 1, 0))
queue.offer(Step(1 to 0, 2, 1, 0))

var lowestHeatLoss = Int.MAX_VALUE

while (!queue.isEmpty()) {
    val step = queue.poll()
    val coord = step.coord
    if (!coord.inRange()) continue
    val currLoss = step.heatLoss + heatMap[coord]
    if (currLoss < memo[coord]!![step.direction][step.stepsTaken - 1]!!) {
        val (_, dir, steps, loss) = step
        memo[coord]!![dir][steps - 1] = currLoss
        val l = dir.turn(LEFT)
        val r = dir.turn(RIGHT)
        val nextL = coord + l.increment()
        val nextR = coord + r.increment()
        val nextS = coord + dir.increment()
        if (coord == (heatMap.size - 1) to (heatMap[0].length - 1)) {
            lowestHeatLoss = minOf(lowestHeatLoss, currLoss)
            break
        }
        queue.offer(Step(nextL, l, 1, currLoss))
        queue.offer(Step(nextR, r, 1, currLoss))
        if (steps < 3) queue.offer(Step(nextS, dir, steps + 1, currLoss))
    } 
}

println(lowestHeatLoss)

fun Int.increment() = directions[this]
fun Int.turn(direction: Int) = (this + direction) % 4
fun Coord.inRange() = first in heatMap.indices && second in heatMap[0].indices
operator fun List<String>.get(coord: Coord): Int = (this[coord.first][coord.second] - '0')
operator fun Coord.plus(other: Coord) = Coord(first + other.first, second + other.second)
operator fun Array<Array<Array<Array<Int>>>>.get(coord: Coord): Array<Array<Int>> = this[coord.first][coord.second]
