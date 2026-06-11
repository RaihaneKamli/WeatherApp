package com.application.mymeteo.weatherData

import androidx.compose.runtime.Composable
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

@Serializable
data class GeocodingResponse(
    // La liste peut être nulle si la ville n'existe pas
    val results: List<Location>? = null
)

@Serializable
data class Location(
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val country: String
)

@Composable
fun getWeatherDescription(code: Int): String {
    return when (code) {
        0 -> "Ciel clair"
        1, 2, 3 -> "Partiellement nuageux"
        45, 48 -> "Brouillard"
        51, 53, 55 -> "Bruine"
        61, 63, 65 -> "Pluie"
        else -> "Conditions variables"
    }
}