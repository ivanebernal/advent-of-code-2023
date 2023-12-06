import java.io.File


val file = if(args.contains("-i")) args[1 + args.indexOf("-i")] else ""

var result = 0

File(file).forEachLine { line ->   
    val firstDigit = line.toCharArray().first { it >= '0' && it <= '9' }
    val lastDigit = line.toCharArray().last { it >= '0' && it <= '9' }
    val calibrationValue = "$firstDigit$lastDigit".toInt()
    result += calibrationValue
}

println("Sum of calibration values: $result")