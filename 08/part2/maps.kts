import java.io.File
import kotlin.math.sqrt
import kotlin.math.roundToInt

val file = if(args.contains("-i")) args[1 + args.indexOf("-i")] else ""

val maps = mutableMapOf<String, Pair<String, String>>()

val mapLines = File(file).readLines()

val instructions = mapLines.first()
val regex = "(\\w{3}) = \\((\\w{3}), (\\w{3})\\)".toRegex()
var startingNodes = mutableListOf<String>()

for (i in 2 until mapLines.size) {
    val regexResult = regex.find(mapLines[i])?.groupValues!!
    val nodeValue = regexResult[1]
    val left = regexResult[2]
    val right = regexResult[3]
    maps[nodeValue] = Pair(left, right)
    if(nodeValue[2] == 'A') startingNodes.add(nodeValue)
}

val intructionLoops = startingNodes.map { findSteps(it) / instructions.length }
var factorizationTable = intructionLoops.toMutableList().toList()
val primes = factorizationTable.flatMap { n -> 
    (2 until sqrt(n.toDouble()).roundToInt()).filter { n % it == 0  }.takeIf { it.size > 0 } ?: listOf(n)  
}

var currPrimeIdx = 0
var lcm = 1L

while (!factorizationTable.all { it == 1 }) {
    val currentFactor = primes[currPrimeIdx]
    while (factorizationTable.any { it % currentFactor == 0 }) {
        factorizationTable = factorizationTable.map { if (it % currentFactor == 0) it / currentFactor else it }
        lcm *= currentFactor.toLong()
    }
    currPrimeIdx++
}

val result = lcm * instructions.length.toLong()

println("Steps $result")

fun findSteps(node: String): Int {
    var steps = 0
    var currNode = node
    while (currNode[2] != 'Z') {
        instructions.forEach { dir ->
            val lr = maps[currNode]!!
            currNode = if(dir == 'L') lr.first else lr.second
            steps++
        }
    }
    return steps
}