package com.example.assignment08

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import androidx.navigation.Navigation
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.assignment08.ex1.SpiritLevelScreen
import com.example.assignment08.ex1.SpiritLevelViewModel
import com.example.assignment08.ex2.LocationTrackingScreen
import com.example.assignment08.ex2.LocationTrackingViewModel
import com.example.assignment08.ui.theme.Assignment08Theme

class MainActivity : ComponentActivity() {
    
    // Initialize the ViewModels
    private val spiritLevelViewModel: SpiritLevelViewModel by viewModels {
        SpiritLevelViewModel.Factory(applicationContext)
    }
    
    private val locationTrackingViewModel: LocationTrackingViewModel by viewModels {
        LocationTrackingViewModel.Factory(applicationContext)
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        setContent {
            Assignment08Theme {
                val navController = rememberNavController()
                NavigationHost(navController)
            }
        }
    }
    @Composable
    fun NavigationHost(navController: NavHostController) {
        NavHost(
            navController = navController,
            startDestination = "main_screen"
        ) {
            composable("main_screen") {
                MainScreen(navController)
            }
            composable("ex1_screen") {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    SpiritLevelScreen(
                        navController = navController,
                        uiState = spiritLevelViewModel.uiState,
                        onModeToggle = spiritLevelViewModel::toggleMode,
                        onSoundToggle = spiritLevelViewModel::toggleSound,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }

            composable("ex2_screen") {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val context = LocalContext.current
                    
                    // State to track whether permissions are granted
                    var hasLocationPermission by remember {
                        mutableStateOf(
                            ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.ACCESS_FINE_LOCATION
                            ) == PackageManager.PERMISSION_GRANTED &&
                            ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            ) == PackageManager.PERMISSION_GRANTED
                        )
                    }
                    
                    // Set up permission launcher
                    val locationPermissionLauncher = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.RequestMultiplePermissions()
                    ) { permissions ->
                        hasLocationPermission = permissions.values.all { it }
                    }
                    
                    // Request permissions if needed
                    LaunchedEffect(key1 = hasLocationPermission) {
                        if (!hasLocationPermission) {
                            locationPermissionLauncher.launch(
                                arrayOf(
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                                )
                            )
                        }
                    }
                    
                    LocationTrackingScreen(
                        navController = navController,
                        uiState = locationTrackingViewModel.uiState.copy(
                            isPermissionGranted = hasLocationPermission
                        ),
                        onStartTracking = locationTrackingViewModel::startTracking,
                        onStopTracking = locationTrackingViewModel::stopTracking,
                        resetTracking = locationTrackingViewModel::resetTracking,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

    @Composable
    fun MainScreen(navController: NavHostController) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "App Development Assignment 08",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Button(
                onClick = { navController.navigate("ex1_screen") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text("Example 1")
            }
            Button(
                onClick = { navController.navigate("ex2_screen") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text("Example 2")
            }
        }
    }
}
