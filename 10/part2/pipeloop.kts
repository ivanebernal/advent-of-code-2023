import java.io.File
import java.util.ArrayDeque

typealias Coord = Pair<Int, Int>

val file = if(args.contains("-i")) args[1 + args.indexOf("-i")] else ""

// map that indicates which directions are accessible depending on the pipe symbol.
// the list is in the following order: n, e, s, w

val accessMap = mapOf(
	'-' to listOf(0, 1, 0, 1),
	'|' to listOf(1, 0, 1, 0),
	'L' to listOf(1, 1, 0, 0),
	'J' to listOf(1, 0, 0, 1),
	'7' to listOf(0, 0, 1, 1),
	'F' to listOf(0, 1, 1, 0),
	'.' to listOf(0, 0, 0, 0),
	'S' to listOf(1, 1, 1, 1),
)

val pipeLoop = File(file).readLines()
val visited = mutableSetOf<Coord>()

var start = Coord(-1, -1)

// Find start
for (r in 0 until pipeLoop.size) {
	for (c in 0 until pipeLoop[r].length) {
		if (pipeLoop[r][c] == 'S') {
			start = Coord(r, c)
			break;
		}
	}
	if (start != Coord(-1, -1)) {
		break;
	}
}

val queue = ArrayDeque<Coord>()
var loopCoords = setOf<Coord>()
val currentCoords = mutableSetOf<Coord>()
val paths = ArrayDeque<Set<Coord>>()

queue.add(start)
// Find coord of elements in loop
while(!queue.isEmpty()) {
	val coord = queue.poll()
	visited.add(coord)
	currentCoords.add(coord)
	val next = findAccessibleSpaces(coord)
	repeat(next.size - 1) {
		paths.add(currentCoords)
	}
	if (next.isEmpty()) {
		if (currentCoords.size > loopCoords.size) {
			loopCoords = currentCoords.toMutableSet()
		}
		currentCoords.clear()
		paths.pollLast()?.let { currentCoords.addAll(it) }
	} else {
		next.forEach { queue.add(it) }
	}
}

var count = 0

val steps = listOf(Coord(-1, 0), Coord(0, 1), Coord(1, 0), Coord(0, -1))

val path = loopCoords.toMutableSet()
val sortedPath = mutableListOf<Coord>(start)
path.remove(start)

// Sort path in a list
while (!path.isEmpty()) {
	val curr = sortedPath.last()
	val next = steps.map {it + curr}
		.filterIndexed { index, nextCoord -> 
			pipeLoop.inRange(nextCoord) &&
				pipeLoop[curr].canGoTo(pipeLoop[nextCoord], index)
		}
		.firstOrNull { path.contains(it) } 
	next ?: break
	path.remove(next)
	sortedPath.add(next)
}
sortedPath.removeFirst()
// Look at one direction of the loop: inside or outside
var direction = accessMap[pipeLoop[sortedPath[0]]]!!.indexOfFirst { it == 0 }
val inside = mutableSetOf<Coord>()
val tilesInDir = findTilesInDirection(direction)
println(inside.size)
println("---------")
val tilesInOppositeDir = findTilesInDirection((direction + 2) % 4)
printMap()
println(inside.size)

val result = Math.max(tilesInDir, tilesInOppositeDir)

fun findTilesInDirection(dir: Int): Int {
	var currDir = dir
	visited.clear()
	var tileCount = 0
	for (pipeIndex in 0 until sortedPath.size - 1) {
		val pipeCoord = sortedPath[pipeIndex]
		val nextPipeCoord = sortedPath[pipeIndex + 1]
		val pipe = pipeLoop[pipeCoord]
		val nextPipe = pipeLoop[nextPipeCoord]
		if (pipe != '|' && pipe != '-') {
			val biggestArea = findBiggestArea(pipeCoord + steps[currDir])
			if (biggestArea == -1) {
				// our current direction is outside, abort
				println("OUTSIDE!!")
				return -1 
			} else {
				// if(biggestArea > 0) println("Counted $biggestArea")
				tileCount += biggestArea
			}
			val step = nextPipeCoord - pipeCoord
			currDir = (currDir + directionChange(pipe, nextPipe, steps.indexOf(step)) + 4) % 4
			// println("Direction change: $pipe -> $nextPipe ; $currDir")
		}
		val biggestArea = findBiggestArea(pipeCoord + steps[currDir])
		if (biggestArea == -1) {
			// our current direction is outside, abort
			println("OUTSIDE!!")
			return -1 
		} else {
			// if(biggestArea > 0) println("Counted $biggestArea")
			tileCount += biggestArea
		}
		
	}
	return tileCount
}

