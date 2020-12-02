package com.alessandrocandolini.splash

import com.alessandrocandolini.RequestGen.requestGen
import com.alessandrocandolini.await
import com.alessandrocandolini.hasQueryParamMatching
import com.alessandrocandolini.withMockServer
import io.kotest.core.spec.style.FunSpec
import io.kotest.property.Gen
import io.kotest.property.checkAll
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest

class ApiKeyInterceptorProps : FunSpec() {

    private val aValidApiKey = "I'm a valid api key"
    private val interceptor: Interceptor = ApiKeyInterceptor { aValidApiKey }

    init {
        test("Given a server that behaves as per specification, for every request (no matter what the http method, headers, body, path, query params are), request with no ApiKeyInterceptor plugged should return 401 (baseline) && request with an instance of ApiKeyInterceptor plugged in should always return 200") {

            // server as per specification
            val dispatcher = { request: RecordedRequest ->
                if (request.hasQueryParamMatching(
                        ApiKeyInterceptor.API_KEY_QUERY_PARAM,
                        aValidApiKey
                    )
                ) {
                    200
                } else {
                    401
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

                    val responseWithoutInterceptor =
                        clientWithoutInterceptor.newCall(request).await()
                    val responseWithInterceptor = clientWithInterceptor.newCall(request).await()

                    responseWithoutInterceptor.code == 401 && responseWithInterceptor.code == 200

                }

            }

        }

        test("Given a echo server that does not require any authentication, for every request the interceptor must be completely transparent (ie, the behaviour must be completely indistinguishable from the behaviour without the interceptor)") {

            val echoDispatcher = { request: RecordedRequest ->
                val body = when (request.method) {
                    "HEAD" -> "" // http spec
                    else -> "${request.body} - ${request.path} - ${request.method} - ${request.headers}"
                }
                MockResponse().setResponseCode(200)
                    .setHeaders(request.headers)
                    .setBody(body)
            }

            withMockServer(echoDispatcher) { server ->

                val clientWithoutInterceptor = OkHttpClient.Builder().build()
                val clientWithInterceptor =
                    clientWithoutInterceptor.newBuilder().addInterceptor(interceptor).build()

                val requestGen: Gen<Request> = requestGen { server.url(it) }

                checkAll(requestGen) { request ->

                    val responseWithoutInterceptor =
                        clientWithoutInterceptor.newCall(request).await()
                    val responseWithInterceptor = clientWithInterceptor.newCall(request).await()

                    responseWithoutInterceptor == responseWithInterceptor

                }

            }

        }

        test("Given a server that behaves as per specification, the interceptor is idempotent") {

            // server as per specification
            val dispatcher = { request: RecordedRequest ->
                if (request.hasQueryParamMatching(
                        ApiKeyInterceptor.API_KEY_QUERY_PARAM,
                        aValidApiKey
                    )
                ) {
                    200
                } else {
                    401
                }.let { responseCode ->
                    MockResponse().setResponseCode(responseCode)
                }
            }

            withMockServer(dispatcher) { server ->

                val clientWithOneInterceptor = OkHttpClient.Builder().addInterceptor(interceptor).build()
                val clientWithTwoInterceptors =
                    clientWithOneInterceptor.newBuilder().addInterceptor(interceptor).build()

                val requestGen: Gen<Request> = requestGen { server.url(it) }

                checkAll(requestGen) { request ->

                    val responseWithoutInterceptor =
                        clientWithOneInterceptor.newCall(request).await()
                    val responseWithInterceptor = clientWithTwoInterceptors.newCall(request).await()

                    responseWithoutInterceptor == responseWithInterceptor

                }

            }

        }
    }
}