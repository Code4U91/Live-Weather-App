package com.example.weatherapp.api.forecast

data class WeatherForecastModel(
    val current: Current,
    val forecast: Forecast,
    val location: Location
)