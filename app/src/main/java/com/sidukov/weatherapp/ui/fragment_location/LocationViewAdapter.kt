package com.sidukov.weatherapp.ui.fragment_location

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sidukov.weatherapp.R
import com.sidukov.weatherapp.data.local.EntityLocation

class LocationViewAdapter(
    private var listLocation: List<EntityLocation>,
    private var clickListener: OnWeatherCardClickListener,
    private var longListener: OnWeatherCardLongClickListener
) : ListAdapter<EntityLocation, LocationViewAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.location_view_item, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("ResourceType")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.itemView.setOnClickListener {
            clickListener.onWeatherCardClicked(listLocation[position].name)
        }

        holder.itemView.setOnLongClickListener {
            longListener.onWeatherCardLongClickListener(listLocation[position])
            return@setOnLongClickListener true
        }

        holder.locationName?.text = listLocation[position].name
        if (listLocation[position].checkBoolean) {
            holder.location?.text = holder.location?.context?.getString(R.string.location)
            holder.imageCheck?.setImageResource(R.drawable.ic_check)
            holder.imageGps?.setImageResource(R.drawable.ic_gps)
        } else {
            holder.location?.visibility = View.GONE
            holder.imageCheck?.visibility = View.GONE
            holder.imageGps?.visibility = View.GONE
        }
        holder.currentTemperature?.text = listLocation[position].temperature.toString()
        holder.currentDate?.text = listLocation[position].date
        holder.imageWeather?.setImageResource(listLocation[position].image)
    }

    override fun getItemCount(): Int {
        return listLocation.size
    }

    class ViewHolder(row: View) : RecyclerView.ViewHolder(row) {
        val locationName: TextView? = row.findViewById(R.id.text_location_name)
        val location: TextView? = row.findViewById(R.id.text_location)
        val imageCheck: ImageView? = row.findViewById(R.id.image_check)
        val imageGps: ImageView? = row.findViewById(R.id.image_gps)
        val currentTemperature: TextView? = row.findViewById(R.id.current_temperature_location)
        val currentDate: TextView? = row.findViewById(R.id.date_location)
        val imageWeather: ImageView? = row.findViewById(R.id.image_weather_location)
    }

    fun updateListLocation(newList: List<EntityLocation>) {
        listLocation = newList
        notifyDataSetChanged()
    }

    override fun submitList(list: MutableList<EntityLocation>?) {
        super.submitList(list)
        list?.let { listLocation = it}
    }

    class DiffCallback : DiffUtil.ItemCallback<EntityLocation>(){

        override fun areItemsTheSame(oldItem: EntityLocation, newItem: EntityLocation): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: EntityLocation, newItem: EntityLocation): Boolean {
            return oldItem.name == newItem.name
        }
    }

    fun deleteCurrentItem(position: Int) {
        val currentList = listLocation.toMutableList()
        println("Position = $position")
        println("SIZE- ${currentList.size}")
        currentList.removeAt(position)
        submitList(currentList)
        notifyItemRemoved(position)
    }
}

@FunctionalInterface
interface OnWeatherCardClickListener {
    fun onWeatherCardClicked(locationName: String)
}

@FunctionalInterface
interface OnWeatherCardLongClickListener {
    fun onWeatherCardLongClickListener(locationItem: EntityLocation)
}


