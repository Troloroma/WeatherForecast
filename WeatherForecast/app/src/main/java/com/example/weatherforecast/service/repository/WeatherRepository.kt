package com.example.weatherforecast.service.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.weatherforecast.service.model.WeatherData
import com.example.weatherforecast.service.requests.RetrofitBuilder

class WeatherRepository {
    var retrofitBuilder = RetrofitBuilder()
    private val weatherData : MutableLiveData<WeatherData> by lazy{
        MutableLiveData<WeatherData>()
    }
    val responseCode: MutableLiveData<Int> by lazy{
        MutableLiveData<Int>()
    }

    suspend fun getWeatherInfo(city: String): MutableLiveData<WeatherData> {

        val response = retrofitBuilder.getWeatherInfo(city)
        if (response.isSuccessful) {
            weatherData.value = response.body()
            Log.d("WeatherRepository", response.body().toString())
        } else {
            Log.d("WeatherRepository", response.errorBody().toString())
        }
        responseCode.value = response.code()
        return weatherData
    }
}