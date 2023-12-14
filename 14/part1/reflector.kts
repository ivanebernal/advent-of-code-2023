import java.io.File

val file = if(args.contains("-i")) args[1 + args.indexOf("-i")] else ""

val platform = File(file).readLines()
var load = 0

for (col in 0 until platform[0].length) {
    var rocks = 0
    var rowLoad = 0
    var topMost = 0
    for (row in 0 until platform.size) {
        val char = platform[row][col]
        if (char == '#') {
            if (rocks > 0) {
                val colLoad = platform.size * rocks - (topMost until topMost + rocks).reduce { acc, row -> row + acc }
                rowLoad +=  colLoad
                rocks = 0
            }
            topMost = row + 1
        }
        if (char == 'O') {
            rocks++
        }
    }
    if (rocks > 0) {
        val colLoad = platform.size * rocks - (topMost until topMost + rocks).reduce { acc, row -> row + acc }
        rowLoad += colLoad
        rocks = 0
    }
    load += rowLoad
}


println(load)