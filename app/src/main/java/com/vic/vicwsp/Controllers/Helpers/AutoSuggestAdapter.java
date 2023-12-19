package com.vic.vicwsp.Controllers.Helpers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.vic.vicwsp.Models.CustomObject;

import java.util.ArrayList;
import java.util.List;

public class AutoSuggestAdapter extends ArrayAdapter {
    private Context context;
    private int resource;
    private ArrayList<CustomObject> items;
    private ArrayList<CustomObject> tempItems;
    private ArrayList<CustomObject> suggestions;

    public AutoSuggestAdapter(Context context, int resource, ArrayList<CustomObject> items) {
        super(context, resource, 0, items);

        this.context = context;
        this.resource = resource;
        this.items = items;
        tempItems = new ArrayList<CustomObject>(items);
        suggestions = new ArrayList<CustomObject>();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(resource, parent, false);
        }

        String item = items.get(position).getName();

        if (item != null && view instanceof TextView) {
            ((TextView) view).setText(item);
        }

        return view;
    }

    @Override
    public Filter getFilter() {
        return nameFilter;
    }

    Filter nameFilter = new Filter() {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if (constraint != null) {
                suggestions.clear();
                for (CustomObject names : tempItems) {
                    if (names.getName().toLowerCase().startsWith(constraint.toString().toLowerCase())) {
                        suggestions.add(names);
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = suggestions;
                filterResults.count = suggestions.size();
                return filterResults;
            } else {
                return new FilterResults();
            }
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            List<CustomObject> filterList = (ArrayList<CustomObject>) results.values;
            if (results != null && results.count > 0) {
                clear();
                for (CustomObject item : filterList) {
                    add(item);
                    notifyDataSetChanged();
                }
            }
        }
    };
}