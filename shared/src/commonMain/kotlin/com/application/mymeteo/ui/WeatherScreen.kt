package com.application.mymeteo.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.application.mymeteo.states.WeatherUiState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brightness5
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import com.application.mymeteo.weatherData.getWeatherDescription

/**
 *
 * @param uiState State from UI
 * @param onRetryClick CallBack for retry data loading
 * @param modifier
 * */

@Composable
fun WeatherScreen(uiState: WeatherUiState,
                  onRetryClick: () -> Unit,
                  modifier: Modifier = Modifier) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when (uiState) {
            is WeatherUiState.Loading -> LoadingComponent()
            is WeatherUiState.Success -> WeatherSuccessComponent(state = uiState)
            is WeatherUiState.Error -> ErrorComponent(message = uiState.message, onRetry = onRetryClick)
        }
    }
}

// 3. Sous-composant de chargement
@Composable
fun LoadingComponent() {
    CircularProgressIndicator()
}

// 4. Sous-composant de succès
@Composable
fun WeatherSuccessComponent(state: WeatherUiState.Success) {
    // 1. On enveloppe dans une Box pour centrer tout le bloc sur l'écran
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        AnimatedContent(targetState = state, label = "weather_anim") { targetState ->
            Card(
                // 2. On utilise 0.9f (90%) pour laisser de l'air sur les côtés
                modifier = Modifier.fillMaxWidth(0.9f),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
            ) {
                Column(
                    // 3. Padding interne pour que le texte ne colle pas aux bords de la carte
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    // 4. Centrage horizontal des éléments internes
                ) {
                    Text(
                        targetState.city,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        targetState.temperature,
                        style = MaterialTheme.typography.displayLarge
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = getWeatherDescription(targetState.description.toIntOrNull() ?: 0),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Icon(
                        imageVector = if ((targetState.description.toIntOrNull() ?: 0) < 10) Icons.Default.Brightness5 else Icons.Default.Cloud,
                        contentDescription = "Météo",
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

    }
}

// 5. Sous-composant d'erreur
@Composable
fun ErrorComponent(message: String, onRetry: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.padding(16.dp)
    ) {
        Text(text = message, color = MaterialTheme.colorScheme.error)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRetry) {
            Text("Try again")
        }
    }
}

@Composable
fun WeatherScreen(
    uiState: WeatherUiState,
    searchQuery: String,                    // Nouvel état reçu
    onSearchQueryChange: (String) -> Unit,  // Nouvel événement
    onSearchClick: () -> Unit,              // Nouvel événement
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                        MaterialTheme.colorScheme.background
                    )
                )
            )
            .padding(16.dp)
    ) {

        // 1. La barre de recherche (toujours visible en haut)
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            label = { Text("Rechercher une ville") },
            leadingIcon = {
                Icon(imageVector = Icons.Default.Search, contentDescription = "Icône de recherche")
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(16.dp), // Bords bien arrondis
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(
                onSearch = { onSearchClick() }
            ),
            trailingIcon = {
                Button(
                    onClick = onSearchClick,
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Text("Go")
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 2. La zone de contenu (prend le reste de l'espace)
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            when (uiState) {
                is WeatherUiState.Loading -> LoadingComponent()
                is WeatherUiState.Success -> WeatherSuccessComponent(state = uiState)
                is WeatherUiState.Error -> ErrorComponent(message = uiState.message, onRetry = onRetryClick)
            }
        }
    }
}

