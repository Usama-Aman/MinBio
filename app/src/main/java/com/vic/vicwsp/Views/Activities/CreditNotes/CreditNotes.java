package com.vic.vicwsp.Views.Activities.CreditNotes;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.vic.vicwsp.Controllers.Helpers.CredtiNotes.CreditNotesAdapter;
import com.vic.vicwsp.Controllers.Interfaces.Api;
import com.vic.vicwsp.Models.Response.CreditNotes.CreditNotesModel;
import com.vic.vicwsp.Models.Response.CreditNotes.Datum;
import com.vic.vicwsp.Network.RetrofitClient;
import com.vic.vicwsp.R;
import com.vic.vicwsp.Utils.Common;
import com.vic.vicwsp.Utils.Constants;
import com.vic.vicwsp.Utils.SharedPreference;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.vic.vicwsp.Utils.Common.showToast;

public class CreditNotes extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CreditNotesAdapter creditNotesAdapter;
    private ImageView back;
    private SwipeRefreshLayout swipeRefreshLayout;
    private int CURRENT_PAGE = 0, LAST_PAGE = 0;
    private boolean isLoading = false;
    private ArrayList<Datum> creditNotesList = new ArrayList<>();

    private ImageView ivListNull;
    private TextView listNull;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credit_notes);

        initViews();
        setUpAdapter();
        initScrollListener();
        Common.showDialog(this);
        getDataFromApi(0, false);
    }

    private void initViews() {
        recyclerView = findViewById(R.id.creditNotesRecycler);

        swipeRefreshLayout = findViewById(R.id.creditsSwipe);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            getDataFromApi(0, true);
        });

        back = findViewById(R.id.creditNotesBack);
        back.setOnClickListener(v -> {
            finish();
        });

        listNull = findViewById(R.id.tvListNull);
        ivListNull = findViewById(R.id.ivListNull);
    }

    private void setUpAdapter() {
        creditNotesAdapter = new CreditNotesAdapter(creditNotesList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(creditNotesAdapter);
    }

    //Initializing the scroll view to check the last vale to show the prograss bar for pagination
    private void initScrollListener() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                if (!isLoading) {
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == creditNotesList.size() - 1) {
                        recyclerView.post(new Runnable() {
                            @Override
                            public void run() {
                                loadMore();
                            }
                        });
                        isLoading = true;
                    }
                }
            }
        });
    }


    private void loadMore() {
        try {
            creditNotesList.add(null);
            creditNotesAdapter.notifyItemInserted(creditNotesList.size() - 1);

            if (CURRENT_PAGE != LAST_PAGE) {
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        getDataFromApi(CURRENT_PAGE, false);
                    }
                }, 2000);
            } else {
                recyclerView.post(new Runnable() {
                    @Override
                    public void run() {
                        creditNotesList.remove(creditNotesList.size() - 1);
                        creditNotesAdapter.notifyItemRemoved(creditNotesList.size());
                    }
                });
            }

            creditNotesAdapter.notifyDataSetChanged();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void getDataFromApi(int c, boolean isRefresh) {
        Api api = RetrofitClient.getClient().create(Api.class);
        Call<CreditNotesModel> call = api.getCreditNotes("Bearer " +
                SharedPreference.getSimpleString(CreditNotes.this, Constants.accessToken), c);
        call.enqueue(new Callback<CreditNotesModel>() {
            @Override
            public void onResponse(Call<CreditNotesModel> call, Response<CreditNotesModel> response) {
                Common.dissmissDialog();
                try {
                    if (response.isSuccessful()) {

                        if (isRefresh) {
                            swipeRefreshLayout.setRefreshing(false);
                            creditNotesList.clear();
                        }

                        LAST_PAGE = response.body().getMeta().getLastPage();
                        CURRENT_PAGE = response.body().getMeta().getCurrentPage();

                        if (creditNotesList.size() > 0) {
                            creditNotesList.remove(creditNotesList.size() - 1);
                            creditNotesAdapter.notifyItemRemoved(creditNotesList.size());
                        }

                        creditNotesList.addAll(response.body().getData());

                        if (creditNotesList.size() == 0) {
                            listNull.setVisibility(View.VISIBLE);
                            ivListNull.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        } else {
                            listNull.setVisibility(View.GONE);
                            ivListNull.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                        }

                        creditNotesAdapter.notifyDataSetChanged();
                        isLoading = false;

                    } else {
                        JSONObject jsonObject = new JSONObject(response.errorBody().string());
                        showToast(CreditNotes.this, jsonObject.getString("message"), false);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<CreditNotesModel> call, Throwable t) {
                Common.dissmissDialog();
                Log.d("Response", "onFailure: " + t.getMessage());
            }
        });
    }
}
