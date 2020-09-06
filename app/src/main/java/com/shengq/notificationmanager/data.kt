package com.shengq.notificationmanager

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import com.shengq.notificationmanager.network.BaseResp

fun <T> BaseResp<T>.dataConvert(): T {
    if (status == 1) {
        return data
    } else {
        throw Exception(msg)
    }
}

/**
 * 全局toast
 */
fun Context.toast(msg: String) {
    Toast.makeText(this, msg,  LENGTH_SHORT).show()
}

/**
 * 全局跳转
 */
fun Activity.openActivity(cls: Class<*>) {
    startActivity(Intent(this, cls))
}