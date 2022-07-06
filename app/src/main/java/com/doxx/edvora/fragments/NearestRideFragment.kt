package com.doxx.edvora.fragments


import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.doxx.edvora.MainActivity
import com.doxx.edvora.R
import com.doxx.edvora.adaptor.RideAdaptor
import com.doxx.edvora.utils.Resource
import com.doxx.edvora.viewmodels.RidesViewModel
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.fragment_nearest_ride.*


@RequiresApi(Build.VERSION_CODES.O)
class NearestRideFragment : Fragment(R.layout.fragment_nearest_ride) {
    private lateinit var rideAdapter: RideAdaptor
    private lateinit var viewModel: RidesViewModel
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
       viewModel= (activity as MainActivity).viewModel
        viewModel.rides.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    hideProgressBar()
                    it.data?.let { rideResponse ->
                        rideAdapter = RideAdaptor(rideResponse)
                        nearestRv.apply {
                            adapter = rideAdapter
                            layoutManager = LinearLayoutManager(activity)
                            setup()
                        }
                    }
                }

                is Resource.Error -> {
                    hideProgressBar()
                    Toast.makeText(context, "Sorry! Unable to get rides", Toast.LENGTH_SHORT).show()
                }
                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        }
        viewModel.user.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    hideProgressBar()
                    it.data?.let { userResponse ->
                        val username =
                            (activity as MainActivity).findViewById<TextView>(R.id.username)
                        val profilePic =
                            (activity as MainActivity).findViewById<ImageView>(R.id.profilePic)
                        username.text = userResponse.name
                        Glide.with(this).load(userResponse.url).into(profilePic)
                    }
                }

                is Resource.Error -> {
                    hideProgressBar()
                    Toast.makeText(context, "Sorry! Unable to get user", Toast.LENGTH_SHORT).show()
                }
                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        }


    }
    private fun setup(){
        val tabLayout = activity?.findViewById<TabLayout>(R.id.tabLayout)
        tabLayout?.getTabAt(1)?.text ="Upcoming("+viewModel.totalUpcomingRides.toString()+")"
        tabLayout?.getTabAt(2)?.text ="Past("+viewModel.totalPastRides.toString()+")"

    }
    private fun hideProgressBar() {
        nearestProgressBar.visibility = View.GONE
    }

    private fun showProgressBar() {
        nearestProgressBar.visibility = View.VISIBLE
    }


}