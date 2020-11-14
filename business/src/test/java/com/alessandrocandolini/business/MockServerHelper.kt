package com.alessandrocandolini.business

import okhttp3.HttpUrl
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer

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

internal fun MockWebServer.fullUrl(path : String) : HttpUrl = url(path)