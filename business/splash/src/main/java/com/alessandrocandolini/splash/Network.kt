package com.alessandrocandolini.splash

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import retrofit2.http.GET
import retrofit2.http.Query
import javax.inject.Inject

inline class CityId(val id: Int)

@Serializable
data class WeatherResponse(
    @SerialName("id")
    val cityId: Int,  // TODO replace type with CityId, inline classes are not yet supported by kotlinx.serialization

    @SerialName("name")
    val name: String

)


interface WeatherApi {

    @GET("/data/2.5/weather")
    suspend fun fetchByCityName(@Query("q") city: String): WeatherResponse

}

fun interface ApiKeyStore {
    fun fetchKey(): String
}


// how to test this?
// We have several options:
//
// 1. we unit test the function intercept(chain: Interceptor.Chain): Response. Cons: we need to go into the details
// of the internals of the okhttp client.
//
// 2. we can check how the interceptor ALONE changes the behaviour of a default instance of the okhttp client
// This will give as confidence in the way the interceptor per se is modifying the behaviour of the okhttp client, when used alone
//
// 3. we can check how the interceptor will change the okhttp behaviour using the same instance of okhttp used in prod ,
// to validate whether this works with a setup close to prod, and to check if the interceptor plays well with other interceptors and/or components

class ApiKeyInterceptor @Inject constructor(private val store: ApiKeyStore) : Interceptor {

    private val addAuthentication : Request.() -> Request by lazy {
        val apiKey = store.fetchKey()
        transformRequestUrl(replaceQueryParamHttpUrl(API_KEY_QUERY_PARAM, apiKey))
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest: Request = chain.request()
        val request = originalRequest.addAuthentication() // just for fun
        return chain.proceed(request)
    }

    companion object ApiKeyInterceptor {
        const val API_KEY_QUERY_PARAM = "appid"

        val transformRequestUrl: ((HttpUrl) -> HttpUrl) -> (Request) -> Request = { transformUrl ->
            { originalRequest ->
                originalRequest.newBuilder().url(originalRequest.url.let(transformUrl)).build()
            }
        }

        val replaceQueryParamHttpUrl: (String, String) -> (HttpUrl) -> HttpUrl = { key, value ->
            { httpUrl ->
                httpUrl.newBuilder()
                    .removeAllQueryParameters(key) // <- is this needed? is this over-defensive code?
                    .addQueryParameter(key, value)
                    .build()
            }
        }

    }

}