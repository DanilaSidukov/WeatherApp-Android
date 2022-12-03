package com.sidukov.weatherapp.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.sidukov.weatherapp.R
import com.sidukov.weatherapp.ui.fragment_weather.WeatherFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        openFragmentAppTopBar(WeatherFragment(), R.id.holder_app_top_bar)

    }
    //У активити есть поле supportFragmentManager, оно ислпьзуется для управления фрагментами, beginTransaction() - запускает фрагмент
    //Аргумент в replace (место для фрагмента(fragment layout), фрагмент который хотим туда поместить)
    private fun openFragmentAppTopBar(f: Fragment, idHolder: Int){
        supportFragmentManager
            .beginTransaction()
            .replace(idHolder, f).commit()
    }
}