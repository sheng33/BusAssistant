package com.shengq.notificationmanager.ui.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.shengq.notificationmanager.ui.PlanActivity
import com.shengq.notificationmanager.R
import com.shengq.notificationmanager.logic.network.WBean
import java.lang.Exception


class AddPlanAdapter(private val context: Context, private val myDataset: ArrayList<WBean>) :
    RecyclerView.Adapter<AddPlanAdapter.MyViewHolder>() {
    var position:Int = 0
    var startAddress:String = ""
    var endAddress:String = ""
    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    // Each data item is just a string in this case that is shown in a TextView.
    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view){
        var linesBusId: TextView = view.findViewById(R.id.linesBusId)
        var linesBusRoute: TextView = view.findViewById(R.id.linesBusRoute)
        var linesBusTime: TextView = view.findViewById(R.id.linesBusTime)
        var direction:Int = 0
    }


    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): AddPlanAdapter.MyViewHolder {
        // create a new view
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.buslines_item, parent, false)
        // set the view's size, margins, paddings and layout parameters
        return MyViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val plan = myDataset[position]
        holder.linesBusId.text = plan.busId
        holder.linesBusRoute.text = plan.routeNumber
        holder.linesBusTime.text = plan.startEndTM
        holder.direction = plan.upperOrdown
        holder.itemView.setOnClickListener {
            val intent = Intent(context, PlanActivity::class.java)
            val busData = "{'id':'${holder.linesBusId.text}','route':'${holder.linesBusRoute.text}','time':'${holder.linesBusTime.text}','direction':${holder.direction}}"
            val busPlanData = "{'startAddress':'$startAddress','endAddress':'$endAddress'}"
            intent.putExtra("busData",busData)
            intent.putExtra("busPlan",busPlanData)
            try {
                context.startActivity(intent)
            }catch (e:Exception){
                e.printStackTrace()
                Log.d("列表","123456")
            }
        }
    }


    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = myDataset.size



}
