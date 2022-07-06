package com.doxx.edvora.fragments

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.doxx.edvora.MainActivity
import com.doxx.edvora.R
import com.doxx.edvora.adaptor.RideAdaptor
import com.doxx.edvora.viewmodels.RidesViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_upcoming_ride.*


class UpcomingRideFragment : Fragment(R.layout.fragment_upcoming_ride) {
    lateinit var rideAdapter: RideAdaptor
    lateinit var viewModel: RidesViewModel
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel= (activity as MainActivity).viewModel
        viewModel.upcomingAndPastRides()
        viewModel.upcomingRides.observe(viewLifecycleOwner, Observer {
            upcomingRv.apply {
                layoutManager=LinearLayoutManager(activity)
                if(it.size == 0) {
                    Snackbar.make(this,"No upcoming rides", Snackbar.LENGTH_SHORT)
                        .show()
                }
                else{
                    rideAdapter=RideAdaptor(it)
                    adapter=rideAdapter
                }
            }
        })

    }
}