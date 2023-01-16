package com.sidukov.weatherapp.ui.fragment_weather

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Build
import android.support.annotation.ColorInt
import android.support.annotation.IntDef
import android.widget.AbsListView
import android.widget.EdgeEffect
import android.widget.HorizontalScrollView
import android.widget.ScrollView
import androidx.core.widget.EdgeEffectCompat
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.sidukov.weatherapp.BuildConfig
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.reflect.Field


/**
 * @author Eugen on 11. 2. 2016.
 */
@SuppressLint("PrivateApi")
class XpEdgeEffect private constructor() {
    @IntDef(ALWAYS, PRE_HONEYCOMB, PRE_KITKAT, PRE_LOLLIPOP)
    @Retention(RetentionPolicy.SOURCE)
    annotation class EdgeGlowColorApi

    init {
        throw AssertionError("No instances!")
    }

    companion object {
        private var CLASS_SCROLL_VIEW = ScrollView::class.java
        private var SCROLL_VIEW_FIELD_EDGE_GLOW_TOP: Field? = null
        private var SCROLL_VIEW_FIELD_EDGE_GLOW_BOTTOM: Field? = null
        private var CLASS_VIEW_PAGER = ViewPager::class.java
        private var VIEW_PAGER_FIELD_LEFT_EDGE: Field? = null
        private var VIEW_PAGER_FIELD_RIGHT_EDGE: Field? = null
        private var CLASS_HORIZONTAL_SCROLL_VIEW =
            HorizontalScrollView::class.java
        private var HORIZONTAL_SCROLL_VIEW_FIELD_EDGE_GLOW_LEFT: Field? = null
        private var HORIZONTAL_SCROLL_VIEW_FIELD_EDGE_GLOW_RIGHT: Field? = null
        private var CLASS_NESTED_SCROLL_VIEW = NestedScrollView::class.java
        private var NESTED_SCROLL_VIEW_FIELD_EDGE_GLOW_TOP: Field? = null
        private var NESTED_SCROLL_VIEW_FIELD_EDGE_GLOW_BOTTOM: Field? = null
        private var CLASS_RECYCLER_VIEW = RecyclerView::class.java
        private var RECYCLER_VIEW_FIELD_EDGE_GLOW_TOP: Field? = null
        private var RECYCLER_VIEW_FIELD_EDGE_GLOW_LEFT: Field? = null
        private var RECYCLER_VIEW_FIELD_EDGE_GLOW_RIGHT: Field? = null
        private var RECYCLER_VIEW_FIELD_EDGE_GLOW_BOTTOM: Field? = null
        private var CLASS_LIST_VIEW = AbsListView::class.java
        private var LIST_VIEW_FIELD_EDGE_GLOW_TOP: Field? = null
        private var LIST_VIEW_FIELD_EDGE_GLOW_BOTTOM: Field? = null
        private var EDGE_GLOW_FIELD_EDGE: Field? = null
        private var EDGE_GLOW_FIELD_GLOW: Field? = null
        private var EDGE_EFFECT_COMPAT_FIELD_EDGE_EFFECT: Field? = null

        init {
            var edgeGlowTop: Field? = null
            var edgeGlowBottom: Field? = null
            var edgeGlowLeft: Field? = null
            var edgeGlowRight: Field? = null
            for (f in CLASS_RECYCLER_VIEW.declaredFields) {
                when (f.name) {
                    "mTopGlow" -> {
                        f.isAccessible = true
                        edgeGlowTop = f
                    }
                    "mBottomGlow" -> {
                        f.isAccessible = true
                        edgeGlowBottom = f
                    }
                    "mLeftGlow" -> {
                        f.isAccessible = true
                        edgeGlowLeft = f
                    }
                    "mRightGlow" -> {
                        f.isAccessible = true
                        edgeGlowRight = f
                    }
                }
            }
            RECYCLER_VIEW_FIELD_EDGE_GLOW_TOP = edgeGlowTop
            RECYCLER_VIEW_FIELD_EDGE_GLOW_BOTTOM = edgeGlowBottom
            RECYCLER_VIEW_FIELD_EDGE_GLOW_LEFT = edgeGlowLeft
            RECYCLER_VIEW_FIELD_EDGE_GLOW_RIGHT = edgeGlowRight
            for (f in CLASS_VIEW_PAGER.declaredFields) {
                when (f.name) {
                    "mLeftEdge" -> {
                        f.isAccessible = true
                        edgeGlowLeft = f
                    }
                    "mRightEdge" -> {
                        f.isAccessible = true
                        edgeGlowRight = f
                    }
                }
            }
            VIEW_PAGER_FIELD_LEFT_EDGE = edgeGlowLeft
            VIEW_PAGER_FIELD_RIGHT_EDGE = edgeGlowRight
            for (f in CLASS_NESTED_SCROLL_VIEW.declaredFields) {
                when (f.name) {
                    "mEdgeGlowTop" -> {
                        f.isAccessible = true
                        edgeGlowTop = f
                    }
                    "mEdgeGlowBottom" -> {
                        f.isAccessible = true
                        edgeGlowBottom = f
                    }
                }
            }
            NESTED_SCROLL_VIEW_FIELD_EDGE_GLOW_TOP = edgeGlowTop
            NESTED_SCROLL_VIEW_FIELD_EDGE_GLOW_BOTTOM = edgeGlowBottom
            for (f in CLASS_SCROLL_VIEW.declaredFields) {
                when (f.name) {
                    "mEdgeGlowTop" -> {
                        f.isAccessible = true
                        edgeGlowTop = f
                    }
                    "mEdgeGlowBottom" -> {
                        f.isAccessible = true
                        edgeGlowBottom = f
                    }
                }
            }
            SCROLL_VIEW_FIELD_EDGE_GLOW_TOP = edgeGlowTop
            SCROLL_VIEW_FIELD_EDGE_GLOW_BOTTOM = edgeGlowBottom
            for (f in CLASS_HORIZONTAL_SCROLL_VIEW.declaredFields) {
                when (f.name) {
                    "mEdgeGlowLeft" -> {
                        f.isAccessible = true
                        edgeGlowLeft = f
                    }
                    "mEdgeGlowRight" -> {
                        f.isAccessible = true
                        edgeGlowRight = f
                    }
                }
            }
            HORIZONTAL_SCROLL_VIEW_FIELD_EDGE_GLOW_LEFT = edgeGlowLeft
            HORIZONTAL_SCROLL_VIEW_FIELD_EDGE_GLOW_RIGHT = edgeGlowRight
            for (f in CLASS_LIST_VIEW.declaredFields) {
                when (f.name) {
                    "mEdgeGlowTop" -> {
                        f.isAccessible = true
                        edgeGlowTop = f
                    }
                    "mEdgeGlowBottom" -> {
                        f.isAccessible = true
                        edgeGlowBottom = f
                    }
                }
            }
            LIST_VIEW_FIELD_EDGE_GLOW_TOP = edgeGlowTop
            LIST_VIEW_FIELD_EDGE_GLOW_BOTTOM = edgeGlowBottom
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                var edge: Field? = null
                var glow: Field? = null
                var cls: Class<*>? = null
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                    cls = EdgeEffect::class.java
                } else {
                    try {
                        cls = Class.forName("android.widget.EdgeGlow")
                    } catch (e: ClassNotFoundException) {
                        if (BuildConfig.DEBUG) e.printStackTrace()
                    }
                }
                if (cls != null) {
                    for (f in cls.declaredFields) {
                        when (f.name) {
                            "mEdge" -> {
                                f.isAccessible = true
                                edge = f
                            }
                            "mGlow" -> {
                                f.isAccessible = true
                                glow = f
                            }
                        }
                    }
                }
                EDGE_GLOW_FIELD_EDGE = edge
                EDGE_GLOW_FIELD_GLOW = glow
            } else {
                EDGE_GLOW_FIELD_EDGE = null
                EDGE_GLOW_FIELD_GLOW = null
            }
            var efc: Field? = null
            try {
                efc = EdgeEffectCompat::class.java.getDeclaredField("mEdgeEffect")
                efc.isAccessible = true
            } catch (e: NoSuchFieldException) {
                if (BuildConfig.DEBUG) e.printStackTrace()
            }
            EDGE_EFFECT_COMPAT_FIELD_EDGE_EFFECT = efc
        }

        const val ALWAYS = Int.MAX_VALUE

        /** Replace yellow glow in vanilla, blue glow on Samsung.  */
        const val PRE_HONEYCOMB = Build.VERSION_CODES.HONEYCOMB

        /** Replace Holo blue glow.  */
        const val PRE_KITKAT = Build.VERSION_CODES.KITKAT

        /** Replace Holo grey glow.  */
        const val PRE_LOLLIPOP = Build.VERSION_CODES.LOLLIPOP
        fun setColor(
            listView: AbsListView, @ColorInt color: Int, @EdgeGlowColorApi `when`: Int,
        ) {
            if (Build.VERSION.SDK_INT < `when`) {
                setColor(listView, color)
            }
        }

        fun setColor(listView: AbsListView, @ColorInt color: Int) {
            try {
                var ee: Any
                ee = LIST_VIEW_FIELD_EDGE_GLOW_TOP!![listView]
                setColor(ee, color)
                ee = LIST_VIEW_FIELD_EDGE_GLOW_BOTTOM!![listView]
                setColor(ee, color)
            } catch (ex: java.lang.Exception) {
                println("Error - $listView")
            }
        }

        fun setColor(
            scrollView: ScrollView, @ColorInt color: Int, @EdgeGlowColorApi `when`: Int,
        ) {
            if (Build.VERSION.SDK_INT < `when`) {
                setColor(scrollView, color)
            }
        }

        fun setColor(scrollView: ScrollView, @ColorInt color: Int) {
            try {
                var ee: Any
                ee = SCROLL_VIEW_FIELD_EDGE_GLOW_TOP!![scrollView]
                setColor(ee, color)
                ee = SCROLL_VIEW_FIELD_EDGE_GLOW_BOTTOM!![scrollView]
                setColor(ee, color)
            } catch (ex: java.lang.Exception) {
                println("Error setColor")
            }
        }

        fun setColor(
            scrollView: HorizontalScrollView, @ColorInt color: Int, @EdgeGlowColorApi `when`: Int,
        ) {
            if (Build.VERSION.SDK_INT < `when`) {
                setColor(scrollView, color)
            }
        }

        fun setColor(scrollView: HorizontalScrollView, @ColorInt color: Int) {
            try {
                var ee: Any
                ee = HORIZONTAL_SCROLL_VIEW_FIELD_EDGE_GLOW_LEFT!![scrollView]
                setColor(ee, color)
                ee = HORIZONTAL_SCROLL_VIEW_FIELD_EDGE_GLOW_RIGHT!![scrollView]
                setColor(ee, color)
            } catch (ex: java.lang.Exception) {
                println("Error setColor")
            }
        }

        fun setColor(
            viewPager: ViewPager, @ColorInt color: Int, @EdgeGlowColorApi `when`: Int,
        ) {
            if (Build.VERSION.SDK_INT < `when`) {
                setColor(viewPager, color)
            }
        }

        fun setColor(viewPager: ViewPager, @ColorInt color: Int) {
            try {
                var ee: Any
                ee = VIEW_PAGER_FIELD_LEFT_EDGE!![viewPager]
                setColor(ee, color)
                ee = VIEW_PAGER_FIELD_RIGHT_EDGE!![viewPager]
                setColor(ee, color)
            } catch (ex: java.lang.Exception) {
                println("Error setColor")
            }
        }

        fun setColor(
            scrollView: CustomScrollView, @ColorInt color: Int, @EdgeGlowColorApi `when`: Int,
        ) {
            if (Build.VERSION.SDK_INT < `when`) {
                setColor(scrollView, color)
            }
        }

        fun setColor(scrollView: CustomScrollView, @ColorInt color: Int) {
            try {
                var ee: Any
                ee = NESTED_SCROLL_VIEW_FIELD_EDGE_GLOW_TOP!![scrollView]
                setColor(ee, color)
                ee = NESTED_SCROLL_VIEW_FIELD_EDGE_GLOW_BOTTOM!![scrollView]
                setColor(ee, color)
            } catch (ex: java.lang.Exception) {
                println("Error setColor")
            }
        }

        fun setColor(
            scrollView: RecyclerView, @ColorInt color: Int, @EdgeGlowColorApi `when`: Int,
        ) {
            if (Build.VERSION.SDK_INT < `when`) {
                setColor(scrollView, color)
            }
        }

        fun setColor(scrollView: RecyclerView, @ColorInt color: Int) {
            try {
                var ee: Any
                ee = RECYCLER_VIEW_FIELD_EDGE_GLOW_TOP!![scrollView]
                setColor(ee, color)
                ee = RECYCLER_VIEW_FIELD_EDGE_GLOW_BOTTOM!![scrollView]
                setColor(ee, color)
                ee = RECYCLER_VIEW_FIELD_EDGE_GLOW_LEFT!![scrollView]
                setColor(ee, color)
                ee = RECYCLER_VIEW_FIELD_EDGE_GLOW_RIGHT!![scrollView]
                setColor(ee, color)
            } catch (ex: java.lang.Exception) {
                println("Error setColor")
            }
        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        private fun setColor(edgeEffect: Any, @ColorInt color: Int) {
            var edgeEffect = edgeEffect
            if (edgeEffect is EdgeEffectCompat) {
                // EdgeEffectCompat
                edgeEffect = try {
                    EDGE_EFFECT_COMPAT_FIELD_EDGE_EFFECT!![edgeEffect]
                } catch (e: IllegalAccessException) {
                    return
                }
            }
            if (edgeEffect == null) return
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                // EdgeGlow or old EdgeEffect
                try {
                    val mEdge = EDGE_GLOW_FIELD_EDGE!![edgeEffect] as Drawable
                    val mGlow = EDGE_GLOW_FIELD_GLOW!![edgeEffect] as Drawable
                    mEdge.setColorFilter(color, PorterDuff.Mode.SRC_IN)
                    mGlow.setColorFilter(color, PorterDuff.Mode.SRC_IN)
                    mEdge.callback = null // free up any references
                    mGlow.callback = null // free up any references
                } catch (ex: IllegalAccessException) {
                    ex.printStackTrace()
                }
            } else {
                // EdgeEffect
                (edgeEffect as EdgeEffect).color = color
            }
        }
    }
}
