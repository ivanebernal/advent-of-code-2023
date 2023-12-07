import java.io.File

val file = if(args.contains("-i")) args[1 + args.indexOf("-i")] else ""

val chart = File(file).readLines()

val regex = "\\d+".toRegex()
val times = regex.findAll(chart[0]).map { it.value.toInt() }.toList()
val distances = regex.findAll(chart[1]).map { it.value.toInt() }.toList()

var result = 1

for (i in 0 until times.size) {
    result *= calculateRangeSize(distances[i], times[i])
}

// d = t * p - p^2
// p^2 - tp + d = 0
// t1 * t2 = d
// t1 + t2 = t

fun calculateRangeSize(distance: Int, time: Int): Int {
    var size = 0
    for (t1 in 1 until time) {
        val t2 = time - t1
        if (t1 * t2 > distance) size++
    }
    return size
}

println(result)