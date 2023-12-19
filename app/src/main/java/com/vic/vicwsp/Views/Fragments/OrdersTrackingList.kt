package com.vic.vicwsp.Views.Fragments

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.vic.vicwsp.Controllers.Helpers.OrderTrackingListAdapter
import com.vic.vicwsp.Controllers.Interfaces.Api
import com.vic.vicwsp.Models.Response.DriversTrackingList.Datum
import com.vic.vicwsp.Models.Response.DriversTrackingList.DriverTrackingListModel
import com.vic.vicwsp.Network.RetrofitClient
import com.vic.vicwsp.R
import com.vic.vicwsp.Utils.Common
import com.vic.vicwsp.Utils.Constants
import com.vic.vicwsp.Utils.SharedPreference
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OrdersTrackingList : Fragment() {

    private lateinit var back: ImageView
    private lateinit var recyclerView: RecyclerView
    private lateinit var orderTrackingListAdapter: OrderTrackingListAdapter
    private lateinit var v: View
    private lateinit var driverTrackingListModel: DriverTrackingListModel
    private var dataArrayList: ArrayList<Datum> = ArrayList()
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private var orderId: Int = 0

    val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
//            if (intent.action.equals("DriverRating")) {
            dataArrayList.clear()
            callApi(context)
//            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        v = inflater.inflate(R.layout.activity_orders_tracking, container, false)

        orderId = arguments?.getInt("orderId") ?: 0

        initViews()
        setAdapter()
        if (dataArrayList.size == 0) {
            if (SharedPreference.getBoolean(context, "FromNoti"))
                SharedPreference.saveBoolean(context, "FromNoti", false)
            else
                Common.showDialog(context)
            callApi(context)
            LocalBroadcastManager.getInstance(context!!).registerReceiver(broadcastReceiver, IntentFilter("DriverRating"))
        }
        return v;
    }

    private fun setAdapter() {
        orderTrackingListAdapter = OrderTrackingListAdapter(context, dataArrayList, orderId.toString())
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = orderTrackingListAdapter
    }


    private fun initViews() {
        recyclerView = v.findViewById(R.id.orderTrackingListRecycler)
        swipeRefreshLayout = v.findViewById(R.id.driversSwipeToRefresh)

        val cartConstraintLayout: ConstraintLayout = activity!!.findViewById(R.id.cartConstraintLayout)
        cartConstraintLayout.visibility = View.VISIBLE

        val ticker: RecyclerView = activity!!.findViewById(R.id.pricesTicker)
        ticker.visibility = View.GONE

        val back: ImageView = activity!!.findViewById(R.id.ivToolbarBack)
        back.visibility = View.VISIBLE

        swipeRefreshLayout.setOnRefreshListener {
            dataArrayList.clear()
            callApi(context)
        }
    }


    fun callApi(context: Context?) {
        val api: Api = RetrofitClient.getClient().create(Api::class.java)

        val call: Call<DriverTrackingListModel> = api.getNavigationLatLng("Bearer " +
                SharedPreference.getSimpleString(context, Constants.accessToken), orderId);

        call.enqueue(object : Callback<DriverTrackingListModel> {

            override fun onResponse(call: Call<DriverTrackingListModel>, response: Response<DriverTrackingListModel>) {
                Common.dissmissDialog()
                swipeRefreshLayout.isRefreshing = false
                try {

                    if (response.isSuccessful) {

                        val body = response.body()
                        driverTrackingListModel = body!!

                        for (i in driverTrackingListModel.data.indices) {
                            dataArrayList.add(driverTrackingListModel.data[i])
                        }

                        if (dataArrayList.size > 0) orderTrackingListAdapter.notifyDataSetChanged()
                        else Common.showToast(activity, "No Data", false)

                    } else {

                        val errorBody = response.message()
                        Common.showToast(activity, errorBody, false)

                    }

                    SharedPreference.saveBoolean(context, "ComingFromNoti", false);

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<DriverTrackingListModel>, t: Throwable) {

            }

        })
    }

}