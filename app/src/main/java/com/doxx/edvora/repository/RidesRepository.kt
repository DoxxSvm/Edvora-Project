package com.doxx.edvora.repository

import com.doxx.edvora.api.RetrofitInstance

class RidesRepository{
    suspend fun getRides() = RetrofitInstance.api.getRides()
    suspend fun getUser()=RetrofitInstance.api.getUser()

}