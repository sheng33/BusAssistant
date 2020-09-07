package com.shengq.notificationmanager.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.shengq.notificationmanager.R
import com.shengq.notificationmanager.network.WBean


class AddPlanAdapter(private val myDataset: ArrayList<WBean>) :
    RecyclerView.Adapter<AddPlanAdapter.MyViewHolder>(),View.OnClickListener {
    var position:Int = 0
    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    // Each data item is just a string in this case that is shown in a TextView.
    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) ,View.OnClickListener{
        var linesBusId: TextView = view.findViewById(R.id.linesBusId)
        var linesBusRoute: TextView = view.findViewById(R.id.linesBusRoute)
        var linesBusTime: TextView = view.findViewById(R.id.linesBusTime)
        fun init(){
            this.itemView.setOnClickListener(this)
        }
        override fun onClick(v: View?) {
            Log.d("列表","linesBusId")
            Log.d("列表",linesBusId.text.toString())
        }
    }


    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): AddPlanAdapter.MyViewHolder {
        // create a new view
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.buslines_item, parent, false)
         view.setOnClickListener(this);
        // set the view's size, margins, paddings and layout parameters
        return MyViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.init()
        val plan = myDataset[position]
        holder.linesBusId.text = plan.busId
        holder.linesBusRoute.text = plan.routeNumber
        holder.linesBusTime.text = plan.startEndTM
    }


    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = myDataset.size
    override fun onClick(v: View?) {
    }


}
