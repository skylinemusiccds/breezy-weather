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

enum class NotificationStyle(
    override val id: String
): BaseEnum {

    NATIVE("native"),
    CITIES("cities"),
    DAILY("daily"),
    HOURLY("hourly");

    companion object {

        fun getInstance(
            value: String
        ) = NotificationStyle.entries.firstOrNull {
            it.id == value
        } ?: DAILY
    }

    override val valueArrayId = R.array.notification_style_values
    override val nameArrayId = R.array.notification_styles

    override fun getName(context: Context) = Utils.getName(context, this)
}
