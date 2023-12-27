package com.example.sunnyweather.logic.network

import com.example.sunnyweather.logic.model.Place
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.await
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

object SunnyWeatherNetwork {

    private val placeService = ServiceCreator.create<PlaceService>()


    /*todo 在当外部调用searchPlaces函数时 Retrofit就会立刻发起网络请求  同时当前的协程也会被阻塞 直到服务器响应请求后 await函数将解析出来的数据模型对象取出并返回 同时恢复当前协程的执行*/
    /*searchPlaces函数在的到await函数返回值后  会将该数据再返回到上一层*/



    /*suspend fun 挂起函数*/
    suspend fun searchPlaces(query: String) = placeService.searchPlaces(query).await()

    /*await在 11.7.3小节*/
    private suspend fun <T> Call<T>.await(): T {
        return suspendCoroutine { continuation ->
            enqueue(object : Callback<T> {
                override fun onResponse(call: Call<T>, response: Response<T>) {
                    val body = response.body()
                    if (body != null) continuation.resume(body)
                    else continuation.resumeWithException(RuntimeException("response body is null"))
                }

                override fun onFailure(call: Call<T>, t: Throwable) {
                    continuation.resumeWithException(t)
                }

            })

        }
    }

}