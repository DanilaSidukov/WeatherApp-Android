package com.sidukov.weatherapp.ui.fragment_weather

import android.annotation.SuppressLint
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

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: MiniCardViewHolder, position: Int) {
        // вот здесь можно извлекать из данных значения с помощью контекста, например:
        // holder.name.context.getString(...)
        holder.name.text = holder.name.context.getString(list[position].name)
        holder.information.text = list[position].information
//        if (list[position].progress > 70) {
//            holder.progressBar.secondaryProgress = R.color.progress_yellow
//        }
        holder.progressBar.progress = list[position].progress
        holder.image.setImageResource(list[position].image)
//"#EF9D4B"
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class MiniCardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.text_name_card_view)
        val information: TextView = itemView.findViewById(R.id.text_condition_card_view)
        var progressBar: ProgressBar = itemView.findViewById(R.id.arc_progress_bar)
        val image: ImageView = itemView.findViewById(R.id.image_card_view)
    }



    fun updateList(newList: List<WeatherDescription>) {
        list = newList
        notifyDataSetChanged()
    }

}