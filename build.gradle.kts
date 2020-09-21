buildscript {
    repositories {
        google()
        mavenCentral()
        jcenter()
    }
    dependencies {
        classpath ("com.android.tools.build:gradle:4.0.1")
        classpath(kotlin("gradle-plugin", version = "1.4.10"))
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