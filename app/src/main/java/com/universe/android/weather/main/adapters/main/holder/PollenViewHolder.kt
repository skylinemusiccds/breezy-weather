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

package com.universe.android.weather.main.adapters.main.holder

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.viewpager2.widget.ViewPager2
import breezyweather.domain.location.model.Location
import com.universe.android.weather.R
import com.universe.android.weather.common.basic.GeoActivity
import com.universe.android.weather.common.source.PollenIndexSource
import com.universe.android.weather.common.utils.helpers.IntentHelper
import com.universe.android.weather.domain.weather.index.PollenIndex
import com.universe.android.weather.domain.weather.model.isIndexValid
import com.universe.android.weather.domain.weather.model.isToday
import com.universe.android.weather.domain.weather.model.pollensWithConcentration
import com.universe.android.weather.main.MainActivity
import com.universe.android.weather.main.adapters.HomePollenAdapter
import com.universe.android.weather.main.adapters.HomePollenViewHolder
import com.universe.android.weather.main.utils.MainThemeColorProvider
import com.universe.android.weather.theme.ThemeManager
import com.universe.android.weather.theme.resource.providers.ResourceProvider
import com.universe.android.weather.theme.weatherView.WeatherViewController

class PollenViewHolder(parent: ViewGroup) : AbstractMainCardViewHolder(
    LayoutInflater
        .from(parent.context)
        .inflate(R.layout.container_main_pollen, parent, false)
) {
    private val mTitle: TextView = itemView.findViewById(R.id.container_main_pollen_title)
    private val mSubtitle: TextView = itemView.findViewById(R.id.container_main_pollen_subtitle)
    private val mIndicator: TextView = itemView.findViewById(R.id.container_main_pollen_indicator)
    private val mPager: ViewPager2 = itemView.findViewById(R.id.container_main_pollen_pager)
    private var mCallback: DailyPollenPageChangeCallback? = null

    private class DailyPollenPagerAdapter(
        location: Location,
        pollenIndexSource: PollenIndexSource?,
        specificPollens: Set<PollenIndex>
    ) : HomePollenAdapter(location, pollenIndexSource, specificPollens) {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomePollenViewHolder {
            val holder = super.onCreateViewHolder(parent, viewType)
            holder.itemView.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            return holder
        }
    }

    private inner class DailyPollenPageChangeCallback(
        private val mContext: Context,
        private val mLocation: Location
    ) : ViewPager2.OnPageChangeCallback() {
        @SuppressLint("SetTextI18n")
        override fun onPageSelected(position: Int) {
            val daily = mLocation.weather!!.dailyForecastStartingToday[position]
            if (daily.isToday(mLocation)) {
                mIndicator.text = mContext.getString(R.string.short_today)
            } else {
                mIndicator.text = (position + 1).toString() + "/" + mLocation.weather!!.dailyForecastStartingToday.filter { it.pollen?.isIndexValid == true }.size
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onBindView(
        activity: GeoActivity, location: Location,
        provider: ResourceProvider,
        listAnimationEnabled: Boolean, itemAnimationEnabled: Boolean, firstCard: Boolean
    ) {
        super.onBindView(
            activity, location, provider,
            listAnimationEnabled, itemAnimationEnabled, firstCard
        )
        if (location.weather?.dailyForecast?.any {
                it.pollen?.isMoldValid == true
            } == true) {
            mTitle.text = context.getString(R.string.pollen_and_mold)
        } else {
            mTitle.text = context.getString(R.string.pollen)
        }
        mTitle.setTextColor(
            ThemeManager.getInstance(context)
                .weatherThemeDelegate
                .getThemeColors(
                    context,
                    WeatherViewController.getWeatherKind(location),
                    WeatherViewController.isDaylight(location)
                )[0]
        )
        mSubtitle.setTextColor(MainThemeColorProvider.getColor(location, R.attr.colorCaptionText))
        mPager.adapter = DailyPollenPagerAdapter(
            location,
            (activity as MainActivity).sourceManager.getPollenIndexSource(
                if (!location.pollenSource.isNullOrEmpty()) {
                    location.pollenSource!!
                } else location.weatherSource
            ),
            location.weather?.dailyForecast?.map { daily ->
                daily.pollen?.pollensWithConcentration ?: setOf()
            }?.flatten()?.toSet() ?: setOf()
        )
        mPager.currentItem = 0
        mCallback = DailyPollenPageChangeCallback(activity, location)
        mPager.registerOnPageChangeCallback(mCallback!!)
        itemView.contentDescription = mTitle.text
        itemView.setOnClickListener { IntentHelper.startPollenActivity(context as GeoActivity, location) }
    }

    override fun onRecycleView() {
        super.onRecycleView()
        if (mCallback != null) {
            mPager.unregisterOnPageChangeCallback(mCallback!!)
            mCallback = null
        }
    }
}
