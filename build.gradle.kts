buildscript {
    repositories {
        google()
        mavenCentral()
        jcenter()
    }
    dependencies {
        classpath ("com.android.tools.build:gradle:4.2.0-alpha16")
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
        maven(url="https://dl.bintray.com/arrow-kt/arrow-kt/")
        maven(url = "https://oss.jfrog.org/artifactory/oss-snapshot-local/") // for SNAPSHOT builds
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
