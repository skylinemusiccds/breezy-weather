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

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.core.net.toUri
import com.universe.android.weather.R
import com.universe.android.weather.background.weather.WeatherUpdateJob
import com.universe.android.weather.common.basic.models.options.UpdateInterval
import com.universe.android.weather.common.extensions.getFormattedDate
import com.universe.android.weather.common.extensions.powerManager
import com.universe.android.weather.common.utils.helpers.SnackbarHelper
import com.universe.android.weather.settings.SettingsManager
import com.universe.android.weather.settings.activities.WorkerInfoActivity
import com.universe.android.weather.settings.preference.bottomInsetItem
import com.universe.android.weather.settings.preference.clickablePreferenceItem
import com.universe.android.weather.settings.preference.composables.ListPreferenceView
import com.universe.android.weather.settings.preference.composables.PreferenceScreen
import com.universe.android.weather.settings.preference.composables.PreferenceView
import com.universe.android.weather.settings.preference.composables.SwitchPreferenceView
import com.universe.android.weather.settings.preference.listPreferenceItem
import com.universe.android.weather.settings.preference.sectionFooterItem
import com.universe.android.weather.settings.preference.sectionHeaderItem
import com.universe.android.weather.settings.preference.switchPreferenceItem
import com.universe.android.weather.theme.compose.DayNightTheme
import java.util.Date

@Composable
fun BackgroundSettingsScreen(
    context: Context,
    updateInterval: UpdateInterval,
    paddingValues: PaddingValues
) {
    val uriHandler = LocalUriHandler.current
    PreferenceScreen(paddingValues = paddingValues) {
        sectionHeaderItem(R.string.settings_background_updates_section_general)
        listPreferenceItem(R.string.settings_background_updates_refresh_title) { id ->
            val dialogNeverRefreshOpenState = remember { mutableStateOf(false) }
            ListPreferenceView(
                titleId = id,
                selectedKey = updateInterval.id,
                valueArrayId = R.array.automatic_refresh_rate_values,
                nameArrayId = R.array.automatic_refresh_rates,
                withState = false,
                onValueChanged = {
                    val newValue = UpdateInterval.getInstance(it)
                    if (newValue == UpdateInterval.INTERVAL_NEVER) {
                        dialogNeverRefreshOpenState.value = true
                    } else {
                        SettingsManager
                            .getInstance(context)
                            .updateInterval = UpdateInterval.getInstance(it)
                        WeatherUpdateJob.setupTask(context)
                    }
                },
            )
            if (dialogNeverRefreshOpenState.value) {
                AlertDialog(
                    onDismissRequest = { dialogNeverRefreshOpenState.value = false },
                    text = {
                        Column(
                            modifier = Modifier
                                .verticalScroll(rememberScrollState())
                        ) {
                            Text(
                                text = stringResource(R.string.settings_background_updates_refresh_never_warning1),
                                color = DayNightTheme.colors.bodyColor,
                                style = MaterialTheme.typography.bodyMedium,
                            )
                            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.normal_margin)))
                            Text(
                                text = stringResource(R.string.settings_background_updates_refresh_never_warning2),
                                color = DayNightTheme.colors.bodyColor,
                                style = MaterialTheme.typography.bodyMedium,
                            )
                            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.normal_margin)))
                            Text(
                                text = stringResource(R.string.settings_background_updates_refresh_never_warning3),
                                color = DayNightTheme.colors.bodyColor,
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        }
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                dialogNeverRefreshOpenState.value = false
                                SettingsManager
                                    .getInstance(context)
                                    .updateInterval = UpdateInterval.INTERVAL_NEVER
                                WeatherUpdateJob.setupTask(context)
                            }
                        ) {
                            Text(
                                text = stringResource(R.string.action_continue),
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.labelLarge,
                            )
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = {
                                dialogNeverRefreshOpenState.value = false
                            }
                        ) {
                            Text(
                                text = stringResource(R.string.action_cancel),
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.labelLarge,
                            )
                        }
                    }
                )
            }
        }
        switchPreferenceItem(R.string.settings_background_updates_refresh_ignore_when_battery_low) { id ->
            SwitchPreferenceView(
                titleId = id,
                summaryOnId = R.string.settings_enabled,
                summaryOffId = R.string.settings_disabled,
                checked = SettingsManager.getInstance(context).ignoreUpdatesWhenBatteryLow,
                enabled = updateInterval != UpdateInterval.INTERVAL_NEVER,
                onValueChanged = {
                    SettingsManager.getInstance(context).ignoreUpdatesWhenBatteryLow = it
                    WeatherUpdateJob.setupTask(context)
                },
            )
        }
        sectionFooterItem(R.string.settings_background_updates_section_general)

        sectionHeaderItem(R.string.settings_background_updates_section_troubleshoot)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            clickablePreferenceItem(R.string.settings_background_updates_battery_optimization) { id ->
                PreferenceView(
                    titleId = id,
                    summaryId = R.string.settings_background_updates_battery_optimization_summary
                ) {
                    val packageName: String = context.packageName
                    if (!context.powerManager.isIgnoringBatteryOptimizations(packageName)) {
                        try {
                            @SuppressLint("BatteryLife")
                            val intent = Intent().apply {
                                action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
                                data = "package:$packageName".toUri()
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            }
                            context.startActivity(intent)
                        } catch (e: ActivityNotFoundException) {
                            SnackbarHelper.showSnackbar(context.getString(R.string.settings_background_updates_battery_optimization_activity_not_found))
                        }
                    } else {
                        SnackbarHelper.showSnackbar(context.getString(R.string.settings_background_updates_battery_optimization_disabled))
                    }
                }
            }
        }
        clickablePreferenceItem(R.string.settings_background_updates_dont_kill_my_app_title) { id ->
            PreferenceView(
                titleId = id,
                summaryId = R.string.settings_background_updates_dont_kill_my_app_summary
            ) {
                uriHandler.openUri("https://dontkillmyapp.com/")
            }
        }
        clickablePreferenceItem(R.string.settings_background_updates_worker_info_title) { id ->
            PreferenceView(
                title = context.getString(id),
                summary = if (SettingsManager.getInstance(context).weatherUpdateLastTimestamp > 0) {
                    context.getString(
                        R.string.settings_background_updates_worker_info_summary,
                        Date(SettingsManager.getInstance(context).weatherUpdateLastTimestamp)
                            .getFormattedDate("yyyy-MM-dd HH:mm")
                    )
                } else null
            ) {
                context.startActivity(Intent(context, WorkerInfoActivity::class.java))
            }
        }
        sectionFooterItem(R.string.settings_background_updates_section_troubleshoot)

        bottomInsetItem()
    }
}
