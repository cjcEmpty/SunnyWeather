package com.example.sunnyweather.ui.place

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sunnyweather.R
import kotlinx.android.synthetic.main.fragment_place.*

class PlaceFragment:Fragment() {

    /*todo 使用懒加载 获取PlaceViewModel的实例  允许在整个类中随时用viewModel这个变量 不用担心何时初始化，为空的前提*/
    val viewModel by lazy { ViewModelProvider(this).get(PlaceViewModel::class.java) }

    private lateinit var adapter:PlaceAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_place,container,false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val layoutManager = LinearLayoutManager(activity)/*设置了管理器*/
         recyclerView.layoutManager = layoutManager/*调用使用管理器*/

        adapter = PlaceAdapter(this,viewModel.placeList)/*设置了适配器*/  /*todo 使用PlaceList集合作为数据源*/
        recyclerView.adapter = adapter/*调用使用适配器*/

        searchPlaceEdit.addTextChangedListener { editable ->/*监听搜索框的内容变化  就可获取新的内容 */
            val content  = editable.toString()
            if (content.isNotEmpty()){
                viewModel.searchPlaces(content)/*传递给searchPlace方法来搜索城市数据的网络请求了*/
            }else{
                recyclerView.visibility = View.GONE/*不可见*//*搜索框内容为空时就把RecyclerView隐藏起来 gone不可见*/
                bgImageView.visibility = View.VISIBLE/*可见*//*搜索框为空时就把美观的图片展示出来*/
                viewModel.placeList.clear()
                adapter.notifyDataSetChanged()
            }
        }

        /*有任何数据变化就会传递到observe接口 对回调的数据进行判断 */
        viewModel.placeLiveData.observe(viewLifecycleOwner, Observer { result ->
//        viewModel.placeLiveData.observe(this, Observer { result ->   原本的代码  这里显示报错
            val places = result.getOrNull()
            if (places != null){
                recyclerView.visibility = View.VISIBLE/*变为可见*/
                bgImageView.visibility = View.GONE/*变为可见 之后变成不可见*/
                viewModel.placeList.clear()
                viewModel.placeList.addAll(places)/*数据不为空就将数据加到PlaceList中*/
                adapter.notifyDataSetChanged()/*不为空就通知适配器刷新界面*/
            }else{
                Toast.makeText(activity, "未能查询到任何地点", Toast.LENGTH_SHORT).show()/*如果数据为空就弹出一个toast*/
            }

        })
    }
}