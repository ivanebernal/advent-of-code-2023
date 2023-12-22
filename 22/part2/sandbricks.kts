import java.io.File
import java.util.ArrayDeque

typealias Coord = Triple<Int, Int, Int>
typealias Brick = Pair<Coord, Coord>
typealias Snapshot = Array<Array<Array<Int>>>

val file = if(args.contains("-i")) args[1 + args.indexOf("-i")] else ""

val bricks = mutableListOf<Brick>()

var sizeX = 0
var sizeY = 0
var sizeZ = 0

File(file).forEachLine { line ->
    val strCoords = line.split("~")
    val coord1 = strCoords[0].split(",").map { it.toInt() }.let { Coord(it[0], it[1], it[2]) }
    val coord2 = strCoords[1].split(",").map { it.toInt() }.let { Coord(it[0], it[1], it[2]) }
    bricks.add(Brick(coord1, coord2))
    sizeX = maxOf(coord1.first, coord2.first, sizeX)
    sizeY = maxOf(coord1.second, coord2.second, sizeY)
    sizeZ = maxOf(coord1.third, coord2.third, sizeZ)
}

val snapshot = Array(sizeX + 1) { Array(sizeY + 1) { Array(sizeZ + 1) { -1 } } }

bricks.forEachIndexed { index, brick ->
    val (coord1, coord2) = brick
    val (x1, y1, z1) = coord1
    val (x2, y2, z2) = coord2

    for (x in x1..x2) {
        for (y in y1..y2) {
            for (z in z1..z2) {
                snapshot[x][y][z] = index
            }
        }
    }
}

// printSides()
while(makeFall());
// printSides()

bricks.sortBy { minOf(it.first.third, it.second.third) }

val result = (0 until bricks.size).fold(0) { acc, i ->
    val snapCopy = snapshot.copy()
    val bricksCopy = bricks.toMutableList()
    snapCopy.removeBrick(bricksCopy.removeAt(i))
    acc + makeFallAndCount(snapCopy, bricksCopy)
}

println(result)


fun makeFall(): Boolean {
    var didFall = false
    for (index in 0 until bricks.size) {
        val brick = bricks[index]
        if (brick.canFall()) {
            didFall = true
            brick.makeFall()
            bricks[index] = brick.toFallen()
        }
    }
    return didFall
}

fun makeFallAndCount(snap: Snapshot, bricksList: MutableList<Brick>): Int {
    var fallen = 0
    for (index in 0 until bricksList.size) {
        val brick = bricksList[index]
        if (brick.canFall(snap)) {
            fallen++
            brick.makeFall(snap)
            bricksList[index] = brick.toFallen()
        }
    }
    return fallen
}

fun Snapshot.removeBrick(brick: Brick) {
    val (x1, y1, z1) = brick.first
    val (x2, y2, z2) = brick.second
    for (x in x1..x2) {
        for (y in y1..y2) {
            for (z in z1..z2) {
                this[x][y][z] = -1
            }
        }
    }
}

fun Brick.canFall(destroyed: Int? = null): Boolean {
    val (x1, y1, z1) = first
    val (x2, y2, z2) = second
    if (z1 == 1 || z2 == 1) return false 
    for (x in x1..x2) {
        for (y in y1..y2) {
            for (z in z1..z2) {
                val brickCube = snapshot[x][y][z]
                val cubeBelow = snapshot[x][y][z - 1]
                if (cubeBelow != -1 && cubeBelow != brickCube && (destroyed == null || cubeBelow != destroyed)) return false
            }
        }
    }
    return true
}

fun Brick.canFall(snap: Snapshot): Boolean {
    val (x1, y1, z1) = first
    val (x2, y2, z2) = second
    if (z1 == 1 || z2 == 1) return false 
    for (x in x1..x2) {
        for (y in y1..y2) {
            for (z in z1..z2) {
                val brickCube = snap[x][y][z]
                val cubeBelow = snap[x][y][z - 1]
                if (cubeBelow != -1 && cubeBelow != brickCube) return false
            }
        }
    }
    return true
}

fun Brick.makeFall() {
    val (x1, y1, z1) = first
    val (x2, y2, z2) = second
    for (x in x1..x2) {
        for (y in y1..y2) {
            for (z in z1..z2) {
                snapshot[x][y][z - 1] = snapshot[x][y][z]
                snapshot[x][y][z] = -1
            }
        }
    }
}


fun Brick.makeFall(snap: Snapshot) {
    val (x1, y1, z1) = first
    val (x2, y2, z2) = second
    for (x in x1..x2) {
        for (y in y1..y2) {
            for (z in z1..z2) {
                snap[x][y][z - 1] = snap[x][y][z]
                snap[x][y][z] = -1
            }
        }
    }
}

fun Brick.toFallen(): Brick {
    return first.copy(third = first.third -1) to second.copy(third = second.third -1)
}

fun Snapshot.copy(): Snapshot = Array (sizeX + 1) { x -> Array (sizeY + 1) { y -> Array(sizeZ + 1) { z -> this[x][y][z]  } } }

fun printSides() {
    val colors = listOf(
        "\u001B[30m",
        "\u001B[31m",
        "\u001B[32m",
        "\u001B[33m",
        "\u001B[34m",
        "\u001B[35m",
        "\u001B[36m",
        "\u001B[37m",
    )
    val colorEnd = "\u001B[0m"
    for (z in sizeZ downTo 0) {
        for (x in 0..sizeX) {
            (0..sizeY).firstOrNull { y -> snapshot[x][y][z] != -1 }?.let { y -> print(colors[snapshot[x][y][z]%8] + '*' + colorEnd) } ?: print('.')
        }
        println()
    }
    println()
    for (z in sizeZ downTo 0) {
        for (y in 0..sizeY) {
            (0..sizeX).firstOrNull { x -> snapshot[x][y][z] != -1 }?.let { x -> print(colors[snapshot[x][y][z]%8] + '*' + colorEnd) } ?: print('.')
        }
        println()
    }
    println()
}