package com.application.mymeteo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.mymeteo.repository.WeatherRepository
import com.application.mymeteo.states.WeatherUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WeatherViewModel(
    private val repository: WeatherRepository
) : ViewModel() {

    // 1. L'état mutable privé (seul le ViewModel peut le modifier)
    private val _uiState = MutableStateFlow<WeatherUiState>(WeatherUiState.Loading)

    // 2. L'état public en lecture seule (l'UI va l'observer)
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()

    init {
        // Au démarrage du ViewModel, on charge la météo
        loadWeather()
    }

    fun loadWeather() {
        // 3. viewModelScope lance une coroutine liée à la durée de vie de l'écran
        viewModelScope.launch {
            // Exemple avec les coordonnées de Paris
            repository.getWeather(latitude = 48.8566, longitude = 2.3522).collect { state ->
                // 4. À chaque nouvel état émis par le Repository, on met à jour l'UI
                _uiState.value = state
            }
        }
    }
}