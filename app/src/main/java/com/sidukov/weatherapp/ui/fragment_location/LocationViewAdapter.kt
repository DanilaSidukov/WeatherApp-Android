package com.sidukov.weatherapp.ui.fragment_location

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sidukov.weatherapp.R
import com.sidukov.weatherapp.data.local.EntityLocation

class LocationViewAdapter(
    private var listLocation: List<EntityLocation>,
    private var listener: OnWeatherCardClickListener,
    private val city: String
) : ListAdapter<EntityLocation, LocationViewAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.location_view_item, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("ResourceType")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = currentList[position]
        if (item.name == city) item.checkBoolean = true
        holder.itemView.setOnClickListener {
            item.checkBoolean = true
            listener.onWeatherCardClicked(item.name)
            currentList.indices.forEach{
                if (currentList[it] != item) currentList[it].checkBoolean = false
                println("AFTER $it - ${currentList[it].checkBoolean}")
            }
            notifyItemChanged(position)
        }

        holder.itemView.setOnLongClickListener {
            listener.onWeatherCardLongClickListener(item)
            return@setOnLongClickListener true
        }

        holder.locationName?.text = item.name

        println("Boolean")
        if (item.checkBoolean) {
            if (holder.location == null){
                holder.location?.text = holder.location?.context?.getString(R.string.location)
                holder.imageCheck?.setImageResource(R.drawable.ic_check)
                holder.imageGps?.setImageResource(R.drawable.ic_gps)
            }
        } else {
            holder.location?.visibility = View.GONE
            holder.imageCheck?.visibility = View.GONE
            holder.imageGps?.visibility = View.GONE
        }
        holder.currentTemperature?.text = item.temperature.toString()
        holder.currentDate?.text = item.date
        holder.imageWeather?.setImageResource(item.image)
    }

    override fun getItemCount(): Int {
        return currentList.size
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

    override fun submitList(list: MutableList<EntityLocation>?) {
        list?.let { listLocation = it }
        super.submitList(list)
    }

    class DiffCallback : DiffUtil.ItemCallback<EntityLocation>() {

        override fun areItemsTheSame(oldItem: EntityLocation, newItem: EntityLocation): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: EntityLocation, newItem: EntityLocation): Boolean {
            return oldItem == newItem
        }
    }

    fun deleteCurrentItem(position: EntityLocation) {
        val currentList = listLocation.toMutableList()
        if (currentList.size == 1) {
            currentList.clear()
            submitList(currentList)
        } else {
            currentList.remove(position)
            submitList(currentList)
        }
    }
}

interface OnWeatherCardClickListener {
    fun onWeatherCardClicked(locationName: String)
    fun onWeatherCardLongClickListener(locationItem: EntityLocation)
}

