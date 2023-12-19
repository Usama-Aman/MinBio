package com.vic.vicwsp.Controllers.Helpers

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.vic.vicwsp.Models.Response.DriversTrackingList.Datum
import com.vic.vicwsp.R
import com.vic.vicwsp.Views.Activities.Buyer2Driver
import com.vic.vicwsp.Views.Activities.DriverRating
import com.vic.vicwsp.Views.Activities.OrderNavigationWithMapBox
import de.hdodenhof.circleimageview.CircleImageView

class OrderTrackingListAdapter() : RecyclerView.Adapter<OrderTrackingListAdapter.ViewHolder>() {

    private var dataArrayList: ArrayList<Datum> = ArrayList()
    var context: Context? = null
    private lateinit var companySiretRecyclerApdater: CompanySiretRecyclerApdater
    private lateinit var orderNumber: String


    constructor(context: Context?, dataArrayList: ArrayList<Datum>, orderNumber: String) : this() {
        this.context = context
        this.dataArrayList = dataArrayList
        this.orderNumber = orderNumber
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_orders_tracking_list, parent, false))
    }

    override fun getItemCount(): Int {
        return dataArrayList.size
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {


        holder.tvTrackingOrderNumber.setText(context?.resources?.getString(R.string.orderNumberLabel, dataArrayList[position].orderId))

        //Sellers Names
        companySiretRecyclerApdater = CompanySiretRecyclerApdater(context, dataArrayList[position].merchants)
        holder.companySiretRecycler.layoutManager = LinearLayoutManager(context)
        holder.companySiretRecycler.adapter = companySiretRecyclerApdater

        if (dataArrayList[position].deliveryStatus.equals("Pending")) {
            holder.trackingStatus.text = context?.resources?.getString(R.string.deliveryPending)
            holder.trackingDriverLayout.visibility = View.GONE
        } else if (dataArrayList[position].deliveryStatus.equals("Completed")) {
            holder.trackingStatus.text = context?.resources?.getString(R.string.deliveryCompleted)
            holder.trackingDriverLayout.visibility = View.VISIBLE
            showDriverInfo(holder, position)

        } else if (dataArrayList[position].deliveryStatus.equals("Accepted")) {
            holder.trackingStatus.text = context?.resources?.getString(R.string.deliveryAccepted)
            holder.trackingDriverLayout.visibility = View.VISIBLE

            showDriverInfo(holder, position)
        }

        holder.ivCarPlate.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(dataArrayList[position].driver.truckPlate))
            context?.startActivity(intent)
        }


        holder.tvTrackingDriverName.text = dataArrayList[position].driver.name

        if (dataArrayList[position].driver.isReviewed == 1) {
            holder.pinTrackLayout.visibility = View.GONE
        } else {
            holder.pinTrackLayout.visibility = View.VISIBLE
            if (dataArrayList[position].deliveryStatus.equals("Completed")) {
                holder.btnTrack.setImageDrawable(context?.resources?.getDrawable(R.drawable.star))
                holder.trackingLabel.text = context?.resources?.getString(R.string.rateLabel)
                holder.chatDriverLayout.visibility = View.GONE
                holder.chatDriverLayout.isEnabled = false
            } else if (dataArrayList[position].deliveryStatus.equals("Accepted")) {
                holder.btnTrack.setImageDrawable(context?.resources?.getDrawable(R.drawable.map_pin))
                holder.trackingLabel.text = context?.resources?.getString(R.string.viewMapLabel)
                holder.chatDriverLayout.visibility = View.VISIBLE
                holder.chatDriverLayout.isEnabled = true
            }
        }

        holder.chatDriverLayout.setOnClickListener {
            val intent = Intent(context, Buyer2Driver::class.java)
            intent.putExtra("orderId", dataArrayList[position].id)
            intent.putExtra("orderNumber", orderNumber)
            intent.putExtra("userName", dataArrayList[position].driver.name)
            context?.startActivity(intent)
        }

        holder.pinTrackLayout.setOnClickListener {

            if (dataArrayList[position].deliveryStatus.equals("Completed")) {
                val bundle = Bundle()
                bundle.putInt("orderId", dataArrayList[position].id)
                bundle.putInt("driverId", dataArrayList[position].driver.id);
                bundle.putInt("position", position);
                val intent = Intent(context, DriverRating::class.java)
                intent.putExtras(bundle)
                context?.startActivity(intent);

            } else if (dataArrayList[position].deliveryStatus.equals("Accepted")) {
                val intent = Intent(context, OrderNavigationWithMapBox::class.java)
                intent.putExtra("driverLat", dataArrayList[position].driver.currentLat)
                intent.putExtra("driverLng", dataArrayList[position].driver.currentLng)
                intent.putExtra("deliveryLat", dataArrayList[position].deliveryLat)
                intent.putExtra("deliveryLng", dataArrayList[position].deliveryLng)
                intent.putExtra("driverId", dataArrayList[position].driver.id)
                context?.startActivity(intent)
            }
        }
    }

    private fun showDriverInfo(holder: ViewHolder, position: Int) {

        Glide.with(context!!).load(dataArrayList[position].driver.profileImage)
                .placeholder(context?.resources?.getDrawable(R.drawable.place_holder, null))
                .into(holder.driverImage)

        val truckPlate = dataArrayList[position].driver.truckPlate

        val ext = truckPlate.substring(truckPlate.lastIndexOf("."))

        if (ext.equals(".png") || ext.equals(".jpg") || ext.equals(".jpeg")) {
            holder.ivCarPlate.isEnabled = false
            Glide.with(context!!).load(dataArrayList[position].driver.truckPlate)
                    .placeholder(context?.resources?.getDrawable(R.drawable.place_holder, null))
                    .into(holder.ivCarPlate)
        } else {
            holder.ivCarPlate.isEnabled = true
            Glide.with(context!!).load(context?.resources?.getDrawable(R.drawable.ic_pdf, null))
                    .placeholder(context?.resources?.getDrawable(R.drawable.place_holder, null))
                    .into(holder.ivCarPlate)
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var tvTrackingOrderNumber: TextView = itemView.findViewById(R.id.tvTrackingOrderNumber)
        var tvTrackingDriverName: TextView = itemView.findViewById(R.id.tvTrackingDriverName)
        var companySiretRecycler: RecyclerView = itemView.findViewById(R.id.companySiretRecycler)
        var trackingDriverLayout: ConstraintLayout = itemView.findViewById(R.id.trackingDriverLayout)
        var driverImage: CircleImageView = itemView.findViewById(R.id.driverImage)
        var btnTrack: ImageView = itemView.findViewById(R.id.btnTrack)
        var pinTrackLayout: ConstraintLayout = itemView.findViewById(R.id.pinTrackLayout)
        var chatDriverLayout: ConstraintLayout = itemView.findViewById(R.id.chatDriverLayout)
        var ivCarPlate: ImageView = itemView.findViewById(R.id.ivCarPlate)
        var trackingStatus: TextView = itemView.findViewById(R.id.trackingStatus)
        var trackingLabel: TextView = itemView.findViewById(R.id.trackingLabel)

    }


}