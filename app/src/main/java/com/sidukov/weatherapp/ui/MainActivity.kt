package com.sidukov.weatherapp.ui

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.sidukov.weatherapp.R
import com.sidukov.weatherapp.ui.common.ViewPagerAdapter
import com.sidukov.weatherapp.ui.fragment_location.LocationFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), OnWeatherCardListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val sharedPreferences: SharedPreferences =
            this.getSharedPreferences("city", Context.MODE_PRIVATE)

        val handler = Handler()
        view_pager_2.adapter = ViewPagerAdapter(this, this)

        LocationFragment(sharedPreferences.getString("city", "").toString(), this)

        if (sharedPreferences.getString("city", "").toString() == "") {
            println("CITY NOW SHARED = ${sharedPreferences.getString("city", "")}")
        } else {
            println("CITY NOW SHARED = ${sharedPreferences.getString("city", "")}")
        }

    }

    private fun openFragmentAppTopBar(f: Fragment, idHolder: Int) {

        supportFragmentManager
            .beginTransaction()
            .replace(idHolder, f).commit()
    }

    private fun openFragmentLocation(f: Fragment, idHolder: Int) {
        supportFragmentManager
            .beginTransaction()
            .replace(idHolder, f).commit()
    }

    override fun onWeatherCardClicked() {
         view_pager_2.setCurrentItem(1, true)
    }

}

interface OnWeatherCardListener {
    fun onWeatherCardClicked()
}