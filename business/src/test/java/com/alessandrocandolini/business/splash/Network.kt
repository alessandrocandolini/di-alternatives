package com.alessandrocandolini.business.splash

import com.alessandrocandolini.business.fullUrl
import com.alessandrocandolini.business.toDispatcher
import com.alessandrocandolini.business.withMockServer
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest

class ApiKeyInterceptorTest : BehaviorSpec({

    given("an instance of ApiKeyInterceptor") {

        val aApiKey = "I'm a valid api key"
        val interceptor: Interceptor = ApiKeyInterceptor { aApiKey }

        val authDispatcher : (RecordedRequest) -> MockResponse = { request ->
            val code = when(val l = request.requestUrl?.queryParameterValues(ApiKeyInterceptor.API_KEY_QUERY_PARAM)) {
                listOf(aApiKey) -> 200
                else -> {
                    println("list = $l")
                    401
                }
            }
            MockResponse().setResponseCode(code)
        }

        `when`("query param is not present in the GET request and the interceptor is not plugged") {
            then("the response should be unauthorised") {
                withMockServer {

                    val client = OkHttpClient.Builder().build()
                    val url = fullUrl("/api?api=test")
                    dispatcher = authDispatcher.toDispatcher()
                    val request: Request = Request.Builder()
                        .get()
                        .url(url)
                        .build()
                    val r = client.newCall(request).execute()
                    r.code shouldBe 401
                }
            }
        }

        `when`("query param is not present in the GET request") {
            then("it should append the query param") {
                withMockServer {
                    val url = fullUrl("/api?api=test")
                    val client = OkHttpClient.Builder()
                        .addInterceptor(interceptor)
                        .build()
                    dispatcher = authDispatcher.toDispatcher()
                    val request: Request = Request.Builder()
                        .get()
                        .url(url)
                        .build()
                    val r = client.newCall(request).execute()
                    r.code shouldBe 200
                }
            }
        }


        `when`("query param is present already in the GET request") {
            then("it should replace the query param") {
                withMockServer {
                    val url = fullUrl("/api?api=test&appid=cacca")
                    val client = OkHttpClient.Builder()
                        .addInterceptor(interceptor)
                        .build()
                    dispatcher = authDispatcher.toDispatcher()
                    val request: Request = Request.Builder()
                        .get()
                        .url(url)
                        .build()
                    val r = client.newCall(request).execute()
                    r.code shouldBe 200
                }

            }
        }
    }

})




