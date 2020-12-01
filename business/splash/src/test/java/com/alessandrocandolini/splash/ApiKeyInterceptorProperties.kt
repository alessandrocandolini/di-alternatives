package com.alessandrocandolini.splash

import com.alessandrocandolini.hasQueryParamMatching
import com.alessandrocandolini.withMockServer
import io.kotest.core.spec.style.FunSpec
import io.kotest.property.Arb
import io.kotest.property.Exhaustive
import io.kotest.property.Gen
import io.kotest.property.arbitrary.*
import io.kotest.property.checkAll
import io.kotest.property.exhaustive.collection
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest
import java.net.HttpURLConnection.HTTP_OK
import java.net.HttpURLConnection.HTTP_UNAUTHORIZED

class ApiKeyInterceptorProperties : FunSpec() {

    private val aValidApiKey = "I'm a valid api key"
    private val interceptor: Interceptor = ApiKeyInterceptor { aValidApiKey }

    init {
        test("Given a server that matches the specification, for every request (no matter what the http method, headers, body, path, query params are), request with no ApiKeyInterceptor plugged should return 401 (baseline) && request with an instance of ApiKeyInterceptor plugged in should always return 200") {

            // server as per specification
            val dispatcher : Dispatcher = object : Dispatcher() {
                override fun dispatch(request: RecordedRequest): MockResponse =
                    if (request.hasQueryParamMatching(ApiKeyInterceptor.API_KEY_QUERY_PARAM, aValidApiKey)) {
                        HTTP_OK
                    } else {
                        HTTP_UNAUTHORIZED
                    }.let { responseCode ->
                        MockResponse().setResponseCode(responseCode)
                    }
            }

            withMockServer(dispatcher) { server ->

                val clientWithoutInterceptor = OkHttpClient.Builder().build()
                val clientWithInterceptor =
                    clientWithoutInterceptor.newBuilder().addInterceptor(interceptor).build()

                val requestGen: Gen<Request> = requestGen { server.url(it) }

                checkAll(requestGen) { request ->

                    val responseWithoutInterceptor = clientWithoutInterceptor.newCall(request).execute()
                    val responseWithInterceptor = clientWithInterceptor.newCall(request).execute()

                    responseWithoutInterceptor.code == HTTP_UNAUTHORIZED && responseWithInterceptor.code == HTTP_OK

                }

            }

        }

        test("Given a echo service that does not require any authentication, for every request the interceptor is completely transparent ") {

            val echoDispatcher : Dispatcher = object : Dispatcher() {

                override fun dispatch(request: RecordedRequest): MockResponse = MockResponse().setResponseCode(HTTP_OK)

            }

            withMockServer(echoDispatcher) { server ->

                val clientWithoutInterceptor = OkHttpClient.Builder().build()
                val clientWithInterceptor =
                    clientWithoutInterceptor.newBuilder().addInterceptor(interceptor).build()

                val requestGen: Gen<Request> = requestGen { server.url(it) }

                checkAll(requestGen) { request ->

                    val responseWithoutInterceptor = clientWithoutInterceptor.newCall(request).execute()
                    val responseWithInterceptor = clientWithInterceptor.newCall(request).execute()

                    responseWithoutInterceptor.code == responseWithInterceptor.code

                }

            }

        }
    }

    companion object ApiKeyInterceptorExampleTestPropertyTest {

        enum class HttpMethod {
            POST,PATCH,DELETE,PUT,GET,HEAD
        }

        fun requestGen(pathToFullUrl: (String) -> HttpUrl): Gen<Request> {

            fun HttpMethod.toOkHttpMethodName() = name.toUpperCase()

            val nonEmptyBodyGen: Arb<String> = Arb.string().filter { it.isNotBlank() }

            val httpUrlGen: Gen<HttpUrl> = Exhaustive.collection(setOf(
                "api/v1/",
                "/api?api=test&appid=something",
                "/api?api=test"
            ).map { u -> pathToFullUrl(u) })

            val httpMethodWithBodyGen : Gen<HttpMethod> = Exhaustive.collection(setOf(HttpMethod.POST, HttpMethod.PATCH, HttpMethod.DELETE, HttpMethod.PUT))
            val httpMethodWithoutBodyGen : Gen<HttpMethod> = Exhaustive.collection(setOf(HttpMethod.GET, HttpMethod.HEAD))

            val requestsWithBody: Arb<Request> = Arb.bind(httpUrlGen, httpMethodWithBodyGen, nonEmptyBodyGen) { httpUrl, httpMethod, body ->
                Request.Builder()
                    .method(httpMethod.toOkHttpMethodName(), body.toRequestBody())
                    .url(httpUrl)
                    .build()
            }

            val requestsWithoutBody: Arb<Request> = Arb.bind(httpUrlGen, httpMethodWithoutBodyGen) { httpUrl, httpMethod ->
                Request.Builder()
                    .method(httpMethod.toOkHttpMethodName(), null)
                    .url(httpUrl)
                    .build()
            }

            return Arb.choice(
                requestsWithBody, requestsWithoutBody
            )

        }
    }
}