object Libs {

    const val kotlin = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:${Versions.kotlin}"

    object Kotlin {
        object Coroutines {
            const val core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.kotlinCoroutines}"
            const val android = "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.kotlinCoroutines}"
            const val test = "org.jetbrains.kotlinx:kotlinx-coroutines-test:${Versions.kotlinCoroutines}"
        }

        object Serialization {

            const val serialization = "org.jetbrains.kotlinx:kotlinx-serialization-json:${Versions.kotlinSerializationRuntime}"

        }
    }

    object AndroidX {
        const val appcompat = "androidx.appcompat:appcompat:1.2.0-rc01"
        const val coreKtx = "androidx.core:core-ktx:1.5.0-alpha01"

        object Compose {
            const val foundation = "androidx.compose.foundation:foundation:${Versions.compose}"
            const val layout = "androidx.compose.foundation:foundation-layout:${Versions.compose}"
            const val material = "androidx.compose.material:material:${Versions.compose}"
            const val runtime = "androidx.compose.runtime:runtime:${Versions.compose}"
            const val tooling = "androidx.ui:ui-tooling:${Versions.compose}"
            const val test = "androidx.compose.test:test-core:${Versions.compose}"
            const val uiTest = "androidx.ui:ui-test:${Versions.compose}"
        }

    }

    object Kotest {
        const val core = "io.kotest:kotest-assertions-core-jvm:${Versions.kotest}"
        const val engine = "io.kotest:kotest-framework-engine-jvm:${Versions.kotest}"
        const val runner = "io.kotest:kotest-runner-junit5-jvm:${Versions.kotest}"
        const val assertions = "io.kotest:kotest-assertions-core:${Versions.kotest}"
        const val property = "io.kotest:kotest-property:${Versions.kotest}"
    }

    object Http {

        const val okhttp = "com.squareup.okhttp3:okhttp:${Versions.okhttp}"
        const val okhttpMockWebServer = "com.squareup.okhttp3:mockwebserver:${Versions.okhttp}"
        const val retrofit = "com.squareup.retrofit2:retrofit:${Versions.retrofit}"
        const val retrofitSerializationAdapter = "com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:${Versions.retrofitSerializationConverter}"


    }

    object Arrow {


    }

}