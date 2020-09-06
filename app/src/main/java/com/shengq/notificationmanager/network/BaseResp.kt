package com.shengq.notificationmanager.network

import com.google.gson.annotations.SerializedName
data class BaseResp<T> (
    var data: T,
    var flag:Int = 0,
    var status:Int = 0,
    var msg:String =""
)