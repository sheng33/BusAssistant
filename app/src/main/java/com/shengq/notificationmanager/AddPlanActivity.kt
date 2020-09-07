package com.shengq.notificationmanager

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.amap.api.services.busline.BusStationItem
import com.amap.api.services.busline.BusStationQuery
import com.amap.api.services.busline.BusStationResult
import com.amap.api.services.busline.BusStationSearch
import com.amap.api.services.route.*
import com.shengq.notificationmanager.amap.OPISearch
import com.shengq.notificationmanager.ui.dao.BUSlineSearchDao
import com.shengq.notificationmanager.network.WBean
import com.shengq.notificationmanager.ui.adapter.AddPlanAdapter
import com.shengq.notificationmanager.ui.model.AddPlanModel
import kotlinx.android.synthetic.main.addcarplan_activity.*
import kotlinx.android.synthetic.main.buslines_item.view.*
import kotlinx.android.synthetic.main.toolbar_activity.*


class AddPlanActivity : AppCompatActivity(), RouteSearch.OnRouteSearchListener, TextWatcher,
        BusStationSearch.OnBusStationSearchListener {
    val editTextStart =  R.id.editTextTextPersonNameStart
    val editTextEnd =  R.id.editTextTextPersonNameEnd
    var endAddress: BusStationItem? = null
    var startAddress: BusStationItem? = null
    var spinnerList:MutableList<BusStationItem> = mutableListOf()
    lateinit var adapter: AddPlanAdapter
    lateinit var addPlanModel: AddPlanModel
    companion object {
        var car01 = WBean("1314路","黄泥头-吕蒙","06:00-22:00"
            ,1)
        var car02 = WBean("332路","吕蒙-黄泥头","06:00-22:00"
            ,2)
    }
    private val mHandler: Handler = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message) {
            when(msg.what){
                editTextStart->{
                    editTextTextPersonNameStart.setText( msg.data.getString("inputMsg"))
                }
                editTextEnd->{
                    editTextTextPersonNameStart.setText(msg.data.getString("inputMsg"))

                }
            }
        }
    }
    inner class TextWatcherSheng(var pd:Int): AdapterView.OnItemClickListener {
        override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            if (view!=null){
                val imm1:InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                var temp:AutoCompleteTextView = findViewById(R.id.editTextTextPersonNameStart)
                var temp1:AutoCompleteTextView = findViewById(R.id.editTextTextPersonNameEnd)
                imm1.hideSoftInputFromWindow(temp.windowToken,0)
                imm1.hideSoftInputFromWindow(temp1.windowToken,0)
                if (pd == R.id.editTextTextPersonNameStart){
                    startAddress = spinnerList[position]
                }else{
                    endAddress = spinnerList[position]
                }
                spinnerList.clear()
            }

        }

    }
    private fun BusListQuery(){
        if (startAddress!=null&&endAddress!=null){
            var data = BUSlineSearchDao()
            data.CITYNAME = OPISearch.city
            data.SIGN = "1472dec68a7d6a024a89bd8c2ba6d9e7"
            data.TIMESTAMP = "1598781018786"
            data.STARTPOINTNAME = startAddress!!.busStationName
            data.STARTPOINTLAT = startAddress!!.latLonPoint.latitude.toString()
            data.STARTPOINTLNG = startAddress!!.latLonPoint.longitude.toString()
            data.ENDPOINTNAME = endAddress!!.busStationName
            data.ENDPOINTLAT = endAddress!!.latLonPoint.latitude.toString()
            data.ENDPOINTLNG= endAddress!!.latLonPoint.longitude.toString()
            addPlanModel.getBusLines(data)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.addcarplan_activity)
        setSupportActionBar(default_toolbar)
        addPlanModel = ViewModelProvider(this).get(AddPlanModel::class.java)
        addPlanModel.list.observe(this, Observer { it ->
            if (it.isNotEmpty()){
                queryView.isVisible = false
                busLinesRecyclerView.isVisible = true
                adapter = AddPlanAdapter(this,it)
                adapter.startAddress = startAddress?.busStationName.toString()
                adapter.endAddress = endAddress?.busStationName.toString()
                busLinesRecyclerView.adapter = adapter
                adapter.notifyDataSetChanged()
                toast("有数据")
            }else{
                toast("无数据")
            }
        })
        breakBtn.setOnClickListener {
            finish()
        }
        titleText.text = "添加行程"
        val layoutManager = GridLayoutManager(this,1)
        busLinesRecyclerView.layoutManager = layoutManager

        editTextTextPersonNameStart.addTextChangedListener(this )
        editTextTextPersonNameEnd.addTextChangedListener(this )

        editTextTextPersonNameStart.onItemClickListener =  TextWatcherSheng(R.id.editTextTextPersonNameStart)
        editTextTextPersonNameEnd.onItemClickListener =  TextWatcherSheng(R.id.editTextTextPersonNameEnd)

        swap.setOnClickListener {
            var tempStr = editTextTextPersonNameStart.text
            editTextTextPersonNameStart.text = editTextTextPersonNameEnd.text
            editTextTextPersonNameEnd.text = tempStr
            it.isFocusable = true
            it.isFocusableInTouchMode = true
            it.requestFocus()
            it.requestFocusFromTouch()
            var  imm:InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(it.windowToken,0)
        }
        queryBussBtn.setOnClickListener {
            if (editTextTextPersonNameStart.text.isNotEmpty()&&editTextTextPersonNameEnd.text.isNotEmpty()){
                BusListQuery()
            }else{
                Toast.makeText(this,"起始点或者终点不能为空",Toast.LENGTH_LONG).show()
            }
        }
    }

        override fun onBusStationSearched(p0: BusStationResult?, p1: Int) {
            val busName:MutableList<String> = mutableListOf()
            p0?.busStations?.forEach {
                spinnerList.add(it)
                busName.add(it.busStationName)
                val adapter = ArrayAdapter<String>(
                    this@AddPlanActivity,
                    android.R.layout.simple_expandable_list_item_1,busName
                )
                editTextTextPersonNameStart.setAdapter(adapter)
                editTextTextPersonNameEnd.setAdapter(adapter)
                adapter.notifyDataSetChanged()
            }
        }
        override fun afterTextChanged(s: Editable?) {

        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            //公交站点模糊搜索
            var busStationQuery = BusStationQuery(s.toString(),OPISearch.city)
            var busStationSearch = BusStationSearch(applicationContext, busStationQuery)
            busStationSearch.setOnBusStationSearchListener(this) // 设置查询结果的监听
            busStationSearch.searchBusStationAsyn()
        }

        override fun onDriveRouteSearched(p0: DriveRouteResult?, p1: Int) {
            TODO("Not yet implemented")
        }

        override fun onBusRouteSearched(p0: BusRouteResult?, p1: Int) {
            if (p0!=null){
                var p = p0.paths
                p.forEach{
                    it.steps.forEach{
                        it.busLines.forEach {
                            Log.d("线路规划3", it.toString())
                        }
                        Log.d("线路规划2", it.toString())
                    }
                    Log.d("线路规划", it.steps[0].busLines[0].busStations.toString())
                }
            }
        }

        override fun onRideRouteSearched(p0: RideRouteResult?, p1: Int) {
            TODO("Not yet implemented")
        }

        override fun onWalkRouteSearched(p0: WalkRouteResult?, p1: Int) {
            TODO("Not yet implemented")
        }

    }