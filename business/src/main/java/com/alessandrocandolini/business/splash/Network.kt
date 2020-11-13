package com.alessandrocandolini.business.splash

//import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import retrofit2.http.GET
import retrofit2.http.Query


inline class CityId(val id: Int)

//@Serializable
data class WeatherResponse(
//    @SerialName("id")
    val id: Int  // TODO replace type with CityId
)


interface WeatherApi {

    @GET("/data/2.5/weather")
    suspend fun fetchByCityName(@Query("q") city: String) : WeatherResponse

}

interface ApiKeyStore {
    fun fetchKey() : String
}

// how to test this?
// We jave several options
//
// 1. we unit test the function intercept(chain: Interceptor.Chain): Response. Cons: we need to go into the details
// of the internals of the okhttp client.
//
// 2. we can check how the interceptr ALONE changes the behaviour of a default instance of the okhttp client
// This will give as confidence in the way the interceptor per se is modifying the behaviour of the okhttp client, when used alone
//
// 3. we can check how the interceptr will change the okhttp behhaviour using the same instance of okhttp used in prod ,
// to validate whether this works with a setup close to prod, and to check if the interceptor plays well with other interceptors and/or components

class ApiKeyInterceptor(private val store : ApiKeyStore) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        var request: Request = chain.request()
        val url: HttpUrl = request.url.newBuilder().addQueryParameter("appid", store.fetchKey()).build()
        request = request.newBuilder().url(url).build()
        return chain.proceed(request)
    }

}

class RemoveQueryParamInterceptor: Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        var request: Request = chain.request()
        val url: HttpUrl = request.url.newBuilder().removeAllQueryParameters("appid").build()
        request = request.newBuilder().url(url).build()
        return chain.proceed(request)
    }

}