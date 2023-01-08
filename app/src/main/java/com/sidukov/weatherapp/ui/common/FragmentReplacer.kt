package com.sidukov.weatherapp.ui.common

interface FragmentReplacer {
    fun replace (position: Int, newFragment: BaseFragment, isNotify: Boolean = true)
    fun replaceDef(position: Int, isNotify: Boolean = true) : BaseFragment
}