package com.sidukov.weatherapp.ui.fragment_weather

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sidukov.weatherapp.R
import com.sidukov.weatherapp.domain.Weather
import kotlinx.coroutines.flow.merge
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.*

// потом потом, добавить DiffCallback или DiffUtilCallback, чтобы данные обновлялись тогда, когда нужно, это позволит избавиться от notifyDataSetChanged(),
// который добавляет определёную сложность
//<> - generic class, он работает с типом объекта, который к нему приходит, в нашем случае с DailyWeatherViewHolder
class DailyWeatherAdapter(private var list: List<Weather>) :
    RecyclerView.Adapter<DailyWeatherAdapter.DailyWeatherViewHolder>() {

    private val listOfDay: Map<Int, String> = mapOf (
        1 to "Monday",
        2 to "Tuesday",
        3 to "Wednesday",
        4 to "Thursday",
        5 to "Friday",
        6 to "Saturday",
        7 to "Sunday"
    )

    private var dayCounter = LocalDate.now().dayOfWeek.value

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyWeatherViewHolder {
        //создаётся view объект
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.daily_weather_item, parent, false)
        return DailyWeatherViewHolder(view)
    }

    //onBindViewHolder - привязывает данные к view элементам, объявленным в ViewHolder. Эти данные были отправлены в адаптер (обычно в виде списка)
    override fun onBindViewHolder(holder: DailyWeatherViewHolder, position: Int) {
        holder.textDateWeather.text = getDay(dayCounter)  //LocalDate.now().dayOfWeek.name
        holder.imageDateWeather.setImageResource(list[position].imageMain.second)
        holder.textDateTemperatureDailyWeather.text = list[position].temperature.toString()
        dayCounter += 1
        if (dayCounter > 7) dayCounter = 1
    }

    override fun getItemCount(): Int {
        return list.size
    }

    //holder - содержит view элементы, представленые в xml layoutе
    class DailyWeatherViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textDateWeather: TextView = itemView.findViewById(R.id.text_date_weather)
        val imageDateWeather: ImageView = itemView.findViewById(R.id.image_date_weather)
        val textDateTemperatureDailyWeather: TextView =
            itemView.findViewById(R.id.text_temperature_date_weather)
    }

    // эта функция принимает лист, который затем назначает внутренней переменной list
    fun updateList(newList: List<Weather>) {
        list = newList
        notifyDataSetChanged()
    }
    fun addList(newList: List<Weather>) : List<Weather>{
        return newList.plus(newList)
        notifyDataSetChanged()
    }

    fun getDay(counter: Int): String {
        for (i in listOfDay.keys){
            if (counter == i) {
                return listOfDay[counter].toString()
            }
        }
        return " "
    }
}