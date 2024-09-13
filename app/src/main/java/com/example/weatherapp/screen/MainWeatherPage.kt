package com.example.weatherapp.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.weatherapp.api.NetworkResponse
import com.example.weatherapp.api.forecast.Forecast
import com.example.weatherapp.api.forecast.Forecastday
import com.example.weatherapp.api.forecast.Hour
import com.example.weatherapp.api.forecast.WeatherForecastModel
import com.example.weatherapp.viewmodels.WeatherViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale


// Main page compose

@Composable
fun MainWeatherPage() {

    val weatherViewModel: WeatherViewModel = hiltViewModel()
    val weatherResult = weatherViewModel.weatherData.observeAsState()

    val keyboardController = LocalSoftwareKeyboardController.current

    val scrollState = rememberScrollState()

    var city by rememberSaveable {  // Persists the state across configuration changes like screen ration etc.
        mutableStateOf("")
    }

    Box(modifier = Modifier
        .fillMaxSize()
        .padding(WindowInsets.systemBars.asPaddingValues()) // Adds padding based on system bars
    ) {


        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState)  // Enable vertical scrolling
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {

                OutlinedTextField(
                    modifier = Modifier
                        .weight(1f),
                    value = city,
                    onValueChange = {
                        city = it
                    },
                    label = {
                        Text(text = "Search for any location")
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Search
                    ),

                    keyboardActions = KeyboardActions(
                        onSearch = {

                            // perform the search action here
                            // Implementation for the search click button
                            weatherViewModel.getWeatherData(city)

                            keyboardController?.hide() // hide the keyboard after the search
                        }
                    )
                )

                IconButton(onClick = {

                    // Implementation for the search click button
                    weatherViewModel.getWeatherData(city)
                    keyboardController?.hide()

                }) {

                    Icon(
                        modifier = Modifier.size(30.dp),
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search Icon"

                    )

                }
            }


            when(val result = weatherResult.value)
            {
                is NetworkResponse.Error ->
                {
                    Text(text = result.message)
                }
                NetworkResponse.Loading ->
                {
                    CircularProgressIndicator()
                }
                is NetworkResponse.Success ->
                {
                    WeatherDetails(result.data)
                }
                null ->  {}
            }
        }
    }



}

// Below contains the compose components used for the main compose component responsible for the view of the page


@Composable
fun WeatherDetails(data : WeatherForecastModel)
{
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Row (
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.Bottom
        ){

            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = "Location icon",
                modifier = Modifier.size(40.dp)
            )
            Text(
                text = data.location.name,
                fontSize = 30.sp
            )
            Spacer(modifier = Modifier.width(8.dp))
            
            Text(
                text = data.location.country,
                fontSize = 18.sp,
                color = Color.Gray
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "${data.current.temp_c}°C",
            fontSize = 56.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        AsyncImage(
            modifier = Modifier.size(160.dp),
            model = "https:${data.current.condition.icon}".replace("64x64", "128x128"),
            contentDescription = "Condition icon"
        )
        Text(
            text = data.current.condition.text,
            fontSize = 45.sp,
            textAlign = TextAlign.Center,
            color = Color.Gray,
            modifier = Modifier.fillMaxWidth(),   // Ensure text takes up full width
            maxLines = 3,                         // Limit to 2 lines if needed
            overflow = TextOverflow.Ellipsis,      // Handle overflow with ellipsis (...)
            lineHeight = 64.sp  // Increase line height to add more space between lines
        )
        Spacer(modifier = Modifier.height(16.dp))

        CardView(data)

        Spacer(modifier = Modifier.height(16.dp))
        ForecastByHour(data.forecast.forecastday[0])

        Spacer(modifier = Modifier.height(16.dp))
        ContentSeparator()
        ForecastByDays(data.forecast)

    }

}

@Composable
fun CardView(data: WeatherForecastModel)
{
    Card {

        Column(
            modifier = Modifier.fillMaxWidth()
        ) {

            CartItem(k = "Humidity", val1 = data.current.humidity.toString()+ "%",
                k2 = "Wind Speed", val2 = data.current.wind_kph.toString()+ " Km/h"
            )


            CartItem(k = "UV", val1 = data.current.uv.toString(),
                k2 = "Precipitation", val2 = data.current.precip_mm.toString()+ " mm"
            )


            CartItem(k = "Local time", val1 = data.location.localtime.split(" ")[1],
                k2 = "Local date", val2 = data.location.localtime.split(" ")[0]
            )

        }
    }
}

@Composable
fun CartItem(k: String, k2 : String, val1: String, val2: String)
{
    // First row
    Row (
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround
    ){
        WeatherKeyVal(key = k, value = val1)
        WeatherKeyVal(key = k2, value = val2)
    }

}

@Composable
fun WeatherKeyVal(key : String, value : String)
{
    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text =  value, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text(text =  key, fontWeight = FontWeight.SemiBold, color = Color.Gray)
    }

}

@Composable
fun ForecastByHour(data: Forecastday)
{
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp)
    ) {
        Text(
            text = "Today",
            fontSize = 20.sp
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        LazyRow {
            items(data.hour){

                HourlyWeatherDisplay(weatherDataByHour = it)
            }
        }
    }
}

@Composable
fun HourlyWeatherDisplay(
    weatherDataByHour :  Hour
)
{

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {

        Text(
            text =  weatherDataByHour.time.split(" ")[1]
        )
        AsyncImage(
            modifier = Modifier.size(100.dp),
            model = "https:${weatherDataByHour.condition.icon}".replace("64x64", "128x128"),
            contentDescription = "Condition icon"
        )

        Text(text = "${weatherDataByHour.temp_c}°C",
            fontWeight = FontWeight.Bold
        )
    }
}



@Composable
fun ForecastByDays(data : Forecast)
{

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp)
    ) {
        Text(
            text = "3 day forecast",
            fontSize = 20.sp
        )

        Spacer(modifier = Modifier.height(16.dp))
        LazyRow {
            items(data.forecastday){

                DailyWeatherDisplay(weatherDataByDay = it)
            }
        }
    }
}

@Composable
fun DailyWeatherDisplay(
    weatherDataByDay :  Forecastday
)
{

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {

        val inputDate = LocalDate.parse(weatherDataByDay.date) // Parse the input date
        val outputFormat = DateTimeFormatter.ofPattern("EEE d", Locale.getDefault()) // Format for abbreviated day of the week
        val formattedDate = inputDate.format(outputFormat) // Format the date

        Text(
            text =   formattedDate
        )

        AsyncImage(
            modifier = Modifier.size(100.dp),
            model = "https:${weatherDataByDay.day.condition.icon}".replace("64x64", "128x128"),
            contentDescription = "Condition icon"
        )

        Text(text = "${weatherDataByDay.day.avgtemp_c}°C",
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun ContentSeparator() {
    HorizontalDivider(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),    // Add padding to create space around the divider
        thickness = 1.dp,                 // Set the thickness of the divider
        color = Color.Gray               // Set the color of the divider
    )
}