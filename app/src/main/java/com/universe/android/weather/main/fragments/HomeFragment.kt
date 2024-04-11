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

package com.universe.android.weather.main.fragments

import android.animation.Animator
import android.annotation.SuppressLint
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.graphics.ColorUtils
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.RecyclerView
import breezyweather.domain.location.model.Location
import kotlinx.coroutines.launch
import com.universe.android.weather.R
import com.universe.android.weather.common.basic.livedata.EqualtableLiveData
import com.universe.android.weather.common.basic.models.options.appearance.BackgroundAnimationMode
import com.universe.android.weather.common.extensions.isMotionReduced
import com.universe.android.weather.common.extensions.isTabletDevice
import com.universe.android.weather.common.ui.widgets.SwipeSwitchLayout
import com.universe.android.weather.databinding.FragmentHomeBinding
import com.universe.android.weather.domain.location.model.getPlace
import com.universe.android.weather.main.MainActivity
import com.universe.android.weather.main.MainActivityViewModel
import com.universe.android.weather.main.adapters.main.MainAdapter
import com.universe.android.weather.main.layouts.MainLayoutManager
import com.universe.android.weather.main.utils.MainModuleUtils
import com.universe.android.weather.main.utils.MainThemeColorProvider
import com.universe.android.weather.settings.SettingsManager
import com.universe.android.weather.theme.ThemeManager
import com.universe.android.weather.theme.resource.ResourcesProviderFactory
import com.universe.android.weather.theme.resource.providers.ResourceProvider
import com.universe.android.weather.theme.weatherView.WeatherView
import com.universe.android.weather.theme.weatherView.WeatherViewController

