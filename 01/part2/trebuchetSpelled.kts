import java.io.File


val file = if(args.contains("-i")) args[1 + args.indexOf("-i")] else ""

data class TrieNode(
    val char: Char,
    val neighbors: MutableMap<Char, TrieNode> = mutableMapOf(),
    var value: Int? = null
) {
    override fun toString(): String {
        return "$char"
    }
}

val root = TrieNode('*')

fun insertStringIntoTrie(string: String, value: Int) {
    var node = root
    string.toCharArray().forEach { c -> 
        if(node.neighbors[c] == null) {
            node.neighbors[c] = TrieNode(c)
        }
        node = node.neighbors[c]!!
    }
    node.value = value
}

listOf(
    "0" to 0,
    "1" to 1,
    "2" to 2,
    "3" to 3,
    "4" to 4,
    "5" to 5,
    "6" to 6,
    "7" to 7,
    "8" to 8,
    "9" to 9,
    "zero" to 0,
    "one" to 1,
    "two" to 2,
    "three" to 3,
    "four" to 4,
    "five" to 5,
    "six" to 6,
    "seven" to 7,
    "eight" to 8,
    "nine" to 9,
).forEach { (str, value) ->
    insertStringIntoTrie(str, value)
}

fun findDigit(input: String, ltr: Boolean): Int {
    val dir = if(ltr) 1 else - 1
    val start = if(ltr) 0 else input.length - 1
    val end = if(ltr) input.length - 1 else 0
    var index = start
    var result = -1
    while ((index <= end && ltr) || (index >= end && !ltr)) {
        result = traverseTrie(input, index)
        if(result >= 0) return result
        index += dir
    }
    return -1
}

fun traverseTrie(input: String, index: Int): Int {
    var i = index
    var node = root
    while(i >= 0 && i < input.length && node.neighbors[input[i]] != null) {
        node = node.neighbors[input[i]]!!
        if (node?.value != null) return node?.value!!
        i++
    }
    return node.value ?: -1
}

var result = 0

File(file).forEachLine { line ->   
    var firstDigit = findDigit(line, true)
    var lastDigit = findDigit(line, false)
    
    val calibrationValue = "$firstDigit$lastDigit".toInt()
    result += calibrationValue
}

println("Sum of calibration values: $result")