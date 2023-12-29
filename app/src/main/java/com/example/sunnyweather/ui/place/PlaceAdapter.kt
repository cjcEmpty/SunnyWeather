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
            val intent = Intent(parent.context, WeatherActivity::class.java).apply {/*再传入Intent*/
                putExtra("location_lng",place.location.lng)
                putExtra("location_lat",place.location.lat)
                putExtra("place_name",place.name)
            }

            fragment.viewModel.savePlace(place)/*在跳转到WeatherActivity之前 先调用PlaceViewModel的savePlace来存储选中的城市*/

            fragment.startActivity(intent)/*调用fragment启动WeatherActivity*/
            fragment.activity?.finish()
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