package com.application.mymeteo

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.application.mymeteo.repository.NetworkWeatherRepository
import com.application.mymeteo.repository.WeatherRepository
import com.application.mymeteo.ui.WeatherScreen
import com.application.mymeteo.viewmodel.WeatherViewModel
import io.ktor.client.*
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

@Composable
@Preview
fun App() {
    // 1. Configuration du client Ktor avec le plugin de sérialisation JSON
    val httpClient = remember {
        HttpClient {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
            // AJOUT DU TIMEOUT : Force une erreur si le serveur ne répond pas en 10s
            install(HttpTimeout) {
                requestTimeoutMillis = 2000
            }
        }
    }

    // 2. Instanciation de la couche Data (Repository)
    val repository = remember { NetworkWeatherRepository(httpClient) }

    // 3. Instanciation du ViewModel (Cerveau)
    val viewModel = remember { WeatherViewModel(repository) }

    // 4. Le Lien Magique : On convertit le StateFlow du ViewModel en un State Compose
    // À chaque fois que le StateFlow change, l'UI va se redessiner (recomposition)
    val uiState by viewModel.uiState.collectAsState()

    // 5. On affiche notre écran en lui passant l'état et l'action à mener en cas de clic
    WeatherScreen(
        uiState = uiState,
        onRetryClick = { viewModel.loadWeather() }
    )
}