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

package com.universe.android.weather.common.basic.models.options

import android.content.Context
import com.universe.android.weather.R
import com.universe.android.weather.common.basic.models.options._basic.BaseEnum
import com.universe.android.weather.common.basic.models.options._basic.Utils

enum class WidgetWeekIconMode(
    override val id: String
): BaseEnum {

    AUTO("auto"),
    DAY("day"),
    NIGHT("night");

    companion object {

        fun getInstance(
            value: String
        ) = WidgetWeekIconMode.entries.firstOrNull {
            it.id == value
        } ?: AUTO
    }

    override val valueArrayId = R.array.week_icon_mode_values
    override val nameArrayId = R.array.week_icon_modes

    override fun getName(context: Context) = Utils.getName(context, this)
}
