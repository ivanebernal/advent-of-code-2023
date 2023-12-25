import java.io.File
import java.util.ArrayDeque
import java.math.BigInteger
import java.math.BigDecimal
import kotlin.math.roundToLong

typealias Position = Triple<Double, Double, Double>
typealias PositionL = Triple<BigInteger, BigInteger, BigInteger>
typealias Velocity = Triple<Double, Double, Double>
typealias VelocityL = Triple<BigInteger, BigInteger, BigInteger>

data class Hailstone(
    val position: Position,
    val velocity: Velocity
)

val file = if(args.contains("-i")) args[1 + args.indexOf("-i")] else ""
val hailstones = mutableListOf<Hailstone>()

File(file).forEachLine { line ->
    val posAndV = line.split(" @ ")
    val pos = posAndV[0].split(", ").map{ it.toDouble() }.let { Position(it[0], it[1], it[2]) }
    val v = posAndV[1].split(", ").map{ it.toDouble() }.let { Velocity(it[0], it[1], it[2]) }
    val stone = Hailstone(pos, v)
    hailstones.add(stone)
}

fun getSumOfStartingCoords() {
    val s1 = hailstones[1]
    val s2 = hailstones[2]
    val s3 = hailstones[3]
    val v1 = s1.velocity
    val v2 = s2.velocity
    val v3 = s3.velocity
    val p1 = s1.position
    val p2 = s2.position
    val p3 = s3.position

    val a = (p1 - p2).cross(v1 - v2)
    val b = (p2 - p3).cross(v2 - v3)
    val c = (p3 - p1).cross(v3 - v1)
    
    val A = (p1 - p2).dot(v1.cross(v2))
    val B = (p2 - p3).dot(v2.cross(v3))
    val C = (p1 - p3).dot(v1.cross(v3))

    val w = ((A * b.cross(c) + B * c.cross(a) + C * a.cross(b))/a.dot(b.cross(c))).toLong()
    val w1 = v1.toLong() - w
    val w2 = v2.toLong() - w
    val ww = w1.cross(w2)

    val E = ww.dot(p2.toLong().cross(w2))
    val F = ww.dot(p1.toLong().cross(w1))
    val G = p1.toLong().dot(ww)
    val S = ww.dot(ww)

    val rock = E * w1 - F * w2 + G * ww 
    println(rock.sum()/S)

}

getSumOfStartingCoords()

@JvmName("dblPlus")
operator fun Position.plus(other: Velocity): Position = Position(first + other.first, second + other.second, third + other.third)
@JvmName("lngPlus")
operator fun Triple<BigInteger, BigInteger, BigInteger>.plus(other: Triple<BigInteger, BigInteger, BigInteger>) = Triple(first + other.first, second + other.second, third + other.third)
@JvmName("dblMinus")
operator fun Position.minus(other: Position): Position = Position(first - other.first, second - other.second, third - other.third)
@JvmName("lngMinus")
operator fun PositionL.minus(other: PositionL) = PositionL(first - other.first, second - other.second, third - other.third)
@JvmName("dblTimes")
operator fun Double.times(other: Position): Position = Position(this * other.first, this * other.second, this * other.third)
@JvmName("lngTimes")
operator fun BigInteger.times(other: PositionL) = PositionL(this * other.first, this * other.second, this * other.third)
operator fun Position.div(other: Double): Position = Position(first/other, second/other, third/other)
@JvmName("dblDot")
fun Triple<Double, Double, Double>.dot(other: Triple<Double, Double, Double>) = first * other.first + second * other.second + third * other.third
@JvmName("lngDot")
fun Triple<BigInteger, BigInteger, BigInteger>.dot(other: Triple<BigInteger, BigInteger, BigInteger>) = first * other.first + second * other.second + third * other.third
@JvmName("dblCross")
fun Triple<Double, Double, Double>.cross(other: Triple<Double, Double, Double>) = 
    Triple(
        first = second * other.third  - third * other.second,
        second = third * other.first - first * other.third,
        third = first * other.second - second * other.first
    )
@JvmName("lngCross")
fun Triple<BigInteger, BigInteger, BigInteger>.cross(other: Triple<BigInteger, BigInteger, BigInteger>) = 
    Triple(
        first = second * other.third  - third * other.second,
        second = third * other.first - first * other.third,
        third = first * other.second - second * other.first
    )
fun Triple<BigInteger, BigInteger, BigInteger>.sum() = first + second + third
fun Triple<Double, Double, Double>.toLong() = Triple(first.roundToLong().toBigInteger(), second.roundToLong().toBigInteger(), third.roundToLong().toBigInteger())


/*

I used vector math that I read in this reddit comment:
https://www.reddit.com/r/adventofcode/comments/18pnycy/comment/kersplf/?utm_source=share&utm_medium=mweb3x&utm_name=mweb3xcss&utm_term=1&utm_content=share_button

Had to use BigInteger in order for this to work

 */