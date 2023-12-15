import java.io.File

val file = if(args.contains("-i")) args[1 + args.indexOf("-i")] else ""

val instructions = File(file).readLines().first().trim()

val result = instructions.split(",").fold(0) { acc, instruction ->
    acc + instruction.fold(0) { value, char ->
        ((value + char.toInt()) * 17) % 256
    }
}

println(result)