package com.sidukov.weatherapp.ui.common

import android.util.ArrayMap
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.sidukov.weatherapp.ui.OnWeatherCardListener
import com.sidukov.weatherapp.ui.fragment_location.LocationFragment
import com.sidukov.weatherapp.ui.fragment_weather.WeatherFragment

class ViewPagerAdapter(container: FragmentActivity, private val listener: OnWeatherCardListener, private val city: String) : FragmentStateAdapter(container), FragmentReplacer {

    companion object {
        private const val PAGE_COUNT = 2
    }

    private val mapOfFragment = ArrayMap<Int, BaseFragment>()

    override fun getItemCount() = PAGE_COUNT

    override fun createFragment(position: Int): Fragment {

        println("Create Fragment = $city")

        return mapOfFragment[position] ?: replaceDef(position,city,false)
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


    override fun replace(position: Int, newFragment: BaseFragment,location: String, isNotify: Boolean) {

        newFragment.setPageInfo(
            pagePosition = position,
            fragmentReplacer = this,
            location = location
        )
        println("replace = $location")
        mapOfFragment[position] = newFragment
        if (isNotify) notifyItemChanged(position)
    }

    override fun replaceDef(position: Int,location: String, isNotify: Boolean): BaseFragment {
        val fragment = when (position) {
            0 -> LocationFragment(location, listener)
            1 -> WeatherFragment(location)
            else -> throw IllegalStateException()
        }
        println("replaceDef = $location")
        replace(position, fragment, location, isNotify)
        return fragment
    }

    override fun getItemId(position: Int) =
        mapOfFragment[position]?.pageId ?: super.getItemId(position)
}