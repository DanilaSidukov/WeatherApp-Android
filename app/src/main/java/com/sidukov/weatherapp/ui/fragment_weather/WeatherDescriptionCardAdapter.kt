package com.sidukov.weatherapp.ui.fragment_weather

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sidukov.weatherapp.R
import com.sidukov.weatherapp.domain.WeatherDescription

class WeatherDescriptionCardAdapter(private var list: List<WeatherDescription>) :
    RecyclerView.Adapter<WeatherDescriptionCardAdapter.MiniCardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MiniCardViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.mini_card_view_item, parent, false)
        return MiniCardViewHolder(view)
    }

    override fun onBindViewHolder(holder: MiniCardViewHolder, position: Int) {
        // вот здесь можно извлекать из данных значения с помощью контекста, например:
        // holder.name.context.getString(...)
        holder.name.text = holder.name.context.getString(list[position].name)
        holder.information.text = list[position].information
        holder.progressBar.progress = list[position].progressBar
        holder.image.setImageResource(list[position].image)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class MiniCardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.text_name_card_view)
        val information: TextView = itemView.findViewById(R.id.text_condition_card_view)
        var progressBar: ProgressBar = itemView.findViewById(R.id.progress_bar)
        val image: ImageView = itemView.findViewById(R.id.image_card_view)
    }

    fun updateList(newList: List<WeatherDescription>) {
        list = newList
        notifyDataSetChanged()
    }

}