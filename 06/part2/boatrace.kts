import java.io.File

val file = if(args.contains("-i")) args[1 + args.indexOf("-i")] else ""

val chart = File(file).readLines()

val regex = "\\d+".toRegex()
val time = regex.findAll(chart[0]).map { it.value }.joinToString("").toLong()
val distance = regex.findAll(chart[1]).map { it.value }.joinToString("").toLong()

val result = calculateRangeSize(distance, time)

// d = t * p - p^2
// p^2 - tp + d = 0
// t1 * t2 = d
// t1 + t2 = t

fun calculateRangeSize(d: Long, t: Long): Long {
    var size = 0L
    for (t1 in 1 until t) {
        val t2 = t - t1
        if (t1 * t2 > d) size++
    }
    return size
}

println(result)