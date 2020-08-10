package com.sizwenje.weappather.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.sizwenje.weappather.R
import com.sizwenje.weappather.model.ForeCastModel
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*

class ForeCastAdapter(context: Context,arrayListDetails:ArrayList<ForeCastModel>) : BaseAdapter(){

    private val layoutInflater: LayoutInflater
    private val arrayListDetails:ArrayList<ForeCastModel>

    init {
        this.layoutInflater = LayoutInflater.from(context)
        this.arrayListDetails=arrayListDetails
    }

    override fun getCount(): Int {
        return arrayListDetails.size
    }

    override fun getItem(position: Int): Any {
        return arrayListDetails.get(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        val view: View?
        val listRowHolder: ListRowHolder


        if (convertView == null) {
            view = this.layoutInflater.inflate(R.layout.forecast_layout, parent, false)
            listRowHolder = ListRowHolder(view)
            view.tag = listRowHolder
        } else {
            view = convertView
            listRowHolder = view.tag as ListRowHolder
        }

        listRowHolder.datestamp.text = arrayListDetails.get(position).datestamp
        listRowHolder.min_adapter.text = arrayListDetails.get(position).temp_min
        listRowHolder.max_adapter.text = arrayListDetails.get(position).temp_max
        listRowHolder.description_adapter.text = arrayListDetails.get(position).description

        if (view != null) {
            Picasso.with(view.getContext())
                .load("https://openweathermap.org/img/wn/"+arrayListDetails.get(position).icon+"@4x.png")
                //.placeholder(R.drawable.wind)
                .error(R.drawable.ic_launcher_foreground)//optional
                .fit()       //optional
                //.centerCrop()                        //optional
                .into(listRowHolder.weather_icon_adapter)
        };




        return view
    }
}

private class ListRowHolder(row: View?) {
    //public val day_adapter: TextView
    public val datestamp : TextView
    public val min_adapter: TextView
    public val max_adapter: TextView
    public val description_adapter: TextView
    public val weather_icon_adapter : ImageView
    public val linearLayout: LinearLayout

    init {
        //this.day_adapter = row?.findViewById<TextView>(R.id.day_adapter) as TextView
        this.datestamp = row?.findViewById<TextView>(R.id.datestamp) as TextView
        this.min_adapter = row?.findViewById<TextView>(R.id.min_adapter) as TextView
        this.max_adapter = row?.findViewById<TextView>(R.id.max_adapter) as TextView
        this.description_adapter = row?.findViewById<TextView>(R.id.description_adapter) as TextView
        this.weather_icon_adapter = row?.findViewById<ImageView>(R.id.weather_icon_adapter) as ImageView
        this.linearLayout = row?.findViewById<LinearLayout>(R.id.linearLayout) as LinearLayout
    }
}