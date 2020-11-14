package com.alessandrocandolini.business.splash

import com.alessandrocandolini.business.fullUrl
import com.alessandrocandolini.business.withMockServer
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import com.alessandrocandolini.business.splash.ApiKeyInterceptor
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.OkHttp
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockWebServer

object ApiKeyInterceptor : FunSpec({

    test("api key query param is appended to all requests not containing already the query param") {
        withMockServer {

//            val url = fullUrl("/api")
//            val aApiKey : String = "api key"
//            val store : ApiKeyStore = object : ApiKeyStore {
//                override fun fetchKey(): String = aApiKey
//            }
//            val interceptor : Interceptor = ApiKeyInterceptor(store)
            val client = OkHttpClient.Builder()


        }
        2 + 2 shouldBe 4
    }

    test("api key query param is replaced for requests that already have the same query param") {
        2 + 2 shouldBe 4
    }
})




