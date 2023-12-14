import java.io.File

val file = if(args.contains("-i")) args[1 + args.indexOf("-i")] else ""

val platform = File(file).readLines()

val cycleNums = 1000000000
val rotationsPerCycle = 4
val map = mutableMapOf<List<String>, Int>()

var rotated = platform
var cycleBreak = 0
for(i in 0 until cycleNums) {
    var newRotated = rotated
    for (j in 0 until rotationsPerCycle) {
        newRotated = tiltAndRotate(newRotated)
    }
    if (map.contains(newRotated)) {
        rotated = newRotated
        cycleBreak = i + 1
        break
    }
    map[newRotated] = i + 1
    rotated = newRotated
}

val cycleSize = cycleBreak - map[rotated]!!
val cycleStart = map[rotated]!!
val reminderCycles = (cycleNums - cycleStart) % cycleSize

for(i in 0 until reminderCycles) {
    var newRotated = rotated
    for (j in 0 until rotationsPerCycle) {
        newRotated = tiltAndRotate(newRotated)
    }
    rotated = newRotated
}

var result = 0
rotated.forEachIndexed { index, row ->
    var rocks = 0
    row.forEach { space -> 
        if (space == 'O') rocks++
    }
    result += (rotated.size - index) * rocks
}


println(result)


fun tiltAndRotate(platform: List<String>): List<String> {
    val rotated = mutableListOf<String>()
    for (col in 0 until platform[0].length) {
        val sb = StringBuilder()
        var rocks = 0
        var topMost = 0
        for (row in 0 until platform.size) {
            val char = platform[row][col]
            if (char == 'O') {
                rocks++
            }
            if (char == '#') {
                repeat(rocks) { sb.append('O') }
                repeat(row - topMost - rocks) { sb.append('.') }
                sb.append('#')
                rocks = 0
                topMost = row + 1
            }
        }
        repeat(rocks) { sb.append('O') }
        repeat(platform.size - topMost - rocks) { sb.append('.') }
        rotated.add(sb.reverse().toString())
    }
    return rotated
}