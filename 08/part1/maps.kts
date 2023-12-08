import java.io.File

val file = if(args.contains("-i")) args[1 + args.indexOf("-i")] else ""

val maps = mutableMapOf<String, Pair<String, String>>()

val mapLines = File(file).readLines()

val instructions = mapLines.first()
val regex = "(\\w{3}) = \\((\\w{3}), (\\w{3})\\)".toRegex()

for (i in 2 until mapLines.size) {
    val regexResult = regex.find(mapLines[i])?.groupValues!!
    val nodeValue = regexResult[1]
    val left = regexResult[2]
    val right = regexResult[3]
    maps[nodeValue] = Pair(left, right)
}

var currNode = "AAA"
var steps = 0

while (currNode != "ZZZ") {
    instructions.forEach { dir ->
        val lr = maps[currNode]!!
        currNode = if(dir == 'L') lr.first else lr.second
        steps++
    }
}

println("Steps to reach the end: $steps")