package com.shengq.notificationmanager

import java.io.Serializable

class CustomResult : Serializable {
    var hasUpdate = false
    var isIgnorable = false
    var versionCode = 0
    var versionName: String? = null
    var updateLog: String? = null
    var apkUrl: String? = null
    var apkSize: Long = 0
}
