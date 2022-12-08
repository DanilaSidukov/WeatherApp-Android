package com.sidukov.weatherapp.ui.fragment_weather

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sidukov.weatherapp.R
import com.sidukov.weatherapp.data.remote.WeatherRepository
import com.sidukov.weatherapp.data.remote.api.APIClient
import com.sidukov.weatherapp.ui.common.GridLayoutItemDecoration
import com.sidukov.weatherapp.ui.fragment_location.LocationFragment
import kotlinx.android.synthetic.main.fragment_weather.view.*
import kotlinx.android.synthetic.main.mini_card_view_item.*
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class WeatherFragment : Fragment() {

    private lateinit var dailyWeatherRecyclerView: RecyclerView
    //adapter привязывается к RecyclerView, он содержит в себе инфу об элементах в списке RecyclerView
    private val adapterDateWeather = DailyWeatherAdapter(emptyList())

    private lateinit var todayWeatherRecyclerView: RecyclerView

    private lateinit var locationName: TextView
    private lateinit var currentWeatherImage: ImageView
    private lateinit var currentDate: TextView
    private lateinit var currentTemperature: TextView
    private lateinit var currentHumidity: TextView

    private lateinit var buttonEdit: Button

    //Объявляю о том, что будет vm
    private lateinit var weatherViewModel: WeatherViewModel
    private lateinit var animation: HeaderImageAnimation
    private lateinit var nestedScrollView: CustomScrollView

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
    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)

        //инициализация vm непосредственно
        weatherViewModel = WeatherViewModel(
            WeatherRepository(APIClient.weatherApiClient)
        )
        //запускается Корутина с помощью launch, scope.launch выполняется асинхронно относительно общего порядка выполнения кода
        //В collect мы указываем что делать с теми данными, которые придут. Выполняется collect каждый раз, когда в weatherList появляются новые данные
        dailyWeatherRecyclerView = view.findViewById(R.id.recycler_view_weather)
        //привязываем adapter к RecycleView
        dailyWeatherRecyclerView.adapter = adapterDateWeather
        dailyWeatherRecyclerView.addItemDecoration(EmptyDividerItemDecoration())

        adapterDateWeather.updateList(weatherViewModel.uiStateFlow.value)
        textView.text = weatherViewModel.uiStateFlow.value.weatherList[0].date

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
        currentWeatherImage = view.findViewById(R.id.image_main)
        currentTemperature = view.findViewById(R.id.text_current_temperature_weather)
        currentHumidity = view.findViewById(R.id.text_humidity_percent_condition_view)

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            weatherViewModel.uiStateFlow.collect {
                adapterDateWeather.updateList(it.weatherList)
                locationName.text = weatherViewModel.uiStateFlow.value.weatherList[0].date
                currentWeatherImage.setImageResource(weatherViewModel.uiStateFlow.value.weatherList[0].image)
                currentTemperature.text = weatherViewModel.uiStateFlow.value.weatherList[0].temperature.toString()
                currentHumidity.text = weatherViewModel.uiStateFlow.value.weatherList[0].humidity.toString()
            }
        }
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            weatherViewModel.cardViewList.collect {
                adapterMiniCardView.updateList(it)
            }
        }

        val animatedImage: View = view.findViewById(R.id.image_secondary)
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


