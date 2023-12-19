package com.vic.vicwsp.Controllers.Helpers

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.vic.vicwsp.Models.Response.DriversTrackingList.Merchant
import com.vic.vicwsp.R

class CompanySiretRecyclerApdater() : RecyclerView.Adapter<CompanySiretRecyclerApdater.ViewHolder>() {

    private var dataArrayList: ArrayList<Merchant> = ArrayList()
    var context: Context? = null


    constructor(context: Context?, dataArrayList: ArrayList<Merchant>) : this() {
        this.context = context
        this.dataArrayList = dataArrayList
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_company_siret_recycler, parent, false))
    }

    override fun getItemCount(): Int {
        return dataArrayList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        try {
            holder.sellerName.text = dataArrayList[position].companyName
            holder.noOfItems.text = dataArrayList[position].items.toString()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var sellerName: TextView = itemView.findViewById(R.id.sellerName)
        var noOfItems: TextView = itemView.findViewById(R.id.noOfItems)
    }
}