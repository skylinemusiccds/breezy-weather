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

package com.universe.android.weather.main.adapters.location

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import breezyweather.domain.location.model.Location
import com.universe.android.weather.common.basic.models.options.unit.TemperatureUnit
import com.universe.android.weather.common.ui.adapters.SyncListAdapter
import com.universe.android.weather.databinding.ItemLocationCardBinding
import com.universe.android.weather.settings.SettingsManager
import com.universe.android.weather.sources.SourceManager
import com.universe.android.weather.theme.resource.ResourcesProviderFactory
import com.universe.android.weather.theme.resource.providers.ResourceProvider

/**
 * Location adapter.
 */
class LocationAdapter(
    private val mContext: Context,
    locationList: List<Location>,
    selectedId: String?,
    private val sourceManager: SourceManager,
    private val mClickListener: (String) -> Unit,
    private val mDragListener: (LocationHolder) -> Unit
) : SyncListAdapter<LocationModel, LocationHolder>(
    ArrayList(), object : DiffUtil.ItemCallback<LocationModel>() {
        override fun areItemsTheSame(oldItem: LocationModel, newItem: LocationModel): Boolean {
            return oldItem.areItemsTheSame(newItem)
        }

        override fun areContentsTheSame(oldItem: LocationModel, newItem: LocationModel): Boolean {
            return oldItem.areContentsTheSame(newItem)
        }
    }
) {
    private val mResourceProvider: ResourceProvider = ResourcesProviderFactory.newInstance
    private val mTemperatureUnit: TemperatureUnit = SettingsManager.getInstance(mContext).temperatureUnit

    init {
        update(locationList, selectedId)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationHolder {
        return LocationHolder(
            ItemLocationCardBinding.inflate(LayoutInflater.from(parent.context)),
            mClickListener,
            mDragListener
        )
    }

    override fun onBindViewHolder(holder: LocationHolder, position: Int) {
        holder.onBindView(mContext, getItem(position), mResourceProvider)
    }

    override fun onBindViewHolder(holder: LocationHolder, position: Int, payloads: List<Any>) {
        holder.onBindView(mContext, getItem(position), mResourceProvider)
    }

    fun update(selectedId: String?) {
        val modelList: MutableList<LocationModel> = ArrayList(itemCount)
        for (model in currentList) {
            modelList.add(
                LocationModel(
                    mContext, model.location, sourceManager.getMainWeatherSource(model.location.weatherSource), mTemperatureUnit, model.location.formattedId == selectedId
                )
            )
        }
        submitList(modelList)
    }

    fun update(newList: List<Location>, selectedId: String?) {
        val modelList: MutableList<LocationModel> = ArrayList(newList.size)
        for (l in newList) {
            modelList.add(LocationModel(mContext, l, sourceManager.getMainWeatherSource(l.weatherSource), mTemperatureUnit, l.formattedId == selectedId))
        }
        submitList(modelList)
    }

    fun update(from: Int, to: Int) {
        submitMove(from, to)
    }
}
