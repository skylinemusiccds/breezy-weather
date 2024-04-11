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

package com.universe.android.weather.common.ui.widgets.insets

import android.content.Context
import android.util.AttributeSet
import android.view.WindowInsets
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.universe.android.weather.R
import com.universe.android.weather.common.basic.insets.FitBothSideBarHelper
import com.universe.android.weather.common.basic.insets.FitBothSideBarView
import com.universe.android.weather.common.basic.insets.FitBothSideBarView.FitSide

class FitSystemBarSwipeRefreshLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : SwipeRefreshLayout(context, attrs), FitBothSideBarView {
    private val mHelper: FitBothSideBarHelper

    init {
        mHelper = FitBothSideBarHelper(this, FitBothSideBarView.SIDE_TOP)
    }

    override fun onApplyWindowInsets(insets: WindowInsets): WindowInsets {
        return mHelper.onApplyWindowInsets(insets) { fitSystemBar() }
    }

    private fun fitSystemBar() {
        val startPosition = mHelper.top() + resources.getDimensionPixelSize(R.dimen.normal_margin)
        val endPosition = (startPosition + 64 * resources.displayMetrics.density).toInt()
        if (startPosition != progressViewStartOffset || endPosition != progressViewEndOffset) {
            setProgressViewOffset(false, startPosition, endPosition)
        }
    }

    override fun addFitSide(@FitSide side: Int) {
        // do nothing.
    }

    override fun removeFitSide(@FitSide side: Int) {
        // do nothing.
    }

    override fun setFitSystemBarEnabled(top: Boolean, bottom: Boolean) {
        mHelper.setFitSystemBarEnabled(top, bottom)
    }

    override val topWindowInset: Int
        get() = mHelper.top()
    override val bottomWindowInset: Int
        get() = 0
}
