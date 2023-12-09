import java.io.File
import java.util.ArrayDeque

val file = if(args.contains("-i")) args[1 + args.indexOf("-i")] else ""

var result = 0

File(file).forEachLine { line ->
    val values = line.split(" ").map { it.toInt() }
    result += findNextValue(values)
}

println("Sum of next values: $result")

fun findNextValue(values: List<Int>): Int {
    var calcs = ArrayDeque<List<Int>>()
    calcs.add(values)
    while (!calcs.getLast().all { it == 0 }) {
        val v = calcs.getLast()
        val newCalc = mutableListOf<Int>()
        for (i in 0 until v.size - 1) {
            newCalc.add(v[i + 1] - v[i])
        }
        calcs.add(newCalc)
    }

    var value = 0
    
    while(!calcs.isEmpty()) {
        val list = calcs.pollLast()
        value = list.first() - value
    }

    return value
}