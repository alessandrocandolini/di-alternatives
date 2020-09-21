import org.gradle.api.artifacts.dsl.DependencyHandler

object Versions {

    object Kotlin {
        const val runtime = "1.4.10"
        const val collections = "0.3.3"
        const val coroutines = "1.3.9"
        const val serializationRuntime = "1.0.0-RC"

    }

    object AndroidX {
        const val core = "1.3.1"
        const val appcompact = "1.2.0"
        const val constraintlayout = "1.1.3"
    }

    object Test {

    }

    const val arrow = "0.11.0"

}

object Libs {
    const val kotlinVersion = "1.4.10"

}

object Plugins {
    const val adarshrTestLoggerVersion = "2.0.0"
    const val kotestGradlePlugin = "0.1.3"

}