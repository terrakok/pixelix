package com.daniebeler.pfpixelix.utils

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.jetbrains.compose.resources.getPluralString
import org.jetbrains.compose.resources.getString
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.ago
import pixelix.app.generated.resources.second
import pixelix.app.generated.resources.minute
import pixelix.app.generated.resources.hour
import pixelix.app.generated.resources.day
import pixelix.app.generated.resources.week
import pixelix.app.generated.resources.month
import pixelix.app.generated.resources.year

object TimeAgo {

    suspend fun convertTimeToText(dataDate: String): String {
        var convTime: String = ""
        val suffix = getString(Res.string.ago)
        try {
            val pasTime: Instant = Instant.parse(dataDate)
            val nowTime: Instant = Clock.System.now()
            val dateDiff = nowTime - pasTime

            val second: Long = dateDiff.inWholeSeconds
            val minute: Long = dateDiff.inWholeMinutes
            val hour: Long = dateDiff.inWholeHours
            val day: Long = dateDiff.inWholeDays
            if (second < 60) {
                convTime =
                    "$second ${getPluralString(Res.plurals.second, second.toInt(), 1)} $suffix"
            } else if (minute < 60) {
                convTime =
                    "$minute ${getPluralString(Res.plurals.minute, minute.toInt(), 1)} $suffix"
            } else if (hour < 24) {
                convTime = "$hour ${getPluralString(Res.plurals.hour, hour.toInt(), 1)} $suffix"
            } else if (day >= 7) {
                if (day > 360) {
                    convTime = "${(day / 360)} ${
                        getPluralString(
                            Res.plurals.year,
                            (day / 360).toInt()
                        )
                    } $suffix"
                } else if (day > 30) {
                    convTime = "${(day / 30)} ${
                        getPluralString(
                            Res.plurals.month,
                            (day / 30).toInt()
                        )
                    } $suffix"
                } else {
                    convTime = "${(day / 7)} ${
                        getPluralString(
                            Res.plurals.week,
                            (day / 7).toInt()
                        )
                    } $suffix"
                }
            } else if (day < 7) {
                convTime = "$day ${getPluralString(Res.plurals.day, day.toInt())} $suffix"
            }
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        }
        return convTime
    }
}