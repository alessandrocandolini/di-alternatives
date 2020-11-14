import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompile

plugins {
    id("java-library")
    kotlin("jvm")
    kotlin("plugin.serialization") version Versions.kotlin
    id("io.kotest")
    kotlin("kapt")
    id("name.remal.check-dependency-updates")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = freeCompilerArgs + listOfNotNull(
            "-Xuse-experimental=kotlin.ExperimentalUnsignedTypes",
            "-Xuse-experimental=kotlinx.coroutines.ExperimentalCoroutinesApi",
            "-XXLanguage:+InlineClasses"
        )
    }
}
tasks.withType<KotlinJvmCompile> {
    kotlinOptions {
        freeCompilerArgs = freeCompilerArgs + listOfNotNull(
            "-Xallow-jvm-ir-dependencies",
            "-Xskip-prerelease-check"
        )
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation(Libs.Kotlin.Coroutines.core)
    implementation(Libs.Kotlin.standardLib)

    // migrate okhttp to a different module, don't use business to do networking
    implementation(Libs.Http.okhttp)
    implementation(Libs.Http.retrofit)
    implementation(Libs.Http.retrofitSerializationAdapter)
    implementation(Libs.Kotlin.serialization)
    testImplementation(Libs.Http.okhttpMockWebServer)

    // arrow
    implementation(Libs.Arrow.core)
    implementation(Libs.Arrow.syntax)
    kapt(Libs.Arrow.meta)

    testImplementation(Libs.Kotlin.Coroutines.test)
    testImplementation(Libs.Kotest.core)
    testImplementation(Libs.Kotest.engine)
    testImplementation(Libs.Kotest.runner) // remove and rely on kotest
    testImplementation(Libs.Kotest.assertions)
    testImplementation(Libs.Kotest.property)
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}
