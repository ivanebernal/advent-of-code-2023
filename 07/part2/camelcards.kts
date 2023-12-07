import java.io.File

typealias ScoreAndBid = Pair<List<Int>, Int> 

val file = if(args.contains("-i")) args[1 + args.indexOf("-i")] else ""

val cards = listOf('J', '2', '3', '4', '5', '6', '7', '8', '9', 'T', 'Q', 'K', 'A')

var result = 0

File(file).readLines().map { 
    val handAndBid = it.split(" ") 
    Pair(getCamelCardsScore(handAndBid[0]), handAndBid[1].toInt())
}.sortedWith<ScoreAndBid>(
    object: Comparator<ScoreAndBid> {
        override fun compare(sb1: ScoreAndBid, sb2: ScoreAndBid): Int {
            for (i in 0 until sb1.first.size) {
                if(sb1.first[i] != sb2.first[i]) {
                    return sb1.first[i] - sb2.first[i]
                }
            }
            return 0
        }
    }
).map { it.second }.forEachIndexed { index, bid ->
    result += bid * (index + 1)
}

println(result)

fun getCamelCardsScore(hand: String): List<Int> {
    val result = mutableListOf<Int>()
    hand.forEach { result.add(cards.indexOf(it)) }
    val typeScore = getTypeScore(hand)
    println("$hand, $typeScore")
    result.add(0, typeScore)
    return result
}

fun getTypeScore(hand: String): Int {
    val handMap = mapCards(hand)
    return when {
        isFiveOfAKind(handMap) -> 7
        isFourOfAKind(handMap) -> 6
        isFullHouse(handMap) -> 5
        isThreeOfAKind(handMap) -> 4
        isTwoPairs(handMap) -> 3
        isOnePair(handMap) -> 2
        isHighCard(handMap) -> 1
        else -> 0
    }
}

fun isFiveOfAKind(hand: Map<Char, Int>) = hand.size == 1 || (hand.size == 2 && hand.contains('J'))

fun isFourOfAKind(hand: Map<Char, Int>): Boolean = (hand['J'] ?: 0) + hand.filter { it.key != 'J' }.map { it.value }.max() == 4

fun isFullHouse(hand: Map<Char, Int>): Boolean = 
    (hand.any { it.value == 3 } && hand.any { it.value == 2 }) || (hand.filter { it.value == 2 }.size == 2 && hand['J'] == 1)

fun isThreeOfAKind(hand: Map<Char, Int>): Boolean = (hand['J'] ?: 0) + hand.filter { it.key != 'J' }.values.max() == 3

fun isTwoPairs(hand: Map<Char, Int>): Boolean = hand.filter { it.value == 2 }.size == 2

fun isOnePair(hand: Map<Char, Int>): Boolean = 
    hand.filter { it.value == 2 }.size == 1 || (hand.size == 5 && hand.contains('J'))

fun isHighCard(hand: Map<Char, Int>): Boolean = hand.size == 5

fun mapCards(hand: String): Map<Char, Int> {
    val cardsToCount = mutableMapOf<Char, Int>()
    hand.forEach { cardsToCount[it] = (cardsToCount[it] ?: 0) + 1 }
    return cardsToCount
}