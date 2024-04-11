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
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.universe.android.weather.common.basic.insets.FitBothSideBarHelper
import com.universe.android.weather.common.basic.insets.FitBothSideBarView
import com.universe.android.weather.common.basic.insets.FitBothSideBarView.FitSide

class FitSystemBarViewPager @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : ViewPager(context, attrs), FitBothSideBarView {
    val helper: FitBothSideBarHelper

    class FitBottomSystemBarPagerAdapter(
        private val mPager: FitSystemBarViewPager,
        private val mViewList: List<View>,
        var mTitleList: List<String>
    ) : PagerAdapter() {
        override fun getCount(): Int {
            return mViewList.size
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view === `object`
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            setWindowInsetsForViewTree(mViewList[position], mPager.helper.windowInsets)
            container.addView(mViewList[position])
            return mViewList[position]
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(mViewList[position])
        }

        override fun getPageTitle(position: Int): CharSequence {
            return mTitleList[position]
        }

        private fun setWindowInsetsForViewTree(view: View, insets: Rect) {
            setWindowInsets(view, insets)
            if (view is ViewGroup) {
                val count = view.childCount
                for (i in 0 until count) {
                    setWindowInsetsForViewTree(view.getChildAt(i), insets)
                }
            }
        }

        private fun setWindowInsets(view: View, insets: Rect) {
            if (view is FitSystemBarNestedScrollView) {
                view.fitSystemWindows(insets)
            } else if (view is FitSystemBarRecyclerView) {
                view.fitSystemWindows(insets)
            }
        }
    }

    init {
        helper = FitBothSideBarHelper(this, FitBothSideBarView.SIDE_TOP)
    }

    override fun onApplyWindowInsets(insets: WindowInsets): WindowInsets {
        return helper.onApplyWindowInsets(insets)
    }

    override fun addFitSide(@FitSide side: Int) {
        helper.addFitSide(side)
    }

    override fun removeFitSide(@FitSide side: Int) {
        helper.removeFitSide(side)
    }

    override fun setFitSystemBarEnabled(top: Boolean, bottom: Boolean) {
        helper.setFitSystemBarEnabled(top, bottom)
    }

    override val topWindowInset: Int
        get() = helper.top()
    override val bottomWindowInset: Int
        get() = helper.bottom()
}
