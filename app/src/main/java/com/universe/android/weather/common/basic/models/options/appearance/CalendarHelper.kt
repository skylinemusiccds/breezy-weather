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

package com.universe.android.weather.common.basic.models.options.appearance

import android.content.Context
import android.icu.util.ULocale
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.text.util.LocalePreferences
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import com.universe.android.weather.R
import com.universe.android.weather.common.extensions.currentLocale
import com.universe.android.weather.common.extensions.isChinese
import com.universe.android.weather.common.extensions.isIndian
import com.universe.android.weather.settings.SettingsManager
import java.util.Locale

object CalendarHelper {

    private const val CALENDAR_EXTENSION_TYPE = "ca"
    private const val DISPLAY_KEYWORD_OF_CALENDAR = "calendar"

    private val supportedCalendars = listOf(
        LocalePreferences.CalendarType.CHINESE,
        LocalePreferences.CalendarType.DANGI,
        LocalePreferences.CalendarType.INDIAN,
        LocalePreferences.CalendarType.ISLAMIC,
        LocalePreferences.CalendarType.ISLAMIC_CIVIL,
        LocalePreferences.CalendarType.ISLAMIC_RGSA,
        LocalePreferences.CalendarType.ISLAMIC_TBLA,
        LocalePreferences.CalendarType.ISLAMIC_UMALQURA,
        LocalePreferences.CalendarType.PERSIAN
    )

    @RequiresApi(api = Build.VERSION_CODES.N)
    fun getCalendars(context: Context): ImmutableList<AlternateCalendar> {
        return supportedCalendars.map {
            val displayName = try {
                val locale = Locale.Builder()
                    .setUnicodeLocaleKeyword(CALENDAR_EXTENSION_TYPE, it)
                    .build()
                ULocale.getDisplayKeywordValue(
                    locale.toLanguageTag(),
                    DISPLAY_KEYWORD_OF_CALENDAR,
                    ULocale.forLocale(context.currentLocale)
                )
            } catch (ignored: Exception) {
                it
            }
            AlternateCalendar(
                id = it,
                displayName = displayName
            )
        }.sortedBy {
            it.displayName
        }.toMutableList().apply {
            add(0, AlternateCalendar("none", context.getString(R.string.settings_none)))
            add(1, AlternateCalendar("", context.getString(R.string.settings_follow_system)))
        }.toImmutableList()
    }

    fun getAlternateCalendarSetting(context: Context): String? {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            return null
        }

        val alternateCalendarSetting = SettingsManager.getInstance(context).alternateCalendar
        if (alternateCalendarSetting == "none") {
            return null
        }
        val alternateCalendar = alternateCalendarSetting.ifEmpty {
            with (context.currentLocale) {
                when {
                    isChinese -> LocalePreferences.CalendarType.CHINESE
                    isIndian -> LocalePreferences.CalendarType.INDIAN
                    // Looks like all locales defaults to Gregorian calendar:
                    // https://unicode-org.github.io/icu/userguide/datetime/calendar/#calendar-locale-and-keyword-handling
                    else -> LocalePreferences.getCalendarType(context.currentLocale)
                }
            }
        }
        return if (supportedCalendars.contains(alternateCalendar)) {
            alternateCalendar
        } else null
    }

    data class AlternateCalendar(
        val id: String,
        val displayName: String
    )
}
