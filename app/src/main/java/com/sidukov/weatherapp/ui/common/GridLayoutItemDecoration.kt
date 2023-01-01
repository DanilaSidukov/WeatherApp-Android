package com.sidukov.weatherapp.ui.common

import android.content.res.Resources
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.roundToInt

class GridLayoutItemDecoration(
    private val spaceSize: Int
) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        with(outRect) {
            parent.getChildAdapterPosition(view).let { position ->
                if (position % 2 == 0) {
                    left = (spaceSize * Resources.getSystem().displayMetrics.density).roundToInt()
                    right = ((spaceSize/2) * Resources.getSystem().displayMetrics.density).roundToInt()
                }
                if (position % 2 != 0) {
                    right = (spaceSize * Resources.getSystem().displayMetrics.density).roundToInt()
                    left = ((spaceSize/2) * Resources.getSystem().displayMetrics.density).roundToInt()
                }
                    top = (spaceSize * Resources.getSystem().displayMetrics.density).roundToInt()
                    bottom = (spaceSize * Resources.getSystem().displayMetrics.density).roundToInt()
            }
        }
    }
}

class GridLayoutItemDecorationLocation(
    private val spaceSize: Int
) : RecyclerView.ItemDecoration(){
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        with(outRect) {
            parent.getChildAdapterPosition(view).let { position ->
                if (position % 2 == 0) {
                    left = (spaceSize * Resources.getSystem().displayMetrics.density).roundToInt()
                    right = ((spaceSize/2) * Resources.getSystem().displayMetrics.density).roundToInt()
                }
                if (position % 2 != 0) {
                    right = (spaceSize * Resources.getSystem().displayMetrics.density).roundToInt()
                    left = ((spaceSize/2) * Resources.getSystem().displayMetrics.density).roundToInt()
                }
                top = (spaceSize/2 * Resources.getSystem().displayMetrics.density).roundToInt()
                bottom = (spaceSize/2 * Resources.getSystem().displayMetrics.density).roundToInt()
            }
        }
    }
}