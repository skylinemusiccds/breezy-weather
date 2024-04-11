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

package com.universe.android.weather.settings.activities

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import breezyweather.domain.location.model.Location
import dagger.hilt.android.AndroidEntryPoint
import com.universe.android.weather.R
import com.universe.android.weather.common.basic.GeoActivity
import com.universe.android.weather.common.ui.widgets.Material3Scaffold
import com.universe.android.weather.common.ui.widgets.generateCollapsedScrollBehavior
import com.universe.android.weather.common.ui.widgets.insets.FitStatusBarTopAppBar
import com.universe.android.weather.settings.compose.SettingsScreenRouter
import com.universe.android.weather.settings.compose.WeatherSourcesSettingsScreen
import com.universe.android.weather.sources.SourceManager
import com.universe.android.weather.theme.compose.BreezyWeatherTheme
import javax.inject.Inject

@AndroidEntryPoint
class SelectWeatherProviderActivity : GeoActivity() {

    @Inject lateinit var sourceManager: SourceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            BreezyWeatherTheme(lightTheme = !isSystemInDarkTheme()) {
                ContentView()
            }
        }
    }

    @Composable
    private fun ContentView() {
        val scrollBehavior = generateCollapsedScrollBehavior()

        Material3Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                FitStatusBarTopAppBar(
                    title = stringResource(R.string.settings_weather_sources),
                    onBackPressed = { finish() },
                    scrollBehavior = scrollBehavior,
                )
            },
        ) { paddings ->
            val navController = rememberNavController()
            NavHost(
                navController = navController,
                startDestination = SettingsScreenRouter.WeatherProviders.route
            ) {
                composable(SettingsScreenRouter.WeatherProviders.route) {
                    WeatherSourcesSettingsScreen(
                        context = this@SelectWeatherProviderActivity,
                        configuredWorldwideSources = sourceManager.getConfiguredMainWeatherSources().filter {
                            it.isFeatureSupportedInMainForLocation(Location())
                        },
                        configurableSources = sourceManager.getConfigurableSources(),
                        paddingValues = paddings,
                    )
                }
            }
        }
    }

    @Preview
    @Composable
    private fun DefaultPreview() {
        BreezyWeatherTheme(lightTheme = isSystemInDarkTheme()) {
            ContentView()
        }
    }
}
