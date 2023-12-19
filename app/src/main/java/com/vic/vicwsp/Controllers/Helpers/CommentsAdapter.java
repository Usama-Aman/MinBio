package com.vic.vicwsp.Controllers.Helpers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vic.vicwsp.Models.Response.Comments.Datum;
import com.vic.vicwsp.R;

import java.util.ArrayList;

public class CommentsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private ArrayList<Datum> commentsData = new ArrayList<>();

    public CommentsAdapter(Context context, ArrayList<Datum> commentsData) {
        this.context = context;
        this.commentsData = commentsData;

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v;
        if (viewType == VIEW_TYPE_ITEM) {
            v = inflater.inflate(R.layout.item_comments, parent, false);
            viewHolder = new Items(v);
        } else {
            v = inflater.inflate(R.layout.item_loader_pagination_products, parent, false);
            viewHolder = new Progress(v);
        }
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof Progress) {

        } else
            ((Items) holder).bind(position);

    }


    @Override
    public int getItemCount() {
        return commentsData == null ? 0 : commentsData.size();
    }


    @Override
    public int getItemViewType(int position) {
        return commentsData.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    private class Items extends RecyclerView.ViewHolder {

        private TextView name, date, comment;

        private Items(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.commentUserName);
            date = itemView.findViewById(R.id.commentDate);
            comment = itemView.findViewById(R.id.comment);

        }

        private void bind(int position) {

            name.setText(commentsData.get(position).getUser());
            date.setText(commentsData.get(position).getDate());
            comment.setText(commentsData.get(position).getComment());
        }
    }


    public class Progress extends RecyclerView.ViewHolder {

        private ProgressBar progressBar;

        private Progress(View v) {
            super(v);
            progressBar = itemView.findViewById(R.id.progressBarPagination);
        }
    }
}
