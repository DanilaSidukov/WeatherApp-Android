package com.sidukov.weatherapp.ui.common

import android.util.ArrayMap
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.sidukov.weatherapp.ui.OnWeatherCardListener
import com.sidukov.weatherapp.ui.fragment_location.LocationFragment
import com.sidukov.weatherapp.ui.fragment_weather.WeatherFragment
import kotlinx.coroutines.currentCoroutineContext

class ViewPagerAdapter(container: FragmentActivity, private val listener: OnWeatherCardListener, private var city: String) : FragmentStateAdapter(container), FragmentReplacer {

    companion object {
        private var PAGE_COUNT = 1
    }

    private val mapOfFragment = ArrayMap<Int, BaseFragment>()

    override fun getItemCount() = PAGE_COUNT

    override fun createFragment(position: Int): Fragment {

        println("Create Fragment = $city")
        if (city != " ") {
            PAGE_COUNT = 2
            itemCount
            println("item count = $itemCount")
            return mapOfFragment[position] ?: replaceDef(position,city,false)
        } else {
            PAGE_COUNT = 1
            itemCount
            return mapOfFragment[position] ?: replaceDef(position, city, false)
        }

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

        city = location
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
        city = location
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