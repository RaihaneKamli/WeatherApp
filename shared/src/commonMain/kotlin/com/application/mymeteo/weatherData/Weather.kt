package com.application.mymeteo.weatherData

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName
@Serializable
data class WeatherResponse(
    val current: CurrentWeather
)

@Serializable
data class CurrentWeather(
    @SerialName("temperature_2m") val temperature: Double,
    @SerialName("weather_code") val weathercode: Int
)