package com.shengq.notificationmanager

import com.alibaba.fastjson.JSONObject

class aaaa {
    companion object{
        @JvmStatic
        fun main(args: Array<String>) {
            val busData = JSONObject.parseObject("{'id':1,'route':2,'time':3,'direction':4}")
            print(busData.toString())
        }
    }
}