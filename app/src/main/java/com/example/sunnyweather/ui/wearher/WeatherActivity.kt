package com.example.sunnyweather.ui.wearher

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.SimpleAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.sunnyweather.R
import com.example.sunnyweather.logic.model.Weather
import com.example.sunnyweather.logic.model.getSky
import kotlinx.android.synthetic.main.activity_weather.*
import kotlinx.android.synthetic.main.forecast.*
import kotlinx.android.synthetic.main.life_index.*
import kotlinx.android.synthetic.main.now.*
import java.text.SimpleDateFormat
import java.util.*

class WeatherActivity : AppCompatActivity() {

    val viewModel by lazy { ViewModelProvider(this).get(WeatherViewModel::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /*用逻辑代码将背景图和状态栏融合一起*/
        val decorView = window.decorView /*拿到当前的Activity的DecorView*/
        /*再调用system来改变系统UI的显示*/
        decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE/*这两个参数会将Activity的布局会显示在状态栏上*/
        window.statusBarColor = Color.TRANSPARENT/*将状态栏设置成透明色*/

        setContentView(R.layout.activity_weather)

        /*从Intent取出 经纬度坐标  和  地区名*//*并赋值到WeatherViewModel相应变量中*/
        if (viewModel.locationLng.isEmpty()){
            viewModel.locationLng = intent.getStringExtra("location_lng") ?: ""
        }
        if (viewModel.locationLat.isEmpty()){
            viewModel.locationLat = intent.getStringExtra("location_lat") ?: ""
        }
        if (viewModel.placeName.isEmpty()){
            viewModel.placeName = intent.getStringExtra("place_name") ?: ""/*为空就显示""*/
        }
        viewModel.weatherLiveData.observe/*观察 属于广播部分*/(this, Observer { result ->
            val weather = result.getOrNull()
            if (weather != null){
                showWeatherInfo(weather)
            }else{
                Toast.makeText(this, "无法成功获取天气信息", Toast.LENGTH_SHORT).show()
                result.exceptionOrNull()?.printStackTrace()
            }
        })
        /*最后执行到这里  就调用这方法执行刷新一次天气的请求*/
        viewModel.refreshWeather(viewModel.locationLng,viewModel.locationLat)
    }

    /*获取到服务器返回的天气数据时  就调用这个方法来解析展示*/
    private fun showWeatherInfo(weather:Weather){
        /*获取weather这个数据 然后显示到相应的控件上*/

         placeName.text = viewModel.placeName
        val realtime = weather.realtime
        val daily = weather.daily

        /*填充now.xml布局中的数据*/
        val currentTempText = "${realtime.temperature.toInt()} °C"
        currentTemp.text = currentTempText
        currentSky.text  = getSky(realtime.skycon).info

        val currentPM25Text = "空气指数 ${realtime.airQuality.aqi.chn.toInt()}"
        currentAQI.text = currentPM25Text
        nowLayout.setBackgroundResource(getSky(realtime.skycon).bg)
        forecastLayout.removeAllViews()/*填充forecast.xml布局中的数据*/
        val days = daily.skycon.size
        for (i in 0 until days){/*这个是未来几天天气预报  处理每天的天气信息*/
            val skycon = daily.skycon[i]
            val temperature = daily.temperature[i]
            val view = LayoutInflater.from(this).inflate(R.layout.forecast_item,forecastLayout,false)/*循环动态加载这个布局并设置相应的数据 然后添加到父布局中*/

            val dateInfo = view.findViewById(R.id.dateInfo) as TextView
            val skyIcon = view.findViewById(R.id.skyIcon) as ImageView
            val skyInfo = view.findViewById(R.id.skyInfo) as TextView
            val temperatureInfo = view.findViewById(R.id.temperatureInfo) as TextView

            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            dateInfo.text = simpleDateFormat.format(skycon.date)
            val sky = getSky(skycon.value)
            skyIcon.setImageResource(sky.icon)
            skyInfo.text = sky.info
            val tempText = "${temperature.min.toInt()} ~ ${temperature.max.toInt()} °C"
            temperatureInfo.text = tempText
            forecastLayout.addView(view)
        }
        /*填充life_index.xml布局中的数据*/
        val lifeIndex = daily.lifeIndex

        /*这里是生活指数 */
        coldRiskText.text = lifeIndex.coldRisk[0].desc/*这里对所有生活指数都取下表为0的元素数据 */
        dressingText.text = lifeIndex.dressing[0].desc
        ultravioletText.text = lifeIndex.ultraviolet[0].desc
        carWashingText.text = lifeIndex.carWashing[0].desc
        weatherLayout.visibility = View.VISIBLE/*让ScrollView滚动视图变成可见状态*/


    }
}