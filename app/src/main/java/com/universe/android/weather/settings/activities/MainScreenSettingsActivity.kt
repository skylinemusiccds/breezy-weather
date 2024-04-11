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

import android.content.Context
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import com.universe.android.weather.R
import com.universe.android.weather.common.basic.GeoActivity
import com.universe.android.weather.common.bus.EventBus
import com.universe.android.weather.common.ui.widgets.Material3Scaffold
import com.universe.android.weather.common.ui.widgets.generateCollapsedScrollBehavior
import com.universe.android.weather.common.ui.widgets.insets.FitStatusBarTopAppBar
import com.universe.android.weather.settings.SettingsChangedMessage
import com.universe.android.weather.settings.SettingsManager
import com.universe.android.weather.settings.compose.MainScreenSettingsScreen
import com.universe.android.weather.settings.compose.SettingsScreenRouter
import com.universe.android.weather.sources.RefreshHelper
import com.universe.android.weather.sources.SourceManager
import com.universe.android.weather.theme.compose.BreezyWeatherTheme
import javax.inject.Inject

@AndroidEntryPoint
class MainScreenSettingsActivity : GeoActivity() {

    @Inject lateinit var sourceManager: SourceManager
    @Inject lateinit var refreshHelper: RefreshHelper
    private val cardDisplayState = mutableStateOf(
        SettingsManager.getInstance(this).cardDisplayList
    )
    private val dailyTrendDisplayState = mutableStateOf(
        SettingsManager.getInstance(this).dailyTrendDisplayList
    )
    private val hourlyTrendDisplayState = mutableStateOf(
        SettingsManager.getInstance(this).hourlyTrendDisplayList
    )
    private val detailsDisplayState = mutableStateOf(
        SettingsManager.getInstance(this).detailDisplayList
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        EventBus.instance.with(SettingsChangedMessage::class.java).observe(this) {
            val cardDisplayList = SettingsManager.getInstance(this).cardDisplayList
            if (cardDisplayState.value != cardDisplayList) {
                cardDisplayState.value = cardDisplayList
            }

            val dailyTrendDisplayList = SettingsManager.getInstance(this).dailyTrendDisplayList
            if (dailyTrendDisplayState.value != dailyTrendDisplayList) {
                dailyTrendDisplayState.value = dailyTrendDisplayList
            }

            val hourlyTrendDisplayList = SettingsManager.getInstance(this).hourlyTrendDisplayList
            if (hourlyTrendDisplayState.value != hourlyTrendDisplayList) {
                hourlyTrendDisplayState.value = hourlyTrendDisplayList
            }

            val detailsDisplayList = SettingsManager.getInstance(this).detailDisplayList
            if (detailsDisplayState.value != detailsDisplayList) {
                detailsDisplayState.value = detailsDisplayList
            }
        }

        setContent {
            BreezyWeatherTheme(lightTheme = !isSystemInDarkTheme()) {
                ContentView()
            }
        }
    }

    @Composable
    private fun ContentView() {
        val scrollBehavior = generateCollapsedScrollBehavior()
        val scope = rememberCoroutineScope()

        Material3Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                FitStatusBarTopAppBar(
                    title = stringResource(R.string.settings_main),
                    onBackPressed = { finish() },
                    scrollBehavior = scrollBehavior,
                )
            },
        ) { paddings ->
            val navController = rememberNavController()
            NavHost(
                navController = navController,
                startDestination = SettingsScreenRouter.MainScreen.route
            ) {
                composable(SettingsScreenRouter.MainScreen.route) {
                    MainScreenSettingsScreen(
                        context = this@MainScreenSettingsActivity,
                        cardDisplayList = remember { cardDisplayState }.value,
                        dailyTrendDisplayList = remember { dailyTrendDisplayState }.value,
                        hourlyTrendDisplayList = remember { hourlyTrendDisplayState }.value,
                        detailDisplayList = remember { detailsDisplayState }.value,
                        paddingValues = paddings,
                        updateWidgetIfNecessary = { context: Context ->
                            scope.launch {
                                refreshHelper.updateWidgetIfNecessary(context)
                            }
                        }
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
