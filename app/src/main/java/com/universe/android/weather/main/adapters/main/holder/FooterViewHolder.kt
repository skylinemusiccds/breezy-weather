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

import android.animation.Animator
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import androidx.annotation.CallSuper
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import breezyweather.domain.location.model.Location
import io.github.giangpham96.expandable_text_compose.ExpandableText
import com.universe.android.weather.R
import com.universe.android.weather.common.source.MainWeatherSource
import com.universe.android.weather.common.source.SecondaryWeatherSource
import com.universe.android.weather.common.source.Source
import com.universe.android.weather.common.ui.composables.AlertDialogNoPadding
import com.universe.android.weather.common.ui.composables.LocationPreference
import com.universe.android.weather.main.MainActivity
import com.universe.android.weather.main.utils.MainThemeColorProvider
import com.universe.android.weather.theme.ThemeManager
import com.universe.android.weather.theme.compose.BreezyWeatherTheme
import com.universe.android.weather.theme.compose.DayNightTheme
import com.universe.android.weather.theme.resource.providers.ResourceProvider

class FooterViewHolder(
    private val composeView: ComposeView
) : AbstractMainViewHolder(composeView) {

    @SuppressLint("SetTextI18n")
    @CallSuper
    override fun onBindView(
        context: Context, location: Location, provider: ResourceProvider,
        listAnimationEnabled: Boolean, itemAnimationEnabled: Boolean
    ) {
        super.onBindView(context, location, provider, listAnimationEnabled, itemAnimationEnabled)

        val cardMarginsVertical = ThemeManager.getInstance(context)
            .weatherThemeDelegate
            .getHomeCardMargins(context).toFloat()

        val distinctSources = mutableMapOf<String, Source?>()
        listOf(
            location.weatherSource,
            location.airQualitySourceNotNull,
            location.pollenSourceNotNull,
            location.minutelySourceNotNull,
            location.alertSourceNotNull,
            location.normalsSourceNotNull
        ).distinct().forEach {
            distinctSources[it] = (context as MainActivity).sourceManager.getSource(it)
        }

        val credits = mutableMapOf<String, String?>()
        credits["weather"] = if (distinctSources[location.weatherSource] is MainWeatherSource) {
            (distinctSources[location.weatherSource] as MainWeatherSource).weatherAttribution
        } else null
        credits["minutely"] = if (distinctSources[location.minutelySourceNotNull] is SecondaryWeatherSource &&
            (distinctSources[location.minutelySourceNotNull] as SecondaryWeatherSource).minutelyAttribution != credits["weather"]) {
            (distinctSources[location.minutelySourceNotNull] as SecondaryWeatherSource).minutelyAttribution
        } else null
        credits["alert"] = if (distinctSources[location.alertSourceNotNull] is SecondaryWeatherSource &&
            (distinctSources[location.alertSourceNotNull] as SecondaryWeatherSource).alertAttribution != credits["weather"]) {
            (distinctSources[location.alertSourceNotNull] as SecondaryWeatherSource).alertAttribution
        } else null
        credits["airQuality"] = if (distinctSources[location.airQualitySourceNotNull] is SecondaryWeatherSource &&
            (distinctSources[location.airQualitySourceNotNull] as SecondaryWeatherSource).airQualityAttribution != credits["weather"]) {
            (distinctSources[location.airQualitySourceNotNull] as SecondaryWeatherSource).airQualityAttribution
        } else null
        credits["pollen"] = if (distinctSources[location.pollenSourceNotNull] is SecondaryWeatherSource &&
            (distinctSources[location.pollenSourceNotNull] as SecondaryWeatherSource).pollenAttribution != credits["weather"]) {
            (distinctSources[location.pollenSourceNotNull] as SecondaryWeatherSource).pollenAttribution
        } else null
        credits["normals"] = if (distinctSources[location.normalsSourceNotNull] is SecondaryWeatherSource &&
            (distinctSources[location.normalsSourceNotNull] as SecondaryWeatherSource).normalsAttribution != credits["weather"]) {
            (distinctSources[location.normalsSourceNotNull] as SecondaryWeatherSource).normalsAttribution
        } else null

        val creditsText = StringBuilder()
        location.weather?.let { weather ->
            creditsText.append(
                context.getString(
                    R.string.weather_data_by,
                    credits["weather"] ?: context.getString(R.string.null_data_text)
                )
            )
            if (weather.minutelyForecast.isNotEmpty() &&
                !credits["minutely"].isNullOrEmpty()) {
                creditsText.append(
                    "\n" +
                    context.getString(R.string.weather_minutely_data_by, credits["minutely"]!!)
                )
            }
            if (weather.alertList.isNotEmpty() &&
                !credits["alert"].isNullOrEmpty()) {
                creditsText.append(
                    "\n" +
                            context.getString(R.string.weather_alert_data_by, credits["alert"]!!)
                )
            }
            // Open-Meteo has a lengthy credits so we merge air quality and pollen identical credit in that case
            if (!credits["airQuality"].isNullOrEmpty()) {
                if (!credits["pollen"].isNullOrEmpty()) {
                    if (credits["airQuality"] == credits["pollen"]) {
                        creditsText.append(
                            "\n" +
                            context.getString(
                                R.string.weather_air_quality_and_pollen_data_by,
                                credits["airQuality"]!!
                            )
                        )
                    } else {
                        creditsText.append(
                            "\n" +
                            context.getString(
                                R.string.weather_air_quality_data_by, credits["airQuality"]!!
                            ) +
                            "\n" +
                            context.getString(R.string.weather_pollen_data_by, credits["pollen"]!!)
                        )
                    }
                } else {
                    creditsText.append(
                        "\n" +
                        context.getString(
                            R.string.weather_air_quality_data_by, credits["airQuality"]!!
                        )
                    )
                }
            } else {
                if (!credits["pollen"].isNullOrEmpty()) {
                    creditsText.append(
                        "\n" +
                        context.getString(R.string.weather_pollen_data_by, credits["pollen"]!!)
                    )
                }
            }
            if (weather.normals?.month != null && !credits["normals"].isNullOrEmpty()) {
                creditsText.append(
                    "\n" + context.getString(R.string.weather_normals_data_by, credits["normals"]!!)
                )
            }
        }

        composeView.setContent {
            BreezyWeatherTheme(lightTheme = MainThemeColorProvider.isLightTheme(context, location)) {
                ComposeView((context as MainActivity), location, creditsText.toString(), cardMarginsVertical.toInt())
            }
        }
    }

    @Composable
    fun ComposeView(
        activity: MainActivity, location: Location, creditsText: String, cardMarginsVertical: Int
    ) {
        var expand by remember { mutableStateOf(false) }
        var dialogOpenState by remember { mutableStateOf(false) }

        val paddingTop = dimensionResource(R.dimen.little_margin) - cardMarginsVertical.dp
        Row(
            modifier = Modifier
                .padding(
                    PaddingValues(
                        start = dimensionResource(R.dimen.normal_margin),
                        top = if (paddingTop > 0.dp) paddingTop else 0.dp,
                        end = dimensionResource(R.dimen.normal_margin),
                        bottom = dimensionResource(R.dimen.little_margin)
                    )
                )
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ExpandableText(
                originalText = creditsText,
                expandAction = stringResource(R.string.action_see_more),
                expand = expand,
                color = Color.White,
                expandActionColor = Color.White,
                limitedMaxLines = 3,
                animationSpec = spring(),
                modifier = Modifier
                    .weight(1f)
                    .clickable { expand = !expand }
            )
            TextButton(
                onClick = {
                    dialogOpenState = true
                }
            ) {
                Text(
                    text = stringResource(R.string.action_edit),
                    color = Color.White,
                    fontSize = dimensionResource(id = R.dimen.content_text_size).value.sp
                )
            }
        }

        if (dialogOpenState) {
            val dialogDeleteLocationOpenState = remember { mutableStateOf(false) }
            AlertDialogNoPadding(
                onDismissRequest = {
                    dialogOpenState = false
                },
                title = {
                    Text(
                        text = stringResource(R.string.action_settings),
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.headlineSmall,
                    )
                },
                text = {
                    LocationPreference(activity, location, true) { newLocation: Location? ->
                        if (newLocation != null) {
                            activity.updateLocation(newLocation)
                        }
                        dialogOpenState = false
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            dialogOpenState = false
                        }
                    ) {
                        Text(
                            text = stringResource(R.string.action_close),
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.labelLarge,
                        )
                    }
                },
                dismissButton = if (activity.locationListSize() > 1) {
                    {
                        TextButton(
                            onClick = {
                                dialogDeleteLocationOpenState.value = true
                            }
                        ) {
                            Text(
                                text = stringResource(R.string.action_delete),
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.labelLarge,
                            )
                        }
                    }
                } else null
            )

            if (dialogDeleteLocationOpenState.value) {
                AlertDialog(
                    onDismissRequest = {
                        dialogDeleteLocationOpenState.value = false
                    },
                    title = {
                        Text(
                            text = stringResource(R.string.location_delete_location_dialog_title),
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.headlineSmall,
                        )
                    },
                    text = {
                        Text(
                            text = if (location.city.isNotEmpty()) {
                                stringResource(
                                    R.string.location_delete_location_dialog_message,
                                    location.city
                                )
                            } else {
                                stringResource(R.string.location_delete_location_dialog_message_no_name)
                            },
                            color = DayNightTheme.colors.bodyColor,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                dialogDeleteLocationOpenState.value = false
                                dialogOpenState = false
                                activity.deleteLocation(location)
                            }
                        ) {
                            Text(
                                text = stringResource(R.string.action_confirm),
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.labelLarge,
                            )
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = {
                                dialogDeleteLocationOpenState.value = false
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
    }

    override fun getEnterAnimator(pendingAnimatorList: List<Animator>): Animator {
        return ObjectAnimator.ofFloat(itemView, "alpha", 0f, 1f).apply {
            duration = 450
            interpolator = FastOutSlowInInterpolator()
            startDelay = pendingAnimatorList.size * 150L
        }
    }
}
