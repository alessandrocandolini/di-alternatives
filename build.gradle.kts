buildscript {
    repositories {
        google()
        mavenCentral()
        jcenter()
    }
    dependencies {
        classpath ("com.android.tools.build:gradle:4.2.0-alpha15")
        classpath(kotlin("gradle-plugin", version = Versions.kotlin))
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
        jcenter()
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}