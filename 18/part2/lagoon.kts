import java.io.File

val dirMap = mapOf(
    "U" to (-1 to 0),
    "R" to (0 to 1),
    "D" to (1 to 0),
    "L" to (0 to -1)
)
val dirOrder = listOf("R", "D", "L", "U")

val file = if(args.contains("-i")) args[1 + args.indexOf("-i")] else ""
val instructions = File(file).readLines()

var currArea = 1L
var currWidth = 1L

instructions.forEach { instruction ->
    val (quantity, direction) = decodeInstruction(instruction)
    when (direction) {
        "R" -> {
            currArea += quantity
            currWidth += quantity
        }
        "D" -> {
            val area = currWidth * quantity
            currArea += area
        }
        "L" -> {
            currWidth -= quantity
        }
        "U" -> {
            val area = (currWidth - 1) * quantity
            currArea -= area
        }
    }
}

println(currArea)

fun decodeInstruction(instruction: String): Pair<Int, String> {
    val instList = instruction.split(" ")
    val hex = instList[2]
    val distance = hex.substring(2, 7).toInt(16)
    val direction = hex.substring(7, 8).toInt()
    return distance to dirOrder[direction]
}
