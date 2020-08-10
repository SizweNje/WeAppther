package com.sizwenje.weappather.model

import java.sql.Timestamp

public class ForeCastModel{
     var main: String? = null
     var description: String? = null
     var icon: String? = null


     var temp: String? = null
     var temp_min: String? = null
     var temp_max: String? = null
     var pressure: String? = null
     var humidity: String? = null
     var wind: String? = null
     var location: String? = null
     var direction: String? = null
     var datestamp : String? = null

    constructor(main: String,description:String,icon:String,temp :String,temp_min:String,temp_max:String,pressure:String,humidity:String,wind:String,location: String,direction:String, datestamp :String) {
        this.main = main
        this.description = description
        this.icon = icon


        this.temp = temp
        this.temp_min =temp_min
        this.temp_max = temp_max
        this.pressure = pressure
        this.humidity = humidity
        this.wind = wind
        this.location = location
        this.direction = direction
        this.datestamp = datestamp
    }

    constructor()
}

