import java.io.File

val file = if(args.contains("-i")) args[1 + args.indexOf("-i")] else ""

val diagram = File(file).readLines().map { it.split(" ").let { 
        val unfoldedSprings = listOf(it[0], it[0], it[0], it[0], it[0]).joinToString("?")
        val unfoldedGroups = listOf(it[1], it[1], it[1], it[1], it[1]).joinToString(",").split(",").map { it.toInt() }
        Pair(unfoldedSprings, unfoldedGroups) 
    } 
}

val visited = mutableMapOf<Exploration, Long>()
var result = 0L

diagram.forEach { (springs, groups) ->
    val combinations = if(springs[0] != '?') {
        findPossibleCombinations('-', springs, 0, 0, groups, 0)
    } else {
        findPossibleCombinations('#', springs, 0, 0, groups, 0) + findPossibleCombinations('.', springs, 0, 0, groups, 0)
    }
    println("$springs $groups = $combinations")
    result += combinations
}

println(result)

fun findPossibleCombinations(replacement: Char, springs: String, springIdx: Int, defective: Int, groups: List<Int>, groupIndex: Int): Long {
    val exploration = Exploration(replacement, springs, springIdx, defective, groups, groupIndex)
    if (visited.contains(exploration)) return visited[exploration]!!
    var currSprIdx = springIdx
    var currDef = defective
    var groupIdx = groupIndex

    if (replacement == '#') {
        currDef++
        if (groupIdx >= groups.size || currDef > groups[groupIdx]) return 0L
    }

    if (replacement == '.' && currDef > 0) {
        if (currDef < groups[groupIdx]) return 0L
        currDef = 0
        groupIdx++
    }

    if (replacement == '.' || replacement == '#') currSprIdx++

    while (currSprIdx < springs.length && springs[currSprIdx] != '?') {
        if (springs[currSprIdx] == '#') {
            currDef++
            if (groupIdx >= groups.size || currDef > groups[groupIdx]) return 0L
        }
        if (springs[currSprIdx] == '.' && currDef > 0) {
            if (currDef < groups[groupIdx]) return 0L
            groupIdx++
            currDef = 0
        }
        currSprIdx++
    }

    if (currSprIdx == springs.length) {
        if(
            (groupIdx == groups.size - 1 && currDef == groups.last()) ||
            (groupIdx == groups.size && currDef == 0)
        ) return 1L
        else return 0L
    }

    val combinations = findPossibleCombinations('#', springs, currSprIdx, currDef, groups, groupIdx) + findPossibleCombinations('.', springs, currSprIdx, currDef, groups, groupIdx)
    visited[exploration] = combinations
    return combinations
}

data class Exploration(
    val replacement: Char, val springs: String, val springIdx: Int, val defective: Int, val groups: List<Int>, val groupIndex: Int
)