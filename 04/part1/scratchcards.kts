import java.io.File

val file = if(args.contains("-i")) args[1 + args.indexOf("-i")] else ""

var result = 0

File(file).forEachLine { line ->
    val idAndCard = line.split(": ")
    val card = idAndCard[1]
    val winningAndNumbers = card.split(" | ")
    val winning = mutableSetOf<String>()
    var score = 0
    val winningStr = winningAndNumbers[0]
    for (i in 0 until winningStr.length step 3) {
        winning.add(winningStr.substring(i, i + 2))
    }
    val numbers = winningAndNumbers[1]
    for (i in 0 until numbers.length step 3) {
        val number = numbers.substring(i, i + 2)
        if(winning.contains(number)) {
            if (score == 0) {
                score = 1
            } else {
                score *= 2
            }
        }
    }
    result += score
}

println("The pile is worth $result")