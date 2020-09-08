package com.shengq.notificationmanager.logic.model

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alibaba.fastjson.JSONObject
import com.shengq.notificationmanager.logic.dao.BUSlineSearchDao
import com.shengq.notificationmanager.logic.dataConvert
import com.shengq.notificationmanager.logic.network.BusService
import com.shengq.notificationmanager.logic.network.RetrofitFactory
import com.shengq.notificationmanager.logic.network.WBean
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.collections.ArrayList

class AddPlanModel:ViewModel() {
    var list = MutableLiveData<ArrayList<WBean>>()

    fun getBusLines(data: BUSlineSearchDao){
        var body = mutableMapOf<String,String>()
        body.put("CMD",data.CMD);
        body.put("SIGN",data.SIGN);
        body.put("TIMESTAMP","1598781018786");
        body.put("CITYNAME",data.CITYNAME);
        body.put("STARTPOINTNAME",data.STARTPOINTNAME);
        body.put("STARTPOINTLNG",data.STARTPOINTLNG);
        body.put("STARTPOINTLAT",data.STARTPOINTLAT);
        body.put("ENDPOINTNAME",data.ENDPOINTNAME);
        body.put("ENDPOINTLNG",data.ENDPOINTLNG);
        body.put("ENDPOINTLAT",data.ENDPOINTLAT);
        Log.d("HTTP请求body信息",body.toString())
        viewModelScope.launch {
            try {
                //withContext表示挂起块  配合Retrofit声明的suspend函数执行 该块会挂起直到里面的网络请求完成 最后一行就是返回值
                val str = withContext(Dispatchers.IO){
                    //dataConvert扩展函数可以很方便的解析出我们想要的数据  接口很多的情况下下可以节省不少无用代码
                    RetrofitFactory.instance.getService(BusService::class.java).startToEndAddressLinesInfo(body).dataConvert()
                }
                var bean = arrayListOf<WBean>()
                str.forEach { its ->
                    var jsonArray = its.getJSONArray("transferDetails")
                    if (jsonArray.isNotEmpty()){
                        jsonArray.forEach {
                            val json = JSONObject.parseObject(it.toString())
                            var string = json.getString("routeNumber").split('(').get(1)
                            string = string.split(')').get(0)
                            var o = WBean(json.getString("lineName"),string
                                ,json.getString("startEndTM"),json.getIntValue("upperOrdown"))
                            bean.add(o)
                        }
                    }
                }
                //给LiveData赋值  ui会自动更新
                list.value = bean
            }catch (e:Exception){
                e.printStackTrace()
                Log.e("net error","网络请求错误${e.toString()}")
            }
        }
    }
}