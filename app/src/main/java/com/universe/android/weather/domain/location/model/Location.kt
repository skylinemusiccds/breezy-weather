package com.universe.android.weather.domain.location.model

import android.content.Context
import breezyweather.domain.location.model.Location
import com.universe.android.weather.R
import com.universe.android.weather.domain.weather.model.getRiseProgress

fun Location.getPlace(context: Context, showCurrentPositionInPriority: Boolean = false): String {
    if (showCurrentPositionInPriority && isCurrentPosition) {
        return context.getString(R.string.location_current)
    }
    val builder = StringBuilder()
    builder.append(cityAndDistrict)
    if (builder.toString().isEmpty() && isCurrentPosition) {
        return context.getString(R.string.location_current)
    }
    return builder.toString()
}

val Location.isDaylight: Boolean
    get() {
        val sunRiseProgress = getRiseProgress(
            astro = this.weather?.today?.sun,
            location = this
        )
        return 0 < sunRiseProgress && sunRiseProgress < 1
    }
