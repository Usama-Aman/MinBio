package com.vic.vicwsp.Controllers.Helpers;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.vic.vicwsp.Models.VendorResearchModel;

import java.util.ArrayList;

public class SpinnerAdapter extends ArrayAdapter<VendorResearchModel> {

    // Your sent context
    private Context context;
    private ArrayList<VendorResearchModel> values;
    private int resourceId;
    ;

    public SpinnerAdapter(Context context, int textViewResourceId,
                          ArrayList<VendorResearchModel> values) {
        super(context, textViewResourceId, values);
        this.context = context;
        this.values = values;
        this.resourceId = textViewResourceId;
    }

    @Override
    public int getCount() {
        return values.size();
    }

    @Override
    public VendorResearchModel getItem(int position) {
        return values.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    // And the "magic" goes here
    // This is for the "passive" state of the spinner
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        return setView(position, convertView, parent);
    }

    private View setView(int position, View convertView, ViewGroup parent) {
        TextView label = (TextView) super.getView(position, convertView, parent);
        label.setTextColor(Color.BLACK);
        label.setText(values.get(position).get_name());
        return label;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v = super.getDropDownView(position, convertView, parent);
        TextView tv = ((TextView) v);
        tv.setText(values.get(position).get_name());
        tv.setTextColor(Color.BLACK);
        return v;
    }

    @Override
    public void setDropDownViewResource(int resource) {
        super.setDropDownViewResource(resource);
    }
}
