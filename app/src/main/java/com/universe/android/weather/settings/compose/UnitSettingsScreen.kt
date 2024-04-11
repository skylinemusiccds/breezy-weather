/**
 * This file is part of Breezy Weather.
 *
 * Breezy Weather is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation, version 3 of the License.
 *
 * Breezy Weather is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Breezy Weather. If not, see <https://www.gnu.org/licenses/>.
 */

package com.universe.android.weather.settings.compose

import android.content.Context
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import com.universe.android.weather.R
import com.universe.android.weather.common.basic.models.options.unit.DistanceUnit
import com.universe.android.weather.common.basic.models.options.unit.PrecipitationUnit
import com.universe.android.weather.common.basic.models.options.unit.PressureUnit
import com.universe.android.weather.common.basic.models.options.unit.SpeedUnit
import com.universe.android.weather.common.basic.models.options.unit.TemperatureUnit
import com.universe.android.weather.settings.SettingsManager
import com.universe.android.weather.settings.preference.composables.ListPreferenceView
import com.universe.android.weather.settings.preference.composables.PreferenceScreen
import com.universe.android.weather.settings.preference.listPreferenceItem

@Composable
fun UnitSettingsScreen(
    context: Context,
    paddingValues: PaddingValues,
) = PreferenceScreen(paddingValues = paddingValues) {
    listPreferenceItem(R.string.settings_units_temperature) { id ->
        ListPreferenceView(
            titleId = id,
            selectedKey = SettingsManager.getInstance(context).temperatureUnit.id,
            valueArrayId = R.array.temperature_unit_values,
            nameArrayId = R.array.temperature_units,
            onValueChanged = {
                SettingsManager
                    .getInstance(context)
                    .temperatureUnit = TemperatureUnit.getInstance(it)
            },
        )
    }
    listPreferenceItem(R.string.settings_units_precipitation) { id ->
        ListPreferenceView(
            titleId = id,
            selectedKey = SettingsManager.getInstance(context).precipitationUnit.id,
            valueArrayId = R.array.precipitation_unit_values,
            nameArrayId = R.array.precipitation_units,
            onValueChanged = {
                SettingsManager
                    .getInstance(context)
                    .precipitationUnit = PrecipitationUnit.getInstance(it)
            },
        )
    }
    listPreferenceItem(R.string.settings_units_distance) { id ->
        ListPreferenceView(
            titleId = id,
            selectedKey = SettingsManager.getInstance(context).distanceUnit.id,
            valueArrayId = R.array.distance_unit_values,
            nameArrayId = R.array.distance_units,
            onValueChanged = {
                SettingsManager
                    .getInstance(context)
                    .distanceUnit = DistanceUnit.getInstance(it)
            },
        )
    }
    listPreferenceItem(R.string.settings_units_speed) { id ->
        ListPreferenceView(
            titleId = id,
            selectedKey = SettingsManager.getInstance(context).speedUnit.id,
            valueArrayId = R.array.speed_unit_values,
            nameArrayId = R.array.speed_units,
            onValueChanged = {
                SettingsManager
                    .getInstance(context)
                    .speedUnit = SpeedUnit.getInstance(it)
            },
        )
    }
    listPreferenceItem(R.string.settings_units_pressure) { id ->
        ListPreferenceView(
            titleId = id,
            selectedKey = SettingsManager.getInstance(context).pressureUnit.id,
            valueArrayId = R.array.pressure_unit_values,
            nameArrayId = R.array.pressure_units,
            onValueChanged = {
                SettingsManager
                    .getInstance(context)
                    .pressureUnit = PressureUnit.getInstance(it)
            },
        )
    }
}
