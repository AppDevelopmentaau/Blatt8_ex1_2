package com.example.assignment08.ex1

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
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.os.Build
import com.example.assignment08.R

class SpiritLevelViewModel(
    private val context: Context,
    private val accelerometerSensor: AccelerometerSensor
) : ViewModel() {

    var uiState by mutableStateOf(SpiritLevelUiState())
        private set
    private var isVibratedForLevel = false
    private var isSoundPlayedForLevel = false
    private var lastVibrationTime: Long = 0
    private val vibrationRateLimit = 500 // Minimum time between vibrations (ms)
      // Setup sound for level feedback
    private val soundPool: SoundPool
    private val levelSoundId: Int
    private var lastPlayTime: Long = 0
    private val playRateLimit = 1000 // Minimum time between sounds (ms)
    
    init {
        // Initialize SoundPool
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
              soundPool = SoundPool.Builder()
            .setMaxStreams(2)
            .setAudioAttributes(audioAttributes)
            .build()
          // Load our custom beep sound
        levelSoundId = soundPool.load(context, R.raw.beep_sound, 1)
        
        // Start listening to sensor data
        accelerometerSensor.sensorData
            .onEach { tiltData ->
                // Update UI state with new sensor data
                uiState = uiState.copy(
                    xTilt = tiltData.xTilt,
                    yTilt = tiltData.yTilt,
                    isLevel = tiltData.isLevel,
                    isAccelerometerAvailable = tiltData.isAccelerometerAvailable
                )
                
                // Provide haptic and sound feedback when the device becomes level
                if (tiltData.isLevel) {
                    if (!isVibratedForLevel) {
                        vibrateDevice()
                        isVibratedForLevel = true
                    }
                    if (!isSoundPlayedForLevel) {
                        playLevelSound()
                        isSoundPlayedForLevel = true
                    }
                } else {
                    isVibratedForLevel = false
                    isSoundPlayedForLevel = false
                }
            }
            .catch { e ->
                // Handle any errors
                println("Error collecting sensor data: ${e.message}")
            }
            .launchIn(viewModelScope)
        
        // Start the sensor
        startAccelerometerSensor()
    }
    

    fun startAccelerometerSensor() {
        accelerometerSensor.startListening()
    }

    fun stopAccelerometerSensor() {
        accelerometerSensor.stopListening()
    }

    fun toggleMode() {
        uiState = uiState.copy(
            isHorizontalMode = !uiState.isHorizontalMode
        )
    }
    

    fun toggleSound() {
        uiState = uiState.copy(
            isSoundEnabled = !uiState.isSoundEnabled
        )
    }

    private fun playLevelSound() {
        // Only play sound if it's enabled
        if (!uiState.isSoundEnabled) return
        
        // Rate limit to avoid sound spam
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastPlayTime < playRateLimit) return
        
        try {
            soundPool.play(levelSoundId, 1f, 1f, 1, 0, 1f)
            lastPlayTime = currentTime
        } catch (e: Exception) {
            // Handle errors gracefully
            println("Sound playback failed: ${e.message}")
        }
    }

    private fun vibrateDevice() {
        // Rate limit to avoid excessive vibration
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastVibrationTime < vibrationRateLimit) return
        
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                val vibrator = vibratorManager.defaultVibrator
                vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator.vibrate(100)
                }
            }
            lastVibrationTime = currentTime
        } catch (e: Exception) {
            // Handle errors gracefully, maybe the device doesn't have a vibrator
            println("Vibration failed: ${e.message}")
        }
    }

    override fun onCleared() {
        super.onCleared()
        stopAccelerometerSensor()
        // Release the SoundPool resources
        soundPool.release()
    }
    

    class Factory(
        private val context: Context
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SpiritLevelViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return SpiritLevelViewModel(
                    context = context,
                    accelerometerSensor = AccelerometerSensor(context)
                ) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

data class SpiritLevelUiState(
    val xTilt: Float = 0f,
    val yTilt: Float = 0f,
    val isLevel: Boolean = false,
    val isAccelerometerAvailable: Boolean = true,
    val isHorizontalMode: Boolean = true,
    val isSoundEnabled: Boolean = true
)
