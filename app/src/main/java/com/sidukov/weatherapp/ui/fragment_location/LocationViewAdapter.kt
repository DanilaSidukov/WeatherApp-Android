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
    private var longListener: OnWeatherCardLongClickListener,
) : ListAdapter<EntityLocation, LocationViewAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.location_view_item, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("ResourceType")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = currentList[position]

        holder.itemView.setOnClickListener {
            item.checkBoolean = true
            clickListener.onWeatherCardClicked(item.name)
        }

        holder.itemView.setOnLongClickListener {
            if (currentList.size <= position) {
                longListener.onWeatherCardLongClickListener(currentList[position.minus(1)])
                notifyItemChanged(position.minus(1))
                notifyDataSetChanged()
            }
            else {
                longListener.onWeatherCardLongClickListener(item)
                notifyItemChanged(position)
                notifyDataSetChanged()
            }
            return@setOnLongClickListener true
        }

        holder.locationName?.text = item.name
        if (item.checkBoolean) {
            holder.location?.text = holder.location?.context?.getString(R.string.location)
            holder.imageCheck?.setImageResource(R.drawable.ic_check)
            holder.imageGps?.setImageResource(R.drawable.ic_gps)
        } else {
            holder.location?.visibility = View.GONE
            holder.imageCheck?.visibility = View.GONE
            holder.imageGps?.visibility = View.GONE
        }
        holder.currentTemperature?.text = item.temperature.toString()
        holder.currentDate?.text = item.date
        holder.imageWeather?.setImageResource(item.image)

//        val diffCallback = DiffCallback()
//        if (listLocation.size != 1){
//            diffCallback.areContentsTheSame(listLocation[position], listLocation[position.minus(1)])
//            diffCallback.areItemsTheSame(listLocation[position], listLocation[position.minus(1)])
//            val diffResult = DiffUtil.calculateDiff(diffCallback as DiffUtil.Callback)
//            diffResult.dispatchUpdatesTo(this)
//        }

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
        super.submitList(list)
        list?.let { listLocation = it }
    }


    class DiffCallback : DiffUtil.ItemCallback<EntityLocation>() {

        override fun areItemsTheSame(oldItem: EntityLocation, newItem: EntityLocation): Boolean {

            println("OLD = $oldItem, \nNEW = $newItem")

            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: EntityLocation, newItem: EntityLocation): Boolean {
            return oldItem == newItem
        }
    }

    fun deleteCurrentItem(position: EntityLocation) {
        val currentList = listLocation.toMutableList()
        if (currentList.size == 1){
            currentList.clear()
            submitList(currentList)
        } else {
            currentList.remove(position)
            submitList(currentList)
        }
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


