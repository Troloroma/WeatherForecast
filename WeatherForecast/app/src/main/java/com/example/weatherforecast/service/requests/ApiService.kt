package com.example.weatherforecast.service.requests

import com.example.weatherforecast.service.model.WeatherData
import retrofit2.Response
import retrofit2.http.*


interface ApiService {
    @GET("premium/v1/weather.ashx?format=json&lang=ru&num_of_days=7&fx=yes&mca=no&tp=24")
    suspend fun getWeatherInfo(@Query("q") city : String, @Query("key") key: String): Response<WeatherData>
}