package com.sidukov.weatherapp.ui.fragment_weather

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.support.annotation.AttrRes
import android.support.annotation.ColorInt
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.drawable.toDrawable
import androidx.core.graphics.toColor
import androidx.core.graphics.toColorLong
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

        holder.progressBar.progressTintList = if (list[position].progress > 50)
            ColorStateList(
                states,
                intArrayOf(holder.image.context.getColorFromAttr(R.attr.progressYellow), 0)
            ) else ColorStateList(
            states,
            intArrayOf(holder.image.context.getColorFromAttr(R.attr.progressGreen), 0)
        )
        holder.progressBar.progress = list[position].progress
        holder.image.setImageResource(list[position].image)

    }

    private val states = arrayOf(
        intArrayOf(android.R.attr.state_enabled),
        intArrayOf(-android.R.attr.state_enabled),
        intArrayOf(-android.R.attr.state_checked),
        intArrayOf(android.R.attr.state_pressed)
    )

    override fun getItemCount(): Int {
        return list.size
    }

    class MiniCardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.item_name_card_view)
        val information: TextView = itemView.findViewById(R.id.item_condition_card_view)
        var progressBar: ProgressBar = itemView.findViewById(R.id.item_progress_bar)
        val image: ImageView = itemView.findViewById(R.id.item_image_card_view)
    }

    fun updateList(newList: List<WeatherDescription>) {
        list = newList
        notifyDataSetChanged()
    }
}

@ColorInt
fun Context.getColorFromAttr(@AttrRes attrColor: Int): Int {
    val typedArray = theme.obtainStyledAttributes(intArrayOf(attrColor))
    val textColor = typedArray.getColor(0, 0)
    typedArray.recycle()
    return textColor
}

