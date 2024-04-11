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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import breezyweather.domain.location.model.Location
import com.universe.android.weather.R
import com.universe.android.weather.common.basic.GeoActivity
import com.universe.android.weather.common.extensions.DEFAULT_CARD_LIST_ITEM_ELEVATION_DP
import com.universe.android.weather.common.extensions.isLandscape
import com.universe.android.weather.common.ui.adapters.TagAdapter
import com.universe.android.weather.common.ui.decorations.GridMarginsDecoration
import com.universe.android.weather.common.ui.widgets.trend.TrendRecyclerView
import com.universe.android.weather.common.utils.ColorUtils
import com.universe.android.weather.main.adapters.trend.DailyTrendAdapter
import com.universe.android.weather.main.layouts.TrendHorizontalLinearLayoutManager
import com.universe.android.weather.main.utils.MainThemeColorProvider
import com.universe.android.weather.main.widgets.TrendRecyclerViewScrollBar
import com.universe.android.weather.settings.SettingsManager
import com.universe.android.weather.theme.ThemeManager
import com.universe.android.weather.theme.resource.providers.ResourceProvider
import com.universe.android.weather.theme.weatherView.WeatherViewController

class DailyViewHolder(
    parent: ViewGroup
) : AbstractMainCardViewHolder(
    LayoutInflater
        .from(parent.context)
        .inflate(R.layout.container_main_daily_trend_card, parent, false)
) {
    private val title: TextView = itemView.findViewById(R.id.container_main_daily_trend_card_title)
    private val subtitle: TextView = itemView.findViewById(R.id.container_main_daily_trend_card_subtitle)
    private val tagView: RecyclerView = itemView.findViewById(R.id.container_main_daily_trend_card_tagView)
    private val trendRecyclerView: TrendRecyclerView = itemView.findViewById(R.id.container_main_daily_trend_card_trendRecyclerView)
    private val scrollBar = TrendRecyclerViewScrollBar()

    init {
        trendRecyclerView.setHasFixedSize(true)
        trendRecyclerView.addItemDecoration(scrollBar)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindView(
        activity: GeoActivity,
        location: Location,
        provider: ResourceProvider,
        listAnimationEnabled: Boolean,
        itemAnimationEnabled: Boolean,
        firstCard: Boolean
    ) {
        super.onBindView(
            activity,
            location,
            provider,
            listAnimationEnabled,
            itemAnimationEnabled,
            firstCard
        )
        location.weather?.let { weather ->

            val colors = ThemeManager
                .getInstance(context)
                .weatherThemeDelegate
                .getThemeColors(
                    context,
                    WeatherViewController.getWeatherKind(location),
                    WeatherViewController.isDaylight(location)
                )

            title.setTextColor(colors[0])

            if (weather.current?.dailyForecast.isNullOrEmpty()) {
                subtitle.visibility = View.GONE
            } else {
                subtitle.visibility = View.VISIBLE
                subtitle.text = weather.current?.dailyForecast
            }

            val trendAdapter = DailyTrendAdapter(activity, trendRecyclerView).apply {
                bindData(location)
            }
            val tagList: MutableList<TagAdapter.Tag> = trendAdapter.adapters.map {
                object : TagAdapter.Tag {
                    override val name = it.getDisplayName(activity)
                }
            }.toMutableList()

            if (tagList.size < 2) {
                tagView.visibility = View.GONE
            } else {
                tagView.visibility = View.VISIBLE
                val decorCount = tagView.itemDecorationCount
                for (i in 0 until decorCount) {
                    tagView.removeItemDecorationAt(0)
                }
                tagView.addItemDecoration(
                    GridMarginsDecoration(
                        context.resources.getDimension(R.dimen.little_margin),
                        context.resources.getDimension(R.dimen.normal_margin),
                        tagView
                    )
                )
                tagView.layoutManager =
                    TrendHorizontalLinearLayoutManager(context)
                tagView.adapter = TagAdapter(
                    tagList,
                    MainThemeColorProvider.getColor(location, com.google.android.material.R.attr.colorOnPrimary),
                    MainThemeColorProvider.getColor(location, com.google.android.material.R.attr.colorOnSurface),
                    MainThemeColorProvider.getColor(location, androidx.appcompat.R.attr.colorPrimary),
                    ColorUtils.getWidgetSurfaceColor(
                        DEFAULT_CARD_LIST_ITEM_ELEVATION_DP,
                        MainThemeColorProvider.getColor(location, androidx.appcompat.R.attr.colorPrimary),
                        MainThemeColorProvider.getColor(location, com.google.android.material.R.attr.colorSurface)
                    ),
                    { _, _, newPosition ->
                        trendAdapter.selectedIndex = newPosition
                        return@TagAdapter false
                    },
                    0
                )
            }
            trendRecyclerView.layoutManager =
                TrendHorizontalLinearLayoutManager(
                    context,
                    if (context.isLandscape) 7 else 5
                )
            trendRecyclerView.setLineColor(MainThemeColorProvider.getColor(location, com.google.android.material.R.attr.colorOutline))
            trendRecyclerView.adapter = trendAdapter
            trendRecyclerView.setKeyLineVisibility(
                SettingsManager.getInstance(context).isTrendHorizontalLinesEnabled
            )
            if (weather.todayIndex >= 0) {
                trendRecyclerView.scrollToPosition(weather.todayIndex)
            }
            scrollBar.resetColor(location)
        }
    }
}
