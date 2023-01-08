package com.sidukov.weatherapp.ui.common

import android.util.ArrayMap
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.sidukov.weatherapp.ui.OnWeatherCardListener
import com.sidukov.weatherapp.ui.fragment_location.LocationFragment
import com.sidukov.weatherapp.ui.fragment_weather.WeatherFragment

class ViewPagerAdapter(container: FragmentActivity, private val listener: OnWeatherCardListener) : FragmentStateAdapter(container), FragmentReplacer {

    companion object {
        private const val PAGE_COUNT = 2
    }

    private val mapOfFragment = ArrayMap<Int, BaseFragment>()

    override fun getItemCount() = PAGE_COUNT

    override fun createFragment(position: Int): Fragment {
        return mapOfFragment[position] ?: replaceDef(position, false)
    }

    override fun containsItem(itemId: Long): Boolean {
        var isContains = false
        mapOfFragment.values.forEach {
            if (it.pageId == itemId) {
                isContains = true
                return@forEach
            }
        }
        return isContains
    }

    override fun replace(position: Int, newFragment: BaseFragment, isNotify: Boolean) {

        newFragment.setPageInfo(
            pagePosition = position,
            fragmentReplacer = this
        )

        mapOfFragment[position] = newFragment
        if (isNotify) notifyItemChanged(position)
    }

    override fun replaceDef(position: Int, isNotify: Boolean): BaseFragment {
        val fragment = when (position) {
            0 -> LocationFragment("Moscow", listener)
            1 -> WeatherFragment("Moscow")
            else -> throw IllegalStateException()
        }
        replace(position, fragment, isNotify)
        return fragment
    }

    override fun getItemId(position: Int) =
        mapOfFragment[position]?.pageId ?: super.getItemId(position)

}