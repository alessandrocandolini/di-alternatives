plugins {
    id("com.android.application")
    kotlin("android")
    id("com.adarshr.test-logger")
}

android {
    compileSdkVersion(30)

    defaultConfig {
        applicationId = "com.alessandrocandolini.di_alternatives"
        minSdkVersion(21)
        targetSdkVersion(30)
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("debug") {
            isMinifyEnabled = false
        }
        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )

        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation(project(":business"))
    implementation(Libs.kotlinCoroutinesCore)
    implementation(Libs.kotlinCoroutinesAndroid)
    implementation(Libs.kotlin)

    implementation("androidx.core:core-ktx:1.3.1")
    implementation("androidx.appcompat:appcompat:1.2.0")
    testImplementation(Libs.kotestRunner)
    testImplementation(Libs.kotestAssertions)
    testImplementation(Libs.kotestProperty)
    androidTestImplementation("androidx.test.ext:junit:1.1.2")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.3.0")

}