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

package com.universe.android.weather.settings.compose

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.universe.android.weather.BuildConfig
import com.universe.android.weather.R
import com.universe.android.weather.common.extensions.currentLocale
import com.universe.android.weather.common.preference.EditTextPreference
import com.universe.android.weather.common.preference.ListPreference
import com.universe.android.weather.common.source.ConfigurableSource
import com.universe.android.weather.common.source.LocationSource
import com.universe.android.weather.common.source.MainWeatherSource
import com.universe.android.weather.common.ui.composables.AlertDialogLink
import com.universe.android.weather.common.ui.composables.SourceView
import com.universe.android.weather.common.ui.widgets.Material3CardListItem
import com.universe.android.weather.settings.SettingsManager
import com.universe.android.weather.settings.preference.bottomInsetItem
import com.universe.android.weather.settings.preference.clickablePreferenceItem
import com.universe.android.weather.settings.preference.composables.EditTextPreferenceView
import com.universe.android.weather.settings.preference.composables.ListPreferenceView
import com.universe.android.weather.settings.preference.composables.PreferenceScreen
import com.universe.android.weather.settings.preference.composables.SectionFooter
import com.universe.android.weather.settings.preference.composables.SectionHeader
import com.universe.android.weather.settings.preference.editTextPreferenceItem
import com.universe.android.weather.settings.preference.listPreferenceItem
import com.universe.android.weather.settings.preference.sectionFooterItem
import com.universe.android.weather.settings.preference.sectionHeaderItem
import com.universe.android.weather.theme.compose.DayNightTheme
import java.text.Collator

@Composable
fun WeatherSourcesSettingsScreen(
    context: Context,
    configuredWorldwideSources: List<MainWeatherSource>,
    configurableSources: List<ConfigurableSource>,
    paddingValues: PaddingValues,
) = PreferenceScreen(paddingValues = paddingValues) {
    if (BuildConfig.FLAVOR == "fdroid") {
        clickablePreferenceItem(R.string.settings_weather_source_fdroid_disclaimer) { id ->
            val dialogLinkOpenState = remember { mutableStateOf(false) }

            Material3CardListItem(
                modifier = Modifier.clickable {
                    dialogLinkOpenState.value = true
                }
            ) {
                Text(
                    text = stringResource(id),
                    color = DayNightTheme.colors.bodyColor,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(dimensionResource(R.dimen.normal_margin))
                )
            }
            if (dialogLinkOpenState.value) {
                AlertDialogLink(
                    onClose = { dialogLinkOpenState.value = false },
                    linkToOpen = "https://github.com/breezy-weather/breezy-weather/blob/main/INSTALL.md"
                )
            }
        }
    }

    sectionHeaderItem(R.string.settings_weather_sources_section_general)
    listPreferenceItem(R.string.settings_weather_sources_default_source) { id ->
        val configuredWorldwideSourcesAssociated = configuredWorldwideSources.associate { it.id to it.name }
        val defaultWeatherSource = SettingsManager.getInstance(context).defaultWeatherSource
        SourceView(
            title = stringResource(id),
            selectedKey = if (configuredWorldwideSourcesAssociated.contains(defaultWeatherSource)) {
                defaultWeatherSource
            } else "auto",
            sourceList = mapOf(
                "auto" to stringResource(R.string.settings_automatic)
            ) + configuredWorldwideSources.associate { it.id to it.name },
            card = true
        ) { defaultSource ->
            SettingsManager.getInstance(context).defaultWeatherSource = defaultSource
        }
    }
    sectionFooterItem(R.string.settings_weather_sources_section_general)

    configurableSources
        .filter { it !is LocationSource } // Exclude location sources configured in its own screen
        .sortedWith { ws1, ws2 -> // Sort by name because there are now a lot of sources
            Collator.getInstance(context.currentLocale).compare(ws1.name, ws2.name)
        }
        .forEach { preferenceSource ->
            item(key = "header_${preferenceSource.id}") {
                SectionHeader(title = preferenceSource.name)
            }
            preferenceSource.getPreferences(context).forEach { preference ->
                when (preference) {
                    is ListPreference -> {
                        listPreferenceItem(preference.titleId) { id ->
                            ListPreferenceView(
                                titleId = id,
                                selectedKey = preference.selectedKey,
                                valueArrayId = preference.valueArrayId,
                                nameArrayId = preference.nameArrayId,
                                onValueChanged = preference.onValueChanged,
                            )
                        }
                    }

                    is EditTextPreference -> {
                        editTextPreferenceItem(preference.titleId) { id ->
                            EditTextPreferenceView(
                                titleId = id,
                                summary = preference.summary,
                                content = preference.content,
                                regex = preference.regex,
                                regexError = preference.regexError,
                                onValueChanged = preference.onValueChanged
                            )
                        }
                    }
                }
            }
            item(key = "footer_${preferenceSource.id}") {
                SectionFooter()
            }
        }

    bottomInsetItem()
}
