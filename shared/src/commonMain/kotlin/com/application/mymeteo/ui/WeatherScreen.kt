package com.application.mymeteo.ui

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
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.text.font.FontWeight

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
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp), // Ajoute une belle ombre
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer // S'adapte au thème clair/sombre du téléphone
        )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp) // Respiration interne
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = "Position",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = state.city,
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = state.temperature,
                style = MaterialTheme.typography.displayLarge, // Température beaucoup plus grosse
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = state.description,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
            )
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
    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {

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

