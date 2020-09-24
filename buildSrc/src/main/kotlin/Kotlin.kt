import org.gradle.api.artifacts.dsl.DependencyHandler

object Kotlin {

    const val runtimeVersion = "1.4.10"

    private val group = "org.jetbrains.kotlin".toGroupId()
    private val names : List<Pair<NameId, Version>> = listOf(
        "kotlin-stdlib" to runtimeVersion
    ).map {
        it.first.toNameId() to it.second.toVersion()
    }

    private val dependencies = names.map { d -> group + d.first + d.second + DependencyConfig.IMPLEMENTATION}

    fun DependencyHandler.addKotlinStandardLibraries() {
        implementDependencies(dependencies)
    }

}