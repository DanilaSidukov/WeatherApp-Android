package com.sidukov.weatherapp.ui.fragment_location

import android.annotation.SuppressLint
import android.opengl.Visibility
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sidukov.weatherapp.R
import com.sidukov.weatherapp.data.Location

class LocationViewAdapter(private var list: List<Location>): RecyclerView.Adapter<LocationViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.location_view_item, parent, false)

        return ViewHolder(view)
    }

    @SuppressLint("ResourceType")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val locationViewModel = list[position]
        holder.locationName?.text = list[position].nameLocation
        if (locationViewModel.location) {
            holder.location?.text = holder.location?.context?.getString(R.string.location)
        } else {
            holder.location?.visibility = View.GONE
        }
        if (locationViewModel.checkImage){
            holder.imageCheck?.setImageResource(R.drawable.ic_check)
        } else{
            holder.imageCheck?.visibility = View.GONE
        }
        if (locationViewModel.gpsImage){
            holder.imageGps?.setImageResource(R.drawable.ic_gps)
        } else{
            holder.imageGps?.visibility = View.GONE
        }
        holder.currentTemperature?.text = list[position].temperatureCurrent
        holder.date?.text = list[position].date
        holder.imageWeather?.setImageResource(locationViewModel.weatherImage)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(row: View): RecyclerView.ViewHolder(row) {
        val locationName : TextView? = row.findViewById(R.id.text_location_name)
        val location : TextView? = row.findViewById(R.id.text_location)
        val imageCheck: ImageView? = row.findViewById(R.id.image_check)
        val imageGps: ImageView? = row.findViewById(R.id.image_gps)
        val currentTemperature: TextView? = row.findViewById(R.id.current_temperature_location)
        val date: TextView? = row.findViewById(R.id.date_location)
        val imageWeather: ImageView? = row.findViewById(R.id.image_weather_location)
    }

    fun updateListLocation(newList: List<Location>){
        list = newList
        notifyDataSetChanged()
    }

}