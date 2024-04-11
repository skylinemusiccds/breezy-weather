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

package com.universe.android.weather.common.ui.activities

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import breezyweather.data.location.LocationRepository
import breezyweather.data.weather.WeatherRepository
import breezyweather.domain.location.model.Location
import dagger.hilt.android.AndroidEntryPoint
import com.universe.android.weather.R
import com.universe.android.weather.common.basic.GeoActivity
import com.universe.android.weather.common.extensions.getFormattedDate
import com.universe.android.weather.common.extensions.getLongWeekdayDayMonth
import com.universe.android.weather.common.ui.composables.PollenGrid
import com.universe.android.weather.common.ui.widgets.Material3CardListItem
import com.universe.android.weather.common.ui.widgets.Material3Scaffold
import com.universe.android.weather.common.ui.widgets.generateCollapsedScrollBehavior
import com.universe.android.weather.common.ui.widgets.getCardListItemMarginDp
import com.universe.android.weather.common.ui.widgets.insets.FitStatusBarTopAppBar
import com.universe.android.weather.common.ui.widgets.insets.bottomInsetItem
import com.universe.android.weather.domain.weather.model.isIndexValid
import com.universe.android.weather.main.utils.MainThemeColorProvider
import com.universe.android.weather.sources.SourceManager
import com.universe.android.weather.theme.compose.BreezyWeatherTheme
import com.universe.android.weather.theme.compose.DayNightTheme
import javax.inject.Inject

// TODO: Consider moving this activity as a fragment of MainActivity, so we don't have to query the database twice
@AndroidEntryPoint
class PollenActivity : GeoActivity() {

    @Inject lateinit var sourceManager: SourceManager
    @Inject lateinit var locationRepository: LocationRepository
    @Inject lateinit var weatherRepository: WeatherRepository

    companion object {
        const val KEY_POLLEN_ACTIVITY_LOCATION_FORMATTED_ID =
            "POLLEN_ACTIVITY_LOCATION_FORMATTED_ID"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ContentView()
        }
    }

    @Composable
    private fun ContentView() {
        val formattedId = intent.getStringExtra(KEY_POLLEN_ACTIVITY_LOCATION_FORMATTED_ID)
        val location = remember { mutableStateOf<Location?>(null) }

        LaunchedEffect(formattedId) {
            var locationC: Location? = null
            if (!formattedId.isNullOrEmpty()) {
                locationC = locationRepository.getLocation(formattedId, withParameters = false)
            }
            if (locationC == null) {
                locationC = locationRepository.getFirstLocation(withParameters = false)
            }
            if (locationC == null) {
                finish()
                return@LaunchedEffect
            }

            val weather = weatherRepository.getWeatherByLocationId(
                locationC.formattedId,
                withDaily = true,
                withHourly = false,
                withMinutely = false,
                withAlerts = false
            )
            if (weather == null) {
                finish()
                return@LaunchedEffect
            }

            location.value = locationC.copy(weather = weather)
        }

        val scrollBehavior = generateCollapsedScrollBehavior()

        BreezyWeatherTheme(lightTheme = MainThemeColorProvider.isLightTheme(this, location.value)) {
            Material3Scaffold(
                modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                topBar = {
                    FitStatusBarTopAppBar(
                        title = stringResource(R.string.pollen),
                        onBackPressed = { finish() },
                        scrollBehavior = scrollBehavior,
                    )
                },
            ) {
                location.value?.weather?.let { weather ->
                    val pollenIndexSource = sourceManager.getPollenIndexSource(
                        if (!location.value!!.pollenSource.isNullOrEmpty()) {
                            location.value!!.pollenSource!!
                        } else location.value!!.weatherSource
                    )

                    LazyColumn(
                        modifier = Modifier.fillMaxHeight(),
                        contentPadding = it,
                    ) {
                        items(weather.dailyForecastStartingToday.filter { d -> d.pollen?.isIndexValid == true }) { daily ->
                            daily.pollen?.let { pollen ->
                                Material3CardListItem(
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column {
                                        Text(
                                            modifier = Modifier.padding(dimensionResource(R.dimen.normal_margin)),
                                            text = daily.date.getFormattedDate(
                                                getLongWeekdayDayMonth(this@PollenActivity),
                                                location.value!!,
                                                this@PollenActivity
                                            ),
                                            color = DayNightTheme.colors.titleColor,
                                            fontWeight = FontWeight.Bold,
                                            style = MaterialTheme.typography.titleMedium,
                                        )
                                        PollenGrid(
                                            pollen = pollen,
                                            pollenIndexSource = pollenIndexSource
                                        )
                                    }
                                }
                            }
                        }

                        bottomInsetItem(
                            extraHeight = getCardListItemMarginDp(this@PollenActivity).dp
                        )
                    }
                }
            }
        }
    }
}
