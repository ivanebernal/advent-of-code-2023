import java.io.File
import java.util.ArrayDeque

val file = if(args.contains("-i")) args[1 + args.indexOf("-i")] else ""

data class Signal(
    val from: String,
    val value: Boolean
)

sealed class Module(
    val name: String,
    val destinations: List<String>
    ) {
    abstract fun processSignal(signal: Signal): Boolean?

    data class FlipFlop(
        val n: String,
        val dest: List<String>
        ): Module(n, dest) {
        private var currentValue = false

        override fun processSignal(signal: Signal): Boolean? {
            if (!signal.value) {
                currentValue = !currentValue
                return currentValue
            }
            return null
        }
    }

    data class Broadcaster(val n: String, val dest: List<String>): Module(n, dest) {
        override fun processSignal(signal: Signal): Boolean? {
            return signal.value
        }
    }

    data class Conjunction(
        val n: String, 
        val dest: List<String>, 
        private val signals: MutableMap<String, Boolean> = mutableMapOf<String, Boolean>()
        ): Module(n, dest) {
        

        override fun processSignal(signal: Signal): Boolean? {
            signals[signal.from] = signal.value
            return !signals.values.all { it }
        }

        fun addConnection(origin: String) {
            signals[origin] = false
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
            if (!visited.contains(destination)) {
                visited.add(destination)
                if (destination is Module.Conjunction) {
                    destination.addConnection(currModule.name)
                }
                queue.addLast(destination)
            }
        }
    }
}


fun sendPulse(): Pair<Int, Int> {
    var lowPulses = 0
    var highPulses = 0
    val queue = ArrayDeque<Pair<Module, Signal>>()
    queue.addLast(modules["broadcaster"]!! to Signal("broadcaster", false))
    while(!queue.isEmpty()) {
        val (currModule, signal) = queue.pollFirst()
        if (signal.value) {
            highPulses++
        } else {
            lowPulses++
        }
        currModule.processSignal(signal)?.let { nextSignal ->
            currModule.destinations.forEach { destination ->
                if (!modules.contains(destination)) {
                    if (nextSignal) {
                        highPulses++
                    } else {
                        lowPulses++
                    }
                } else {
                    modules[destination]?.let { queue.addLast(it to Signal(currModule.name, nextSignal)) }
                }
            }
        }
    }
    return lowPulses to highPulses
}

println((0 until 1000).fold(0 to 0) { acc, _ -> acc + sendPulse() }.let { it.first * it.second })

operator fun Pair<Int, Int>.plus(other: Pair<Int, Int>) = first + other.first to second + other.second