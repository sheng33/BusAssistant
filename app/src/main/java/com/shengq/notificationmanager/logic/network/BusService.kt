package com.shengq.notificationmanager.logic.network

import com.alibaba.fastjson.JSONObject
import retrofit2.http.*

interface BusService {
    @Headers("Host:wx.mygolbs.com","Connection: keep-alive"
        ,"User-Agent:Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.143 Safari/537.36 MicroMessenger/7.0.9.501 NetType/WIFI MiniProgramEnv/Windows WindowsWechat"
        ,"Cotent-Type:application/x-www-form-urlencoded"
        ,"Referer:https://servicewechat.com/wxb9e506ed4f66afc4/66/page-frame.html"
        ,"Accept-Encoding:gzip, deflate, br")
    @POST("ApiData.do")
    @FormUrlEncoded
    suspend fun startToEndAddressLinesInfo(@FieldMap  body: MutableMap<String,String>): BaseResp<List<JSONObject>>
    @POST("ApiData.do")
    @FormUrlEncoded
    suspend fun busLineInfoSearch(@FieldMap  body: MutableMap<String,String>): BaseResp<List<JSONObject>>
    @POST("ApiData.do")
    @FormUrlEncoded
    suspend fun busQueryInfoSearch(@FieldMap  body: MutableMap<String,String>): BaseResp<List<JSONObject>>

}