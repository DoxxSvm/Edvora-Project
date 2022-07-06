package com.doxx.edvora.api


import com.doxx.edvora.models.RideResponse
import com.doxx.edvora.models.User
import retrofit2.Response
import retrofit2.http.GET

interface RideAPI {
    @GET("rides")
    suspend fun getRides(): Response<RideResponse>

    @GET("user")
    suspend fun getUser(): Response<User>




}