fun findBiggestArea(coord: Coord): Int {
	queue.clear()
	if(!pipeLoop.inRange(coord)) return -1
	var currentCount = 0
	if (!visited.contains(coord) && !loopCoords.contains(coord)) {
		var foundOutside = false
		queue.add(coord)
		while (!queue.isEmpty()) {
			val tile = queue.poll()
			if(!visited.contains(tile)) currentCount++
			inside.add(tile)
			visited.add(tile)
			val nextCoords = steps.map { it + tile }
			if (nextCoords.any { !pipeLoop.inRange(it) }) {
				foundOutside = true
			}
			nextCoords.filter { nextTile ->
				!visited.contains(nextTile) && 
				// !queue.contains(nextTile) && 
				pipeLoop.inRange(nextTile) && 
				!loopCoords.contains(nextTile)
			}.forEach{ queue.add(it) }
		}

		if (foundOutside) {
			// This means it's not inside
			return -1
		}
	}
	queue.clear()
	return currentCount
}
println("Inside: $result")

fun printMap() {
	pipeLoop.forEachIndexed { row, line ->
		line.forEachIndexed { col, char ->
			if (loopCoords.contains(Coord(row, col))) {
				print(char)
			} else {
				if (inside.contains(Coord(row, col))) {
					print("A")
				} else {
					print(".")
				}
			}
		}
	println()
	}
}

fun findAccessibleSpaces(coord: Coord): List<Coord> {
	// n, e, s, w -> s, w, n, e from the perspective of the space we are going to
	return listOf(Coord(-1, 0), Coord(0, 1), Coord(1, 0), Coord(0, -1))
		.map { it + coord }
		.filterIndexed { index, nextCoord -> 
			accessMap[pipeLoop[coord]]!![index] == 1 && pipeLoop.inRange(nextCoord) && 
				!visited.contains(nextCoord) && accessMap[pipeLoop[nextCoord]]!![(index + 2) % 4] == 1
		}
}

fun findStart(): Char {
	val directionFrom = (sortedPath[1] - sortedPath[0]).let { steps.indexOf(it) }
	val directionTo = (sortedPath[0] - sortedPath.last()).let { steps.indexOf(it) }
	return accessMap.filter { entry ->
		val access = mutableListOf(0, 0, 0, 0)
		access[(directionTo + 2) % 4] = 1
		access[directionFrom] = 1
		entry.value == access.toList()
	}.map { it.key }.first()
}

operator fun Coord.plus(other: Coord): Coord {
	return Coord(this.first + other.first, this.second + other.second)
}

operator fun Coord.minus(other: Coord): Coord {
	return Coord(this.first - other.first, this.second - other.second)
}

operator fun List<String>.get(coord: Coord): Char = this[coord.first][coord.second]

fun List<String>.inRange(coord: Coord): Boolean = 
	coord.first >= 0 && coord.second >= 0 && 
	this.size > 0 && coord.first < this.size && 
	!this[0].isEmpty() && coord.second < this[0].length

fun Char.canGoTo(b: Char, direction: Int) = accessMap[this]!![direction] == 1 && accessMap[b]!![(direction + 2) % 4] == 1

fun directionChange(curr: Char, next: Char, direction: Int): Int {
	val prevDir = ((0 until 4).first { it != direction && accessMap[curr]!![it] == 1 } + 2) % 4
	return if (direction - prevDir == 1 || direction - prevDir == -3) 1 else -1
}

// println(directionChange('L', '7', 1))