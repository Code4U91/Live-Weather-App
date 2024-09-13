package com.example.weatherapp.api

import com.example.weatherapp.api.forecast.WeatherForecastModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

// Implementation methods of the retrofit object
interface WeatherAPI {

     @GET("/v1/forecast.json")
     suspend fun getWeatherForecast(
         @Query("key") apiKey : String,
         @Query("q") city : String,
         @Query("days")days : Int
     ) : Response<WeatherForecastModel>

}