package com.vic.vicwsp.Controllers.Helpers;

import android.app.Dialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.vic.vicwsp.Models.Response.SendComplaintResponse.Item;
import com.vic.vicwsp.R;

import java.util.ArrayList;

public class MultiSelectSearchSpinner extends Dialog {

    private ArrayList<Item> filteredList;
    private ArrayList<Item> unFilteredList;
    private ListView workflowListView;
    private TextView dialogTitle;
    private EditText editTextSearch;
    private Button btnCloseSpinnerDialog, btnDoneSpinnerDialog, btnSelectAllSpinnerDialog;
    private Context context;
    private OnMultiSelectDoneListner onMultiSelectDoneListner;
    private boolean isDoneHit = false;
    private ArrayList<Integer> result = new ArrayList<>();

    private ArrayList<Integer> trueIds = new ArrayList<>();

    public MultiSelectSearchSpinner(@NonNull Context context, ArrayList<Item> strings) {
        super(context);
        this.context = context;
        this.filteredList = strings;
        this.unFilteredList = strings;

        for (int i = 0; i < unFilteredList.size(); i++)
            if (unFilteredList.get(i).isChecked())
                trueIds.add(unFilteredList.get(i).getId());

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_dialog_list_layout);
        dialogTitle = findViewById(R.id.dilog_list_title);
        workflowListView = findViewById(R.id.listview_dialog);
        editTextSearch = findViewById(R.id.editTextSpinnerDialog);
        btnCloseSpinnerDialog = findViewById(R.id.btnCloseSpinnerDialog);
        btnDoneSpinnerDialog = findViewById(R.id.btnDoneSpinnerDialog);
        btnSelectAllSpinnerDialog = findViewById(R.id.btnSelectAllSpinnerDialog);

        dialogTitle.setText(context.getResources().getString(R.string.select_items_complaint));

        ListAdapter listAdapter = new ListAdapter(context, unFilteredList);
        workflowListView.setAdapter(listAdapter);

        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                listAdapter.getFilter().filter(s.toString());
            }
        });

        btnCloseSpinnerDialog.setOnClickListener(v -> {
            dismiss();
        });

        btnDoneSpinnerDialog.setOnClickListener(v -> {
            for (int i = 0; i < filteredList.size(); i++) {
                if (trueIds.contains(filteredList.get(i).getId()))
                    filteredList.get(i).setChecked(true);
                else
                    filteredList.get(i).setChecked(false);
            }
            onMultiSelectDoneListner.onMultiSelectDone(trueIds);

            dismiss();
        });

        btnSelectAllSpinnerDialog.setOnClickListener(v -> {
            editTextSearch.setText("");
            listAdapter.notifyDataSetChanged();
            for (int i = 0; i < filteredList.size(); i++)
                if (!trueIds.contains(filteredList.get(i).getId()))
                    trueIds.add(filteredList.get(i).getId());
        });
    }

    private class ListAdapter extends BaseAdapter implements Filterable {
        private LayoutInflater mInflater;


        public ListAdapter(Context c, ArrayList<Item> list) {
            context = c;
            filteredList = list;
            unFilteredList = list;
            this.mInflater = LayoutInflater.from(c);
        }

        @Override
        public int getCount() {
            return filteredList.size();
        }

        @Override
        public Object getItem(int position) {
            return filteredList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            try {
                if (convertView == null) {
                    holder = new ViewHolder();
                    convertView = mInflater.inflate(R.layout.item_multiselect_dialog_layout, null);
                    holder.name = convertView.findViewById(R.id.multiSelectName);
                    holder.checkBox = convertView.findViewById(R.id.multiSelectCheckBox);
                    holder.constraintLayout = convertView.findViewById(R.id.constraintItemMultiSelect);
                    convertView.setTag(holder);

                } else {
                    holder = (ViewHolder) convertView.getTag();
                }
                holder.name.setText(filteredList.get(position).getValue());

                if (trueIds.contains(filteredList.get(position).getId()))
                    holder.checkBox.setChecked(true);
                else
                    holder.checkBox.setChecked(false);

                holder.constraintLayout.setOnClickListener(v -> {
                    if (holder.checkBox.isChecked()) {
                        holder.checkBox.setChecked(false);
                    } else {
                        holder.checkBox.setChecked(true);
                    }
                });

                holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (isChecked) {
                        if (!trueIds.contains(filteredList.get(position).getId()))
                            trueIds.add(filteredList.get(position).getId());
                    } else {
                        if (trueIds.contains(filteredList.get(position).getId()))
                            trueIds.remove(Integer.valueOf(filteredList.get(position).getId()));
                    }
                });

            } catch (
                    Exception e) {
                e.printStackTrace();
            }

            return convertView;
        }

        @Override
        public Filter getFilter() {
            Filter filter = new Filter() {
                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    if (results.count == 0) {
                        notifyDataSetInvalidated();
                    } else {

                        filteredList = (ArrayList<Item>) results.values;
                        notifyDataSetChanged();
                    }
                }

                @Override
                protected FilterResults performFiltering(CharSequence charSequence) {

                    String charString = charSequence.toString();
                    if (charString.isEmpty()) {
                        filteredList = unFilteredList;
                    } else {
                        ArrayList<Item> fl = new ArrayList<>();
                        for (Item row : unFilteredList) {
                            if (row.getValue().toLowerCase().startsWith(charString.toLowerCase())) {
                                fl.add(row);
                            }
                        }

                        filteredList = fl;
                    }

                    FilterResults filterResults = new FilterResults();
                    filterResults.values = filteredList;
                    return filterResults;

                }
            };

            return filter;
        }

        private class ViewHolder {
            TextView name;
            CheckBox checkBox;
            ConstraintLayout constraintLayout;
        }
    }

    public void setOnworkflowlistclicklistener(OnMultiSelectDoneListner listener) {
        this.onMultiSelectDoneListner = listener;
    }

    public interface OnMultiSelectDoneListner {
        void onMultiSelectDone(ArrayList<Integer> results);
    }


}
