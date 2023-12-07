import java.io.File

data class Mapping(
    val destStart: Long, 
    val srcStart: Long, 
    val length: Long
) {
    fun isInRange(value: Long) = value in srcStart until srcStart + length

    fun mapValue(value: Long) = (value - srcStart) + destStart

    override fun toString() = "$destStart, $srcStart, $length"
}

val file = if(args.contains("-i")) args[1 + args.indexOf("-i")] else ""

val almanac = File(file).readLines()

val seeds = almanac.first().substring("Seeds: ".length)

var mapsIndex = 2

val almanacMap = mutableMapOf<String, Pair<String, List<Mapping>>>()
var currSrc = "seed"
var currDest = "soil"
var currMappings = mutableListOf<Mapping>()
val mapIdRegex = "(\\w+)-to-(\\w+) map:".toRegex()

while(mapsIndex < almanac.size) {
    val line = almanac[mapsIndex]
    if (line.contains("map")) {
        val srcDest = mapIdRegex.find(almanac[mapsIndex++])?.groupValues ?: listOf()
        currSrc = srcDest[1]
        currDest = srcDest[2]
    } else if (!line.isEmpty()) {
        val line = almanac[mapsIndex++].split(" ")
        val mapping = Mapping(line[0].toLong(), line[1].toLong(), line[2].toLong())
        currMappings.add(mapping)
    } else {
        almanacMap[currSrc] = Pair(currDest, currMappings.toMutableList())
        currMappings.clear()
        mapsIndex++
    }
    if(mapsIndex == almanac.size - 1) {
        almanacMap[currSrc] = Pair(currDest, currMappings.toMutableList())
        currMappings.clear()
    }
}

var minLocation = Long.MAX_VALUE

seeds.split(" ").map { it.toLong() }.forEach { seed ->
    var src = "seed"
    var value = seed

    do {
        val (dest, mappings) = almanacMap[src]!!
        src = dest
        value = mappings.filter { it.isInRange(value) }.firstOrNull()?.mapValue(value) ?: value
    } while (src != "location")

    minLocation = Math.min(minLocation, value)

}

println("Smallest location: $minLocation")