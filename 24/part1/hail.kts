import java.io.File
import java.util.ArrayDeque
import kotlin.math.sign

typealias Position = Triple<Double, Double, Double>
typealias Velocity = Triple<Double, Double, Double>

data class Hailstone(
    val position: Position,
    val velocity: Velocity
) {
    // m = (y2 - y1)/ (x2 - x1)
    val slope: Double by lazy {
        val a = position
        val b = position + velocity
        (b.second - a.second)/(b.first - a.first)
    }
    // b = y - mx
    val b: Double by lazy {
        position.second - (slope * position.first)
    }

    // x1 == x2 && y1 == y2 is a collision
    // b1 + m1*x1 = b2 + m2*x2
    // b1 - b2 = (m2 - m1)*x
    // x = (b1 - b2) / (m2 - m1) 
    fun calculateCollision(other: Hailstone): Position? {
        val x = (b - other.b) / (other.slope - slope)
        val y = (slope * x) + b
        val possibleCollision = Position(x, y, 0.0) // ignore z for now
        // check whether the intersection already happened
        val distanceA = possibleCollision - position
        val distanceB = possibleCollision - other.position
        return possibleCollision.takeIf { distanceA.sameDirection(velocity) && distanceB.sameDirection(other.velocity) }
    }
}

val file = if(args.contains("-i")) args[1 + args.indexOf("-i")] else ""
val hailstones = mutableListOf<Hailstone>()

File(file).forEachLine { line ->
    val posAndV = line.split(" @ ")
    val pos = posAndV[0].split(", ").map{ it.toDouble() }.let { Position(it[0], it[1], it[2]) }
    val v = posAndV[1].split(", ").map{ it.toDouble() }.let { Velocity(it[0], it[1], it[2]) }
    val stone = Hailstone(pos, v)
    hailstones.add(stone)
}

fun getCollisions(min: Double, max: Double): Long {
    var count = 0L
    for (a in 0 until hailstones.size - 1) {
        for (b in a until hailstones.size) {
            val stoneA = hailstones[a]
            val stoneB = hailstones[b]
            stoneA.calculateCollision(stoneB)?.takeIf {
                it.first >= min && it.first <= max && it.second >= min && it.second <= max
            }?.let {
                count++
            }
        }
    }
    return count
}

println(getCollisions(200000000000000.0, 400000000000000.0))
// println(getCollisions(7.0, 27.0))

operator fun Position.plus(other: Velocity): Position = Position(first + other.first, second + other.second, third + other.third)
operator fun Position.minus(other: Position): Position = Position(first - other.first, second - other.second, third - other.third)
fun Position.sameDirection(other: Velocity): Boolean = sign(first) == sign(other.first) && sign(second) == sign(other.second)