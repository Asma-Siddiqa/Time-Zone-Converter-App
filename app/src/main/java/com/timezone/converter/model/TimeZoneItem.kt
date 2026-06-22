package com.timezone.converter.model

import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

data class TimeZoneItem(
    val id: String = ZoneId.systemDefault().id,
    val label: String = "",
    val offset: String = ""
) {
    companion object {
        private val timeFormatter = DateTimeFormatter.ofPattern("hh:mm a")
        private val dateFormatter = DateTimeFormatter.ofPattern("EEE, MMM d")
        private val fullFormatter = DateTimeFormatter.ofPattern("hh:mm a, EEE MMM d")

        fun fromZoneId(zoneId: ZoneId): TimeZoneItem {
            val now = ZonedDateTime.now(zoneId)
            val offsetStr = now.offset.id
            val label = zoneId.id
                .replace("_", " ")
                .substringAfterLast("/")
                .replace("/", " / ")
            return TimeZoneItem(
                id = zoneId.id,
                label = "$label ($offsetStr)",
                offset = offsetStr
            )
        }

        fun getCurrentTime(zoneId: String): String {
            val now = ZonedDateTime.now(ZoneId.of(zoneId))
            return now.format(timeFormatter)
        }

        fun getCurrentDate(zoneId: String): String {
            val now = ZonedDateTime.now(ZoneId.of(zoneId))
            return now.format(dateFormatter)
        }

        fun getFullTime(zoneId: String): String {
            val now = ZonedDateTime.now(ZoneId.of(zoneId))
            return now.format(fullFormatter)
        }

        fun convertTime(
            fromZone: String,
            toZone: String,
            year: Int,
            month: Int,
            day: Int,
            hour: Int,
            minute: Int
        ): String {
            val fromId = ZoneId.of(fromZone)
            val toId = ZoneId.of(toZone)
            val sourceTime = ZonedDateTime.of(year, month, day, hour, minute, 0, 0, fromId)
            val convertedTime = sourceTime.withZoneSameInstant(toId)
            return convertedTime.format(fullFormatter)
        }
    }
}
