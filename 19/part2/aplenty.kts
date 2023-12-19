import java.io.File
import java.util.ArrayDeque

val file = if(args.contains("-i")) args[1 + args.indexOf("-i")] else ""

sealed class Instruction(
    val nextInstruction: String
) {
    data class Compare(
        val param: String,
        val condition: String,
        val value: Int,
       val next: String
    ): Instruction(next)

    data class Redirect(
        val next: String
    ): Instruction(next)
}

val nameToInstructions = mutableMapOf<String, List<Instruction>>()

val instructionRegex = "(.+)\\{(.+)\\}".toRegex()
val stepRegex = "(.+)(<|>)(\\d+):(.+)".toRegex()
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
    }
}

val queue = ArrayDeque<Pair<Instruction, MutableMap<String, List<IntRange>>>>()
queue.addLast(Instruction.Redirect("in") to mutableMapOf())
var totalCombinations = 0L
val combsToA = mutableListOf<Map<String, IntRange>>()

while (!queue.isEmpty()) {
    val (instruction, lastIntervals) = queue.pollLast()
    val intervals = lastIntervals.toMutableMap()
    val neighbors = nameToInstructions[instruction.nextInstruction]

    if (neighbors == null && instruction.nextInstruction == "A") {
        val paramAndRanges = instruction.calculateRange()
        paramAndRanges?.let { (param, positive, _) -> intervals[param] = (intervals[param]?:listOf()) + listOf(positive) }
        val combs = calculateCombinations(intervals)
        combsToA.add(combs)
    }

    val negatives = mutableListOf<Pair<String, IntRange>>()

    neighbors?.forEach {
        val currIntervals = intervals.toMutableMap()
        val paramAndRanges = it.calculateRange()
        paramAndRanges?.let { (param, positive, _) ->
            currIntervals[param] = (currIntervals[param]?:listOf()) + listOf(positive)
        }
        negatives.forEach { (p, r) ->
            currIntervals[p] = (currIntervals[p]?:listOf()) + listOf(r)
        }
        paramAndRanges?.let { (param, _, negative) ->
            negatives.add(param to negative)
        }
        queue.add(it to currIntervals) 
    }
}

fun Instruction.calculateRange(): Triple<String, IntRange, IntRange>? {
    val range =  1..4000
    if (this is Instruction.Compare) {
        return when (condition) {
            ">" -> {
                Triple(param, value + 1..range.last, range.first()..value)
            }
            "<" -> {
                Triple(param, range.first() until value, value..range.last())
            }
            else -> error("Invalid value")
        }
    } else {
        return null
    }
}

fun calculateCombinations(params: Map<String, List<IntRange>>): Map<String, IntRange> {
    val ranges = mutableMapOf<String, IntRange>()
    listOf("x", "m", "a", "s").forEach { key ->
        ranges[key] = params[key]?.joinRanges() ?: 1..4000
    }
    return ranges
}

fun Map<String, IntRange>.getCombinations() = values.fold(1L) { acc, range -> acc * range.count() }

fun List<IntRange>.joinRanges(): IntRange {
    val sortedList = this.sortedBy { it.first() }.toMutableList()
    while (sortedList.size > 1) {
        val i1 = sortedList[0]
        val i2 = sortedList[1]
        if (i1.intersect(i2).count() == 0) { return 0 until 0 }
        val newInterval = maxOf(i1.first(), i2.first())..minOf(i1.last(), i2.last())
        repeat(2) { sortedList.removeFirst() }
        sortedList.add(0, newInterval)
    }
    return sortedList.first()
}

for (i in 0 until combsToA.size) {
    totalCombinations += combsToA[i].getCombinations()
}

println(totalCombinations)
