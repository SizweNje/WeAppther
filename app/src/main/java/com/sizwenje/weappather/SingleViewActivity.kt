package com.sizwenje.weappather

import android.annotation.SuppressLint
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.RequiresApi
import com.squareup.picasso.Picasso.*
import kotlinx.android.synthetic.main.activity_single_view.current_weather
import kotlinx.android.synthetic.main.activity_single_view.date_weather
import kotlinx.android.synthetic.main.activity_single_view.description_weather
import kotlinx.android.synthetic.main.activity_single_view.humidity_weather
import kotlinx.android.synthetic.main.activity_single_view.location_result
import kotlinx.android.synthetic.main.activity_single_view.main_weather
import kotlinx.android.synthetic.main.activity_single_view.min_weather
import kotlinx.android.synthetic.main.activity_single_view.weather_icon
import kotlinx.android.synthetic.main.activity_single_view.close
import kotlinx.android.synthetic.main.activity_single_view_night.*

class SingleViewActivity : AppCompatActivity() {
    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single_view_night)

        //fetch code that was sent through intent and load it int variables
        val intent = intent
        val name= intent.getStringExtra("name")
        val description = intent.getStringExtra("desc")
        val temp = intent.getStringExtra("temp")
        val mintemp = intent.getStringExtra("mintemp")
        val maxtemp = intent.getStringExtra("maxtemp")
        val hum = intent.getStringExtra("hum")
        val wind = intent.getStringExtra("wind")
        val icon = intent.getStringExtra("icon")
        val day = intent.getStringExtra("day")

        location_result.text = name

        with(baseContext)
            .load("https://openweathermap.org/img/wn/$icon@4x.png")
            //.placeholder(R.drawable.wind)
            .error(R.drawable.ic_launcher_foreground)//optional
            .fit()       //optional
            //.centerCrop()                        //optional
            .into(weather_icon)


        //attach collected variables to components
        main_weather.text = temp
        description_weather.text = description


        //attach collected variables to components
        current_weather.text = temp
        min_weather.text = mintemp
        max_weather.text = maxtemp+"\t\t"
        humidity_weather.text = hum
        date_weather.text = "$day midday"
        wind_weather.text = wind

        //close the current view and show the latest view
        close.setOnClickListener{
            finish()
        }

    }
}