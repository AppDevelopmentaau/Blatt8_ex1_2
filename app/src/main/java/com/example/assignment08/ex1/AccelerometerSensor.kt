package com.example.assignment08.ex1

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.sqrt


//provides access to data for x and y tilt, as a Flow
class AccelerometerSensor(
    private val context: Context
) : SensorEventListener {

    private val sensorManager: SensorManager = 
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    
    private val accelerometer: Sensor? = 
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    
    private val _sensorDataChannel = Channel<TiltData>(Channel.CONFLATED)
    val sensorData: Flow<TiltData> = _sensorDataChannel.receiveAsFlow()

    init {
        // Check if accelerometer is available
        if (accelerometer == null) {
            _sensorDataChannel.trySend(
                TiltData(
                    xTilt = 0f,
                    yTilt = 0f,
                    isLevel = false,
                    isAccelerometerAvailable = false
                )
            )
        }
    }


    fun startListening() {
        if (accelerometer != null) {
            sensorManager.registerListener(
                this,
                accelerometer,
                SensorManager.SENSOR_DELAY_UI
            )
        }
    }


    fun stopListening() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]

            // Calculate the tilt angles using the accelerometer data
            // Math.atan2 returns the angle in radians, convert to degrees
            val xTilt = Math.toDegrees(
                atan2(
                    x.toDouble(),
                    sqrt((y * y + z * z).toDouble())
                )
            ).toFloat()
            
            val yTilt = Math.toDegrees(
                atan2(
                    y.toDouble(),
                    sqrt((x * x + z * z).toDouble())
                )
            ).toFloat()

            // Check if the device is level (within 1 degree tolerance)
            val isLevel = abs(xTilt) < 1.0 && abs(yTilt) < 1.0

            _sensorDataChannel.trySend(
                TiltData(
                    xTilt = xTilt,
                    yTilt = yTilt,
                    isLevel = isLevel,
                    isAccelerometerAvailable = true
                )
            )
        }
    }


    //notwendig für Vererbung
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // braucht man aber nicht
    }
}


data class TiltData(
    val xTilt: Float,
    val yTilt: Float,
    val isLevel: Boolean,
    val isAccelerometerAvailable: Boolean
)
