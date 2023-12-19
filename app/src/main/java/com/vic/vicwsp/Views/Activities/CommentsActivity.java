package com.vic.vicwsp.Views.Activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vic.vicwsp.Controllers.Helpers.CommentsAdapter;
import com.vic.vicwsp.Controllers.Interfaces.Api;
import com.vic.vicwsp.Models.Response.Comments.CommentsModel;
import com.vic.vicwsp.Models.Response.Comments.Datum;
import com.vic.vicwsp.Network.RetrofitClient;
import com.vic.vicwsp.R;
import com.vic.vicwsp.Utils.Constants;
import com.vic.vicwsp.Utils.SharedPreference;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import me.zhanghai.android.materialratingbar.MaterialRatingBar;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.vic.vicwsp.Utils.Common.showToast;

public class CommentsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CommentsAdapter commentsAdapter;
    private LinearLayoutManager linearLayoutManager;
    private boolean isLoading = false;
    private int CURRENT_PAGE = 1, LAST_PAGE = -1;
    private int sellerId = 0;
    private String TAG = "CommentActivity.this";
    private ConstraintLayout mainConstraint, loaderConstraint;
    private CommentsModel commentsModel;
    private ArrayList<Datum> commentsData = new ArrayList<>();
    private MaterialRatingBar ratingPricing, ratingProductQ, ratingAvailability, ratingRelation, ratingTrust;
    private TextView tvListNull;
    private ImageView ivListNull, back;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_comments);
        initViews();
        setAdapter();
        getComments(CURRENT_PAGE);
        initScrollListener();
    }

    private void initViews() {

        Bundle bundle = getIntent().getExtras();
        sellerId = bundle.getInt("sellerId");
        recyclerView = findViewById(R.id.commentsRecycler);
        mainConstraint = findViewById(R.id.mainConstraint);
        loaderConstraint = findViewById(R.id.loaderConstraint);
        ratingAvailability = findViewById(R.id.ratingAvailability);
        ratingPricing = findViewById(R.id.ratingPricing);
        ratingProductQ = findViewById(R.id.ratingProductQ);
        ratingRelation = findViewById(R.id.ratingRelation);
        ratingTrust = findViewById(R.id.ratingTrust);
        tvListNull = findViewById(R.id.tvListNull);
        ivListNull = findViewById(R.id.ivListNull);

        back = findViewById(R.id.commentsToolbarBack);
        back.setOnClickListener(view -> finish());

    }

    private void setAdapter() {
        commentsAdapter = new CommentsAdapter(CommentsActivity.this, commentsData);
        linearLayoutManager = new LinearLayoutManager(CommentsActivity.this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(commentsAdapter);
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
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == commentsData.size() - 1) {
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
//        isLastPage = true;
        try {
            commentsData.add(null);
            commentsAdapter.notifyItemInserted(commentsData.size() - 1);


            if (CURRENT_PAGE != LAST_PAGE) {
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        getComments(CURRENT_PAGE + 1);
                    }
                }, 2000);
            } else {
                recyclerView.post(new Runnable() {
                    @Override
                    public void run() {
                        commentsData.remove(commentsData.size() - 1);
                        commentsAdapter.notifyItemRemoved(commentsData.size());
                    }
                });
            }

            commentsAdapter.notifyDataSetChanged();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void getComments(int c) {

        Api api = RetrofitClient.getClient().create(Api.class);
        Call<CommentsModel> call = api.getComments("Bearer " +
                SharedPreference.getSimpleString(CommentsActivity.this, Constants.accessToken), sellerId, c);

        call.enqueue(new Callback<CommentsModel>() {
            @Override
            public void onResponse(Call<CommentsModel> call, Response<CommentsModel> response) {

                try {
                    if (response.isSuccessful()) {
                        LAST_PAGE = response.body().getMeta().getLastPage();
                        CURRENT_PAGE = response.body().getMeta().getCurrentPage();

                        if (commentsData.size() > 0) {
                            commentsData.remove(commentsData.size() - 1);
                            commentsAdapter.notifyItemRemoved(commentsData.size());
                        }

                        commentsModel = new CommentsModel(response.body().getData(), response.body().getLinks(), response.body().getMeta(),
                                response.body().getRating(), response.body().getStatus(), response.body().getMessage());

                        commentsData.addAll(commentsModel.getData());

                        if (commentsData.size() > 0) {
                            loaderConstraint.setVisibility(View.GONE);
                            mainConstraint.setVisibility(View.VISIBLE);

                            if (commentsModel.getMeta().getCurrentPage() == 0 || commentsModel.getMeta().getCurrentPage() == 1) {

                                ratingPricing.setRating(Float.parseFloat (commentsModel.getRating().getPricing()));
                                ratingAvailability.setRating(Float.parseFloat (commentsModel.getRating().getAvailability()));
                                ratingProductQ.setRating(Float.parseFloat (commentsModel.getRating().getQuality()));
                                ratingRelation.setRating(Float.parseFloat (commentsModel.getRating().getRelation()));
                                ratingTrust.setRating(Float.parseFloat (commentsModel.getRating().getTrustrelation()));
                            }

                            commentsAdapter.notifyDataSetChanged();
                            isLoading = false;


                        } else {
                            loaderConstraint.setVisibility(View.GONE);
                            ivListNull.setVisibility(View.VISIBLE);
                            tvListNull.setVisibility(View.VISIBLE);
                        }

                    } else {
                        loaderConstraint.setVisibility(View.GONE);
                        ivListNull.setVisibility(View.VISIBLE);
                        tvListNull.setVisibility(View.VISIBLE);

                        JSONObject jsonObject = new JSONObject(response.errorBody().string());
                        showToast(CommentsActivity.this, jsonObject.getString("message"), false);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<CommentsModel> call, Throwable t) {
                loaderConstraint.setVisibility(View.GONE);
                ivListNull.setVisibility(View.VISIBLE);
                tvListNull.setVisibility(View.VISIBLE);
                Log.d(TAG, "onFailure: ");
            }
        });

    }
}
