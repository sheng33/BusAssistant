package com.shengq.notificationmanager.logic.dao

import com.shengq.notificationmanager.logic.dao.ZSGJinfoDao.BUS_CMD
import com.shengq.notificationmanager.logic.dao.ZSGJinfoDao.BUS_SIGN
import com.shengq.notificationmanager.logic.dao.ZSGJinfoDao.BUS_SIGN_TIME
import com.shengq.notificationmanager.logic.dao.ZSGJinfoDao.LINE_CMD
import com.shengq.notificationmanager.logic.dao.ZSGJinfoDao.LINE_SIGN
import com.shengq.notificationmanager.logic.dao.ZSGJinfoDao.REALBUS_CMD
import com.shengq.notificationmanager.logic.dao.ZSGJinfoDao.REALBUS_SIGN
import java.util.*

data class BUSlineSearchDao(val CMD:String = LINE_CMD, var TIMESTAMP:String = Date().time.toString(),
                            var SIGN:String = LINE_SIGN
                            , var CITYNAME:String ="", var STARTPOINTNAME:String="", var STARTPOINTLNG:String = "", var STARTPOINTLAT:String = ""
                            , var ENDPOINTNAME:String="", var ENDPOINTLNG:String = "", var ENDPOINTLAT:String = "")
data class BUSInfoSearchDao(val CMD:String = BUS_CMD,var TIMESTAMP:String = BUS_SIGN_TIME,val SIGN:String = BUS_SIGN
                       ,var CITYNAME:String,var LINENAME:String,var DIRECTION:String)
