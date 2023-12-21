import java.io.File
import java.util.ArrayDeque
import java.math.BigInteger
import kotlin.math.sqrt
import kotlin.math.roundToLong

val file = if(args.contains("-i")) args[1 + args.indexOf("-i")] else ""

sealed class Module(
    val name: String,
    val destinations: List<String>
) {
    val ancestors = mutableSetOf<String>()

    data class FlipFlop(
        val n: String,
        val dest: List<String>): Module(n, dest)

    data class Broadcaster(
        val n: String, 
        val dest: List<String>
    ): Module(n, dest)

    data class Conjunction(
        val n: String, 
        val dest: List<String>, 
        val neighbors: MutableSet<Module> = mutableSetOf<Module>()
    ): Module(n, dest) {
        fun addConnection(prevModule: Module) {
            neighbors.add(prevModule)
        }
    }
}

val modules = mutableMapOf<String, Module>()

val moduleRegex = "((?:&|%)*)(.+) -> (.+)".toRegex()

File(file).forEachLine { line ->
    val matches = moduleRegex.find(line)?.groupValues!!
    val moduleType = matches[1]
    val moduleName = matches[2]
    val destinations = matches[3].split(", ")

    val module = when (moduleType) {
        "%" -> Module.FlipFlop(moduleName, destinations)
        "&" -> Module.Conjunction(moduleName, destinations)
        else -> Module.Broadcaster(moduleName, destinations)
    }

    modules[moduleName] = module
}

// connect conjunctions
val queue = ArrayDeque<Module>()
val visited = mutableSetOf<Module>()
queue.addLast(modules["broadcaster"])
while(!queue.isEmpty()) {
    val currModule = queue.pollFirst()
    currModule.destinations.forEach { destinationStr ->
        modules[destinationStr]?.let { destination ->
            destination.ancestors.add(currModule.name)
            if (!visited.contains(destination)) {
                visited.add(destination)
                if (destination is Module.Conjunction) {
                    destination.addConnection(currModule)
                }
                queue.addLast(destination)
            }
        }
    }
}

fun minToRxLow(): Long {
    return modules["broadcaster"]!!.destinations.map { modules[it]!! }.map { startModule ->
        var activate = 1L
        var currentModule = startModule
        var idx = 0
        while (true) {
            val activateValue = if(currentModule.ancestors.size == 1) 1 else 0
            activate = activate + (activateValue shl idx)
            currentModule = currentModule.destinations.map { modules[it]!! }.firstOrNull { it is Module.FlipFlop } ?: break
            idx++
        }
        activate
    }.fold(1L) { acc, factor -> acc * factor }
}

println(minToRxLow())