package com.sidukov.weatherapp.ui.fragment_weather

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sidukov.weatherapp.R
import com.sidukov.weatherapp.data.remote.WeatherRepository
import com.sidukov.weatherapp.data.remote.api.APIClient
import com.sidukov.weatherapp.ui.OnDayNightStateChanged
import com.sidukov.weatherapp.ui.WeatherApplication
import com.sidukov.weatherapp.ui.common.BaseFragment
import com.sidukov.weatherapp.ui.common.GridLayoutItemDecoration
import com.sidukov.weatherapp.ui.fragment_location.LocationFragment
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class WeatherFragment(val city: String) : BaseFragment(R.layout.fragment_weather),
    OnDayNightStateChanged {

    private lateinit var dailyWeatherRecyclerView: RecyclerView

    private val adapterDailyWeather = DailyWeatherAdapter(emptyList())
    private val adapterTodayWeather = DailyWeatherAdapter(emptyList())
    private val adapterMiniCardView = WeatherDescriptionCardAdapter(emptyList())

    private lateinit var todayWeatherRecyclerView: RecyclerView
    private lateinit var cardViewRecyclerView: RecyclerView

    private lateinit var locationName: TextView
    private lateinit var currentWeatherImageMain: ImageView
    private lateinit var currentWeatherMovingImage: ImageView
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

    private lateinit var weatherViewModel: WeatherViewModel
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

        if (LocalDateTime.now().hour in 22..23 || LocalDateTime.now().hour in 0..6) {
//            val fragmentWeather: LinearLayout = view.findViewById(R.id.weather_fragment)
//            val motionFragmentWeather: MotionLayout = view.findViewById(R.id.motion_appTopBar)
//            fragmentWeather.rootView.setBackgroundColor(Color.parseColor("#808080"))
//            motionFragmentWeather.backgroundTintList = context?.let { ColorStateList.valueOf(it.getColorFromAttr(R.attr.nightTimeItemBackground))}
//        //(states, intArrayOf(motionFragmentWeather.context.getColorFromAttr(R.attr.nightTimeItemBackground), 0))
        } else {
//            val fragmentWeather: LinearLayout = view.findViewById(R.id.weather_fragment)
//            val motionFragmentWeather: MotionLayout = view.findViewById(R.id.motion_appTopBar)
//            fragmentWeather.rootView.setBackgroundColor(Color.parseColor("#F6F6F6"))
//            motionFragmentWeather.backgroundTintList = ColorStateList.valueOf(R.color.general_background)
//        // (states, intArrayOf(motionFragmentWeather.context.getColorFromAttr(R.attr.screenBackground), 0))
        }

        if (LocalDateTime.now().hour in 22.. 23 || LocalDateTime.now().hour in 0..6) AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        else AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        weatherViewModel = WeatherViewModel(
            WeatherRepository(
                APIClient.weatherApiClient,
                APIClient.geoApiClient,
                APIClient.aqiApiClient,
                WeatherApplication.database.daoLocation(),
                requireContext()
            ),
            city
        )

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
        currentWeatherImageMain = view.findViewById(R.id.image_main)
        currentWeatherMovingImage = view.findViewById(R.id.image_secondary)
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

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            weatherViewModel.todayStateFlow.collect { uiTodayState ->
                locationName.text = uiTodayState.date?: "Unknown"
                if (uiTodayState.imageMain.first == uiTodayState.imageMain.second) {
                    currentWeatherImageMain.setImageResource(uiTodayState.imageMain.second)
                    currentWeatherMovingImage.setImageResource(0)
                } else {
                    currentWeatherImageMain.setImageResource(uiTodayState.imageMain.first)
                    currentWeatherMovingImage.setImageResource(uiTodayState.imageMain.second)
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

        val animatedImage = currentWeatherMovingImage
        animatedImage.viewTreeObserver.addOnGlobalLayoutListener {
            if (!this::animation.isInitialized) {
                animation = HeaderImageAnimation(animatedImage)
            }
            animation.marginFlow = animatedImage.width
        }

        currentDate = view.findViewById(R.id.text_datetime_weather)
        currentDate.text = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM"))

    }

//    private val states = arrayOf(
//        intArrayOf(android.R.attr.state_enabled),
//        intArrayOf(-android.R.attr.state_enabled),
//        intArrayOf(-android.R.attr.state_checked),
//        intArrayOf(android.R.attr.state_pressed)
//    )

    override fun onDayNightApplied(state: Int) {
        if (state == OnDayNightStateChanged.DAY) AppCompatDelegate.setDefaultNightMode(
            AppCompatDelegate.MODE_NIGHT_NO)
        else AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
    }

}
