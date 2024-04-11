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
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import dagger.hilt.android.AndroidEntryPoint
import com.universe.android.weather.R
import com.universe.android.weather.common.basic.GeoActivity
import com.universe.android.weather.common.extensions.currentLocale
import com.universe.android.weather.common.ui.widgets.Material3Scaffold
import com.universe.android.weather.common.ui.widgets.generateCollapsedScrollBehavior
import com.universe.android.weather.common.ui.widgets.insets.FitStatusBarTopAppBar
import com.universe.android.weather.settings.preference.bottomInsetItem
import com.universe.android.weather.settings.preference.clickablePreferenceItem
import com.universe.android.weather.settings.preference.composables.PreferenceScreen
import com.universe.android.weather.settings.preference.composables.PreferenceView
import com.universe.android.weather.sources.SourceManager
import com.universe.android.weather.theme.compose.BreezyWeatherTheme
import java.text.Collator
import javax.inject.Inject

@AndroidEntryPoint
class PrivacyPolicyActivity : GeoActivity() {

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
        val uriHandler = LocalUriHandler.current

        Material3Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                FitStatusBarTopAppBar(
                    title = stringResource(R.string.about_privacy_policy),
                    onBackPressed = { finish() },
                    scrollBehavior = scrollBehavior,
                )
            },
        ) { paddingValues ->
            PreferenceScreen(paddingValues = paddingValues) {
                clickablePreferenceItem(R.string.breezy_weather) { id ->
                    val url = "https://github.com/breezy-weather/breezy-weather/blob/main/PRIVACY.md"
                    PreferenceView(
                        title = stringResource(id),
                        summary = url
                    ) {
                        uriHandler.openUri(url)
                    }
                }

                items(sourceManager.getHttpSources()
                    .filter { it.privacyPolicyUrl.startsWith("http") }
                    .sortedWith { s1, s2 -> // Sort by name because there are now a lot of sources
                        Collator.getInstance(
                            this@PrivacyPolicyActivity.currentLocale
                        ).compare(s1.name, s2.name)
                    }) { preferenceSource ->
                    PreferenceView(
                        title = preferenceSource.name,
                        summary = preferenceSource.privacyPolicyUrl
                    ) {
                        uriHandler.openUri(preferenceSource.privacyPolicyUrl)
                    }
                }

                bottomInsetItem()
            }
        }
    }
}
