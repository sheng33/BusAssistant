package com.shengq.notificationmanager.logic.network

import android.util.Log
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class RetrofitFactory private constructor() {
    private val retrofit: Retrofit


    init {


        retrofit = Retrofit.Builder()
            .baseUrl("https://wx.mygolbs.com/WxBusServer/")
            .client(initOkhttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    }


    companion object {
        val instance: RetrofitFactory by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            RetrofitFactory()
        }

    }


    private fun initOkhttpClient(): OkHttpClient {

        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(5, TimeUnit.SECONDS)
            .addInterceptor(initLogInterceptor())
            .build()

        return okHttpClient
    }


    /*
    * 日志拦截器
    * */
    private fun initLogInterceptor(): HttpLoggingInterceptor {

        val interceptor = HttpLoggingInterceptor(object : HttpLoggingInterceptor.Logger {
            override fun log(message: String) {
                Log.i("Retrofit", message)
            }
        })

        interceptor.level = HttpLoggingInterceptor.Level.BODY

        return interceptor
    }


    /*
    * 具体服务实例化
    * */
    fun <T> getService(service: Class<T>): T {
        return retrofit.create(service)
    }
}