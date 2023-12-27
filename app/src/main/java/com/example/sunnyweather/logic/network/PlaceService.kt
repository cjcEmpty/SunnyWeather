package com.example.sunnyweather.logic.network


import com.example.sunnyweather.SunnyWeatherApplication
import com.example.sunnyweather.logic.model.PlaceResponse


import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.Call

interface PlaceService {

    @GET("v2/place?token=${SunnyWeatherApplication.TOKEN}&lang=zh_CN")
    fun searchPlaces(@Query("query") query: String): Call<PlaceResponse>/*类型错误就重新调用 retrofit2的Call*/
}