package com.application.mymeteo.repository

import com.application.mymeteo.states.WeatherUiState
import com.application.mymeteo.weatherData.GeocodingResponse
import com.application.mymeteo.weatherData.Location
import com.application.mymeteo.weatherData.WeatherResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.* import io.ktor.client.statement.* import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withTimeout

// 1. L'interface mise à jour avec les deux fonctions nécessaires
interface WeatherRepository {
    suspend fun searchCityCoordinates(query: String): Location?
    fun getWeather(cityName: String, latitude: Double, longitude: Double): Flow<WeatherUiState>
}

// 2. L'implémentation propre
class NetworkWeatherRepository(private val httpClient: HttpClient) : WeatherRepository {

    // Chercher les coordonnées d'une ville (Géocodage)
    override suspend fun searchCityCoordinates(query: String): Location? {
        return try {
            val response: HttpResponse = httpClient.get("https://geocoding-api.open-meteo.com/v1/search") {
                parameter("name", query)
                parameter("count", 1)
                parameter("language", "fr")
            }

            if (response.status.isSuccess()) {
                val data: GeocodingResponse = response.body()
                data.results?.firstOrNull()
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // Récupérer la météo avec le nom de la ville et ses coordonnées
    override fun getWeather(cityName: String, latitude: Double, longitude: Double): Flow<WeatherUiState> = flow {
        emit(WeatherUiState.Loading)
        try {
            withTimeout(3000L) {
                val urlString = "https://api.open-meteo.com/v1/forecast?latitude=$latitude&longitude=$longitude&current=temperature_2m,weather_code"

                val response: HttpResponse = httpClient.get(urlString) {
                    header(HttpHeaders.UserAgent, "MonAppMeteoEntretien/1.0")
                    header(HttpHeaders.Accept, "application/json")
                }

                if (response.status.isSuccess()) {
                    val weatherData: WeatherResponse = response.body()
                    emit(
                        WeatherUiState.Success(
                            city = cityName,
                            temperature = "${weatherData.current.temperature}°C",
                            description = "Code météo: ${weatherData.current.weathercode}"
                        )
                    )
                } else {
                    emit(WeatherUiState.Error("Erreur serveur : Code ${response.status.value}"))
                }
            }
        } catch (e: Exception) {
            emit(WeatherUiState.Error("Problème réseau : ${e.message}"))
        }
    }
}