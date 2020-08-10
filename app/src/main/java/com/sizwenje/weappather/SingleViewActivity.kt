package com.sizwenje.weappather

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.RequiresApi
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.current_weather
import kotlinx.android.synthetic.main.activity_main.date_weather
import kotlinx.android.synthetic.main.activity_main.description_weather
import kotlinx.android.synthetic.main.activity_main.humidity_weather
import kotlinx.android.synthetic.main.activity_main.location_result
import kotlinx.android.synthetic.main.activity_main.main_weather
import kotlinx.android.synthetic.main.activity_main.min_weather
import kotlinx.android.synthetic.main.activity_main.minmax_weather
import kotlinx.android.synthetic.main.activity_main.weather_icon
import kotlinx.android.synthetic.main.activity_single_view.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class SingleViewActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single_view)

        val intent = getIntent();
        val name= intent.getStringExtra("name")
        val description = intent.getStringExtra("desc")
        val temp = intent.getStringExtra("temp")
        val mintemp = intent.getStringExtra("mintemp")
        val maxtemp = intent.getStringExtra("maxtemp")
        val hum = intent.getStringExtra("hum")
        val wind = intent.getStringExtra("wind")
        val icon = intent.getStringExtra("icon")
        val day = intent.getStringExtra("day")

        location_result.setText(name)

        Picasso.with(baseContext)
            .load("https://openweathermap.org/img/wn/"+icon+"@4x.png")
            //.placeholder(R.drawable.wind)
            .error(R.drawable.ic_launcher_foreground)//optional
            .fit()       //optional
            //.centerCrop()                        //optional
            .into(weather_icon);


        //attach collected variables to components
        main_weather.setText(temp)
        description_weather.setText(description)

        //location_result.setText(location)


        //attach collected variables to components
        current_weather.setText(temp)
        minmax_weather.setText(mintemp+" | "+maxtemp   )
        /*min_weather.setText(temp_min)
        max_weather.setText(temp_max)
        pressure_weather.setText(pressure)*/
        humidity_weather.setText(hum)



        date_weather.setText(day)

        min_weather.setText(wind)

        close.setOnClickListener{
            finish()
        }

    }
}