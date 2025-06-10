package com.example.assignment08.ex2

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach


class LocationTrackingViewModel(
    private val context: Context,
    private val locationSensor: LocationSensor
) : ViewModel() {

    var uiState by mutableStateOf(LocationTrackingUiState())
        private set
    
    init {
        // Start listening to location data
        locationSensor.locationData
            .onEach { locationData ->
                uiState = uiState.copy(
                    latitude = locationData.latitude,
                    longitude = locationData.longitude,
                    totalDistance = locationData.totalDistance,
                    isPermissionGranted = locationData.isPermissionGranted
                )
            }
            .catch { error ->
                // Handle errors
                uiState = uiState.copy(errorMessage = error.message)
            }
            .launchIn(viewModelScope)
    }
    

    fun startTracking() {
        locationSensor.startTracking()
        uiState = uiState.copy(isTracking = true)
    }
    

    fun stopTracking() {
        locationSensor.stopTracking()
        uiState = uiState.copy(isTracking = false)
    }
    

    fun resetTracking() {
        locationSensor.resetTracking()
        uiState = uiState.copy(
            latitude = 0.0,
            longitude = 0.0,
            totalDistance = 0.0f,
            isTracking = false
        )
    }
    
    override fun onCleared() {
        super.onCleared()
        locationSensor.stopTracking()
    }
    

    class Factory(
        private val context: Context
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(LocationTrackingViewModel::class.java)) {
                return LocationTrackingViewModel(
                    context = context,
                    locationSensor = LocationSensor(context)
                ) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }    }
}
