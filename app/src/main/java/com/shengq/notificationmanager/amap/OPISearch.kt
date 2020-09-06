package com.shengq.notificationmanager.amap

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.amap.api.location.AMapLocation
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.location.AMapLocationListener
import com.amap.api.services.core.LatLonPoint
import com.amap.api.services.core.PoiItem
import com.amap.api.services.poisearch.PoiResult
import com.amap.api.services.poisearch.PoiSearch
import com.amap.api.services.poisearch.PoiSearch.OnPoiSearchListener
import com.shengq.notificationmanager.MainActivity
import com.shengq.notificationmanager.R
import kotlinx.android.synthetic.main.activity_main.*

class OPISearch(private var context: Context) :  OnPoiSearchListener,AMapLocationListener {
    companion object{
        var city:String = ""
        var latitude:Double = 0.0
        var longitude:Double = 0.0
        var address:String = ""
    }

    public fun getLocation(): AMapLocationClient {
        val mLocationClient : AMapLocationClient = AMapLocationClient(context)
        mLocationClient.setLocationListener(this)
        var mLocationOption: AMapLocationClientOption = AMapLocationClientOption()
        mLocationOption.locationMode = AMapLocationClientOption.AMapLocationMode.Hight_Accuracy
        mLocationOption.isOnceLocationLatest = true
        mLocationOption.isNeedAddress = true
        mLocationClient.setLocationOption(mLocationOption)

        return mLocationClient
    }

    public fun getPoi() {
        var poiServer = PoiSearch.Query("公交车站","150700",city)
        poiServer.pageSize = 10
        var poiSearch = PoiSearch(context,poiServer)
        poiSearch.setOnPoiSearchListener(this)
        var latLonPoint = LatLonPoint(latitude,longitude)
        var searchBound = PoiSearch.SearchBound(latLonPoint,1000)
        poiSearch.bound = searchBound
        poiSearch.searchPOIAsyn()
    }

    override fun onPoiItemSearched(p0: PoiItem?, p1: Int) {
    }

    override fun onPoiSearched(p0: PoiResult?, p1: Int) {
        Log.d("Poi查询","打印Poi信息${p1},${p0?.pois?.size}")
        var temp = p0?.pois

    }

    override fun onLocationChanged(it: AMapLocation?) {
        if (it != null) {
            if (it.errorCode == 0) {
                //可在其中解析amapLocation获取相应内容。
                latitude = it.latitude
                longitude = it.longitude
                address = it.city
                city = it.city
            } else {
                //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                Log.e(
                    "AmapError", "location Error, ErrCode:"
                            + it.errorCode + ", errInfo:"
                            + it.errorInfo
                )
            }
        }
    }
}