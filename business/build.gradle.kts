import KoTest.addKotest
import Kotlin.addKotlinStandardLibraries

plugins {
    id("java-library")
    id("kotlin")
    id("io.kotest")
    id("com.adarshr.test-logger")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    addKotlinStandardLibraries()
    addKotest()
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}
