package com.example.weatherapp.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.api.NetworkResponse
import com.example.weatherapp.api.forecast.WeatherForecastModel
import com.example.weatherapp.repository.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(private val weatherRepository: WeatherRepository) :
    ViewModel() {

    val weatherData: LiveData<NetworkResponse<WeatherForecastModel>>
        get() = weatherRepository.weatherData


    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading


    private val _coordinates = MutableLiveData<String>()
    val coordinates: LiveData<String> get() = _coordinates

    fun getWeatherData(city: String) = viewModelScope.launch {

        weatherRepository.fetchWeatherData(city)

    }

    fun getWeatherDataByCoordinates(lat: Double, lon: Double) {

        _coordinates.value = "$lat,$lon"
        getWeatherData(coordinates.value.toString())
    }

    fun setLoading(b: Boolean) {

        _isLoading.value = b
    }


}