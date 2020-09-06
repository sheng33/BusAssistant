package com.shengq.notificationmanager.ui.dao

import com.shengq.notificationmanager.ui.dao.ZSGJinfoDao.BUS_CMD
import com.shengq.notificationmanager.ui.dao.ZSGJinfoDao.BUS_SIGN
import com.shengq.notificationmanager.ui.dao.ZSGJinfoDao.LINE_CMD
import com.shengq.notificationmanager.ui.dao.ZSGJinfoDao.LINE_SIGN
import com.shengq.notificationmanager.ui.dao.ZSGJinfoDao.REALBUS_CMD
import com.shengq.notificationmanager.ui.dao.ZSGJinfoDao.REALBUS_SIGN
import java.sql.Date
import java.util.*

data class BUSlineSearchDao(val CMD:String = LINE_CMD, var TIMESTAMP:String = Date().time.toString(),
                            var SIGN:String = LINE_SIGN
                            , var CITYNAME:String ="", var STARTPOINTNAME:String="", var STARTPOINTLNG:String = "", var STARTPOINTLAT:String = ""
                            , var ENDPOINTNAME:String="", var ENDPOINTLNG:String = "", var ENDPOINTLAT:String = "")
data class BUSInfoSearchDao(val CMD:String = BUS_CMD,var TIMESTAMP:String = Date().time.toString(),val SIGN:String = BUS_SIGN
                       ,var CITYNAME:String,var LINENAME:String,var DIRECTION:String)
data class BUSRealInfoSearchDao(val CMD:String = REALBUS_CMD,var TIMESTAMP:String = Date().time.toString(),val SIGN:String = REALBUS_SIGN
                           ,var CITYNAME:String,var LINENAME:String,var DIRECTION:String,var BUSNUMBER:String)