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

package com.universe.android.weather.main.widgets

import android.content.Context
import android.util.AttributeSet
import com.universe.android.weather.common.extensions.getTabletListAdaptiveWidth
import com.universe.android.weather.common.ui.widgets.insets.FitSystemBarRecyclerView

class FitTabletRecyclerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
): FitSystemBarRecyclerView(context, attrs, defStyleAttr) {

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val viewWidth = measuredWidth
        val adaptiveWidth = context.getTabletListAdaptiveWidth(viewWidth)
        val paddingHorizontal = (viewWidth - adaptiveWidth) / 2
        setPadding(paddingHorizontal, paddingTop, paddingHorizontal, paddingBottom)
    }
}
