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
import com.sidukov.weatherapp.domain.Weather
import com.sidukov.weatherapp.ui.common.GridLayoutItemDecoration
import com.sidukov.weatherapp.ui.fragment_location.LocationFragment
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class WeatherFragment : Fragment() {

    private lateinit var dailyWeatherRecyclerView: RecyclerView

    //adapter привязывается к RecyclerView, он содержит в себе инфу об элементах в списке RecyclerView
    private val adapterDateWeather = DailyWeatherAdapter(emptyList())

    private lateinit var todayWeatherRecyclerView: RecyclerView

    private lateinit var locationName: TextView
    private lateinit var currentWeatherImageMain: ImageView
    private lateinit var currentWeatherMovingImage: ImageView
    private lateinit var currentDate: TextView
    private lateinit var currentTemperature: TextView
    private lateinit var currentHumidity: TextView
    private lateinit var todayDescription: TextView

    var image: Pair<Int, Int> = Pair(0, 0)

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
        //Возвращает View всего xml(fragment_weather)
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
        //привязываем adapter к RecycleView
        dailyWeatherRecyclerView.adapter = adapterDateWeather
        dailyWeatherRecyclerView.addItemDecoration(EmptyDividerItemDecoration())

        todayWeatherRecyclerView = view.findViewById(R.id.today_weather_recycler_view)
        todayWeatherRecyclerView.adapter = adapterDateWeather
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

        val index: Int = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)

        val transparentImage = R.drawable.ic_sky_with_sun_light

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            weatherViewModel.uiStateFlow.collect { uiState ->
                if (uiState.weatherList.isEmpty()) return@collect
                adapterDateWeather.updateList(uiState.weatherList)
                locationName.text = uiState.weatherList[0].date
                if (uiState.weatherList[0].imageMain.first == uiState.weatherList[0].imageMain.second) {
                    currentWeatherImageMain.setImageResource(uiState.weatherList[0].imageMain.second)
                    currentWeatherMovingImage.setImageResource(0)
                } else {
                    currentWeatherImageMain.setImageResource(uiState.weatherList[0].imageMain.first)
                    currentWeatherMovingImage.setImageResource(uiState.weatherList[0].imageMain.second)
                }
                currentTemperature.text = uiState.weatherList[0].temperature.toString()
                currentHumidity.text = "${uiState.weatherList[0].humidity} %"
                todayDescription.text = getString(uiState.weatherList[0].description)

                (index..23).map { index ->
                    println("IT IS = F L A G = $index")
                    val hour = if (index < 10) "0$index"
                    else index.toString()

                    if (uiState.weatherList[0].imageMain.first == uiState.weatherList[0].imageMain.second) {
                        image.copy(uiState.weatherList[0].imageMain.second, 0)
                    } else {
                        image.copy(first = 0, second = transparentImage)
                    }

                    val hourlyItem = Weather(
                        hour,
                        image,
                        uiState.weatherList[index].temperature,
                        humidity = 0,
                        description = 0
                    )
                    adapterDateWeather.addList(listOf(hourlyItem))
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
//            weatherViewModel.cardViewList.collect {
//                adapterMiniCardView.updateList(it)
//            }
        }

        val animatedImage = currentWeatherMovingImage
        //Вызываю класс, который отвечает за анимацию заглавного изображения
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


