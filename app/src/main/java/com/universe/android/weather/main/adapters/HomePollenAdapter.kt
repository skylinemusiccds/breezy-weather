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

package com.universe.android.weather.main.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import breezyweather.domain.location.model.Location
import breezyweather.domain.weather.model.Daily
import com.universe.android.weather.R
import com.universe.android.weather.common.extensions.getFormattedDate
import com.universe.android.weather.common.extensions.getLongWeekdayDayMonth
import com.universe.android.weather.common.source.PollenIndexSource
import com.universe.android.weather.common.ui.composables.PollenGrid
import com.universe.android.weather.databinding.ItemPollenDailyBinding
import com.universe.android.weather.domain.weather.index.PollenIndex
import com.universe.android.weather.domain.weather.model.isIndexValid
import com.universe.android.weather.main.utils.MainThemeColorProvider
import com.universe.android.weather.theme.compose.BreezyWeatherTheme

open class HomePollenAdapter(
    private val location: Location,
    private val pollenIndexSource: PollenIndexSource?,
    private val specificPollens: Set<PollenIndex>
) : RecyclerView.Adapter<HomePollenViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomePollenViewHolder {
        return HomePollenViewHolder(
            ItemPollenDailyBinding.inflate(
                LayoutInflater.from(parent.context)
            )
        )
    }

    override fun onBindViewHolder(holder: HomePollenViewHolder, position: Int) {
        holder.onBindView(
            location,
            location.weather!!.dailyForecastStartingToday[position],
            pollenIndexSource,
            specificPollens
        )
    }

    override fun getItemCount() = location.weather?.dailyForecastStartingToday?.filter {
        it.pollen?.isIndexValid == true
    }?.size ?: 0
}

class HomePollenViewHolder internal constructor(
    private val binding: ItemPollenDailyBinding
) : RecyclerView.ViewHolder(
    binding.root
) {
    @SuppressLint("SetTextI18n", "RestrictedApi")
    fun onBindView(
        location: Location,
        daily: Daily,
        pollenIndexSource: PollenIndexSource?,
        specificPollens: Set<PollenIndex>
    ) {
        val context = itemView.context

        binding.title.text = daily.date.getFormattedDate(
            getLongWeekdayDayMonth(context), location, context
        )
        binding.title.setTextColor(MainThemeColorProvider.getColor(location, R.attr.colorTitleText))

        daily.pollen?.let {
            binding.composeView.setContent {
                BreezyWeatherTheme(lightTheme = MainThemeColorProvider.isLightTheme(context, location)) {
                    PollenGrid(
                        pollen = it,
                        pollenIndexSource = pollenIndexSource,
                        specificPollens = specificPollens
                    )
                }
            }
        }

        itemView.setOnClickListener { }
    }
}
