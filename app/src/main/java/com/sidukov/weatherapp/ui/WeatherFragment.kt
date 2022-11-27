package com.sidukov.weatherapp.ui

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.sidukov.weatherapp.R
import com.sidukov.weatherapp.data.WeatherRepository
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper

class WeatherFragment : Fragment() {

    private lateinit var dailyWeatherRecycleView: RecyclerView

    //adapter привязывается к RecyclerView, он содержит в себе инфу об элементах в списке RecyclerView
    private val adapter = DailyWeatherAdapter(emptyList())

    //Объявляю о том, что будет vm
    private lateinit var weatherViewModel: WeatherViewModel

    private lateinit var animation: HeaderAnimationImage

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
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)

        //инициализация vm непосредственно
        weatherViewModel = WeatherViewModel(WeatherRepository(requireContext()))
        //запускается Корутина с помощью launch, scope.launch выполняется асинхронно относительно общего порядка выполнения кода
        //В collect мы указываем что делать с теми данными, которые придут. Выполняется collect каждый раз, когда в weatherList появляются новые данные
        dailyWeatherRecycleView = view.findViewById(R.id.recycler_view_weather)
        //привязываем adapter к RecycleView
        dailyWeatherRecycleView.adapter = adapter

        OverScrollDecoratorHelper.setUpOverScroll(
            dailyWeatherRecycleView,
            OverScrollDecoratorHelper.ORIENTATION_HORIZONTAL
        )

        dailyWeatherRecycleView.addItemDecoration(EmptyDividerItemDecoration())

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            weatherViewModel.weatherList.collect {
                adapter.updateList(it)
            }
        }

        val animatedImage: View = view.findViewById(R.id.imageSky)
        //Вызываю класс, который отвечает за анимацию заглавного изображения

        animatedImage.viewTreeObserver.addOnGlobalLayoutListener {
            if (!this::animation.isInitialized) {
                animation = HeaderAnimationImage(animatedImage)
            }
<<<<<<< Updated upstream
            animation.marginFlow = animatedImage.width
            if (animation.marginFlow == animation.startWidth){
                if (animation.checkRunning()){
                    animation.stopRunning()
                }
                animation.bigAnimationWidth(animation.startWidth)
                animation.executeAnimation()
            } else{
                if (animation.checkRunning()){
                    animation.stopRunning()
                }
                animation.smallAnimationWidth(animation.marginFlow)
                animation.executeAnimation()
            }
            animation.marginFlow = animatedImage.width
=======
>>>>>>> Stashed changes
        }
    }
}


