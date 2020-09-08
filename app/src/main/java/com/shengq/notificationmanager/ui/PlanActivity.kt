package com.shengq.notificationmanager.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import com.alibaba.fastjson.JSONObject
import com.shengq.notificationmanager.R
import kotlinx.android.synthetic.main.add_plan_time_activity.*
import java.util.*

class PlanActivity: AppCompatActivity() {
    lateinit var busData:JSONObject
    lateinit var busPlanData:JSONObject
    private fun init(){
        var tempStr = intent.getStringExtra("busData")
        Log.d("窗口打印",tempStr)
        busData = JSONObject.parseObject(tempStr)
        tempStr = intent.getStringExtra("busPlan")
        Log.d("窗口打印",tempStr)
        busPlanData = JSONObject.parseObject(tempStr)
        var direction = busData.getIntValue("direction")
        add_busId.text = busData.getString("id")
        add_busLine.text = busData.getString("route")
        add_waitSite.text = busPlanData.getString("startAddress")
        timepicker.setIs24HourView(true)
        timepicker.descendantFocusability = TimePicker.FOCUS_BLOCK_DESCENDANTS
        val time = Calendar.getInstance()
        timepicker.hour = time.get(Calendar.HOUR_OF_DAY)
        timepicker.minute = time.get(Calendar.MINUTE)
        var returnTrip: SwitchCompat = this.findViewById(R.id.ReturnTrip_switch)
        returnTrip.setOnCheckedChangeListener{ _, isChecked ->
            if (isChecked){
                Toast.makeText(this,"true", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(this,"false", Toast.LENGTH_SHORT).show()
            }
        }
        var roticeShow: SwitchCompat = this.findViewById(R.id.NoticeShow_switch)
        roticeShow.setOnCheckedChangeListener{ _, isChecked ->
            if (isChecked){
                Toast.makeText(this,"true", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(this,"false", Toast.LENGTH_SHORT).show()
            }
        }
        savePlan.setOnClickListener {
            var planStr = "{'busId':'${add_busId.text}','line':'${add_busLine.text}','time':'${timepicker.hour}:${timepicker.minute}','waitSite':'${add_waitSite.text}','direction':$direction}"
            var json = JSONObject.parseObject(planStr)

            var intent = Intent(this,
                MainActivity::class.java)
            intent.putExtra("data",json.toString())
            startActivity(intent)
            Log.d("最后输出",json.toString())
        }

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_plan_time_activity)
        this.init()
    }
}