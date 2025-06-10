package com.example.assignment08.ex2


data class LocationTrackingUiState(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val totalDistance: Float = 0.0f,
    val isTracking: Boolean = false,
    val isPermissionGranted: Boolean = false,
    val errorMessage: String? = null
)
