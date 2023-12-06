import java.io.File

val file = if(args.contains("-i")) args[1 + args.indexOf("-i")] else ""

var result = 0
val cards = File(file).readLines()
val cardPiles = Array<Int>(cards.size) { 1 }

cards.forEach { line ->
    val idAndCard = line.split(": ")
    val card = idAndCard[1]
    val cardNum = idAndCard[0].filter { it.isDigit() }.toInt()
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
            score++
            cardPiles[cardNum + score]+= cardPiles[cardNum]
        }
    }
}

result = cardPiles.reduce() { acc, cardNum -> acc + cardNum }

println("We end up with $result cards")