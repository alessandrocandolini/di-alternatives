
private fun getOptionalProperty(key: String): String? {

    infix fun String?.or(s : String?) : String? = this ?: s

    return (System.getProperty(key) or System.getenv(key))
        ?.apply {
            println("*** Property $key: ${this.take(6)} ***")
        }

}

private fun getProperty(key: String): String = getOptionalProperty(key)!!

fun String.escape() = "\"$this\""

val apiKey: String by lazy {
    getProperty("API_KEY")
}