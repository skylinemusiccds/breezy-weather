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
import androidx.core.graphics.ColorUtils
import com.universe.android.weather.R
import com.universe.android.weather.common.ui.widgets.RoundProgress
import com.universe.android.weather.daily.adapter.DailyWeatherAdapter
import com.universe.android.weather.daily.adapter.model.DailyAirQuality
import com.universe.android.weather.domain.weather.model.getColor
import com.universe.android.weather.domain.weather.model.getIndex
import com.universe.android.weather.domain.weather.model.getName

class AirQualityHolder(parent: ViewGroup) : DailyWeatherAdapter.ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.item_weather_daily_air, parent, false)
) {
    private val mProgress: RoundProgress = itemView.findViewById(R.id.item_weather_daily_air_progress)
    private val mContent: TextView = itemView.findViewById(R.id.item_weather_daily_air_content)

    @SuppressLint("SetTextI18n")
    override fun onBindView(model: DailyWeatherAdapter.ViewModel, position: Int) {
        val airQuality = (model as DailyAirQuality).airQuality
        val aqi = airQuality.getIndex()
        val color = airQuality.getColor(itemView.context)
        mProgress.apply {
            max = 400f
            if (aqi != null) {
                progress = aqi.toFloat()
            }
            setProgressColor(color)
            setProgressBackgroundColor(ColorUtils.setAlphaComponent(color, (255 * 0.1).toInt()))
        }
        mContent.text = aqi.toString() + " / " + airQuality.getName(itemView.context)
    }
}
