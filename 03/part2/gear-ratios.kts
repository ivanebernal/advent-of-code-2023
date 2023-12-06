import java.io.File

val file = if(args.contains("-i")) args[1 + args.indexOf("-i")] else ""

val schematic = File(file).readLines().map { line -> line.toCharArray().toList() }

val rows = schematic.size
val cols = schematic[0].size

var result = 0
val gearsToNumbers = mutableMapOf<Pair<Int, Int>, List<Int>>()

for(row in 0 until rows) {
    var currentNum = ""
    for(col in 0 until cols) {
        if(schematic[row][col].isDigit()) {
            currentNum += schematic[row][col]
        } else {
            if (!currentNum.isEmpty()) {
                getAdjacentGear(row, col - currentNum.length, col - 1)?.let { gear ->
                    val n = currentNum.toInt()
                    gearsToNumbers[gear] = (gearsToNumbers[gear] ?: listOf()) + listOf(n)
                }
            }
            currentNum = ""
        }
    }
    if (!currentNum.isEmpty()) {
        getAdjacentGear(row, cols - currentNum.length, cols - 1)?.let { gear ->
            val n = currentNum.toInt()
            gearsToNumbers[gear] = (gearsToNumbers[gear] ?: listOf()) + listOf(n)
        }
    }
}

result = gearsToNumbers.values.filter { it.size == 2 }.fold(0) { acc, numbers -> acc + numbers[0] * numbers[1] }



fun getAdjacentGear(row: Int, start: Int, end: Int): Pair<Int, Int>? {
    val range = start - 1 .. end + 1
    for (r in row - 1 .. row + 1) {
        for (c in range) {
            if(isInRange(r, c) && isGear(r, c)) {
                return Pair(r, c)
            }
        }
    }
    return null
}

fun isInRange(row: Int, col: Int): Boolean = 
    row >= 0 && row < rows && col >= 0 && col < cols

fun isGear(row: Int, col: Int): Boolean = schematic[row][col] == '*'

println("Sum of ratios: $result")