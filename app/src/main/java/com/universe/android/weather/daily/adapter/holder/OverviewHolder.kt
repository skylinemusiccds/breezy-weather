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

package com.universe.android.weather.daily.adapter.holder

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import com.universe.android.weather.R
import com.universe.android.weather.common.basic.models.options.unit.TemperatureUnit
import com.universe.android.weather.common.ui.widgets.AnimatableIconView
import com.universe.android.weather.daily.adapter.DailyWeatherAdapter
import com.universe.android.weather.daily.adapter.model.Overview
import com.universe.android.weather.settings.SettingsManager
import com.universe.android.weather.theme.resource.ResourcesProviderFactory
import com.universe.android.weather.theme.resource.providers.ResourceProvider

class OverviewHolder(parent: ViewGroup) : DailyWeatherAdapter.ViewHolder(
    LayoutInflater.from(parent.context)
        .inflate(R.layout.item_weather_daily_overview, parent, false)
) {
    private val mIcon: AnimatableIconView = itemView.findViewById(R.id.item_weather_daily_overview_icon)
    private val mTitle: TextView = itemView.findViewById(R.id.item_weather_daily_overview_text)
    private val mProvider: ResourceProvider = ResourcesProviderFactory.newInstance
    private val mTemperatureUnit: TemperatureUnit = SettingsManager.getInstance(parent.context).temperatureUnit

    init {
        itemView.setOnClickListener { mIcon.startAnimators() }
    }

    @SuppressLint("SetTextI18n")
    override fun onBindView(model: DailyWeatherAdapter.ViewModel, position: Int) {
        val overview = model as Overview
        if (overview.halfDay.weatherCode != null) {
            mIcon.setAnimatableIcon(
                mProvider.getWeatherIcons(overview.halfDay.weatherCode, overview.isDaytime),
                mProvider.getWeatherAnimators(overview.halfDay.weatherCode, overview.isDaytime)
            )
        }
        val builder = StringBuilder()
        if (!overview.halfDay.weatherText .isNullOrEmpty()) {
            builder.append(overview.halfDay.weatherText)
        }
        overview.halfDay.temperature?.temperature?.let {
            if (builder.toString().isNotEmpty()) {
                builder.append(mTitle.context.getString(R.string.comma_separator))
            }
            builder.append(
                mTemperatureUnit.getValueText(mTitle.context, it)
            )
        }
        if (builder.toString().isNotEmpty()) {
            mTitle.text = builder.toString()
        }
    }
}
