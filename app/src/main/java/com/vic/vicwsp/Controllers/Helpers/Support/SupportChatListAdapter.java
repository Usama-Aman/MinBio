package com.vic.vicwsp.Controllers.Helpers.Support;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.vic.vicwsp.Models.Response.Support.SupportChat.SupportChatData;
import com.vic.vicwsp.R;
import com.vic.vicwsp.Utils.AppUtils;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


public class SupportChatListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;

    private ArrayList<SupportChatData> compliantsListData;

    public SupportChatListAdapter(Context context, ArrayList<SupportChatData> compliantsListData) {
        this.context = context;
        this.compliantsListData = compliantsListData;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.item_complain_detail, parent, false);
        viewHolder = new Items(v);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        //((Items) holder).messageName.setVisibility(View.GONE);
        ((Items) holder).messageName.setText("");

        if (compliantsListData.get(position).getCommentFrom().equals("user")) {
            if (AppUtils.isSet(compliantsListData.get(position).getSupportChatUserDetail().getFullName())) {
                ((Items) holder).messageName.setText(compliantsListData.get(position).getSupportChatUserDetail().getFullName());
                ((Items) holder).messageName.setVisibility(View.VISIBLE);
            }

            if (AppUtils.isSet(compliantsListData.get(position).getSupportChatUserDetail().getImagePath())) {
                Glide.with(context).load(compliantsListData.get(position).getSupportChatUserDetail().getImagePath()).into(((Items) holder).userImage);
            }
        } else {
            if (AppUtils.isSet(compliantsListData.get(position).getAdminDetail().getName())) {
                ((Items) holder).messageName.setText(compliantsListData.get(position).getAdminDetail().getName());
            }

        }


        ((Items) holder).messageDate.setText("");
        if (AppUtils.isSet(compliantsListData.get(position).getCreatedAt())) {
            ((Items) holder).messageDate.setText(AppUtils.formatDateFromDateTime(compliantsListData.get(position).getCreatedAt()));
        }

        ((Items) holder).textMessage.setText("");
        if (AppUtils.isSet(compliantsListData.get(position).getComment())) {
            ((Items) holder).textMessage.setText(compliantsListData.get(position).getComment());
        }

        if (AppUtils.isSet(compliantsListData.get(position).getImagePath())) {
            Glide.with(context).load(compliantsListData.get(position).getImagePath()).
                    fitCenter()
                    .into(((Items) holder).userImage);
        }

    }

    @Override
    public int getItemCount() {
        return compliantsListData == null ? 0 : compliantsListData.size();
    }

    public class Items extends RecyclerView.ViewHolder {

        RelativeLayout constraintLayout;
        TextView messageName, messageDate, textMessage;
        CircleImageView userImage;

        public Items(@NonNull View itemView) {
            super(itemView);
            constraintLayout = itemView.findViewById(R.id.compDetailConstraint);
            messageName = itemView.findViewById(R.id.messageName);
            messageDate = itemView.findViewById(R.id.messageDate);
            textMessage = itemView.findViewById(R.id.textMessage);
            userImage = itemView.findViewById(R.id.userImage);
        }
    }


}