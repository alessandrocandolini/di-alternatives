package com.alessandrocandolini.splash

import com.alessandrocandolini.withMockServer
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeIn
import io.kotest.property.Exhaustive
import io.kotest.property.Gen
import io.kotest.property.checkAll
import io.kotest.property.exhaustive.collection
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest
import retrofit2.Retrofit
import retrofit2.create

class WeatherApiTest : FunSpec() {

    init {
        test("WeatherApi can correctly return parsed responses when the server return 200 with valid json response body") {

            withMockServer { server ->

                val validJsonResponses: Gen<String> = Exhaustive.collection(
                    setOf(
                        """
            {"coord":{"lon":-0.13,"lat":51.51},"weather":[{"id":802,"main":"Clouds","description":"scattered clouds","icon":"03n"}],"base":"stations","main":{"temp":279.48,"feels_like":276.56,"temp_min":278.71,"temp_max":280.37,"pressure":1017,"humidity":81},"visibility":10000,"wind":{"speed":2.1,"deg":50},"clouds":{"all":40},"dt":1606501853,"sys":{"type":1,"id":1414,"country":"GB","sunrise":1606462727,"sunset":1606492689},"timezone":0,"id":2643743,"name":"London","cod":200}
        """,
                        """
            {"coord":{"lon":147.22,"lat":-9.52},"weather":[{"id":802,"main":"Clouds","description":"scattered clouds","icon":"03n"}],"base":"stations","main":{"temp":296.15,"feels_like":298.32,"temp_min":296.15,"temp_max":296.15,"pressure":1007,"humidity":94},"visibility":10000,"wind":{"speed":3.6,"deg":320},"clouds":{"all":40},"dt":1606503876,"sys":{"type":1,"id":42,"country":"PG","sunrise":1606506038,"sunset":1606551473},"timezone":36000,"id":2088122,"name":"Pari","cod":200}
        """,
                        """
            {"coord":{"lon":-10.59,"lat":6.65},"weather":[{"id":802,"main":"Clouds","description":"scattered clouds","icon":"03n"}],"base":"stations","main":{"temp":301.15,"feels_like":306.42,"temp_min":301.15,"temp_max":301.15,"pressure":1010,"humidity":83},"visibility":10000,"wind":{"speed":1.5,"deg":190},"clouds":{"all":40},"dt":1606503899,"sys":{"type":1,"id":2389,"country":"LR","sunrise":1606459026,"sunset":1606501412},"timezone":0,"id":2274890,"name":"New","cod":200}
        """
                    ).map { it.trimIndent() }
                )

                val retrofit = Retrofit.Builder()
                    .addConverterFactory(Json {
                        ignoreUnknownKeys = true
                    }.asConverterFactory("application/json".toMediaType()))
                    .baseUrl(server.url("/"))
                    .build()

                val weatherApi: WeatherApi = retrofit.create()

                checkAll(validJsonResponses) { body ->

                    server.dispatcher = object : Dispatcher() {
                        override fun dispatch(request: RecordedRequest): MockResponse =
                            when (request.requestUrl?.encodedPath) {
                                "/data/2.5/weather" -> MockResponse().setResponseCode(200)
                                    .setBody(body)
                                else -> MockResponse().setResponseCode(404)
                            }

                    }

                    weatherApi.fetchByCityName(city = "anyCity") shouldBeIn listOf(
                        WeatherResponse(2643743, "London"),
                        WeatherResponse(2088122, "Pari"),
                        WeatherResponse(2274890, "New"),
                    )

                }
            }


        }
    }
}