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

import android.Manifest
import android.app.Activity
import android.os.Build
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.core.app.ActivityCompat
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.PermissionStatus
import com.universe.android.weather.BuildConfig
import com.universe.android.weather.R
import com.universe.android.weather.common.extensions.openApplicationDetailsSettings
import com.universe.android.weather.common.preference.EditTextPreference
import com.universe.android.weather.common.preference.ListPreference
import com.universe.android.weather.common.source.ConfigurableSource
import com.universe.android.weather.common.source.LocationSource
import com.universe.android.weather.common.utils.helpers.SnackbarHelper
import com.universe.android.weather.settings.SettingsManager
import com.universe.android.weather.settings.preference.bottomInsetItem
import com.universe.android.weather.settings.preference.clickablePreferenceItem
import com.universe.android.weather.settings.preference.composables.EditTextPreferenceView
import com.universe.android.weather.settings.preference.composables.ListPreferenceView
import com.universe.android.weather.settings.preference.composables.PreferenceScreen
import com.universe.android.weather.settings.preference.composables.PreferenceView
import com.universe.android.weather.settings.preference.composables.SectionFooter
import com.universe.android.weather.settings.preference.composables.SectionHeader
import com.universe.android.weather.settings.preference.editTextPreferenceItem
import com.universe.android.weather.settings.preference.listPreferenceItem
import com.universe.android.weather.settings.preference.sectionFooterItem
import com.universe.android.weather.settings.preference.sectionHeaderItem

@Composable
fun LocationSettingsScreen(
    context: Activity,
    locationSources: List<LocationSource>,
    accessCoarseLocationPermissionState: PermissionState,
    accessFineLocationPermissionState: PermissionState,
    accessBackgroundLocationPermissionState: PermissionState,
    paddingValues: PaddingValues,
) = PreferenceScreen(paddingValues = paddingValues) {
    if (BuildConfig.FLAVOR != "fdroid") {
        sectionHeaderItem(R.string.settings_location_section_general)
        listPreferenceItem(R.string.settings_location_service) { id ->
            ListPreferenceView(
                title = context.getString(id),
                selectedKey = SettingsManager.getInstance(context).locationSource,
                valueArray = locationSources.map { it.id }.toTypedArray(),
                nameArray = locationSources.map { it.name }.toTypedArray(),
                summary = { _, value -> locationSources.firstOrNull { it.id == value }?.name },
                onValueChanged = { sourceId ->
                    SettingsManager.getInstance(context).locationSource = sourceId
                }
            )
        }
        sectionFooterItem(R.string.settings_location_section_general)
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        sectionHeaderItem(R.string.location_service_native)
        clickablePreferenceItem(R.string.settings_location_access_switch_title) { id ->
            PreferenceView(
                titleId = id,
                summaryId = if (accessCoarseLocationPermissionState.status == PermissionStatus.Granted) {
                    R.string.settings_location_access_switch_summaryOn
                } else {
                    R.string.settings_location_access_switch_summaryOff
                },
                onClick = {
                    if (accessCoarseLocationPermissionState.status != PermissionStatus.Granted) {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(context, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                            accessCoarseLocationPermissionState.launchPermissionRequest()
                        } else {
                            context.openApplicationDetailsSettings()
                        }
                    } else {
                        SnackbarHelper.showSnackbar(context.getString(R.string.settings_location_access_permission_already_granted))
                    }
                }
            )
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            clickablePreferenceItem(R.string.settings_location_access_background_title) { id ->
                PreferenceView(
                    titleId = id,
                    summaryId = if (accessBackgroundLocationPermissionState.status == PermissionStatus.Granted) {
                        R.string.settings_location_access_background_summaryOn
                    } else {
                        R.string.settings_location_access_background_summaryOff
                    },
                    enabled = accessCoarseLocationPermissionState.status == PermissionStatus.Granted,
                    onClick = {
                        if (accessBackgroundLocationPermissionState.status != PermissionStatus.Granted) {
                            if (ActivityCompat.shouldShowRequestPermissionRationale(context, Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                                accessBackgroundLocationPermissionState.launchPermissionRequest()
                            } else {
                                context.openApplicationDetailsSettings()
                            }
                        } else {
                            SnackbarHelper.showSnackbar(context.getString(R.string.settings_location_access_permission_already_granted))
                        }
                    }
                )
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            clickablePreferenceItem(R.string.settings_location_access_precise_title) { id ->
                PreferenceView(
                    titleId = id,
                    summaryId = if (accessFineLocationPermissionState.status == PermissionStatus.Granted) {
                        R.string.settings_location_access_precise_summaryOn
                    } else {
                        R.string.settings_location_access_precise_summaryOff
                    },
                    enabled = accessCoarseLocationPermissionState.status == PermissionStatus.Granted,
                    onClick = {
                        if (accessFineLocationPermissionState.status != PermissionStatus.Granted) {
                            if (ActivityCompat.shouldShowRequestPermissionRationale(context, Manifest.permission.ACCESS_FINE_LOCATION)) {
                                accessFineLocationPermissionState.launchPermissionRequest()
                            } else {
                                context.openApplicationDetailsSettings()
                            }
                        } else {
                            SnackbarHelper.showSnackbar(context.getString(R.string.settings_location_access_permission_already_granted))
                        }
                    }
                )
            }
        }
        sectionFooterItem(R.string.location_service_native)
    }

    // TODO: Duplicate code from weather sources
    locationSources.filterIsInstance<ConfigurableSource>().forEach { preferenceSource ->
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
