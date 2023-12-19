package com.vic.vicwsp.Views.Activities.CreditNotes;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vic.vicwsp.Controllers.Helpers.CredtiNotes.CreditSavedCardsAdapter;
import com.vic.vicwsp.Controllers.Interfaces.Api;
import com.vic.vicwsp.Models.Response.SaveCards.Datum;
import com.vic.vicwsp.Models.Response.SaveCards.SaveCardsModel;
import com.vic.vicwsp.Network.RetrofitClient;
import com.vic.vicwsp.R;
import com.vic.vicwsp.Utils.Common;
import com.vic.vicwsp.Utils.Constants;
import com.vic.vicwsp.Utils.SharedPreference;

import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.vic.vicwsp.Utils.Common.showToast;

public class CreditSavedCards extends AppCompatActivity {

    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private ImageView back, btnAddCard;
    private ProgressDialog dialog;
    private ImageView ivListNull;
    private TextView tvListNull;

    private ArrayList<Datum> saveCardsModelArrayList;

    private int creditNoteId = 0;
    private CreditSavedCardsAdapter creditSavedCardsAdapter;
    private AlertDialog alertDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_cards);

        getDataFromBundle();
        initViews();
        setUpAdapter();
        getCardsFromApi();
    }

    private void getDataFromBundle() {
        creditNoteId = getIntent().getIntExtra("creditNoteId", 0);
    }

    private void initViews() {
        saveCardsModelArrayList = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerSaveCards);

        back = findViewById(R.id.ivToolbarBack);
        back.setOnClickListener(view -> finish());

        btnAddCard = findViewById(R.id.ivAddCard);
        btnAddCard.setOnClickListener(view -> {

            Intent intent = new Intent(CreditSavedCards.this, CreditStripe.class);
            intent.putExtra("creditNoteId", creditNoteId);
            startActivity(intent);
        });

        ivListNull = findViewById(R.id.ivListNull);
        tvListNull = findViewById(R.id.tvListNull);
    }

    private void setUpAdapter() {
        creditSavedCardsAdapter = new CreditSavedCardsAdapter(saveCardsModelArrayList, this);
        linearLayoutManager = new LinearLayoutManager(CreditSavedCards.this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(creditSavedCardsAdapter);
    }

    private void getCardsFromApi() {

        Common.showDialog(CreditSavedCards.this);
        Api api = RetrofitClient.getClient().create(Api.class);
        Call<SaveCardsModel> call = api.getUserCards("Bearer " +
                SharedPreference.getSimpleString(CreditSavedCards.this, Constants.accessToken));
        call.enqueue(new Callback<SaveCardsModel>() {
            @Override
            public void onResponse(Call<SaveCardsModel> call, Response<SaveCardsModel> response) {
                Common.dissmissDialog();
                try {

                    if (response.isSuccessful()) {

                        saveCardsModelArrayList.clear();

                        SaveCardsModel saveCardsModel = new SaveCardsModel(response.body().getData(), response.body().getStatus(), response.body().getMessage());
                        saveCardsModelArrayList.addAll(saveCardsModel.getData());

                        if (saveCardsModelArrayList.size() <= 0) {
                            ivListNull.setVisibility(View.VISIBLE);
                            tvListNull.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        } else {
                            ivListNull.setVisibility(View.GONE);
                            tvListNull.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);

                            creditSavedCardsAdapter.notifyDataSetChanged();
                        }

                    } else {
                        JSONObject jsonObject = new JSONObject(response.errorBody().string());
                        showToast(CreditSavedCards.this, jsonObject.getString("message"), false);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<SaveCardsModel> call, Throwable t) {
                Common.dissmissDialog();
                Log.d("Response", "onFailure: " + t.getMessage());
            }
        });
    }

    public void doPayment(String cardId) {

        Common.showDialog(CreditSavedCards.this);
        Api api = RetrofitClient.getClient().create(Api.class);
        Call<ResponseBody> call = api.postCreditPayment("Bearer " +
                SharedPreference.getSimpleString(CreditSavedCards.this, Constants.accessToken), creditNoteId, cardId, 1);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Common.dissmissDialog();
                try {

                    if (response.isSuccessful()) {
                        JSONObject jsonObject = new JSONObject(response.body().string());

                        alertDialog = new AlertDialog.Builder(CreditSavedCards.this)
                                .setCancelable(false)
                                .setMessage(jsonObject.getString("message"))
                                .setPositiveButton(getResources().getString(R.string.ok), (dialog, which) -> {
                                    alertDialog.dismiss();
                                    Common.goToMain(CreditSavedCards.this);

                                })
                                .show();


                    } else {
                        JSONObject jsonObject = new JSONObject(response.errorBody().string());
                        showToast(CreditSavedCards.this, jsonObject.getString("message"), false);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Common.dissmissDialog();
                Log.d("Response", "onFailure: " + t.getMessage());
            }
        });
    }

    public void deleteCard(int id, int position) {

        Common.showDialog(CreditSavedCards.this);
        Api api = RetrofitClient.getClient().create(Api.class);
        Call<ResponseBody> call = api.deleteUserCard("Bearer " +
                SharedPreference.getSimpleString(CreditSavedCards.this, Constants.accessToken), id);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Common.dissmissDialog();
                try {

                    if (response.isSuccessful()) {
                        JSONObject jsonObject = new JSONObject(response.body().string());

                        saveCardsModelArrayList.remove(position);

                        if (saveCardsModelArrayList.size() <= 0) {
                            ivListNull.setVisibility(View.VISIBLE);
                            tvListNull.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        } else {
                            ivListNull.setVisibility(View.GONE);
                            tvListNull.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);

                            creditSavedCardsAdapter.notifyDataSetChanged();
                        }
                        showToast(CreditSavedCards.this, jsonObject.getString("message"), true);

                    } else {
                        JSONObject jsonObject = new JSONObject(response.errorBody().string());
                        showToast(CreditSavedCards.this, jsonObject.getString("message"), false);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Common.dissmissDialog();
                Log.d("Response", "onFailure: " + t.getMessage());
            }
        });

    }


}
