package com.example.sunnyweather.logic

import androidx.lifecycle.liveData
import com.example.sunnyweather.logic.model.Place
import com.example.sunnyweather.logic.network.SunnyWeatherNetwork
import kotlinx.coroutines.Dispatchers
import java.lang.Exception
import java.lang.RuntimeException

object Repository {

    /*将异步获取的数据以响应式变成的方式通知给上一层 通常会返回一个liveData  13.4小节*/
    /*自动构建并返回一个LiveData对象 代码块提供函数的上下文 再liveData函数的代码块任意调用任意的挂起函数 */
    fun searchPlaces(query: String) =
        /*todo 不允许主线程进行网络请求 如：读写数据库之类的本地操作也是不建议再主线程中进行的*/
        liveData(Dispatchers.IO)/*将LiveData函数线程参数类型指定成Dispatchers.IO这样代码块中所有代码都运行再子线程了*/{
        /*来搜索城市数据*/
        val result = try {
            val placeResponse = SunnyWeatherNetwork.searchPlaces(query)
            if (placeResponse.status == "ok") {/*如果服务器响应是ok*/
                val places = placeResponse.places
                Result.success(places)/*响应ok 就来包装获取的城市数据列表*/
            } else {
                Result.failure(RuntimeException("response status is ${placeResponse.status}"))/*否则就 包装一个异常信息*/
            }
        } catch (e: Exception) {
            Result.failure<List<Place>>(e)
        }
        emit(result)/*将包装结果发射出去 类似于调用LiveData的setValue方法通知数据变化  不过这里无法取得返回的LivData对象*/
    }
}