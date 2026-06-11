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
    val httpClient = remember {
        HttpClient {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
            install(HttpTimeout) {
                requestTimeoutMillis = 3000 // 3 secondes
            }
        }
    }

    val repository = remember { NetworkWeatherRepository(httpClient) }
    val viewModel = remember { WeatherViewModel(repository) }

    // On écoute les deux états du ViewModel
    val uiState by viewModel.uiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    // On connecte les états et événements au composant visuel
    WeatherScreen(
        uiState = uiState,
        searchQuery = searchQuery,
        onSearchQueryChange = { viewModel.updateSearchQuery(it) },
        onSearchClick = { viewModel.onSearchClick() },
        onRetryClick = { viewModel.onSearchClick() } // Relance la recherche actuelle
    )
}