package com.application.mymeteo

import app.cash.turbine.test
import com.application.mymeteo.repository.WeatherRepository
import com.application.mymeteo.states.WeatherUiState
import com.application.mymeteo.viewmodel.WeatherViewModel
import com.application.mymeteo.weatherData.Location // Assure-toi d'importer ton modèle Location
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

    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun successLoading() = runTest {
        val fakeRepository = object : WeatherRepository {
            // Implémentation de la fonction de géocodage appelée par le ViewModel
            override suspend fun searchCityCoordinates(query: String): Location? {
                return Location(name = "Paris (Mock)", latitude = 48.8566, longitude = 2.3522, country = "France")
            }

            // Mise à jour de la signature avec les 3 paramètres
            override fun getWeather(cityName: String, latitude: Double, longitude: Double): Flow<WeatherUiState> {
                return flowOf(
                    WeatherUiState.Loading,
                    WeatherUiState.Success("Paris (Mock)", "20°C", "Ensoleillé")
                )
            }
        }

        val viewModel = WeatherViewModel(fakeRepository)

        viewModel.uiState.test {
            assertEquals(WeatherUiState.Loading, awaitItem())

            // Selon l'ordre des appels dans ton ViewModel,
            // il peut y avoir un autre état Loading après la géolocalisation
            val secondState = awaitItem()
            if (secondState is WeatherUiState.Loading) {
                assertEquals("Paris (Mock)", (awaitItem() as WeatherUiState.Success).city)
            } else {
                assertEquals("Paris (Mock)", (secondState as WeatherUiState.Success).city)
            }

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun errorLoading() = runTest {
        val fakeRepository = object : WeatherRepository {
            override suspend fun searchCityCoordinates(query: String): Location? {
                return Location(name = "Paris", latitude = 48.8566, longitude = 2.3522, country = "France")
            }

            // Mise à jour de la signature avec les 3 paramètres
            override fun getWeather(cityName: String, latitude: Double, longitude: Double): Flow<WeatherUiState> {
                return flowOf(WeatherUiState.Loading, WeatherUiState.Error("Mock Http Error"))
            }
        }

        val viewModel = WeatherViewModel(fakeRepository)

        viewModel.uiState.test {
            assertEquals(WeatherUiState.Loading, awaitItem())

            // Gestion similaire du double Loading selon l'implémentation du ViewModel
            val nextItem = awaitItem()
            val errorState = if (nextItem is WeatherUiState.Loading) awaitItem() else nextItem

            assertEquals("Mock Http Error", (errorState as WeatherUiState.Error).message)
            cancelAndIgnoreRemainingEvents()
        }
    }
}