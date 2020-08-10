package com.sizwenje.weappather

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.DisplayMetrics
import android.util.Log
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.sizwenje.weappather.adapter.ForeCastAdapter
import com.sizwenje.weappather.model.ForeCastModel
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.text.DecimalFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

private  const val PERMISSION_REQUEST =10;

class MainActivityc : AppCompatActivity() {

    lateinit var locationManager: LocationManager
    private var GPS = false
    private var Network = false
    private  var GPSLocation :Location? = null
    private  var NetworkLocation :Location? = null

    lateinit var listView_details: ListView
    var arrayList_details:ArrayList<ForeCastModel> = ArrayList();




    //Declare permissions that will be used
    private var permissions = arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION)

    private val client = OkHttpClient()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        disableView()

        listView_details = findViewById<ListView>(R.id.listView) as ListView


        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)

        var width = displayMetrics.widthPixels
        var height = displayMetrics.heightPixels

        // Gets linearlayout
        val layout: LinearLayout = findViewById(R.id.mainblock)
        // Gets the layout params that will allow you to resize the layout
        val params: ViewGroup.LayoutParams = layout.layoutParams
        params.width = width
        params.height = height
        layout.layoutParams = params






        //Check if current android sdk is greater than Marshmallow
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            //test is the user has given permission before of not
            if(checkPermission(permissions)){
                enableView()
            }else{
                requestPermissions(permissions, PERMISSION_REQUEST)
            }

        }else{
            enableView()
        }
    }

    private fun disableView() {
        /*btn_get_location.isEnabled = false
        btn_get_location.alpha = 0.5F*/

    }

    private fun enableView() {
        getLocation()
       /* btn_get_location.isEnabled = true
        btn_get_location.alpha = 1F*/
        weather_icon.setOnClickListener{ getLocation()}
        Toast.makeText(this, "Done Enable", Toast.LENGTH_SHORT).show()
    }

    @SuppressLint("MissingPermission")
    private fun getLocation(){
        //init locataionManager
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        //assign values to GPS and Network variables
        GPS = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        Network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        //if GPS / Network is true then process location else ask user to enable GPS
        if (GPS ||Network){

            if(GPS){
                Log.d("GPS Location: ", "Location collected from GPS")


                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,20000,1F, object  :
                    LocationListener {
                    override fun onLocationChanged(p0: Location) {
                        if(p0 != null){
                            GPSLocation = p0
                            Log.d("MainActivity","GPS Latitude: "+ GPSLocation!!.latitude)
                            Log.d("MainActivity","GPS Longitude: "+ GPSLocation!!.longitude)

                        }
                    }

                    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {

                    }

                    override fun onProviderEnabled(provider: String) {


                    }


                })
                val locaNetworkLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                if(locaNetworkLocation != null)
                    NetworkLocation = locaNetworkLocation
            }

            if(Network){
                Log.d("Network Location: ", "Location collected from Network")


                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,5000,0F, object  :
                    LocationListener {
                    override fun onLocationChanged(p0: Location) {
                        if(p0 != null){
                            NetworkLocation = p0
                            Log.d("MainActivity","Network Latitude: "+ NetworkLocation!!.latitude)
                            Log.d("MainActivity","Network Longitude: "+ NetworkLocation!!.longitude)

                        }
                    }

                    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {

                    }

                    override fun onProviderEnabled(provider: String) {

                    }


                })
                val locaGPSLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                if(locaGPSLocation != null)
                    GPSLocation = locaGPSLocation
            }

            if(GPSLocation != null && NetworkLocation!= null){

                var long: String
                var lat: String
                if(GPSLocation!!.accuracy > NetworkLocation!!.accuracy){
                    /*location_result.append("\nNetwork")
                    location_result.append("\nLatitude: "+ NetworkLocation!!.latitude)
                    location_result.append("\nLongitude: "+ NetworkLocation!!.longitude)*/
                    Log.d("MainActivity","Network Latitude: "+ NetworkLocation!!.latitude)
                    Log.d("MainActivity","Network Longitude: "+ NetworkLocation!!.longitude)

                    long= NetworkLocation!!.longitude.toString();
                    lat= NetworkLocation!!.latitude.toString();
                }else{
                   /* location_result.append("\nGPS")
                    location_result.append("\nLatitude: "+ GPSLocation!!.latitude)
                    location_result.append("\nLongitude: "+ GPSLocation!!.longitude)*/
                    Log.d("MainActivity","GPS Latitude: "+ GPSLocation!!.latitude)
                    Log.d("MainActivity","GPS Longitude: "+ GPSLocation!!.longitude)

                    long= GPSLocation!!.longitude.toString();
                    lat= GPSLocation!!.latitude.toString();
                }

                //current weather
                run("https://api.openweathermap.org/data/2.5/weather?lat="+lat+"&lon="+long +"&appid=b8851f820a63438538293c291ed5a270&units=metric")

                //Forecast weather
                runforcast("https://api.openweathermap.org/data/2.5/forecast?lat="+lat+"&lon="+long +"&appid=b8851f820a63438538293c291ed5a270&units=metric&cnt=7&exclude=hourly")
            }
        }else{
            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
        }
    }


    fun run(url: String) {
        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
            }

            @RequiresApi(Build.VERSION_CODES.O)
            override fun onResponse(call: Call, response: Response) {
                var str_response = response.body()!!.string()
                //creating json object
                val json_contact:JSONObject = JSONObject(str_response)
                //creating json array
                var jsonarray_weather:JSONArray= json_contact.getJSONArray("weather")
                var jsonarray_main:JSONObject= json_contact.getJSONObject("main")
                var jsonarray_wind:JSONObject= json_contact.getJSONObject("wind")
                var location : String = json_contact.getString("name")

                var json_object_weather:JSONObject=jsonarray_weather.getJSONObject(0)
                //var json_object_main:JSONObject=jsonarray_main.getJSONObject(0)

                //assign collected values to var
                var main: String = json_object_weather.getString("main")
                var description: String = json_object_weather.getString("description")
                var icon: String = json_object_weather.getString("icon")


                var temp: String = (roundTwoDecimals(jsonarray_main.getString("temp").toDouble())) + "\u00BA C";
                var temp_min: String = (roundTwoDecimals(jsonarray_main.getString("temp_min").toDouble())) + " \u00BA";
                var temp_max: String = (roundTwoDecimals(jsonarray_main.getString("temp_max").toDouble())) + " \u00BA";
                var pressure: String = jsonarray_main.getString("pressure");
                var humidity: String = jsonarray_main.getString("humidity")+ "\uFE6A";

                var wind: String = jsonarray_wind.getString("speed");
                var direction: String = jsonarray_wind.getString("deg");

                Log.v("temp", temp)



                Log.v("MainWeather", json_object_weather.toString());
                //Log.v("MainWeather", json_object_main.toString());
                Log.v("MainWeather",jsonarray_main.toString())
                runOnUiThread {
                    /*/stuff that updates ui
                    val obj_adapter : CustomAdapter
                    obj_adapter = CustomAdapter(applicationContext,arrayList_details)
                    listView_details.adapter=obj_adapter*/

                    Picasso.with(baseContext)
                        .load("https://openweathermap.org/img/wn/"+icon+"@4x.png")
                        //.placeholder(R.drawable.wind)
                        .error(R.drawable.ic_launcher_foreground)//optional
                        .fit()       //optional
                        //.centerCrop()                        //optional
                        .into(weather_icon);


                    //attach collected variables to components
                    main_weather.setText(main)
                    description_weather.setText(description)

                    location_result.setText(location)


                    //attach collected variables to components
                    current_weather.setText(temp)
                    minmax_weather.setText(temp_min+" | "+temp_max)
                    min_weather.setText(temp_min)
                    max_weather.setText(temp_max)
                    pressure_weather.setText(pressure)
                    humidity_weather.setText(humidity)

                    val current = LocalDateTime.now()

                    val formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
                    val formatted = current.format(formatter)

                    date_weather.setText(formatted)

                    min_weather.setText(wind+direction(direction.toInt()))

                }
                //progress.visibility = View.GONE
            }
        })
    }

    fun runforcast(url: String) {

        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
            }

            @RequiresApi(Build.VERSION_CODES.O)
            override fun onResponse(call: Call, response: Response) {
                var str_response = response.body()!!.string()
                //creating json object
                val json_contact:JSONObject = JSONObject(str_response)
                //creating json array
                var jsonarray_info:JSONArray= json_contact.getJSONArray("list")



                //init size and set default value for it
                var i:Int = 0
                var size:Int = jsonarray_info.length()
                arrayList_details= ArrayList();

                //go through the list and then add rows
                for (i in 0.. size-1) {
                    var json_objectdetail:JSONObject=jsonarray_info.getJSONObject(i)

                    //create new model that will hold values
                    var model:ForeCastModel= ForeCastModel();

                    Log.v("array pos :"+i.toString(), json_objectdetail.toString())

                    //store values
                    /*model.location =json_objectdetail.getString("name")*/

                    //weather block
                    var jsonarray_weather:JSONArray= json_objectdetail.getJSONArray("weather")
                    var json_objectweather:JSONObject=jsonarray_weather.getJSONObject(0)

                    model.datestamp = json_objectdetail.getString("dt_txt")

                    model.main = json_objectweather.getString("main")
                    model.description = json_objectweather.getString("description")

                    //main block
                    var jsonarray_main:JSONObject= json_objectdetail.getJSONObject("main")

                    model.temp = roundTwoDecimals(jsonarray_main.getString("temp").toDouble()) + "\u00BA C";
                    model.temp_min = roundTwoDecimals(jsonarray_main.getString("temp_min").toDouble())+ " \u00BA";
                    model.temp_max = roundTwoDecimals(jsonarray_main.getString("temp_max").toDouble())+ " \u00BA";
                    model.pressure = jsonarray_main.getString("pressure")
                    model.humidity = jsonarray_main.getString("humidity")





                    var jsonarray_wind:JSONObject= json_objectdetail.getJSONObject("wind")


                    //var jsonarray_main:JSONArray= json_objectdetail.getJSONArray("main")
                    //var json_objectmain:JSONObject=jsonarray_main.getJSONObject(0)


                    /*model.temp = json_objectmain.getString("temp")
                    model.temp_min = json_objectmain.getString("temp_min")
                    model.temp_max = json_objectmain.getString("temp_max")
                    model.pressure = json_objectmain.getString("pressure")
                    model.humidity = json_objectmain.getString("humidity")*/

                    //Log.v("Forecast temp: ",jsonarray_main.toString())

                    //wind block
                    /*var jsonarray_wind:JSONArray= json_objectdetail.getJSONArray("wind")
                    var json_objectwind:JSONObject=jsonarray_wind.getJSONObject(0)

                    model.wind = json_objectwind.getString("speed")
                    model.direction = json_objectwind.getString("deg")*/



                    arrayList_details.add(model)
                }

                runOnUiThread {
                    //stuff that updates ui
                    val obj_adapter : ForeCastAdapter
                    obj_adapter = ForeCastAdapter(applicationContext,arrayList_details)
                    listView_details.adapter=obj_adapter
                }
            }
        })
    }

    //convert the direction to a co-ordinate
    fun direction(d: Int) :String{
        var pos: String = "N"


            if(d in 0.0..30.0) {
                pos = "N"
            }else if(d in 30.0..60.0) {
                pos = "NE"
            }else if(d in 60.0..120.0) {
                pos = "E"
            }else if(d in 120.0..150.0) {
                pos = "SE"
            }else if(d in 150.0..210.0) {
                pos = "E"
            }else if(d in 210.0..240.0) {
                pos = "SW"
            }else if(d in 240.0..300.0) {
                pos = "W"
            }else if(d in 300.0..330.0) {
                pos = "NW"
            }else if(d in 330.0..360.0) {
                pos = "N"
            }


        return pos
    }

    fun roundTwoDecimals(d: Double): String {
        val twoDForm = DecimalFormat("#.#")
        return twoDForm.format(d).toString()
    }

    private fun checkPermission(permissionArray: Array<String>) :Boolean {
        var allSuccess = true

        for( i in permissionArray.indices){
            if (ActivityCompat.checkSelfPermission(this, permissionArray[i]) != PackageManager.PERMISSION_GRANTED)
                allSuccess = false
        }
        return allSuccess
    }
}

