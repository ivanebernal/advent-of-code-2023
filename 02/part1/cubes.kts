import java.io.File

val file = if(args.contains("-i")) args[1 + args.indexOf("-i")] else ""

var result = 0

// red, green, blue
val possibleCubes = mutableMapOf("red" to 12, "green" to 13, "blue" to 14)

File(file).forEachLine { line ->
    val gameIdToGame = line.split(": ")
    val gameId = gameIdToGame[0].substring("Game ".length).toInt()

    val rounds = gameIdToGame[1].split("; ")
    var possible = true

    for (round in rounds) {
        val coloredCubes = round.split(", ")
        coloredCubes.map { cubes ->
            val number = cubes.split(" ")[0].toInt()
            val color = cubes.split(" ")[1]
            color to number
        }.forEach { (color, number) ->
            if (possibleCubes[color]!! < number) {
                possible = false
            }
        }
        if(!possible) break;
    }
    if (possible) result += gameId
}

println("Sum of possible games Ids: $result")