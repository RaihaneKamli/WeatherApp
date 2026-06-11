package com.application.mymeteo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.mymeteo.repository.NetworkWeatherRepository
import com.application.mymeteo.repository.WeatherRepository
import com.application.mymeteo.states.WeatherUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WeatherViewModel(
    private val repository: NetworkWeatherRepository
) : ViewModel() {

    // 1. État pour stocker ce que l'utilisateur tape dans la barre de recherche
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // L'état de l'interface (comme avant)
    private val _uiState = MutableStateFlow<WeatherUiState>(WeatherUiState.Loading)
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()

    init {
        // Au démarrage, on charge Paris par défaut
        searchWeatherForCity("Paris")
    }

    // Mise à jour du texte tapé
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    // 2. La fonction clé qui chaîne les deux appels réseau
    fun onSearchClick() {
        val query = _searchQuery.value
        if (query.isNotBlank()) {
            searchWeatherForCity(query)
        }
    }

    private fun searchWeatherForCity(cityName: String) {
        viewModelScope.launch {
            _uiState.value = WeatherUiState.Loading // On affiche le loader

            // Étape A : Trouver les coordonnées
            val location = repository.searchCityCoordinates(cityName)

            if (location != null) {
                // Étape B : Si on a trouvé la ville, on cherche sa météo
                // On utilise collect pour écouter les émissions du Flow
                repository.getWeather(location.name, location.latitude, location.longitude).collect { state ->
                    _uiState.value = state
                }
            } else {
                // Étape C : Si la ville n'existe pas
                _uiState.value = WeatherUiState.Error("Ville '$cityName' introuvable.")
            }
        }
    }
}