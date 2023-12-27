package com.example.sunnyweather.logic.model

import android.location.Location
import com.google.gson.annotations.SerializedName

data class Place(val name:String, val location: Location, @SerializedName("formatted_address") val address:String)
