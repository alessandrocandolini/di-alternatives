private fun getOptionalProperty(key: String): String? {

    infix fun String?.or(s : String?) : String? = this ?: s

    fun String?.filterNonEmpty() : String? = this?.let { s -> if (s == "") null else s }

    return (System.getProperty(key) or System.getenv(key))
        .filterNonEmpty()
        .apply {
            println(logString(key, this))
        }

}

private fun logString(key: String, s: String?) : String {

    fun logBasicString(s: String?) : String = s?.take(6) ?: "not found"

    val r = "Property $key: ${logBasicString(s)} "

    return if (s != null) {
        successString(r)
    } else {
        errorString(r)
    }
}

private fun getProperty(key: String): String = getOptionalProperty(key)!!

fun String.escape() = "\"$this\""

val apiKey: String by lazy {
    getProperty("API_KEY")
}