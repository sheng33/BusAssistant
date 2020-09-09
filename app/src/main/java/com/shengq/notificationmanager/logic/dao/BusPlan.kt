package com.shengq.notificationmanager.logic.dao

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class BusPlan(var busName:String,var startSite:String,var endSite:String,var startAddress:String,var time:String,var direction:Int) {
    @PrimaryKey(autoGenerate = true)
    var id:Long = 0
}