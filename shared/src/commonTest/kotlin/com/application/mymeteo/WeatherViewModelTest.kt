package com.application.mymeteo

import app.cash.turbine.test
import com.application.mymeteo.repository.WeatherRepository
import com.application.mymeteo.states.WeatherUiState
import com.application.mymeteo.viewmodel.WeatherViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class WeatherViewModelTest {

    // 1. Le TestDispatcher remplace le Thread UI. Il exécute les coroutines instantanément.
    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setUp() {
        // Avant chaque test, on force l'application à utiliser notre "faux" Thread
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        // Après le test, on remet tout en ordre
        Dispatchers.resetMain()
    }

    @Test
    fun successLoading() = runTest {
        // ARRANGE : On crée un "Faux" Repository qui n'utilise pas Ktor ni internet
        val fakeRepository = object : WeatherRepository {
            override fun getWeather(latitude: Double, longitude: Double): Flow<WeatherUiState> {
                // flowOf émet immédiatement ces deux valeurs l'une après l'autre
                return flowOf(
                    WeatherUiState.Loading,
                    WeatherUiState.Success("Paris (Mock)", "20°C", "Ensoleillé")
                )
            }
        }

        // ACT : On instancie notre ViewModel avec ce faux repository
        val viewModel = WeatherViewModel(fakeRepository)

        // ASSERT : On utilise Turbine pour écouter les états émis par le StateFlow
        viewModel.uiState.test {
            // Le tout premier état doit être Loading (valeur initiale du StateFlow)
            val firstState = awaitItem()
            assertEquals(WeatherUiState.Loading, firstState)

            // Le deuxième état doit être notre Success simulé
            val secondState = awaitItem()
            assertEquals("Paris (Mock)", (secondState as WeatherUiState.Success).city)

            // On s'assure qu'aucun autre état parasite n'est émis
            cancelAndIgnoreRemainingEvents()
        }
    }
    @Test
    fun errorLoading() = runTest{
        val fakeRepository = object : WeatherRepository {
            override fun getWeather(latitude: Double, longitude: Double): Flow<WeatherUiState> {
                return flowOf(WeatherUiState.Loading, WeatherUiState.Error("Mock Http Error"))
            }
        }

        val viewModel = WeatherViewModel(fakeRepository)

        viewModel.uiState.test {
            val firstState = awaitItem()
            assertEquals(WeatherUiState.Loading, firstState)

            val secondState = awaitItem()
            assertEquals("Mock Http Error", (secondState as WeatherUiState.Error).message)

            cancelAndIgnoreRemainingEvents()
        }
    }
}