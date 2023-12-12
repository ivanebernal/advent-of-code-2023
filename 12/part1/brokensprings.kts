import java.io.File

val file = if(args.contains("-i")) args[1 + args.indexOf("-i")] else ""

val diagram = File(file).readLines().map { it.split(" ").let { Pair(it[0], it[1].split(",").map { it.toInt() }) } }

var result = 0

diagram.forEach { (springs, groups) ->
    val combinations = if(springs[0] != '?') {
        findPossibleCombinations('-', springs, 0, 0, groups, 0)
    } else {
        findPossibleCombinations('#', springs, 0, 0, groups, 0) + findPossibleCombinations('.', springs, 0, 0, groups, 0)
    }
    result += combinations
}

println(result)

fun findPossibleCombinations(replacement: Char, springs: String, springIdx: Int, defective: Int, groups: List<Int>, groupIndex: Int): Int {
    var currSprIdx = springIdx
    var currDef = defective
    var groupIdx = groupIndex

    if (replacement == '#') {
        currDef++
        if (groupIdx >= groups.size || currDef > groups[groupIdx]) return 0
    }

    if (replacement == '.' && currDef > 0) {
        if (currDef < groups[groupIdx]) return 0
        currDef = 0
        groupIdx++
    }

    if (replacement == '.' || replacement == '#') currSprIdx++

    while (currSprIdx < springs.length && springs[currSprIdx] != '?') {
        if (springs[currSprIdx] == '#') {
            currDef++
            if (groupIdx >= groups.size || currDef > groups[groupIdx]) return 0
        }
        if (springs[currSprIdx] == '.' && currDef > 0) {
            if (currDef < groups[groupIdx]) return 0
            groupIdx++
            currDef = 0
        }
        currSprIdx++
    }

    if (currSprIdx == springs.length) {
        if(
            (groupIdx == groups.size - 1 && currDef == groups.last()) ||
            (groupIdx == groups.size && currDef == 0)
        ) return 1
        else return 0
    }
    
    return findPossibleCombinations('#', springs, currSprIdx, currDef, groups, groupIdx) + findPossibleCombinations('.', springs, currSprIdx, currDef, groups, groupIdx)

}