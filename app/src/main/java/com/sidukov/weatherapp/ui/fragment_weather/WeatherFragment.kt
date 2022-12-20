package com.sidukov.weatherapp.ui.fragment_weather

import android.annotation.SuppressLint
import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sidukov.weatherapp.R
import com.sidukov.weatherapp.data.remote.WeatherRepository
import com.sidukov.weatherapp.data.remote.api.APIClient
import com.sidukov.weatherapp.domain.CurrentWeather
import com.sidukov.weatherapp.ui.common.GridLayoutItemDecoration
import com.sidukov.weatherapp.ui.fragment_location.LocationFragment
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class WeatherFragment : Fragment() {

    private lateinit var dailyWeatherRecyclerView: RecyclerView

    //adapter привязывается к RecyclerView, он содержит в себе инфу об элементах в списке RecyclerView
    private val adapterDailyWeather = DailyWeatherAdapter(emptyList())

    private val adapterTodayWeather = DailyWeatherAdapter(emptyList())

    private lateinit var todayWeatherRecyclerView: RecyclerView

    private lateinit var tempList: List<CurrentWeather>

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

    var image: Pair<Int, Int> = Pair(0, 0)
    var collectImage: Pair<Int, Int> = Pair(0, 0)

    private lateinit var buttonEdit: Button

    //Объявляю о том, что будет vm
    private lateinit var weatherViewModel: WeatherViewModel
    private lateinit var animation: HeaderImageAnimation

    private val adapterMiniCardView = WeatherDescriptionCardAdapter(emptyList())
    private lateinit var cardViewRecyclerView: RecyclerView

    //Создётся менеджер Корутины (scope), CoroutineScope возвращает Корутину, Dispatchers.Main - область, в которой будет работать Корутина
    //Main обозначает, что будет выполняться это в главном потоке (где рисуются элементы, запускаются анимации..)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_weather, container, false)
    }

    //Вызывается после создания фрагмента (View)
    @SuppressLint("ClickableViewAccessibility", "SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)

        //инициализация vm непосредственно
        weatherViewModel = WeatherViewModel(
            WeatherRepository(
                APIClient.weatherApiClient,
                Geocoder(requireContext()),
                APIClient.geoApiClient
            )
        )
        //запускается Корутина с помощью launch, scope.launch выполняется асинхронно относительно общего порядка выполнения кода
        //В collect мы указываем что делать с теми данными, которые придут. Выполняется collect каждый раз, когда в weatherList появляются новые данные
        dailyWeatherRecyclerView = view.findViewById(R.id.recycler_view_weather)
        dailyWeatherRecyclerView.adapter = adapterDailyWeather
        dailyWeatherRecyclerView.addItemDecoration(EmptyDividerItemDecoration())

        todayWeatherRecyclerView = view.findViewById(R.id.today_weather_recycler_view)
        todayWeatherRecyclerView.adapter = adapterTodayWeather
        todayWeatherRecyclerView.addItemDecoration(EmptyDividerItemDecoration())

        OverScrollDecoratorHelper.setUpOverScroll(
            dailyWeatherRecyclerView,
            OverScrollDecoratorHelper.ORIENTATION_HORIZONTAL
        )

        OverScrollDecoratorHelper.setUpOverScroll(
            todayWeatherRecyclerView,
            OverScrollDecoratorHelper.ORIENTATION_HORIZONTAL
        )

        cardViewRecyclerView = view.findViewById(R.id.card_view_recycler_view)
        cardViewRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        cardViewRecyclerView.adapter = adapterMiniCardView
        cardViewRecyclerView.addItemDecoration(GridLayoutItemDecoration(16))

        locationName = view.findViewById(R.id.text_location_weather)
        currentWeatherImageMain = view.findViewById(R.id.image_main)
        currentWeatherMovingImage = view.findViewById(R.id.image_secondary)
        currentTemperature = view.findViewById(R.id.text_current_temperature_weather)
        currentHumidity = view.findViewById(R.id.text_humidity_percent_condition_view)
        todayDescription = view.findViewById(R.id.weather_conditions_description)
        sunriseTime = view.findViewById(R.id.text_sunrise_time)
        sunsetTime = view.findViewById(R.id.text_sunset_time)
        arcProgressBar = view.findViewById(R.id.progress_bar)
        currentPrecipitation = view.findViewById(R.id.precipitation_data)
        currentDayTimeDigest = view.findViewById(R.id.text_daytime_condition_condition_view)
        currentNightTimeDigest = view.findViewById(R.id.text_nightitme_condition_condition_view)

        val index: Int = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH")).toInt()

        var flagWeatherList = false

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            weatherViewModel.todayStateFlow.collect { uiTodayState ->
                locationName.text = uiTodayState.date
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
                println("received = ${arcProgressBar.sunGetCondition}")
                currentPrecipitation.text = "${uiTodayState.precipitation.toInt()} %"
                currentDayTimeDigest.text = getString(uiTodayState.dayTimeDigest)
                currentNightTimeDigest.text = getString(uiTodayState.nightTimeDigest)
            }
        }
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            weatherViewModel.hourlyStateFlow.collect { uiHourlyState ->
                if (uiHourlyState.isEmpty()) return@collect
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
                if (uiAngleState.isNaN()) return@collect
                arcProgressBar.sunGetCondition = uiAngleState
            }
        }

        val animatedImage = currentWeatherMovingImage
        animatedImage.viewTreeObserver.addOnGlobalLayoutListener {
            if (!this::animation.isInitialized) {
                animation = HeaderImageAnimation(animatedImage)
            }
            animation.marginFlow = animatedImage.width
        }

        buttonEdit = view.findViewById(R.id.button_edit)
        buttonEdit.setOnClickListener {
            val locationFragment = LocationFragment()
            val transaction: FragmentTransaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.container, locationFragment)
            transaction.commit()
        }

        currentDate = view.findViewById(R.id.text_datetime_weather)
        currentDate.text = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM"))

    }
}


