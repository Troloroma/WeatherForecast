package com.example.weatherforecast.service.model

data class Data(
    val current_condition: List<CurrentCondition>,
    val request: List<Request>,
    val weather: List<Weather>
)