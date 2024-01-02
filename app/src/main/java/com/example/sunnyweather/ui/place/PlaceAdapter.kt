package com.example.sunnyweather.ui.place

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.sunnyweather.R
import com.example.sunnyweather.logic.model.Place
import com.example.sunnyweather.ui.wearher.WeatherActivity
import kotlinx.android.synthetic.main.activity_weather.*

class PlaceAdapter(private val fragment: PlaceFragment, private val placeList: List<Place>) :
    RecyclerView.Adapter<PlaceAdapter.ViewHolder>() {
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val placeName: TextView = view.findViewById(R.id.placeName)
        val placeAddress: TextView = view.findViewById(R.id.placeAddress)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.place_item, parent, false)

        val holder = ViewHolder(view)
        holder.itemView.setOnClickListener {/*最外层的布局注册了点击事件 */
            val position = holder.adapterPosition/*在点击事件之后获取经纬度坐标 和 地区名称 */
            val place = placeList[position]
            val activity = fragment.activity

            /*显示滑动菜单的界面*/
            if (activity is WeatherActivity){/*如果是在WeatherActivity中 */
                activity.drawerLayout.closeDrawers()/*在里面  那么就关闭滑动菜单*/
                activity.viewModel.locationLng = place.location.lng/*赋值新的经纬度坐标*/
                activity.viewModel.locationLat = place.location.lat
                activity.viewModel.placeName = place.name/*和地区名称*/
                activity.refreshWeather()/*刷新城市的天气信息*/
            }else{/*否则在Activity中 就保持之前的处理逻辑不变*/
                val intent = Intent(parent.context, WeatherActivity::class.java).apply {/*再传入Intent*/
                    putExtra("location_lng",place.location.lng)/*传递经纬度坐标*/
                    putExtra("location_lat",place.location.lat)
                    putExtra("place_name",place.name)/*和地区名称*/
                }
                fragment.startActivity(intent)/*调用fragment启动WeatherActivity*/
                fragment.activity?.finish()
            }
            fragment.viewModel.savePlace(place)/*在跳转到WeatherActivity之前 先调用PlaceViewModel的savePlace来存储选中的城市*/
        }
        return holder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val place = placeList[position]
        holder.placeName.text = place.name
        holder.placeAddress.text = place.address

    }

    override fun getItemCount(): Int {
      return  placeList.size
    }


}