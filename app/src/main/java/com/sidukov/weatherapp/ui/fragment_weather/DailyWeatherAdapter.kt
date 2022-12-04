package com.sidukov.weatherapp.ui.fragment_weather

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sidukov.weatherapp.R
import com.sidukov.weatherapp.data.Weather

// потом потом, добавить DiffCallback или DiffUtilCallback, чтобы данные обновлялись тогда, когда нужно, это позволит избавиться от notifyDataSetChanged(),
// который добавляет определёную сложность
//<> - generic class, он работает с типом объекта, который к нему приходит, в нашем случае с DailyWeatherViewHolder
class DailyWeatherAdapter(private var list: List<Weather>) : RecyclerView.Adapter<DailyWeatherAdapter.DailyWeatherViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyWeatherViewHolder {
        //создаётся view объект
        val view = LayoutInflater.from(parent.context).inflate(R.layout.daily_weather_item, parent, false)
        return DailyWeatherViewHolder(view)
    }
    //onBindViewHolder - привязывает данные к view элементам, объявленным в ViewHolder. Эти данные были отправлены в адаптер (обычно в виде списка)
    override fun onBindViewHolder(holder: DailyWeatherViewHolder, position: Int) {
        holder.textDailyWeather.text = list[position].day
        holder.imageDailyWeather.setImageResource(list[position].image)
        holder.textDayTemperatureDailyWeather.text = list[position].temperature.toString()
    }

    override fun getItemCount(): Int {
        return list.size
    }
    //holder - содержит view элементы, представленые в xml layoutе
    class DailyWeatherViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textDailyWeather: TextView = itemView.findViewById(R.id.text_day_daily_weather)
        val imageDailyWeather: ImageView = itemView.findViewById(R.id.image_daily_weather)
        val textDayTemperatureDailyWeather: TextView = itemView.findViewById(R.id.text_day_temperature_daily_weather)
    }

    fun updateList(newList: List<Weather>) {
        list = newList
        notifyDataSetChanged()
    }
}