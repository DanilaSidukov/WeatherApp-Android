package com.sidukov.weatherapp.ui.common

interface FragmentReplacer {
    fun replace (position: Int, newFragment: BaseFragment,location:String, isNotify: Boolean = true)
    fun replaceDef(position: Int,location: String, isNotify: Boolean = true) : BaseFragment
}