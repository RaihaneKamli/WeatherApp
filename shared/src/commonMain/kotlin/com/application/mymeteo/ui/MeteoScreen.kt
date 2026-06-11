package com.application.mymeteo.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.application.mymeteo.states.WeatherUiState
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.unit.dp
import mymeteo.shared.generated.resources.Res
import org.jetbrains.compose.resources.getString

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
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = state.city, style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = state.temperature, style = MaterialTheme.typography.displayMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = state.description, style = MaterialTheme.typography.bodyLarge)
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