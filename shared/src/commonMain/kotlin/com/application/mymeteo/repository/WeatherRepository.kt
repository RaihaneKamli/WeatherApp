package com.application.mymeteo.repository

import com.application.mymeteo.states.WeatherUiState
import com.application.mymeteo.weatherData.WeatherResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import io.ktor.http.* // Ajout pour isSuccess()
import io.ktor.client.statement.* // Ajout important pour HttpResponse
import kotlinx.coroutines.delay
import io.ktor.client.*
import kotlinx.coroutines.withTimeout

// 1. L'interface

interface WeatherRepository {
    fun getWeather(latitude: Double, longitude: Double): Flow<WeatherUiState>
}

// 2. L'implémentation (Le vrai code réseau que tu as déjà.)
class NetworkWeatherRepository(private val httpClient: HttpClient) : WeatherRepository {
    override fun getWeather(latitude: Double, longitude: Double): Flow<WeatherUiState> = flow {
        emit(WeatherUiState.Loading)
        println("TRACKER 1 : Début de l'appel réseau")

        try {
            // Quoi qu'il arrive, le bloc s'arrête net.
            withTimeout(2000L) {

                val urlString = "https://api.open-meteo.com/v1/forecast?latitude=$latitude&longitude=$longitude&current=temperature_2m,weather_code"
                println("@@@ hello url = $urlString")

                val response: HttpResponse = httpClient.get(urlString) {
                    header(HttpHeaders.UserAgent, "MonAppMeteoEntretien/1.0")
                    header(HttpHeaders.Accept, "application/json")
                }

                println("TRACKER 2 : Réponse HTTP = ${response.status.value}")

                if (response.status.isSuccess()) {
                    val weatherData: WeatherResponse = response.body()
                    emit(WeatherUiState.Success(
                        city = "Paris",
                        temperature = "${weatherData.current.temperature}°C",
                        description = "Code météo: ${weatherData.current.weathercode}"
                    ))
                } else {
                    emit(WeatherUiState.Error("Erreur serveur : Code ${response.status.value}"))
                }
            } // Fin du withTimeout

        } catch (e: Exception) {
            // Si le timeout des 3 secondes expire, une TimeoutCancellationException est levée et capturée ici !
            println("TRACKER ERREUR : L'appel a été annulé ou a échoué -> ${e.message}")
            e.printStackTrace()
            emit(WeatherUiState.Error("Problème réseau (ou Timeout) : ${e.message}"))
        }
    }
}

