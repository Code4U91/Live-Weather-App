package com.example.weatherapp.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.weatherapp.api.Constant
import com.example.weatherapp.api.NetworkResponse
import com.example.weatherapp.api.WeatherAPI
import com.example.weatherapp.api.forecast.WeatherForecastModel
import javax.inject.Inject

class WeatherRepository @Inject constructor(private val weatherAPI: WeatherAPI) {


    private val _weatherData = MutableLiveData<NetworkResponse<WeatherForecastModel>>()
    val weatherData: LiveData<NetworkResponse<WeatherForecastModel>>
        get() = _weatherData


    suspend fun fetchWeatherData(city: String) {
        _weatherData.value = NetworkResponse.Loading

        try {

            val response = weatherAPI.getWeatherForecast(Constant.apiKey, city, 10)

            Log.i("RepositoryW", response.toString())
            if (response.isSuccessful && response.body() != null) {

                _weatherData.value = NetworkResponse.Success(response.body()!!)
            } else {
                _weatherData.value = NetworkResponse.Error("Failed to load data")
            }
        } catch (e: Exception) {
            _weatherData.value = NetworkResponse.Error("Failed to load data due to exception")
        }

    }

}