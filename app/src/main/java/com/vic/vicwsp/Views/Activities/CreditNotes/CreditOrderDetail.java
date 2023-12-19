package com.vic.vicwsp.Views.Activities.CreditNotes;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vic.vicwsp.Controllers.Helpers.CredtiNotes.CreditNotesDetailAdapter;
import com.vic.vicwsp.Controllers.Interfaces.Api;
import com.vic.vicwsp.Models.Response.CreditNotesDetails.CreditNotesDetailModel;
import com.vic.vicwsp.Models.Response.CreditNotesDetails.Data;
import com.vic.vicwsp.Network.RetrofitClient;
import com.vic.vicwsp.R;
import com.vic.vicwsp.Utils.Common;
import com.vic.vicwsp.Utils.Constants;
import com.vic.vicwsp.Utils.SharedPreference;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.vic.vicwsp.Utils.Common.showToast;

public class CreditOrderDetail extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CreditNotesDetailAdapter creditNotesDetailAdapter;
    private Data creditDetailData;
    private ImageView back;
    private int orderId = 0;
    private String creditNoteStatus = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credit_notes_detail);
        initViews();

        orderId = getIntent().getIntExtra("id", 0);
        creditNoteStatus = getIntent().getStringExtra("status");

        if (orderId > 0)
            getDataFromApi();
    }

    private void getDataFromApi() {
        Common.showDialog(this);
        Api api = RetrofitClient.getClient().create(Api.class);
        Call<CreditNotesDetailModel> call = api.getCreditNoteDetail("Bearer " +
                SharedPreference.getSimpleString(CreditOrderDetail.this, Constants.accessToken), orderId);
        call.enqueue(new Callback<CreditNotesDetailModel>() {
            @Override
            public void onResponse(Call<CreditNotesDetailModel> call, Response<CreditNotesDetailModel> response) {
                Common.dissmissDialog();
                try {
                    if (response.isSuccessful()) {

                        creditDetailData = response.body().getData();

                        if (creditDetailData != null)
                            initRecyclerView();

                    } else {
                        JSONObject jsonObject = new JSONObject(response.errorBody().string());
                        showToast(CreditOrderDetail.this, jsonObject.getString("message"), false);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<CreditNotesDetailModel> call, Throwable t) {
                Common.dissmissDialog();
                Log.d("Response", "onFailure: " + t.getMessage());
            }
        });
    }


    //Initializing the views
    private void initViews() {
        recyclerView = findViewById(R.id.cnDetailRecycler);
        back = findViewById(R.id.creditNotesDetailBack);
        back.setOnClickListener(v -> finish());
    }

    //Initializing the adapter for recycler view
    private void initRecyclerView() {
        creditNotesDetailAdapter = new CreditNotesDetailAdapter(creditDetailData,creditNoteStatus);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(creditNotesDetailAdapter);
    }


}