class HomeFragment : MainModuleFragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var viewModel: MainActivityViewModel
    private lateinit var weatherView: WeatherView

    private var adapter: MainAdapter? = null
    private var scrollListener: OnScrollListener? = null
    private var recyclerViewAnimator: Animator? = null
    private var resourceProvider: ResourceProvider? = null

    private val previewOffset = EqualtableLiveData(0)
    private var callback: Callback? = null
    private var lastCurrentLocation: Location? = null

    interface Callback {
        fun onManageIconClicked()
        fun onSettingsIconClicked()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater, container, false)

        initModel()

        // attach weather view.
        weatherView = ThemeManager
            .getInstance(requireContext())
            .weatherThemeDelegate
            .getWeatherView(requireContext())
        (binding.switchLayout.parent as CoordinatorLayout).addView(
            weatherView as View,
            0,
            CoordinatorLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )

        initView()
        setCallback(requireActivity() as Callback)

        return binding.root
    }

    private fun isBackgroundAnimationEnabled() =
        when (SettingsManager.getInstance(requireContext()).backgroundAnimationMode) {
            BackgroundAnimationMode.SYSTEM -> !requireContext().isMotionReduced
            BackgroundAnimationMode.ENABLED -> true
            BackgroundAnimationMode.DISABLED -> false
        }

    override fun onResume() {
        super.onResume()
        weatherView.setDrawable(!isHidden)
    }

    override fun onPause() {
        super.onPause()
        weatherView.setDrawable(false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapter = null
        binding.recyclerView.clearOnScrollListeners()
        scrollListener = null
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        weatherView.setDrawable(!hidden)
    }

    override fun setSystemBarStyle() {
        ThemeManager
            .getInstance(requireContext())
            .weatherThemeDelegate
            .setSystemBarStyle(
                requireContext(),
                requireActivity().window,
                statusShader = scrollListener?.topOverlap == true,
                lightStatus = false,
                navigationShader = true,
                lightNavigation = false
            )
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        updateDayNightColors()
        updateViews()
    }

    // init.

    private fun initModel() {
        viewModel = ViewModelProvider(requireActivity())[MainActivityViewModel::class.java]
    }

    @SuppressLint("ClickableViewAccessibility", "NonConstantResourceId", "NotifyDataSetChanged")
    private fun initView() {
        ensureResourceProvider()

        weatherView.setGravitySensorEnabled(
            SettingsManager.getInstance(requireContext()).isGravitySensorEnabled
        )

        weatherView.setDoAnimate(
            isBackgroundAnimationEnabled()
        )

        binding.toolbar.setNavigationOnClickListener {
            callback?.onManageIconClicked()
        }
        binding.toolbar.inflateMenu(R.menu.activity_main)
        binding.toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_manage -> callback?.onManageIconClicked()
                R.id.action_settings -> callback?.onSettingsIconClicked()
            }
            true
        }

        binding.switchLayout.setOnSwitchListener(switchListener)
        binding.switchLayout.reset()
        binding.indicator.setSwitchView(binding.switchLayout)
        binding.indicator.setCurrentIndicatorColor(Color.WHITE)
        binding.indicator.setIndicatorColor(
            ColorUtils.setAlphaComponent(Color.WHITE, (0.5 * 255).toInt())
        )

        binding.refreshLayout.setOnRefreshListener {
            viewModel.updateWithUpdatingChecking(
                triggeredByUser = true,
                checkPermissions = true
            )
        }

        val listAnimationEnabled = SettingsManager
            .getInstance(requireContext())
            .isCardsFadeInEnabled
        val itemAnimationEnabled = SettingsManager
            .getInstance(requireContext())
            .isElementsAnimationEnabled
        adapter = MainAdapter(
            (requireActivity() as MainActivity),
            binding.recyclerView,
            weatherView,
            null,
            resourceProvider!!,
            listAnimationEnabled,
            itemAnimationEnabled
        )
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = MainLayoutManager()
        binding.recyclerView.addOnScrollListener(OnScrollListener().also { scrollListener = it })
        binding.recyclerView.setOnTouchListener(indicatorStateListener)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.currentLocation.collect {
                    // TODO: Dirty workaround to avoid recollecting on lifecycle resume
                    if (it?.location != lastCurrentLocation) {
                        updateViews(it?.location)
                        lastCurrentLocation = it?.location
                    }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.loading.collect {
                    setRefreshing(it)
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.indicator.collect {
                    binding.switchLayout.isEnabled = it.total > 1

                    if (binding.switchLayout.totalCount != it.total ||
                        binding.switchLayout.position != it.index
                    ) {
                        binding.switchLayout.setData(it.index, it.total)
                        binding.indicator.setSwitchView(binding.switchLayout)
                    }

                    binding.indicator.visibility = if (it.total > 1) View.VISIBLE else View.GONE
                }
            }
        }

        previewOffset.observe(viewLifecycleOwner) {
            binding.root.post {
                if (isFragmentViewCreated) {
                    updatePreviewSubviews()
                }
            }
        }
    }

    private fun updateDayNightColors() {
        binding.refreshLayout.setProgressBackgroundColorSchemeColor(
            MainThemeColorProvider.getColor(
                location = viewModel.currentLocation.value?.location,
                id = com.google.android.material.R.attr.colorSurface
            )
        )
    }

    // control.
    fun updateViews(location: Location? = viewModel.currentLocation.value?.location) {
        ensureResourceProvider()
        updateContentViews(location = location)
        binding.root.post {
            if (isFragmentViewCreated) {
                updatePreviewSubviews()
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility", "NotifyDataSetChanged")
    private fun updateContentViews(location: Location?) {
        recyclerViewAnimator?.let {
            it.cancel()
            recyclerViewAnimator = null
        }

        updateDayNightColors()

        binding.switchLayout.reset()

        if (location?.weather == null) {
            adapter!!.setNullWeather()
            adapter!!.notifyDataSetChanged()
            binding.recyclerView.setOnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_DOWN &&
                    !binding.refreshLayout.isRefreshing
                ) {
                    viewModel.updateWithUpdatingChecking(
                        triggeredByUser = true,
                        checkPermissions = true
                    )
                }
                false
            }
            return
        }

        binding.recyclerView.setOnTouchListener(null)

        val listAnimationEnabled = SettingsManager
            .getInstance(requireContext())
            .isCardsFadeInEnabled
        val itemAnimationEnabled = SettingsManager
            .getInstance(requireContext())
            .isElementsAnimationEnabled
        adapter!!.update(
            (requireActivity() as MainActivity),
            binding.recyclerView,
            weatherView,
            location,
            resourceProvider!!,
            listAnimationEnabled,
            itemAnimationEnabled
        )
        adapter!!.notifyDataSetChanged()

        scrollListener!!.postReset(binding.recyclerView)

        if (!listAnimationEnabled) {
            binding.recyclerView.alpha = 0f
            recyclerViewAnimator = MainModuleUtils.getEnterAnimator(
                binding.recyclerView,
                0
            ).apply {
                startDelay = 150
            }.also { it.start() }
        }
    }

    private fun ensureResourceProvider() {
        val iconProvider = SettingsManager
            .getInstance(requireContext())
            .iconProvider
        if (resourceProvider == null || resourceProvider!!.packageName != iconProvider) {
            resourceProvider = ResourcesProviderFactory.newInstance
        }
    }

    private fun updatePreviewSubviews() {
        val location = viewModel.getValidLocation(previewOffset.value)
        val daylight = WeatherViewController.isDaylight(location)
        val weatherKind = WeatherViewController.getWeatherKind(location)

        // Show "current position" icon:
        // - On the left on mobile because it might not be visible on small displays otherwise
        // - On the right on tablet because on the left it would be confused with the action icon
        binding.toolbar.title = (if (location?.isCurrentPosition == true && !requireContext().isTabletDevice) "⊙ " else "") + (location?.getPlace(requireContext()) ?: "") + (if (location?.isCurrentPosition == true && requireContext().isTabletDevice) " ⊙" else "")

        val textColor = ThemeManager.getInstance(requireContext())
            .weatherThemeDelegate
            .getHeaderTextColor(requireContext())
        binding.refreshTimeText.setTextColor(textColor)
        location?.weather?.base?.refreshTime?.let {
            binding.refreshTimeText.visibility = View.VISIBLE
            binding.refreshTimeText.setDate(it)
        } ?: run {
            binding.refreshTimeText.visibility = View.GONE
        }

        weatherView.setWeather(
            weatherKind,
            daylight,
            resourceProvider!!
        )
        binding.refreshLayout.setColorSchemeColors(
            ThemeManager
                .getInstance(requireContext())
                .weatherThemeDelegate
                .getThemeColors(
                    requireContext(),
                    weatherKind,
                    daylight
                )[0]
        )
    }

    private fun setRefreshing(b: Boolean) {
        binding.refreshLayout.post {
            if (isFragmentViewCreated) {
                binding.refreshLayout.isRefreshing = b
            }
        }
    }

    // interface.

    private fun setCallback(callback: Callback?) {
        this.callback = callback
    }

    // on touch listener.

    @SuppressLint("ClickableViewAccessibility")
    private val indicatorStateListener = View.OnTouchListener { _, event ->
        when (event.action) {
            MotionEvent.ACTION_MOVE ->
                binding.indicator.setDisplayState(true)

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL ->
                binding.indicator.setDisplayState(false)
        }
        false
    }

    // on swipe listener (swipe switch layout).

    private val switchListener = object : SwipeSwitchLayout.OnSwitchListener {

        override fun onSwiped(swipeDirection: Int, progress: Float) {
            binding.indicator.setDisplayState(progress != 0f)

            if (progress >= 1) {
                previewOffset.setValue(
                    if (swipeDirection == SwipeSwitchLayout.SWIPE_DIRECTION_LEFT) 1 else -1
                )
            } else {
                previewOffset.setValue(0)
            }
        }

        override fun onSwitched(swipeDirection: Int) {
            binding.indicator.setDisplayState(false)

            viewModel.offsetLocation(
                if (swipeDirection == SwipeSwitchLayout.SWIPE_DIRECTION_LEFT) 1 else -1
            )
            previewOffset.setValue(0)
        }
    }

    // on scroll changed listener.

    private inner class OnScrollListener : RecyclerView.OnScrollListener() {

        private var mTopChanged: Boolean? = null
        var topOverlap = false
        private var mFirstCardMarginTop = 0
        private var mScrollY = 0
        private var mLastAppBarTranslationY = 0f

        fun postReset(recyclerView: RecyclerView) {
            recyclerView.post {
                if (!isFragmentViewCreated) {
                    return@post
                }
                mTopChanged = null
                topOverlap = false
                mFirstCardMarginTop = 0
                mScrollY = 0
                mLastAppBarTranslationY = 0f
                onScrolled(recyclerView, 0, 0)
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            mFirstCardMarginTop = if (recyclerView.childCount > 0) {
                recyclerView.getChildAt(0).top
            } else -1

            mScrollY = recyclerView.computeVerticalScrollOffset()
            mLastAppBarTranslationY = binding.appBar.translationY
            weatherView.onScroll(mScrollY)

            adapter?.onScroll()

            // set translation y of toolbar.
            if (adapter != null) {
                if (adapter!!.headerTop == -1 || mScrollY < (adapter!!.headerTop - binding.appBar.measuredHeight)) {
                    // Keep app bar on top until we reach top of temperature
                    binding.appBar.translationY = 0f
                } else if (mScrollY < adapter!!.headerTop) {
                    // Make the app bar disappear when we reach top of temperature
                    binding.appBar.translationY = (
                        adapter!!.headerTop
                            - binding.appBar.measuredHeight
                            - mScrollY
                        ).toFloat()
                } else {
                    // Make appbar completely disappear in other cases
                    binding.appBar.translationY = -binding.appBar.measuredHeight.toFloat()
                }
            }

            // set system bar style.
            mTopChanged = if (mFirstCardMarginTop <= 0) {
                (binding.appBar.translationY != 0f) != (mLastAppBarTranslationY != 0f)
            } else true
            topOverlap = binding.appBar.translationY != 0f
            if (mTopChanged!!) {
                checkToSetSystemBarStyle()
            }
        }
    }
}
