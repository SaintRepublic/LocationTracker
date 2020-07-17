package com.saintrepublic.locationtracker.vm

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import com.saintrepublic.locationtracker.common.TrackerData
import com.saintrepublic.locationtracker.common.Event
import com.saintrepublic.locationtracker.database.DatabaseService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivityViewModel(
    context: Context,
    val databaseService: DatabaseService
) : BaseViewModel() {

    private val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    private val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

    private var lastAccelerometerData: FloatArray? = null
    private var lastGyroscopeData: FloatArray? = null

    private val locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location?) {
            val data = TrackerData.fromData(
                location,
                lastAccelerometerData,
                lastGyroscopeData
            )
            saveData(data)
            trackerInfoLiveData.postValue(data)
            isProcessing.postValue(false)
        }

        override fun onProviderDisabled(provider: String) {
            if (provider == LocationManager.NETWORK_PROVIDER && isProcessing.value == true) {
                isProcessing.postValue(false)
                providerDisabledEvent.postValue(
                    Event(Unit)
                )
            }
        }

        override fun onProviderEnabled(provider: String) {}

        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
    }

    private val sensorListener = object : SensorEventListener {

        var isRegistered = false

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

        override fun onSensorChanged(event: SensorEvent?) {
            event?.let {
                when (it.sensor.type) {
                    Sensor.TYPE_ACCELEROMETER -> lastAccelerometerData = it.values
                    Sensor.TYPE_GYROSCOPE -> lastGyroscopeData = it.values
                }
            }
        }
    }

    val trackerInfoLiveData = MutableLiveData(TrackerData.emptyData())

    val isProcessing = MutableLiveData(false)

    val providerDisabledEvent = MutableLiveData<Event<Unit>>()

    fun isProviderEnabled() = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

    @SuppressLint("MissingPermission")
    fun startTracking() {
        launch {
            isProcessing.postValue(true)
            locationManager.requestSingleUpdate(
                LocationManager.NETWORK_PROVIDER,
                locationListener,
                null
            )
        }
    }

    fun enableSensorsListener(enable: Boolean) {
        if (!enable) {
            sensorManager.unregisterListener(sensorListener)
        } else if (!sensorListener.isRegistered) {
            sensorManager.registerListener(
                sensorListener,
                accelerometer,
                SensorManager.SENSOR_DELAY_NORMAL
            )
            sensorManager.registerListener(
                sensorListener,
                gyroscope,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }
        sensorListener.isRegistered = enable
    }

    private fun saveData(data: TrackerData) {
        launch(Dispatchers.IO) {
            databaseService.save(data)
        }
    }

}