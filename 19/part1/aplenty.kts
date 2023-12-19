import java.io.File

val file = if(args.contains("-i")) args[1 + args.indexOf("-i")] else ""

sealed class Instruction {
    abstract fun getNextInstruction(part: Part): String?

    data class Compare(
        val param: String,
        val condition: String,
        val value: Int,
        val nextInstruction: String
    ): Instruction() {
        
        override fun getNextInstruction(part: Part): String? {
            val paramValue = part.getValue(param)
            return nextInstruction.takeIf {
                ((condition == "<" && paramValue < value) || (condition == ">" && paramValue > value))
            } 
        }
    }

    data class Redirect(
        val nextInstruction: String
    ): Instruction() {
        override fun getNextInstruction(part: Part): String? {
            return nextInstruction
        }
    }
}

data class Part(
    val x: Int,
    val m: Int,
    val a: Int,
    val s: Int,
) {
    fun getValue(str: String): Int {
        return when (str) {
            "x" -> x
            "m" -> m
            "a" -> a
            "s" -> s
            else -> error("Invalid value")
        }
    }

    fun sumRatings() = x + m + a + s
}

val nameToInstructions = mutableMapOf<String, List<Instruction>>()
val parts = mutableListOf<Part>()
val acceptedParts = mutableListOf<Part>()

val instructionRegex = "(.+)\\{(.+)\\}".toRegex()
val stepRegex = "(.+)(<|>)(\\d+):(.+)".toRegex()
val xmasRegex = "x=(\\d+),m=(\\d+),a=(\\d+),s=(\\d+)".toRegex()
var readingInstructions = true

File(file).forEachLine { line ->
    if (line.isEmpty()) {
        readingInstructions = false
    } else if (readingInstructions) {
        val matches = instructionRegex.find(line)?.groupValues!!
        val instrName = matches[1]
        val instrStr = matches[2]
        val steps = instrStr.split(",").map { step ->
            val stepMatches = stepRegex.find(step)?.groupValues
            if (stepMatches == null) {
                Instruction.Redirect(step)
            } else {
                val part = stepMatches!![1]
                val condition = stepMatches!![2]
                val value = stepMatches!![3].toInt()
                val nextInstruction = stepMatches!![4]
                Instruction.Compare(part, condition, value, nextInstruction)
            }
        }
        nameToInstructions[instrName] = steps
    } else {
        val xmasMatches = xmasRegex.find(line)?.groupValues!!
        val x = xmasMatches[1].toInt()
        val m = xmasMatches[2].toInt()
        val a = xmasMatches[3].toInt()
        val s = xmasMatches[4].toInt()
        parts.add(Part(x, m, a, s))
    }
}

parts.forEach { part ->
    var currentInstruction = "in"
    while (currentInstruction != "A" && currentInstruction != "R") {
        val instructions = nameToInstructions[currentInstruction]!!
        for (instr in instructions) {
            val nextInstr = instr.getNextInstruction(part)
            if (nextInstr != null) {
                currentInstruction = nextInstr
                break
            }
        }
    }
    if (currentInstruction == "A") acceptedParts.add(part)
}

println(acceptedParts.fold(0) { acc, part -> acc + part.sumRatings() })