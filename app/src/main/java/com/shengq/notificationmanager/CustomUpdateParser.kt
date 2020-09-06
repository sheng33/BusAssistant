package com.shengq.notificationmanager

import android.util.Log
import com.xuexiang.xupdate.entity.UpdateEntity
import com.xuexiang.xupdate.listener.IUpdateParseCallback
import com.xuexiang.xupdate.proxy.IUpdateParser
import com.xuexiang.xutil.net.JsonUtil

class CustomUpdateParser : IUpdateParser {
    @Throws(Exception::class)
    override fun parseJson(json: String): UpdateEntity {
        return getParseResult(json)!!
    }

    private fun getParseResult(json: String): UpdateEntity? {
        val result: CustomResult = JsonUtil.fromJson(json, CustomResult::class.java)
        return if (result != null) {
            Log.d("打印",result.toString())
            UpdateEntity()
                .setHasUpdate(result.hasUpdate)
                .setIsIgnorable(result.isIgnorable)
                .setVersionCode(result.versionCode)
                .setVersionName(result.versionName)
                .setUpdateContent(result.updateLog)
                .setDownloadUrl(result.apkUrl)
                .setSize(result.apkSize)
        } else null
    }

    @Throws(Exception::class)
    override fun parseJson(
        json: String,
        callback: IUpdateParseCallback
    ) {
        //当isAsyncParser为 true时调用该方法, 所以当isAsyncParser为false可以不实现
        callback.onParseResult(getParseResult(json))
    }

    override fun isAsyncParser(): Boolean {
        return false
    }
}
