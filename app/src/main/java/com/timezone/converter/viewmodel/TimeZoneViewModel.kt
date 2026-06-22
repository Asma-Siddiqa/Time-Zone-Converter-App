package com.timezone.converter.viewmodel

import android.app.Application
import android.util.Log
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.timezone.converter.model.TimeZoneItem
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.time.ZoneId
import java.time.ZonedDateTime

class TimeZoneViewModel(application: Application) : AndroidViewModel(application) {

    // Use mutableStateMapOf for safer state updates
    private val _currentTimes = mutableStateMapOf<String, String>()
    val currentTimes: Map<String, String> = _currentTimes

    private val _currentDates = mutableStateMapOf<String, String>()
    val currentDates: Map<String, String> = _currentDates

    var selectedZones by mutableStateOf(
        listOf(
            TimeZoneItem.fromZoneId(ZoneId.systemDefault()),
            TimeZoneItem.fromZoneId(ZoneId.of("UTC")),
            TimeZoneItem.fromZoneId(ZoneId.of("America/New_York"))
        ).distinctBy { it.id }
    )
        private set

    var searchQuery by mutableStateOf("")
        private set

    var searchResults by mutableStateOf<List<TimeZoneItem>>(emptyList())
        private set

    var convertFromZone by mutableStateOf(ZoneId.systemDefault().id)
        private set

    var convertToZone by mutableStateOf("UTC")
        private set

    var convertHour by mutableStateOf(ZonedDateTime.now().hour)
        private set

    var convertMinute by mutableStateOf(ZonedDateTime.now().minute)
        private set

    var conversionResult by mutableStateOf("")
        private set

    var convertYear by mutableStateOf(ZonedDateTime.now().year)
        private set

    var convertMonth by mutableStateOf(ZonedDateTime.now().monthValue)
        private set

    var convertDay by mutableStateOf(ZonedDateTime.now().dayOfMonth)
        private set

    init {
        Log.d("TimeZoneVM", "ViewModel initialized")
        
        // Initialize with current times
        selectedZones.forEach { zone ->
            try {
                _currentTimes[zone.id] = TimeZoneItem.getCurrentTime(zone.id)
                _currentDates[zone.id] = TimeZoneItem.getCurrentDate(zone.id)
            } catch (e: Exception) {
                Log.e("TimeZoneVM", "Error initializing ${zone.id}", e)
                _currentTimes[zone.id] = "--"
                _currentDates[zone.id] = "--"
            }
        }

        // Start timer using a safer approach
        viewModelScope.launch {
            while (isActive) {
                try {
                    delay(30_000)
                    // Update times only if viewModel is still active
                    if (isActive) {
                        updateAllTimes()
                    }
                } catch (e: Exception) {
                    Log.e("TimeZoneVM", "Timer error", e)
                }
            }
        }
    }

    private fun updateAllTimes() {
        Log.d("TimeZoneVM", "Updating times for ${selectedZones.size} zones")
        selectedZones.forEach { zone ->
            try {
                _currentTimes[zone.id] = TimeZoneItem.getCurrentTime(zone.id)
                _currentDates[zone.id] = TimeZoneItem.getCurrentDate(zone.id)
            } catch (e: Exception) {
                Log.e("TimeZoneVM", "Error updating ${zone.id}", e)
                // Keep previous values if update fails
            }
        }
    }

    fun addTimeZone(zoneId: String) {
        if (selectedZones.none { it.id == zoneId }) {
            try {
                val newItem = TimeZoneItem.fromZoneId(ZoneId.of(zoneId))
                selectedZones = selectedZones + newItem
                _currentTimes[zoneId] = TimeZoneItem.getCurrentTime(zoneId)
                _currentDates[zoneId] = TimeZoneItem.getCurrentDate(zoneId)
                Log.d("TimeZoneVM", "Added time zone: $zoneId")
            } catch (e: Exception) {
                Log.e("TimeZoneVM", "Error adding time zone $zoneId", e)
            }
        }
    }

    fun removeTimeZone(zoneId: String) {
        selectedZones = selectedZones.filter { it.id != zoneId }
        _currentTimes.remove(zoneId)
        _currentDates.remove(zoneId)
        Log.d("TimeZoneVM", "Removed time zone: $zoneId")
    }

    fun updateSearchQuery(query: String) {
        searchQuery = query
        searchResults = if (query.isBlank()) {
            emptyList()
        } else {
            try {
                ZoneId.getAvailableZoneIds()
                    .filter { it.contains(query, ignoreCase = true) }
                    .sorted()
                    .take(50)
                    .map { TimeZoneItem.fromZoneId(ZoneId.of(it)) }
            } catch (e: Exception) {
                Log.e("TimeZoneVM", "Search error", e)
                emptyList()
            }
        }
    }

    fun clearSearch() {
        searchQuery = ""
        searchResults = emptyList()
    }

    fun updateConvertFromZone(zoneId: String) {
        convertFromZone = zoneId
        // Clear previous conversion result
        conversionResult = ""
    }

    fun updateConvertToZone(zoneId: String) {
        convertToZone = zoneId
        conversionResult = ""
    }

    fun setConvertTime(hour: Int, minute: Int) {
        convertHour = hour.coerceIn(0, 23)
        convertMinute = minute.coerceIn(0, 59)
        conversionResult = ""
    }

    fun setConvertDate(year: Int, month: Int, day: Int) {
        convertYear = year.coerceAtLeast(1900)
        convertMonth = month.coerceIn(1, 12)
        convertDay = day.coerceIn(1, 31)
        conversionResult = ""
    }

    fun performConversion() {
        conversionResult = try {
            val result = TimeZoneItem.convertTime(
                fromZone = convertFromZone,
                toZone = convertToZone,
                year = convertYear,
                month = convertMonth,
                day = convertDay,
                hour = convertHour,
                minute = convertMinute
            )
            Log.d("TimeZoneVM", "Conversion successful: $result")
            result
        } catch (e: Exception) {
            Log.e("TimeZoneVM", "Conversion error", e)
            "Conversion error: ${e.message}"
        }
    }

    fun getAllTimeZones(): List<TimeZoneItem> {
        return try {
            ZoneId.getAvailableZoneIds()
                .sorted()
                .map { TimeZoneItem.fromZoneId(ZoneId.of(it)) }
        } catch (e: Exception) {
            Log.e("TimeZoneVM", "Error getting all time zones", e)
            emptyList()
        }
    }

    fun getZoneTimeNow(zoneId: String): String {
        return try {
            TimeZoneItem.getFullTime(zoneId)
        } catch (e: Exception) {
            Log.e("TimeZoneVM", "Error getting zone time for $zoneId", e)
            "--"
        }
    }

    // Clean up resources when ViewModel is cleared
    override fun onCleared() {
        super.onCleared()
        Log.d("TimeZoneVM", "ViewModel cleared")
        _currentTimes.clear()
        _currentDates.clear()
    }
}