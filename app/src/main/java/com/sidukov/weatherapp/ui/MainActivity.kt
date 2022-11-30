package com.sidukov.weatherapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.sidukov.weatherapp.*
import com.sidukov.weatherapp.data.Weather
import kotlinx.android.synthetic.main.fragment_weather.*

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