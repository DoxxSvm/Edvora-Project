package com.doxx.edvora.adaptor

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.doxx.edvora.R
import com.doxx.edvora.models.RideResponse
import kotlinx.android.synthetic.main.ride_item.view.*

class RideAdaptor(private var items: RideResponse):RecyclerView.Adapter<RideViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RideViewHolder {
       return RideViewHolder(
           LayoutInflater.from(parent.context).inflate(
               R.layout.ride_item,
               parent,
               false
           )
       )
    }

    override fun onBindViewHolder(holder: RideViewHolder, position: Int) {
        val ride = items[position]
        holder.itemView.apply {
            Glide.with(this).load(ride.map_url).into(map)
            map.clipToOutline=true
            "Ride ID : ${ride.id}".also { rideID.text = it }
            "Origin Station : ${ride.origin_station_code}".also { originStn.text = it }
            "Station Path : ${ride.station_path}".also { station_path.text = it }
            "Date : ${ride.date}".also { Date.text = it }
            city.text=ride.city
            state.text=ride.state
            "Distance : ${ride.dist}".also { Distance.text = it }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }
}
class RideViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView)