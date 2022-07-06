package com.doxx.edvora

import android.app.Dialog
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.Window
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import com.doxx.edvora.fragments.ViewPagerAdapter
import com.doxx.edvora.repository.RidesRepository
import com.doxx.edvora.viewmodels.RidesViewModel
import com.doxx.edvora.viewmodels.RidesViewModelProviderFactory
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.filter_dialog.*

@RequiresApi(Build.VERSION_CODES.O)
class MainActivity : AppCompatActivity() {
    lateinit var viewModel: RidesViewModel
    private var tabTitle = arrayOf("Nearest","Upcoming","Past")
    lateinit var state:String
    var selectedCityPosition=0;
    private var selectedStatePosition=0
    lateinit var city:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        actionBar?.hide()
        window.statusBarColor= Color.BLACK

        val repository = RidesRepository()
        val viewModelProviderFactory = RidesViewModelProviderFactory(repository)
        viewModel = ViewModelProvider(this,viewModelProviderFactory).get(RidesViewModel::class.java)
        setUpTab()


    }

    private fun setUpTab() {
        viewPager.adapter=ViewPagerAdapter(supportFragmentManager,lifecycle)
        TabLayoutMediator(tabLayout,viewPager){
                tab,position ->
            tab.text=tabTitle[position]
        }.attach()
        filter.setOnClickListener{
            showDialog()
        }

    }


    private fun showDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.filter_dialog)
        dialog.window?.setGravity(Gravity.END)
        val states = mutableListOf<String>()
        var cities = mutableListOf<String>()
        val sList = viewModel.filterMap.keys

        for(i in sList.indices){
            states.add(sList.elementAt(i))
        }
        states.sort()
        states.add(0,"States")
        for(i in viewModel.cities.indices){
            cities.add(viewModel.cities[i])
        }

        cities.distinct()
        cities.sort()
        cities.add(0,"Cities")
        val stateSpinner = dialog.findViewById<Spinner>(R.id.statesSpinner)
        val stateAdaptor = ArrayAdapter(this@MainActivity,R.layout.spinner_text,states)
        stateSpinner.adapter=stateAdaptor

        var citiAdaptor = ArrayAdapter(this@MainActivity,R.layout.spinner_text,cities)
        val citySpinner = dialog.findViewById<Spinner>(R.id.cityspinner)
        citySpinner.adapter=citiAdaptor

        stateSpinner.setSelection(selectedStatePosition)
        citySpinner.setSelection(selectedCityPosition)
        stateSpinner.onItemSelectedListener = object :AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if(position!=0){
                    selectedStatePosition=position
                    state = parent?.getItemAtPosition(position).toString()
                    viewModel.rides.postValue(viewModel.filter(state=state))

                    cities = mutableListOf("Cities")
                    val cList= viewModel.filterMap[state]?.distinct()
                    for(i in cList!!.indices){
                        cities.add(cList[i])
                    }
                    cities.sort()
                    citiAdaptor=ArrayAdapter(this@MainActivity,R.layout.spinner_text,cities)
                    citySpinner.adapter=citiAdaptor

                    citySpinner.onItemSelectedListener=object :AdapterView.OnItemSelectedListener{
                        override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {
                            if(position!=0){
                                selectedCityPosition=position
                                city=parent?.getItemAtPosition(position).toString()
                                viewModel.rides.postValue(viewModel.filter(state,city))
                                dialog.dismiss()
                            }
                        }

                        override fun onNothingSelected(parent: AdapterView<*>?) {}
                    }
                }

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}

        }

        citySpinner.onItemSelectedListener = object :AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if(position!=0 && parent != null){
                    selectedCityPosition=position
                    city = parent.getItemAtPosition(position).toString()
                    viewModel.rides.postValue(viewModel.filter(city=city))
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        var removeFilters = dialog.findViewById<Button>(R.id.removeFilters)
        removeFilters.setOnClickListener {
            viewModel.rides.postValue(viewModel.nearestRideResponse)
            dialog.dismiss()
        }
        dialog.show()

    }
}