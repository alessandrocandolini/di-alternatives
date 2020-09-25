import org.gradle.api.artifacts.dsl.DependencyHandler

object Kotlin {

    const val runtimeVersion = "1.4.10"
    private const val coroutineVersion = "1.3.9"

    private val kotlinGroup = "org.jetbrains.kotlin".toGroupId()
    private val kotlinxGroup = "org.jetbrains.kotlinx".toGroupId()

    private val kotlinStdlib = kotlinGroup + "kotlin-stdlib-jdk8".toNameId() + runtimeVersion.toVersion() + DependencyConfig.IMPLEMENTATION

    private val kotlinxCoroutines = kotlinxGroup + "kotlinx-coroutines-core".toNameId() + coroutineVersion.toVersion() + DependencyConfig.IMPLEMENTATION
    private val kotlinxCoroutinesAndroid = kotlinxGroup + "kotlinx-coroutines-android".toNameId() + coroutineVersion.toVersion() + DependencyConfig.IMPLEMENTATION

    fun DependencyHandler.addKotlinJvm() {
        implementDependencies(listOf(
            kotlinStdlib,
            kotlinxCoroutines
        ))
    }

    fun DependencyHandler.addKotlinAndroid() {
        implementDependencies(listOf(
            kotlinxCoroutinesAndroid
        ))
    }

}