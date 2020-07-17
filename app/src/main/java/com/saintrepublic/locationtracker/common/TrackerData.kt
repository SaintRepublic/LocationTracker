package com.saintrepublic.locationtracker.common

import android.location.Location
import java.text.SimpleDateFormat
import java.util.*

data class TrackerData(
    var id: Long,
    val time: String,
    val latitude: String,
    val longitude: String,
    val speed: String,
    val accX: String,
    val accY: String,
    val accZ: String,
    val angX: String,
    val angY: String,
    val angZ: String
) {
    companion object {
        private val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault())

        fun emptyData(): TrackerData = fromData(null, null, null)

        fun fromData(
            location: Location?,
            accelerometerData: FloatArray?,
            gyroscopeData: FloatArray?
        ): TrackerData {
            return TrackerData(
                -1,
                location?.time?.let { dateFormat.format(Date(it)) } ?: "N/A",
                location?.latitude?.let { String.format("%.6f", it) } ?: "N/A",
                location?.longitude?.let { String.format("%.6f", it) } ?: "N/A",
                location?.speed?.let { "$it м/с" } ?: "N/A",
                accelerometerData?.getOrNull(0)?.let { String.format("%.6f м/с^2", it) } ?: "N/A",
                accelerometerData?.getOrNull(1)?.let { String.format("%.6f м/с^2", it) } ?: "N/A",
                accelerometerData?.getOrNull(2)?.let { String.format("%.6f м/с^2", it) } ?: "N/A",
                gyroscopeData?.getOrNull(0)?.let { String.format("%.6f рад/с", it) } ?: "N/A",
                gyroscopeData?.getOrNull(1)?.let { String.format("%.6f рад/с", it) } ?: "N/A",
                gyroscopeData?.getOrNull(2)?.let { String.format("%.6f рад/с", it) } ?: "N/A"
            )
        }
    }
}