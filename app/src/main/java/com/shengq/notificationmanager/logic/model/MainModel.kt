package com.shengq.notificationmanager.logic.model

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alibaba.fastjson.JSONObject
import com.shengq.notificationmanager.logic.dao.BUSInfoSearchDao
import com.shengq.notificationmanager.logic.dao.BUSlineSearchDao
import com.shengq.notificationmanager.logic.dao.ZSGJinfoDao.BUS_CMD
import com.shengq.notificationmanager.logic.dao.ZSGJinfoDao.BUS_SIGN
import com.shengq.notificationmanager.logic.dao.ZSGJinfoDao.BUS_SIGN_TIME
import com.shengq.notificationmanager.logic.dao.ZSGJinfoDao.REALBUS_CMD
import com.shengq.notificationmanager.logic.dao.ZSGJinfoDao.REALBUS_SIGN
import com.shengq.notificationmanager.logic.dao.ZSGJinfoDao.REALBUS_SIGN_TIME
import com.shengq.notificationmanager.logic.dataConvert
import com.shengq.notificationmanager.logic.network.BusService
import com.shengq.notificationmanager.logic.network.RetrofitFactory
import com.shengq.notificationmanager.logic.network.WBean
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainModel : ViewModel(){
    var list = MutableLiveData<ArrayList<String>>()

    fun getBusLines(data: BUSInfoSearchDao,waitAddress:String){

        var siteArray = arrayListOf<Int>()
        viewModelScope.launch {
            Log.d("测试","查询开始,城市:${data.CITYNAME}")
            try {
                var count:Int = 0;
                //withContext表示挂起块  配合Retrofit声明的suspend函数执行 该块会挂起直到里面的网络请求完成 最后一行就是返回值
                val busLineInfo = withContext(Dispatchers.IO){
                    var body = mutableMapOf<String,String>()
                    body.put("CMD",BUS_CMD);
                    body.put("SIGN",BUS_SIGN);
                    body.put("TIMESTAMP",BUS_SIGN_TIME);
                    body.put("CITYNAME",data.CITYNAME);
                    body.put("LINENAME",data.LINENAME);
                    body.put("DIRECTION",data.DIRECTION);
                    Log.d("HTTP请求body信息",body.toString())

                    //dataConvert扩展函数可以很方便的解析出我们想要的数据  接口很多的情况下下可以节省不少无用代码
                    RetrofitFactory.instance.getService(BusService::class.java).busLineInfoSearch(body).dataConvert()
                }
                var bean = arrayListOf<String>()
                busLineInfo.forEach { its ->
                    val json = JSONObject.parseObject(its.toString())
                    if (waitAddress.equals(json.getString("stationName"))){
                        count = json.getIntValue("stationOrder")
                    }
                    bean.add(json.getString("stationName"))
                }
                val busQuery = withContext(Dispatchers.IO){
                    var body = mutableMapOf<String,String>()
                    body.put("CMD",REALBUS_CMD);
                    body.put("SIGN",REALBUS_SIGN);
                    body.put("TIMESTAMP",REALBUS_SIGN_TIME);
                    body.put("CITYNAME",data.CITYNAME);
                    body.put("LINENAME",data.LINENAME);
                    body.put("DIRECTION",data.DIRECTION);
                    Log.d("HTTP请求body信息",body.toString())

                    RetrofitFactory.instance.getService(BusService::class.java).busQueryInfoSearch(body).dataConvert()
                }

                busQuery.forEach { its ->
                    val json = JSONObject.parseObject(its.toString())
                    var index = json.getIntValue("index")
                    var arrive = json.getIntValue("arrive")
                    if (arrive == 1){
                        siteArray.add(index)
                    }else{
                        siteArray.add(index-1)
                    }
                }
                var siteList = arrayListOf<String>()
                var min = 99
                var site1 = ""
                var site2 = ""

                if (bean.isNotEmpty()&&siteArray.isNotEmpty()){
                    siteArray.forEach {
                        var tempInt = count - bean.lastIndexOf(bean[it])
                        if (min>tempInt){
                            min = tempInt
                            site1 = bean[it]
                            site2 = if (bean.size > (it+1)){
                                bean[it+1]
                            }else{
                                "null"
                            }
                        }
//                        Log.d("位置",bean.toString())
//                        Log.d("位置一",bean.lastIndexOf(bean[it]).toString())
//                        Log.d("位置二",count.toString())
                    }
                }
                siteList.add(site1)
                siteList.add(site2)
                if (min!=0){
                    siteList.add("$min 站")
                }else{
                    siteList.add("已到站")
                }
                //给LiveData赋值  ui会自动更新
                list.value = siteList
            }catch (e:Exception){
                e.printStackTrace()
                Log.e("net error","网络请求错误")
            }
        }
    }
}

