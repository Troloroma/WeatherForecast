package com.example.weatherforecast.service.requests

import com.example.weatherforecast.service.model.WeatherData
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val BASE_URL = "https://api.worldweatheronline.com/"
private const val API_KEY = "0d29e282fb914f8bb29200426221311"
class RetrofitBuilder {
    private var apiService: ApiService

    init {
        val okHttpClient = OkHttpClient.Builder().addInterceptor(Interceptor { chain ->
            val newRequest: Request = chain.request().newBuilder()
                .build()
            chain.proceed(newRequest)
        }).build()

        fun getRetrofit(): Retrofit {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build()
        }
        apiService = getRetrofit().create(ApiService::class.java)
    }

    suspend fun getWeatherInfo(city: String) : Response<WeatherData>{
        return apiService.getWeatherInfo(city, API_KEY)
    }
}