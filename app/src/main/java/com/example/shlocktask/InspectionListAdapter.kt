package com.example.shlocktask

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.shlocktask.model.InspectionList


class InspectionListAdapter(
    private val mContext: Context,
    private var data: ArrayList<InspectionList.InspectionItem>
) :
    RecyclerView.Adapter<InspectionListAdapter.CustomViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val inflater = LayoutInflater.from(mContext)
        var view: View? = null
        view = inflater.inflate(R.layout.employee_list_item, parent, false)
        return CustomViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        holder.hdnScheduleCode.text = data[position].LTE_INSP_Label2
        holder.LTEINSPLabel1.text = data[position].LTE_INSP_Label1
        holder.LTEINSPLabel3.text = data[position].LTE_INSP_Label3
        holder.LTEINSPLabel5.text =data[position].LTE_INSP_Label5
        print("msg===="+data[position].hdn_ScheduleCode)
    }

    override fun getItemCount(): Int {
        println("size===="+data.size)
        return data.size
    }

    inner class CustomViewHolder(v: View?) : ViewHolder(v!!) {
        var hdnScheduleCode: TextView
        var LTEINSPLabel1: TextView
        var LTEINSPLabel3: TextView
        var LTEINSPLabel5: TextView

        init {
            hdnScheduleCode = v!!.findViewById<View>(R.id.hdn_ScheduleCode) as TextView
            LTEINSPLabel1 = v.findViewById<View>(R.id.LTE_INSP_Label1) as TextView
            LTEINSPLabel3 = v.findViewById<View>(R.id.LTE_INSP_Label3) as TextView
            LTEINSPLabel5 = v.findViewById<View>(R.id.LTE_INSP_Label5) as TextView
            
        }
    }

    fun updateList(list: ArrayList<InspectionList.InspectionItem>) {
        data = list
        print("\n======list"+list.size+"\n")
        notifyDataSetChanged()
    }
}