package com.alessandrocandolini

import io.kotest.core.test.TestContext
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest

internal fun MockResponse.setBodyFromResource(path: String): MockResponse =
    javaClass.classLoader?.getResource(path)?.readText().let { fileContent ->
        setBody(fileContent ?: "")
    }

internal suspend fun <V> TestContext.withMockServer(test: suspend TestContext.(MockWebServer) -> V): V =
    withMockServer(
        setup = { s ->
            s.start()
        },
        tierDown = { s ->
            s.shutdown()
        },
        test
    )

internal suspend fun <V> TestContext.withMockServer(dispatcher : Dispatcher, test: suspend TestContext.(MockWebServer) -> V): V =
    withMockServer(
        setup = { s ->
            s.start()
            s.dispatcher = dispatcher
        },
        tierDown = { s ->
            s.shutdown()
        },
        test
    )


private suspend fun <V> TestContext.withMockServer(setup : (MockWebServer) -> Unit, tierDown : (MockWebServer) -> Unit, test: suspend TestContext.(MockWebServer) -> V): V {
    val s = MockWebServer()
    return try {
        setup(s)
        test(s)
    } finally {
        tierDown(s)
    }
}

internal fun ((RecordedRequest) -> MockResponse).toDispatcher() : Dispatcher =
    let { block -> object : Dispatcher() {
        override fun dispatch(request: RecordedRequest): MockResponse = block(request)
    }
}

internal fun RecordedRequest.hasQueryParamMatching(key : String , expectedValue : String) : Boolean =
    when (requestUrl?.queryParameterValues(key)) {
        listOf(expectedValue) -> true
        else -> false
    }