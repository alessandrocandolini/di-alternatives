
// TODO check if we can import https://github.com/ajalt/mordant in the gradle DSL
private enum class ConsoleColor {
    RED,
    GREED
}

private fun colorString(s: String, c: ConsoleColor): String {
    val ansiForegroundColour = when (c) {
        ConsoleColor.RED -> "\u001B[31m"
        ConsoleColor.GREED -> "\u001B[32m"
    }
    val ansiReset = "\u001B[0m"
    return "$ansiForegroundColour$s$ansiReset"
}

internal fun errorString(s: String): String = colorString(s, ConsoleColor.RED)
internal fun successString(s: String): String = colorString(s, ConsoleColor.GREED)