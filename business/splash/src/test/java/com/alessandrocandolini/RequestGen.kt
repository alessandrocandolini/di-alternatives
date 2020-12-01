package com.alessandrocandolini

import io.kotest.property.Arb
import io.kotest.property.Exhaustive
import io.kotest.property.Gen
import io.kotest.property.arbitrary.bind
import io.kotest.property.arbitrary.choice
import io.kotest.property.arbitrary.filter
import io.kotest.property.arbitrary.string
import io.kotest.property.exhaustive.collection
import okhttp3.HttpUrl
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

internal object RequestGen {

    private enum class HttpMethod {
        POST, PATCH, DELETE, PUT, GET, HEAD
    }

    private val nonEmptyBodyGen: Arb<String> = Arb.string().filter { it.isNotBlank() }

    fun requestGen(pathToFullUrl: (String) -> HttpUrl): Gen<Request> {
        val httpUrlGen: Gen<HttpUrl> = Exhaustive.collection(setOf(
            "api/v1/",
            "/api?api=test&appid=something",
            "/api?api=test"
        ).map { u -> pathToFullUrl(u) })

        return requestGen(httpUrlGen)
    }

    fun requestGen(
        httpUrlGen: Gen<HttpUrl>,
        bodyGen: Gen<String> = nonEmptyBodyGen,

        ): Gen<Request> {

        fun HttpMethod.toOkHttpMethodName() = name.toUpperCase()

        val httpMethodWithBodyGen: Gen<HttpMethod> = Exhaustive.collection(
            setOf(
                HttpMethod.POST,
                HttpMethod.PATCH,
                HttpMethod.DELETE,
                HttpMethod.PUT
            )
        )
        val httpMethodWithoutBodyGen: Gen<HttpMethod> =
            Exhaustive.collection(setOf(HttpMethod.GET, HttpMethod.HEAD))

        val requestsWithBody: Arb<Request> = Arb.bind(
            httpUrlGen,
            httpMethodWithBodyGen,
            bodyGen
        ) { httpUrl, httpMethod, body ->
            Request.Builder()
                .method(httpMethod.toOkHttpMethodName(), body.toRequestBody())
                .url(httpUrl)
                .build()
        }

        val requestsWithoutBody: Arb<Request> =
            Arb.bind(httpUrlGen, httpMethodWithoutBodyGen) { httpUrl, httpMethod ->
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