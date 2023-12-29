package com.example.sunnyweather.logic.dao

import android.content.Context
import androidx.core.content.edit
import com.example.sunnyweather.SunnyWeatherApplication
import com.example.sunnyweather.logic.model.Place
import com.google.gson.Gson

object PlaceDao {
    fun savePlace(place:Place){/*这方法将Place对象存储到SharedPreferences文件中*/
        sharedPreferences().edit{
            putString("place", Gson().toJson(place))/*这里通过Gson将Place对象转成JSON字符串 然后就可以用字符串存储的方式保存数据了*/
        }
    }
    fun getSavedPlace():Place{
        val placeJson = sharedPreferences().getString("place","")
        return Gson().fromJson(placeJson,Place::class.java)
    }

    fun isPlaceSaved() = sharedPreferences().contains("place")/*判断数据是否被存储*/

    private fun sharedPreferences() = SunnyWeatherApplication.context.getSharedPreferences("sunny_weather",Context.MODE_PRIVATE)


}