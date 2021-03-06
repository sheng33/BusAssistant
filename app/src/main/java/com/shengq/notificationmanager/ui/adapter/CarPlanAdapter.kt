package com.shengq.notificationmanager.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.shengq.notificationmanager.R
import com.shengq.notificationmanager.logic.dao.*
import com.shengq.notificationmanager.logic.model.MainModel
import com.shengq.notificationmanager.ui.MainActivity

class CarPlanAdapter(
    private val myDataset: ArrayList<CarPlan>,
    val busPlanDao: BusPlanDao,
    val mainModel: MainModel,
    mainActivity: MainActivity
) :
    RecyclerView.Adapter<CarPlanAdapter.MyViewHolder>(), View.OnClickListener {
    lateinit var carPlan: CarPlan
    var position:Int = 0
    var mainActivity = mainActivity
    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    // Each data item is just a string in this case that is shown in a TextView.
    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val carName:TextView = view.findViewById(R.id.car_name)
        var carStartaddress:TextView = view.findViewById(R.id.car_startAddress)
        var carEndaddress:TextView = view.findViewById(R.id.car_endAddress)
        var carStarttime:TextView = view.findViewById(R.id.car_startTime)
        var carNowsite:TextView = view.findViewById(R.id.car_nowSite)
        var carNextsite:TextView = view.findViewById(R.id.car_nextSite)
        var carLeftsitenumber:TextView = view.findViewById(R.id.car_leftSiteNumber)
    }


    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): MyViewHolder {

        // create a new view
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.carplan_item, parent, false)
        var stautSwitch:SwitchCompat = view.findViewById(R.id.staut_switch)
        stautSwitch.setOnCheckedChangeListener{ _, isChecked ->
            if (isChecked){
                stautSwitch.text = "已启用"
                Toast.makeText(view.context,"true",Toast.LENGTH_SHORT).show()
            }else{
                stautSwitch.text = "已禁用"
                Toast.makeText(view.context,"false",Toast.LENGTH_SHORT).show()
            }
        }

        // set the view's size, margins, paddings and layout parameters
        return MyViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val plan = myDataset[position]
//        var data = BUSInfoSearchDao("","","", OPISearch.city,plan.name,plan.direction.toString())
        var data = BUSInfoSearchDao("","","", "景德镇市",plan.name,plan.direction.toString())
        Log.d("等车站点",plan.startAddress)
        if (plan.startAddress.lastIndexOf("(")!=-1)
            plan.startAddress = plan.startAddress.substring(0,plan.startAddress.lastIndexOf("("))
        mainModel.getBusLines(data,plan.startAddress)
        Log.d("等车站点",plan.startAddress)
        mainModel.list.observe(mainActivity, Observer {
            Log.d("测试123",it.toString())
            plan.nowSite = it[0]
            plan.nextSite = it[1]
            plan.leftSiteNumber = it[2]
            holder.carName.text = plan.name
            holder.carStartaddress.text = plan.startSite
            holder.carEndaddress.text = plan.endSite
            holder.carStarttime.text = plan.startTime
            holder.carNowsite.text = plan.nowSite
            holder.carNextsite.text = plan.nextSite
            holder.carLeftsitenumber.text = plan.leftSiteNumber
        })
        holder.carName.text = plan.name
        holder.carStartaddress.text = plan.startSite
        holder.carEndaddress.text = plan.endSite
        holder.carStarttime.text = plan.startTime
        holder.carNowsite.text = plan.nowSite
        holder.carNextsite.text = plan.nextSite
        holder.carLeftsitenumber.text = plan.leftSiteNumber
        var cardDelete:Button = holder.itemView.findViewById(R.id.card_delete)
        this.position = position
        cardDelete.setOnClickListener {
            val mySnackbar = Snackbar.make(it,
                R.string.channelMsg,6000)
            this.position = position
            this.carPlan = myDataset[position]
            mySnackbar.setAction("撤销",this)
            mySnackbar.show()
            removeData(position)
            busPlanDao.deleteUserByLastId(carPlan.id)
        }
    }

    fun addData(position: Int,carPlan: CarPlan) {
//      在list中添加数据，并通知条目加入一条
        myDataset.add(position, carPlan)
        //添加动画
        notifyItemInserted(position)
        notifyDataSetChanged()
    }

    fun removeData(position: Int) {
        Log.d("test","撤销操作${position}")
        myDataset.removeAt(position)
        //删除动画
        notifyItemRemoved(position)
        notifyDataSetChanged()
    }
    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = myDataset.size
    //SnackbarListener
    override fun onClick(v: View?) {
        addData(position,carPlan)
        var busPlan = BusPlan(carPlan.name,carPlan.startSite,carPlan.endSite,carPlan.startAddress,carPlan.startTime,carPlan.direction)
        busPlanDao.insertBusPlan(busPlan)
        notifyDataSetChanged()
    }


}
