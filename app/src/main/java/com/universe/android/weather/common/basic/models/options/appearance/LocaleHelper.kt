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
import androidx.core.os.LocaleListCompat
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import com.universe.android.weather.R
import org.xmlpull.v1.XmlPullParser
import java.util.Locale

object LocaleHelper {

    fun getLangs(context: Context): ImmutableList<Language> {
        val langs = mutableListOf<Language>()
        val parser = context.resources.getXml(R.xml.locales_config)
        var eventType = parser.eventType
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG && parser.name == "locale") {
                for (i in 0..<parser.attributeCount) {
                    if (parser.getAttributeName(i) == "name") {
                        val langTag = parser.getAttributeValue(i)
                        val displayName = getLocalizedDisplayName(langTag)
                        if (displayName.isNotEmpty()) {
                            langs.add(Language(langTag, displayName/*, getDisplayName(langTag)*/))
                        }
                    }
                }
            }
            eventType = parser.next()
        }

        langs.sortBy { it.displayName }
        langs.add(0, Language("", context.getString(R.string.settings_follow_system)/*, null*/))

        return langs.toImmutableList()
    }

    fun getDisplayName(lang: String): String {
        val normalizedLang = when (lang) {
            "zh-CN" -> "zh-Hans"
            "zh-TW" -> "zh-Hant"
            else -> lang
        }

        return Locale.forLanguageTag(normalizedLang).displayName
    }

    /**
     * Returns display name of a string language code.
     *
     * @param lang empty for system language
     */
    fun getLocalizedDisplayName(lang: String?): String {
        if (lang == null) {
            return ""
        }

        val locale = when (lang) {
            "" -> LocaleListCompat.getAdjustedDefault()[0]
            "zh-CN" -> Locale.forLanguageTag("zh-Hans")
            "zh-TW" -> Locale.forLanguageTag("zh-Hant")
            else -> Locale.forLanguageTag(lang)
        }
        return locale!!.getDisplayName(locale).replaceFirstChar { it.uppercase(locale) }
    }

    data class Language(
        val langTag: String,
        val displayName: String,
        //val localizedDisplayName: String?,
    )
}
