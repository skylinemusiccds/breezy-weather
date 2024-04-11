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

sealed class SettingsScreenRouter(val route: String) {
    object Root : SettingsScreenRouter("com.universe.android.weather.settings.root")
    object BackgroundUpdates : SettingsScreenRouter("com.universe.android.weather.settings.background")
    object Location : SettingsScreenRouter("com.universe.android.weather.settings.location")
    object WeatherProviders : SettingsScreenRouter("com.universe.android.weather.settings.providers")
    object Appearance : SettingsScreenRouter("com.universe.android.weather.settings.appearance")
    object MainScreen : SettingsScreenRouter("com.universe.android.weather.settings.main")
    object Notifications : SettingsScreenRouter("com.universe.android.weather.settings.notifications")
    object Unit : SettingsScreenRouter("com.universe.android.weather.settings.unit")
    object Widgets : SettingsScreenRouter("com.universe.android.weather.settings.widgets")
    object Debug : SettingsScreenRouter("com.universe.android.weather.settings.debug")
}
