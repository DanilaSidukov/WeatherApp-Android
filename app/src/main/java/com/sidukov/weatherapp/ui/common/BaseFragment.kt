package com.sidukov.weatherapp.ui.common

import android.icu.text.Transliterator.Position
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlin.random.Random

abstract class BaseFragment(private val layoutId: Int): Fragment() {

    val pageId = Random.nextLong(2021, 2021*3)
    var pagePosition = -1
    protected lateinit var fragmentReplacer: FragmentReplacer
    protected lateinit var location: String


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(layoutId, container, false)
    }

    fun setPageInfo(pagePosition: Int, fragmentReplacer: FragmentReplacer, location: String){
        this.pagePosition = pagePosition
        this.fragmentReplacer = fragmentReplacer
        this.location = location
    }

}