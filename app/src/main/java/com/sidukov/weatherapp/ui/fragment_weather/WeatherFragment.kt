package com.sidukov.weatherapp.ui.fragment_weather

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.sidukov.weatherapp.R
import com.sidukov.weatherapp.di.injectViewModel
import com.sidukov.weatherapp.ui.MainViewModel
import com.sidukov.weatherapp.ui.OnDayNightStateChanged
import com.sidukov.weatherapp.ui.WeatherApplication
import com.sidukov.weatherapp.ui.common.BaseFragment
import com.sidukov.weatherapp.ui.common.GridLayoutItemDecoration
import com.sidukov.weatherapp.ui.fragment_location.LocationViewModel
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper
import java.lang.annotation.Inherited
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject


class WeatherFragment() : BaseFragment(R.layout.fragment_weather),
    OnDayNightStateChanged {

    private lateinit var dailyWeatherRecyclerView: RecyclerView

    private val adapterDailyWeather = DailyWeatherAdapter(emptyList())
    private val adapterTodayWeather = DailyWeatherAdapter(emptyList())
    private val adapterMiniCardView = WeatherDescriptionCardAdapter(emptyList())

    private lateinit var todayWeatherRecyclerView: RecyclerView
    private lateinit var cardViewRecyclerView: RecyclerView

    private lateinit var locationName: TextView
    private lateinit var currentWeatherImage: LottieAnimationView
    private lateinit var currentDate: TextView
    private lateinit var currentTemperature: TextView
    private lateinit var currentHumidity: TextView
    private lateinit var todayDescription: TextView
    private lateinit var sunsetTime: TextView
    private lateinit var sunriseTime: TextView
    private lateinit var arcProgressBar: ArcProgressBar
    private lateinit var currentPrecipitation: TextView
    private lateinit var currentDayTimeDigest: TextView
    private lateinit var currentNightTimeDigest: TextView
    private lateinit var currentAQI: TextView


    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    lateinit var weatherViewModel: WeatherViewModel

    private lateinit var animation: HeaderImageAnimation

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_weather, container, false)
    }

    @SuppressLint("ClickableViewAccessibility", "SetTextI18n", "ResourceAsColor")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)

        WeatherApplication.appComponent.inject(this)

        weatherViewModel = injectViewModel(viewModelFactory)



        dailyWeatherRecyclerView = view.findViewById(R.id.recycler_view_weather)
        dailyWeatherRecyclerView.adapter = adapterDailyWeather
        dailyWeatherRecyclerView.addItemDecoration(EmptyDividerItemDecoration())

        todayWeatherRecyclerView = view.findViewById(R.id.today_weather_recycler_view)
        todayWeatherRecyclerView.adapter = adapterTodayWeather
        todayWeatherRecyclerView.addItemDecoration(EmptyDividerItemDecoration())

        cardViewRecyclerView = view.findViewById(R.id.card_view_recycler_view)
        cardViewRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        cardViewRecyclerView.adapter = adapterMiniCardView
        cardViewRecyclerView.addItemDecoration(GridLayoutItemDecoration(16))


        OverScrollDecoratorHelper.setUpOverScroll(
            dailyWeatherRecyclerView,
            OverScrollDecoratorHelper.ORIENTATION_HORIZONTAL
        )

        OverScrollDecoratorHelper.setUpOverScroll(
            todayWeatherRecyclerView,
            OverScrollDecoratorHelper.ORIENTATION_HORIZONTAL
        )

        locationName = view.findViewById(R.id.text_location_weather)
        currentWeatherImage = view.findViewById(R.id.raw_animation)
        currentTemperature = view.findViewById(R.id.text_current_temperature_weather)
        currentHumidity = view.findViewById(R.id.text_humidity_percent_condition_view)
        todayDescription = view.findViewById(R.id.weather_conditions_description)
        sunriseTime = view.findViewById(R.id.text_sunrise_time)
        sunsetTime = view.findViewById(R.id.text_sunset_time)
        arcProgressBar = view.findViewById(R.id.arc_progress_bar)
        currentPrecipitation = view.findViewById(R.id.precipitation_data)
        currentDayTimeDigest = view.findViewById(R.id.text_daytime_condition_condition_view)
        currentNightTimeDigest = view.findViewById(R.id.text_nightitme_condition_condition_view)
        currentAQI = view.findViewById(R.id.aqi_data_today_digest)

        //currentWeatherImage.imageAssetsFolder = null
        currentWeatherImage.setAnimation(R.raw.sun_and_sky_raw)

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            weatherViewModel.todayStateFlow.collect { uiTodayState ->
                locationName.text = uiTodayState.date?: "Unknown"
                when (uiTodayState.imageMain) {
                    R.drawable.ic_sun -> {
                        println("im here")
                        currentWeatherImage.setAnimation(R.raw.sun_and_sky_raw)
                        currentWeatherImage.playAnimation()
                    }
                    R.drawable.ic_sky_with_sun_light -> {
                        println("no, im here")
                        currentWeatherImage.setAnimation(R.raw.sun_and_sky_raw)
                        currentWeatherImage.playAnimation()
                    }
                    R.drawable.ic_sky_rainy_light -> {
                        currentWeatherImage.setAnimation(R.raw.light_sky_drop_raw)
                        currentWeatherImage.playAnimation()
                    }
                    R.drawable.ic_snowflake -> {
                        currentWeatherImage.setAnimation(R.raw.snowflake_raw)
                        currentWeatherImage.playAnimation()
                    }
                    R.drawable.ic_sky_snow_light -> {
                        currentWeatherImage.setAnimation(R.raw.light_sky_drop_snow_raw)
                        currentWeatherImage.playAnimation()
                    }
                    R.drawable.ic_sky_rainy_dark -> {
                        currentWeatherImage.setAnimation(R.raw.dark_sky_drop_raw)
                        currentWeatherImage.playAnimation()
                    }
                }
                currentTemperature.text = uiTodayState.temperature.toString()
                currentHumidity.text = "${uiTodayState.humidity} %"
                todayDescription.text = getString(uiTodayState.description)
                currentPrecipitation.text = "${uiTodayState.precipitation.toInt()} %"
                currentDayTimeDigest.text = getString(uiTodayState.dayTimeDigest)
                currentNightTimeDigest.text = getString(uiTodayState.nightTimeDigest)
                currentAQI.text = getString(uiTodayState.currentAQI)
            }
        }
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            weatherViewModel.hourlyStateFlow.collect { uiHourlyState ->
//                if (uiHourlyState.isEmpty()) return@collect
                adapterTodayWeather.updateList(uiHourlyState)
            }
        }
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            weatherViewModel.dailyStateFlow.collect { uiDailyState ->
                if (uiDailyState.isEmpty()) return@collect
                adapterDailyWeather.updateList(uiDailyState)
                sunriseTime.text = uiDailyState[0].sunrise
                sunsetTime.text = uiDailyState[0].sunset
            }
        }
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            weatherViewModel.angleStateFlow.collect{ uiAngleState ->
//                if (uiAngleState.isNaN()) return@collect
                arcProgressBar.sunGetCondition = uiAngleState
            }
        }
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            weatherViewModel.todayCardViewDescription.collect{ uiWeatherDescription ->
                if (uiWeatherDescription.isEmpty()) return@collect
                uiWeatherDescription[0].information = getString(uiWeatherDescription[0].information.toInt())
                adapterMiniCardView.updateList(uiWeatherDescription)
            }
        }

//        val animatedImage = currentWeatherMovingImage
//        animatedImage.viewTreeObserver.addOnGlobalLayoutListener {
//            if (!this::animation.isInitialized) {
//                animation = HeaderImageAnimation(animatedImage)
//            }
//            animation.marginFlow = animatedImage.width
//        }

        currentDate = view.findViewById(R.id.text_datetime_weather)
        currentDate.text = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM"))

    }

    override fun onDayNightApplied(state: Int) {
        if (state == OnDayNightStateChanged.DAY) AppCompatDelegate.setDefaultNightMode(
            AppCompatDelegate.MODE_NIGHT_NO)
        else AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
    }

}
