package com.doxx.edvora.viewmodels

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.doxx.edvora.models.RideResponse
import com.doxx.edvora.models.User
import com.doxx.edvora.repository.RidesRepository
import com.doxx.edvora.utils.Resource
import kotlinx.coroutines.launch
import retrofit2.Response
import java.time.LocalDateTime
import java.time.Month

@RequiresApi(Build.VERSION_CODES.O)
class RidesViewModel(val ridesRepository: RidesRepository):ViewModel() {
    val rides:MutableLiveData<Resource<RideResponse>> = MutableLiveData()
    val upcomingRides: MutableLiveData<RideResponse> = MutableLiveData()
    val pastRides: MutableLiveData<RideResponse> = MutableLiveData()
    var user:MutableLiveData<Resource<User>> = MutableLiveData()

    lateinit var nearestRideResponse : Resource<RideResponse>
    lateinit var filterMap :MutableMap<String,ArrayList<String>>
    lateinit var cities:ArrayList<String>
    var totalUpcomingRides =0
    var totalPastRides=0

    init {
        getUser()
    }


    private fun getRides()=viewModelScope.launch {
        rides.postValue(Resource.Loading())
        val response = ridesRepository.getRides()

        val handledResponse = handleResponse(response)
        nearestRideResponse=nearestRide(handledResponse)
        rides.postValue(nearestRideResponse)
        filterMapInit(handledResponse)
        upcomingAndPastRides()

    }

    fun getUser()=viewModelScope.launch {
        user.postValue(Resource.Loading())
        val userResponse = ridesRepository.getUser()
        user.postValue(handleResponse(userResponse))
        getRides()
    }

    private fun <T> handleResponse(response: Response<T>) : Resource<T> {
        if(response.isSuccessful){
            response.body()?.let {
                return Resource.Success(it)
            }
        }
        return Resource.Error(response.message())
    }

    fun upcomingAndPastRides(){
        val upcomingRidesList = RideResponse()
        val pastRidesList = RideResponse()
        val queryList = nearestRideResponse.data
        if (queryList != null) {
            for(i in queryList.indices){
                if(LocalDateTime.now() > getDateTime(queryList[i].date)) {
                    pastRidesList.add(queryList[i])
                    totalPastRides++
                }
                else {
                    upcomingRidesList.add(queryList[i])
                    totalUpcomingRides++
                }
            }
        }
        upcomingRides.postValue(upcomingRidesList)
        pastRides.postValue(pastRidesList)
    }

    private fun getDateTime(dateTime:String):LocalDateTime{

        val month=dateTime.subSequence(0,2).toString().toInt()
        val date=dateTime.subSequence(3,5).toString().toInt()
        val year= dateTime.subSequence(6,10).toString().toInt()
        var hour = dateTime.subSequence(11,13).toString().toInt()
        val min = dateTime.subSequence(14,16).toString().toInt()
        val amOrPm = dateTime.subSequence(17,19)
        if(amOrPm == "AM" && hour == 12) hour =0
        if(amOrPm == "PM" && hour != 12) hour+=12
        return LocalDateTime.of(year,month,date,hour,min)

    }
    private fun filterMapInit(handledResponse: Resource<RideResponse>) {
        filterMap = mutableMapOf()
        cities= arrayListOf()
        val ridesList = handledResponse.data

        if (ridesList != null) {

            for (i in ridesList.indices){
                filterMap[ridesList[i].state]= ArrayList()
            }
            for (i in ridesList.indices){
                filterMap[ridesList[i].state]?.add(ridesList[i].city)
                cities.add(ridesList[i].city)
            }

        }
    }

    private fun nearestRide(handledResponse: Resource<RideResponse>) : Resource<RideResponse> {
        val userStnCode = user.value?.data?.station_code
        val ridesList = handledResponse.data
        val map = HashMap<Int,Int>()
        val newRideResponse =RideResponse ()

        if (ridesList != null) {
            for(i in ridesList.indices){
                map[i]=getMin(ridesList[i].station_path,userStnCode)
            }
        }
        //sorting by values
        val newMap = map.toList().sortedBy { (_, value) -> value}.toMap()

        for(entries in newMap){
            ridesList?.get(entries.key)?.let {
                it.dist = entries.value
                newRideResponse.add(it)
            }

        }
        return Resource.Success(newRideResponse)
    }
    private fun getMin(stnPath : List<Int>,stnCode: Int?) :Int{
        var min=Int.MAX_VALUE
        for(i in stnPath.indices){
            val dis = (stnPath[i] - stnCode!!)
            if( dis >=0 && min > dis ) min = dis
        }
        return min
    }
    fun filter(state: String? =null, city: String? =null):Resource<RideResponse>{
        val filteredRideResponse = RideResponse()
        val queryList = nearestRideResponse.data
        if(state == null){
            for(i in queryList?.indices!!){
                if(queryList[i].city == city ) filteredRideResponse.add(queryList[i])
            }
        }
        else if(city == null) {
            for(i in queryList?.indices!!){
                if(queryList[i].state == state) filteredRideResponse.add(queryList[i])
            }
        }
        else {
            for(i in queryList?.indices!!){
                if(queryList[i].city == city && queryList[i].state == state) filteredRideResponse.add(queryList[i])
            }
        }
        return Resource.Success(filteredRideResponse)

    }

}