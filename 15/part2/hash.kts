import java.io.File

val file = if(args.contains("-i")) args[1 + args.indexOf("-i")] else ""

val instructions = File(file).readLines().first().trim()

val boxes = Array<Box>(256) { Box(it, mutableListOf()) }

instructions.split(",").map { instruction ->
    if (instruction.indexOf('=') > 0) {
        // add lens
        val label = instruction.split('=')[0]
        val lens = instruction.split('=')[1].toInt()
        val boxNumber = getBox(label)
        val box = boxes[boxNumber]
        box.addLens(label, lens)
    } else if (instruction.indexOf('-') > 0) {
        val label = instruction.substring(0, instruction.length - 1)
        val boxNumber = getBox(label)
        val box = boxes[boxNumber]
        box.removeLens(label)
    }
}

val focusingPower = boxes.foldIndexed(0) { index, acc, box ->
    acc + box.lenses.foldIndexed(0) { position, acc, (label, lens) ->
        val power = (index + 1) * (position  + 1) * lens
        acc + power
    }
}

println(focusingPower)

fun getBox(label: String): Int =
    label.fold(0) { value, char ->
        ((value + char.toInt()) * 17) % 256
    }


data class Box(
    val index: Int,
    val lenses: MutableList<Pair<String, Int>>
) {
    fun addLens(label: String, lens: Int) {
        if (lenses.any { it.first == label }) {
            val position = lenses.indexOfFirst { it.first == label }
            lenses.removeAt(position)
            lenses.add(position, Pair(label, lens))
        } else {
            lenses.add(Pair(label, lens))
        }
    }

    fun removeLens(label: String) {
        if (lenses.any { it.first == label }) {
            val position = lenses.indexOfFirst { it.first == label }
            lenses.removeAt(position)
        }
    }

    override fun toString(): String {
        val sb = StringBuilder()
        sb.append("Box $index: ")
        lenses.forEachIndexed { index, (label, lens)  ->
            sb.append("[$label: $lens],")
        }
        return sb.toString()
    }
}