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

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import com.universe.android.weather.R
import com.universe.android.weather.daily.adapter.DailyWeatherAdapter
import com.universe.android.weather.daily.adapter.model.LargeTitle

class LargeTitleHolder(parent: ViewGroup) : DailyWeatherAdapter.ViewHolder(
    LayoutInflater.from(parent.context)
        .inflate(R.layout.item_weather_daily_title_large, parent, false)
) {
    override fun onBindView(model: DailyWeatherAdapter.ViewModel, position: Int) {
        val title = model as LargeTitle
        (itemView as TextView).text = title.title
    }
}