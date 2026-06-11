package com.application.mymeteo.states

import androidx.compose.runtime.Composable

sealed interface WeatherUiState {
    object Loading : WeatherUiState
    data class Success(val city: String, val temperature: String, val description: String) : WeatherUiState
    data class Error(val message: String) : WeatherUiState
}
