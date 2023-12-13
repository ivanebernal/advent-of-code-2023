import java.io.File

val file = if(args.contains("-i")) args[1 + args.indexOf("-i")] else ""

var currPattern = mutableListOf<String>()
var patterns = mutableListOf<List<String>>()

File(file).forEachLine { line ->
    if (line.isEmpty()) {
        patterns.add(currPattern)
        currPattern = mutableListOf<String>()
    } else {
        currPattern.add(line)
    }
}
patterns.add(currPattern)

val result = patterns.fold(0) { acc, pattern -> 
    val reflection = findReflectionNoSmudge(pattern, findReflection(pattern))
    acc + reflection
}

println(result)

fun findReflectionNoSmudge(pattern: List<String>, originalResult: Pair<Int, Int>): Int {
    for (row in 0 until pattern.size) {
        for (col in 0 until pattern[0].length) {
            val cleanSpot = if (pattern[row][col] == '.') '#' else '.'
            val sb = StringBuilder(pattern[row])
            sb[col] = cleanSpot
            val newPattern = pattern.toMutableList()
            newPattern[row] = sb.toString()
            val reflection = findReflection(newPattern, originalResult)
            if (reflection != originalResult && reflection.second > 0) return reflection.second
        }
    }
    return 0
}

// Return a pair of row/col, reflection. Row = 0, col = 1, invalid = -1
fun findReflection(pattern: List<String>, originalResult: Pair<Int, Int> = -1 to -1): Pair<Int, Int> {
    val cols = pattern.first().length
    val rows = pattern.size

    // Check cols
    for (col in 0 until cols - 1) {
        var reflectedCol = 0
        while (
            col - reflectedCol >= 0 &&
            col + reflectedCol + 1 < cols &&
            pattern.all { it[col - reflectedCol] == it[col + reflectedCol + 1] }
        ) {
            reflectedCol++
        }
        if ((col - reflectedCol + 1 == 0 || col + reflectedCol + 1 == cols) && originalResult != Pair(0, col + 1)) {
            return Pair(0, col + 1)
        }
    }

    // Check rows
    for (row in 0 until rows - 1) {
        var reflectedRow = 0
        while (
            row - reflectedRow >= 0 &&
            row + reflectedRow + 1 < rows &&
            pattern[row - reflectedRow] == pattern[row + reflectedRow + 1]
        ) {
            reflectedRow++
        }
        if ((row - reflectedRow + 1 == 0 || row + reflectedRow + 1 == rows) && originalResult != Pair(1, 100 * (row + 1))) {
            return Pair(1, 100 * (row + 1))
        }
    }
    return Pair(-1, 0)
}