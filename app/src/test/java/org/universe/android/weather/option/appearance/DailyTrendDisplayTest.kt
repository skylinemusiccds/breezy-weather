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

package com.universe.android.weather.option.appearance

import android.content.Context
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import com.universe.android.weather.R
import com.universe.android.weather.common.basic.models.options.appearance.DailyTrendDisplay
import org.junit.jupiter.api.Test

class DailyTrendDisplayTest {
    @Test
    fun toDailyTrendDisplayList() = runTest {
        val value = "temperature&air_quality&wind&uv_index&precipitation&sunshine&feels_like"
        val list = DailyTrendDisplay.toDailyTrendDisplayList(value)
        list[0] shouldBe DailyTrendDisplay.TAG_TEMPERATURE
        list[1] shouldBe DailyTrendDisplay.TAG_AIR_QUALITY
        list[2] shouldBe DailyTrendDisplay.TAG_WIND
        list[3] shouldBe DailyTrendDisplay.TAG_UV_INDEX
        list[4] shouldBe DailyTrendDisplay.TAG_PRECIPITATION
        list[5] shouldBe DailyTrendDisplay.TAG_SUNSHINE
        list[6] shouldBe DailyTrendDisplay.TAG_FEELS_LIKE
    }

    @Test
    fun toValue() = runTest {
        val list = arrayListOf(
            DailyTrendDisplay.TAG_TEMPERATURE,
            DailyTrendDisplay.TAG_AIR_QUALITY,
            DailyTrendDisplay.TAG_WIND,
            DailyTrendDisplay.TAG_UV_INDEX,
            DailyTrendDisplay.TAG_PRECIPITATION,
            DailyTrendDisplay.TAG_SUNSHINE,
            DailyTrendDisplay.TAG_FEELS_LIKE
        )
        val value = "temperature&air_quality&wind&uv_index&precipitation&sunshine&feels_like"
        DailyTrendDisplay.toValue(list) shouldBe value
    }

    @Test
    fun getSummary() = runTest {
        val context = mockk<Context>()
        every { context.getString(any()) } returns "Name"
        every { context.getString(R.string.comma_separator) } returns ", "
        val list = arrayListOf(
            DailyTrendDisplay.TAG_TEMPERATURE,
            DailyTrendDisplay.TAG_AIR_QUALITY,
            DailyTrendDisplay.TAG_WIND,
            DailyTrendDisplay.TAG_UV_INDEX,
            DailyTrendDisplay.TAG_PRECIPITATION,
            DailyTrendDisplay.TAG_SUNSHINE,
            DailyTrendDisplay.TAG_FEELS_LIKE
        )
        val value = "Name, Name, Name, Name, Name, Name, Name"
        DailyTrendDisplay.getSummary(context, list) shouldBe value
    }
}
