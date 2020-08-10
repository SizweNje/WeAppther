package com.sizwenje.weappather

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.DisplayMetrics
import android.util.Log
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ListView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.sizwenje.weappather.adapter.ForeCastAdapter
import com.sizwenje.weappather.model.ForeCastModel
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.current_weather
import kotlinx.android.synthetic.main.activity_main.date_weather
import kotlinx.android.synthetic.main.activity_main.description_weather
import kotlinx.android.synthetic.main.activity_main.humidity_weather
import kotlinx.android.synthetic.main.activity_main.location_result
import kotlinx.android.synthetic.main.activity_main.main_weather
import kotlinx.android.synthetic.main.activity_main.min_weather
import kotlinx.android.synthetic.main.activity_main.minmax_weather
import kotlinx.android.synthetic.main.activity_main.weather_icon
import kotlinx.android.synthetic.main.activity_main_night.*
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.text.DecimalFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

private  const val PERMISSION_REQUEST =10

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {

    lateinit var locationManager: LocationManager
    private var GPS = false
    private var Network = false
    private var GPSLocation :Location? = null
    private var NetworkLocation :Location? = null
    private var currentLocation :String ="Location"

    lateinit var listviewDetails: ListView
    var arraylistDetails:ArrayList<ForeCastModel> = ArrayList();




    //Declare permissions that will be used
    private var permissions = arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION)

    private val client = OkHttpClient()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_night)
        disableView()

        listviewDetails = findViewById<ListView>(R.id.listView) as ListView
        //find the current screen size and use it to split screen for two sections (force main view t fill screen)
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)

        val width = displayMetrics.widthPixels
        val height = displayMetrics.heightPixels

        // Gets linearlayout
        val layout: LinearLayout = findViewById(R.id.mainblock)

        // Gets the layout params that will allow you to resize the layout
        val params: ViewGroup.LayoutParams = layout.layoutParams
        params.width = width
        params.height = height
        layout.layoutParams = params





        // Check for Internet Connection
        if (isConnected()) {
            //Check if current android sdk is greater than Marshmallow
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                //test is the user has given permission before of not
                if (checkPermission(permissions)) {
                    enableView()
                } else {
                    requestPermissions(permissions, PERMISSION_REQUEST)
                }

            } else {
                enableView()
            }
            }else{
            showInternetErrorBox()
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
        //Toast.makeText(this, "Done Enable", Toast.LENGTH_SHORT).show()
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


                //load new location every 30 seconds
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,30000,5F, object  :
                    LocationListener {
                    override fun onLocationChanged(p0: Location) {
                        GPSLocation = p0
                        Log.d("MainActivity","GPS Latitude: "+ GPSLocation!!.latitude)
                        Log.d("MainActivity","GPS Longitude: "+ GPSLocation!!.longitude)

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
                        NetworkLocation = p0
                        Log.d("MainActivity","Network Latitude: "+ NetworkLocation!!.latitude)
                        Log.d("MainActivity","Network Longitude: "+ NetworkLocation!!.longitude)

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

                val long: String
                val lat: String
                if(GPSLocation!!.accuracy > NetworkLocation!!.accuracy){
                    /*location_result.append("\nNetwork")
                    location_result.append("\nLatitude: "+ NetworkLocation!!.latitude)
                    location_result.append("\nLongitude: "+ NetworkLocation!!.longitude)*/
                    Log.d("MainActivity","Network Latitude: "+ NetworkLocation!!.latitude)
                    Log.d("MainActivity","Network Longitude: "+ NetworkLocation!!.longitude)

                    long= NetworkLocation!!.longitude.toString()
                    lat= NetworkLocation!!.latitude.toString()
                }else{
                   /* location_result.append("\nGPS")
                    location_result.append("\nLatitude: "+ GPSLocation!!.latitude)
                    location_result.append("\nLongitude: "+ GPSLocation!!.longitude)*/
                    Log.d("MainActivity","GPS Latitude: "+ GPSLocation!!.latitude)
                    Log.d("MainActivity","GPS Longitude: "+ GPSLocation!!.longitude)

                    long= GPSLocation!!.longitude.toString()
                    lat= GPSLocation!!.latitude.toString()
                }

                //current weather
                run("https://api.openweathermap.org/data/2.5/weather?lat=$lat&lon=$long&appid=b8851f820a63438538293c291ed5a270&units=metric",long,lat)
            }
        }else{
            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
        }
    }


    private fun run(url: String, long: String, lat :String) {
        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {

            }

            @RequiresApi(Build.VERSION_CODES.O)
            override fun onResponse(call: Call, response: Response) {
                val strResponse = response.body()!!.string()
                val jsonContact = JSONObject(strResponse)

                val location : String = jsonContact.getString("name")


                runOnUiThread {

                    location_result.text = location
                    currentLocation = location

                    //forecast weather
                    runonecall("https://api.openweathermap.org/data/2.5/onecall?lat=$lat&lon=$long&appid=b8851f820a63438538293c291ed5a270&units=metric&exclude=hourly")


                }
            }
        })
    }


    private fun runonecall(url: String) {

        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {

            }

            @SuppressLint("SetTextI18n")
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onResponse(call: Call, response: Response) {
                val strResponse = response.body()!!.string()
                //creating json object
                val jsonContact = JSONObject(strResponse)

                //creating json array
                val jsonarrayInfo:JSONArray= jsonContact.getJSONArray("daily")



                //init size and set default value for it
                val size:Int = jsonarrayInfo.length()
                arraylistDetails= ArrayList()


                /**
                 * This is to load data in the top section of the application
                 */


                val jsonarray_current:JSONObject= jsonContact.getJSONObject("current")

                val temp: String = (roundTwoDecimals(jsonarray_current.getString("temp").toDouble())) + "\u00BA"
                val uvi: String = jsonarray_current.getString("uvi")
                val humidity: String = jsonarray_current.getString("humidity")+ "\uFE6A"
                val wind: String = roundTwoDecimals(jsonarray_current.getString("wind_speed").toDouble())
                val direction: String = jsonarray_current.getString("wind_deg")


                //assign collected values to val


                //weather block
                val jsonarray_weather:JSONArray= jsonarray_current.getJSONArray("weather")
                val json_objectweather:JSONObject=jsonarray_weather.getJSONObject(0)


                val icon = json_objectweather.getString("icon")
                val main = json_objectweather.getString("main")
                val description = json_objectweather.getString("description")



                /**
                 * Go through the list and then add rows to the bottom list row section
                 */

                for (i in 1.. size-1) {
                    val jsonObjectdetail:JSONObject=jsonarrayInfo.getJSONObject(i)

                    //create new model that will hold values
                    val model:ForeCastModel= ForeCastModel();

                    //Log.v("array pos :$i", jsonObjectdetail.toString())

                    //weather block
                    val jsonarrayWeather:JSONArray= jsonObjectdetail.getJSONArray("weather")
                    val jsonObjectweather:JSONObject=jsonarrayWeather.getJSONObject(0)


                    model.icon = jsonObjectweather.getString("icon")
                    model.main = jsonObjectweather.getString("main")
                    model.description = jsonObjectweather.getString("description")

                    Log.v("weather section : ",jsonObjectweather.toString())

                    model.datestamp = Instant.ofEpochSecond(jsonObjectdetail.getString("dt").toLong())
                        .atZone(ZoneId.systemDefault()).dayOfWeek
                        .toString()

                    model.pressure = jsonObjectdetail.getString("pressure")
                    model.humidity = jsonObjectdetail.getString("humidity")+ "\uFE6A"
                    model.wind = roundTwoDecimals(jsonObjectdetail.getString("wind_speed").toDouble())
                    model.direction = jsonObjectdetail.getString("wind_deg")



                    //main block
                    val jsonarray_main:JSONObject= jsonObjectdetail.getJSONObject("temp")

                    model.temp = roundTwoDecimals(jsonarray_main.getString("day").toDouble()) + "\u00BA"
                    model.temp_min = roundTwoDecimals(jsonarray_main.getString("min").toDouble())+ "\u00BA"
                    model.temp_max = roundTwoDecimals(jsonarray_main.getString("max").toDouble())+ "\u00BA"


                    arraylistDetails.add(model)
                }

                runOnUiThread {

                    Picasso.with(baseContext)
                        .load("https://openweathermap.org/img/wn/$icon@4x.png")
                        //.placeholder(R.drawable.wind)
                        .error(R.drawable.ic_launcher_foreground)//optional
                        .fit()       //optional
                        //.centerCrop()                        //optional
                        .into(weather_icon)


                    //attach collected variables to components
                    main_weather.text = main
                    description_weather.text = description

                    //location_result.setText(location)


                    //attach collected variables to components
                    current_weather.text = temp
                    current_weather1.text = temp
                    minmax_weather.text = "UV $uvi"
                    humidity_weather.text = humidity

                    val current = LocalDateTime.now()

                    val formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)
                    val formatted = current.format(formatter)

                    date_weather.text = formatted

                    min_weather.text = wind+direction(direction.toInt())

                    //stuff that updates ui
                    val obj_adapter : ForeCastAdapter
                    obj_adapter = ForeCastAdapter(applicationContext,arraylistDetails)
                    listviewDetails.adapter=obj_adapter

                    listviewDetails.setOnItemClickListener { parent, view, position, id ->


                        //Toast.makeText(this@MainActivity, "Item One: "+ arrayList_details.map { it.temp }[position].toString(),   Toast.LENGTH_SHORT).show()

                        IntentNewView(currentLocation,
                            arraylistDetails.map { it.description }[position].toString(),
                            arraylistDetails.map { it.temp }[position].toString(),
                            arraylistDetails.map { it.temp_min }[position].toString(),
                            arraylistDetails.map { it.temp_max }[position].toString(),
                            arraylistDetails.map { it.humidity }[position].toString(),
                            arraylistDetails.map { it.wind }[position].toString()+" "+direction(arraylistDetails.map { it.direction }[position].toString().toInt()),
                            arraylistDetails.map { it.icon }[position].toString(),
                            arraylistDetails.map { it.datestamp }[position].toString())


                    }

                    }
            }
        })
    }

    fun isConnected(): Boolean {
        var connected = false
        try {
            val cm: ConnectivityManager =
                applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val nInfo: NetworkInfo? = cm.getActiveNetworkInfo()
            connected = nInfo != null && nInfo.isAvailable() && nInfo.isConnected()
            return connected
        } catch (e: Exception) {
            Log.e("Connectivity Exception", e.message!!)
        }
        return connected
    }

    fun showInternetErrorBox(){
        // Show Internet connection error if no internet connect was found
        val dialogBuilder = AlertDialog.Builder(this)

        // set message of alert dialog
        dialogBuilder.setMessage("Please make sure your connected to the internet.")
            .setCancelable(false)
            .setPositiveButton("Retry", DialogInterface.OnClickListener {
                    dialog, id ->

                finish();
                startActivity(getIntent());
            })
        // negative button text and action


        // create dialog box
         val alert = dialogBuilder.create()
        // set title for alert dialog box
        alert.setTitle("Internet connection failure")
        // show alert dialog
        alert.show()
    }

    fun IntentNewView(name:String,description:String,temp:String, mintemp:String,maxtemp:String,hum:String,wind:String,icon:String,day :String){
        val intent = Intent(this, SingleViewActivity::class.java)
        intent.putExtra("name", name)
        intent.putExtra("desc", description)
        intent.putExtra("temp", temp)
        intent.putExtra("mintemp", mintemp)
        intent.putExtra("maxtemp", maxtemp)
        intent.putExtra("hum", hum)
        intent.putExtra("wind", wind)
        intent.putExtra("icon",icon)
        intent.putExtra("day",day)
        startActivity(intent)
    }

    //convert the direction to a co-ordinate
    fun direction(d: Int) :String{
        var pos = "N"


        when (d) {
            in 0.0..30.0 -> {
                pos = "N"
            }
            in 30.0..60.0 -> {
                pos = "NE"
            }
            in 60.0..120.0 -> {
                pos = "E"
            }
            in 120.0..150.0 -> {
                pos = "SE"
            }
            in 150.0..210.0 -> {
                pos = "E"
            }
            in 210.0..240.0 -> {
                pos = "SW"
            }
            in 240.0..300.0 -> {
                pos = "W"
            }
            in 300.0..330.0 -> {
                pos = "NW"
            }
            in 330.0..360.0 -> {
                pos = "N"
            }
        }


        return pos
    }

    fun roundTwoDecimals(d: Double): String {
        val twoDForm = DecimalFormat("#")
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

