buildscript {
    repositories {
        google()
        mavenCentral()
        jcenter()
        maven(url = "https://plugins.gradle.org/m2/") // for check-dependency-updates
    }
    dependencies {
        // https://mvnrepository.com/artifact/com.android.tools.build/gradle?repo=google
        classpath ("com.android.tools.build:gradle:4.2.0-beta01")
        classpath(kotlin("gradle-plugin", version = Versions.kotlin))
        classpath (  "com.google.dagger:hilt-android-gradle-plugin:${Versions.hint}")
    }
}
plugins {
    id("com.adarshr.test-logger") version Plugins.adarshrTestLoggerVersion
    id("io.kotest") version Plugins.kotestGradlePlugin
    id("name.remal.check-dependency-updates") version Plugins.checkDependencyUpdates
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

