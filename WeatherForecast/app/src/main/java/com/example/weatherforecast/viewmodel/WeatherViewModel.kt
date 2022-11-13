package com.example.weatherforecast.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherforecast.service.model.WeatherData
import com.example.weatherforecast.service.repository.WeatherRepository
import kotlinx.coroutines.launch
import java.io.IOException

class WeatherViewModel : ViewModel() {
    private var weatherRepository = WeatherRepository()
    var responseCode = MutableLiveData<Int>()
    var weatherLiveData =MutableLiveData<WeatherData>()

    fun getWeatherInfo(city: String){
        viewModelScope.launch {
            try {
                weatherLiveData.value = weatherRepository.getWeatherInfo(city).value
                responseCode.value = weatherRepository.responseCode.value
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

}