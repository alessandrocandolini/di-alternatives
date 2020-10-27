buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath ("com.android.tools.build:gradle:4.0.2")
        classpath(kotlin("gradle-plugin", version = Versions.kotlinRuntime))
    }
}
plugins {
    id("com.adarshr.test-logger") version Plugins.adarshrTestLoggerVersion
    id("io.kotest") version Plugins.kotestGradlePlugin
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}