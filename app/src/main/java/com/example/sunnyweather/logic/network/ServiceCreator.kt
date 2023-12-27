package com.example.sunnyweather.logic.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// TODO: 2023/12/26  11.6.3小节
object ServiceCreator {
    private const val BASE_URL = "https://api.caiyunapp.com/"/*url路径*/

    private val retrofit = Retrofit.Builder()/*创建retrofit*/
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())/*创建构造器*/
        .build()

    fun <T> create(serviceClass: Class<T>): T = retrofit.create(serviceClass)/*创建调用*/


    /*内联函数*/
    inline fun <reified T> create(): T = create(T::class.java)
}