package com.alessandrocandolini.di_alternatives.splash

data class WeatherResponse(
    val id : Int // city id
)

interface WeatherApi {

    fun fetchByCityName(city : String) : WeatherResponse

}