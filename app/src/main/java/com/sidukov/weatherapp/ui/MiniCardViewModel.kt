package com.sidukov.weatherapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sidukov.weatherapp.data.WeatherDescription
import com.sidukov.weatherapp.data.MiniCardViewRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

// посмотреть в интернете, что такое вообще ViewModel, посмотреть на какой-нибудь проект, где используется MVVM-архитектура
// я рекомендую переместить всё в WeatherViewModel, но поступай как хочешь
class MiniCardViewModel(private val repository: MiniCardViewRepository
    ): ViewModel(){

        private var _cardViewList = MutableStateFlow<List<WeatherDescription>>(mutableListOf())

        val cardViewList = _cardViewList.asSharedFlow()

        init {
            viewModelScope.launch {
                _cardViewList.value = repository.getCardView()
            }
        }

}