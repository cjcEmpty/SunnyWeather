package com.example.sunnyweather.logic

import androidx.lifecycle.liveData
import com.example.sunnyweather.logic.dao.PlaceDao
import com.example.sunnyweather.logic.model.Place
import com.example.sunnyweather.logic.model.Weather
import com.example.sunnyweather.logic.network.SunnyWeatherNetwork
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import java.lang.Exception
import java.lang.RuntimeException
import kotlin.coroutines.CoroutineContext

/*仓库层相关的代码*/


object Repository {

    /*替换的代码*/
    /*将异步获取的数据以响应式变成的方式通知给上一层 通常会返回一个liveData  13.4小节*/
    /*自动构建并返回一个LiveData对象 代码块提供函数的上下文 再liveData函数的代码块任意调用任意的挂起函数 */
    /*  fun searchPlaces(query: String) =
        todo 不允许主线程进行网络请求 如：读写数据库之类的本地操作也是不建议再主线程中进行的
        liveData(Dispatchers.IO)将LiveData函数线程参数类型指定成Dispatchers.IO这样代码块中所有代码都运行再子线程了{
            来搜索城市数据
            val result = try {
                val placeResponse = SunnyWeatherNetwork.searchPlaces(query)
                if (placeResponse.status == "ok") {如果服务器响应是ok
                    val places = placeResponse.places
                    Result.success(places)响应ok 就来包装获取的城市数据列表
                } else {
                    Result.failure(RuntimeException("response status is ${placeResponse.status}"))否则就 包装一个异常信息
                }
            } catch (e: Exception) {
                Result.failure<List<Place>>(e)
            }
            emit(result)将包装结果发射出去 类似于调用LiveData的setValue方法通知数据变化  不过这里无法取得返回的LivData对象
        }*/


    fun searchPlaces(query: String) = fire(Dispatchers.IO) {
        val placeResponse = SunnyWeatherNetwork.searchPlaces(query)
        if (placeResponse.status == "ok") {/*如果服务器响应是ok*/
            val places = placeResponse.places
            Result.success(places)/*响应ok 就来包装获取的城市数据列表*/
        } else {
            Result.failure(RuntimeException("response status is ${placeResponse.status}"))/*否则就 包装一个异常信息*/
        }
    }


    fun refreshWeather(lng: String, lat: String) = fire(Dispatchers.IO) {
        coroutineScope {

            val deferredRealtime = async {
                SunnyWeatherNetwork.getRealtimeWeather(lng, lat)
            }

            val deferredDaily = async {
                SunnyWeatherNetwork.getDailyWeather(lng, lat)
            }
            val realtimeResponse = deferredRealtime.await()
            val dailyResponse = deferredDaily.await()
            if (realtimeResponse.status == "ok" && dailyResponse.status == "ok") {/*如果这两个参数都是返回ok 就取出放在weather对象中*/
                val weather = Weather(realtimeResponse.result.realtime, dailyResponse.result.daily)
                Result.success(weather)/*使用success包装这个weather对象*/
            } else {
                Result.failure(/*对象返回的参数不ok就调用failure包装异常信息*/
                    RuntimeException(
                        "realtime response status is ${realtimeResponse.status}" +
                                "daily response status is ${dailyResponse.status}"
                    )
                )
            }
        }
    }






    /*替换的代码*/
   /* fun refreshWeather(lng: String, lat: String) = liveData(Dispatchers.IO) {
        val result = try {
            coroutineScope {
                *//* 在11小节  分别在两个async中调用 网络请求就可以都请求成功  todo async 必须在作用域才能调用 这里使用coroutineScope创建一个作用域*//*
                val deferredRealtime = async {
                    SunnyWeatherNetwork.getRealtimeWeather(lng, lat)
                }
                val deferredDaily = async {
                    SunnyWeatherNetwork.getDailyWeather(lng, lat)
                }
                val realtimeResponse = deferredRealtime.await()
                val dailyResponse = deferredDaily.await()
                if (realtimeResponse.status == "ok" && dailyResponse.status == "ok") {*//*如果这两个参数都是返回ok 就取出放在weather对象中*//*
                    val weather =
                        Weather(realtimeResponse.result.realtime, dailyResponse.result.daily)
                    Result.success(weather)*//*使用success包装这个weather对象*//*
                } else {
                    Result.failure(*//*对象返回的参数不ok就调用failure包装异常信息*//*
                        RuntimeException(
                            "realtime response status is ${realtimeResponse.status}" +
                            "daily response status is ${dailyResponse.status}"))
                }
            }
        } catch (e: Exception) {
            Result.failure<Weather>(e)
        }
        emit(result)*//*包装异常信息  再发射出去*//*
    }*/



    /*按照liveData()方法的参数定义的高阶函数*/
    /*再fire内部会先调用一下liveData函数 再liveData函数的代码块统一进行try catch 并再try传入Lambda表达式代码  最终获取Lambda的结果并调用emit发射出去*/
   /*todo 先在liveData方法中声明suspend挂起函数  这个lambda才有资格称为挂起函数*/
    private fun <T> fire(context: CoroutineContext, block: suspend () -> Result<T>) = liveData<Result<T>>(context) {
            val result = try {
                block()
            } catch (e: Exception) {
                Result.failure<T>(e)
            }
            emit(result)
        }


    fun savePlace(place:Place)  = PlaceDao.savePlace(place)/*sharedPreferences不建议在主线程中执行  因为毕竟是耗时操作*/
    fun getSavedPlace() = PlaceDao.getSavedPlace()
    fun isPlaceSaved() = PlaceDao.isPlaceSaved()

}