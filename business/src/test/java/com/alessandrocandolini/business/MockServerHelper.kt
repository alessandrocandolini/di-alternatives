package com.alessandrocandolini.business

import okhttp3.HttpUrl
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest

internal fun MockResponse.setBodyFromResource(path: String): MockResponse =
    javaClass.classLoader?.getResource(path)?.readText().let { fileContent ->
        setBody(fileContent ?: "")
    }

internal fun <V> withMockServer(f: MockWebServer.() -> V): V {
    val s = MockWebServer().apply {
        start()
    }
    val r = s.run(f)
    s.shutdown()
    return r
}

internal suspend fun <V> withMockServer2(f: suspend (MockWebServer) -> V): V {
    val s = MockWebServer().apply {
        start()
    }
    val r = f(s)
    s.shutdown()
    return r
}

internal fun MockWebServer.fullUrl(path : String) : HttpUrl = url(path)

internal fun ((RecordedRequest) -> MockResponse).toDispatcher() : Dispatcher =
    let { block -> object : Dispatcher() {
        override fun dispatch(request: RecordedRequest): MockResponse = block(request)
    }
}