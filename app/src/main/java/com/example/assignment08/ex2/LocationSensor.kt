package com.example.assignment08.ex2

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Looper
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

class LocationSensor(
    private val context: Context
) {
    private val locationClient: FusedLocationProviderClient = 
        LocationServices.getFusedLocationProviderClient(context)
    
    private val _locationDataChannel = Channel<LocationData>(Channel.CONFLATED)
    val locationData: Flow<LocationData> = _locationDataChannel.receiveAsFlow()
    
    private var isTracking = false
    private var lastLocation: Location? = null
    private var totalDistance = 0.0f
    
    // Location request configuration
    private val locationRequest = LocationRequest.Builder(
        Priority.PRIORITY_HIGH_ACCURACY, 
        5000 // Update interval in milliseconds (5 seconds)
    ).build()
    
    // Location callback to receive updates
    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            result.lastLocation?.let { location ->
                updateLocationData(location)
            }
        }
    }
    

    fun hasLocationPermission(): Boolean {
        return hasPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
               hasPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
    }
    
    private fun hasPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            context, 
            permission
        ) == PermissionChecker.PERMISSION_GRANTED
    }
    
    /**
     * Start tracking location updates
     */
    @SuppressLint("MissingPermission")
    fun startTracking() {
        if (!hasLocationPermission()) {
            _locationDataChannel.trySend(
                LocationData(
                    latitude = 0.0,
                    longitude = 0.0,
                    totalDistance = totalDistance,
                    isPermissionGranted = false
                )
            )
            return
        }
        
        if (!isTracking) {
            isTracking = true
            
            // Reset distance if starting a new tracking session
            if (lastLocation == null) {
                totalDistance = 0.0f
            }
            
            // Request location updates
            locationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        }
    }
    

    fun stopTracking() {
        if (isTracking) {
            locationClient.removeLocationUpdates(locationCallback)
            isTracking = false
        }
    }
    

    fun resetTracking() {
        lastLocation = null
        totalDistance = 0.0f
        
        _locationDataChannel.trySend(
            LocationData(
                latitude = 0.0,
                longitude = 0.0,
                totalDistance = totalDistance,
                isPermissionGranted = hasLocationPermission()
            )
        )
    }
    

    private fun updateLocationData(location: Location) {
        // Calculate distance if we have a previous location
        if (lastLocation != null) {
            val distance = lastLocation!!.distanceTo(location)
            totalDistance += distance
        }
        
        // Update last location
        lastLocation = location
        
        // Send updated data through the channel
        _locationDataChannel.trySend(
            LocationData(
                latitude = location.latitude,
                longitude = location.longitude,
                totalDistance = totalDistance,
                isPermissionGranted = true
            )
        )
    }
}


data class LocationData(
    val latitude: Double,
    val longitude: Double,
    val totalDistance: Float,
    val isPermissionGranted: Boolean
)
