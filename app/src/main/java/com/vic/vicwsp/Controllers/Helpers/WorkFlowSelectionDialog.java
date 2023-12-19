package com.vic.vicwsp.Controllers.Helpers;

import android.app.Dialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.TextView;


import com.vic.vicwsp.Models.CustomObject;
import com.vic.vicwsp.R;

import java.util.ArrayList;

public class WorkFlowSelectionDialog extends Dialog implements OnItemClickListener {

    private ArrayList<CustomObject> customObjects;

    private ArrayList<CustomObject> unfilteredData;
    private ArrayList<CustomObject> filteredData;

    private ListView workflowListView;
    private QariListAdapter listAdapter;
    private TextView dialogTitle;
    private EditText editTextSearch;
    private Button btnCloseSpinnerDialog;
    private OnWorkFlowListclickListener listclicklistener;

    public void setOnworkflowlistclicklistener(OnWorkFlowListclickListener listener) {
        this.listclicklistener = listener;
    }

    public interface OnWorkFlowListclickListener {
        void onWorkFlowListclick(CustomObject customObject);
    }

    public WorkFlowSelectionDialog(Context context, ArrayList<CustomObject> list_workflow) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_dialog_list_layout);
        dialogTitle = (TextView) findViewById(R.id.dilog_list_title);
        dialogTitle.setText(context.getResources().getString(R.string.researchSelectMerchant));
        workflowListView = (ListView) findViewById(R.id.listview_dialog);
        editTextSearch = findViewById(R.id.editTextSpinnerDialog);
        btnCloseSpinnerDialog = findViewById(R.id.btnCloseSpinnerDialog);
        customObjects = list_workflow;
        listAdapter = new QariListAdapter(context, customObjects);
        workflowListView.setAdapter(listAdapter);
        workflowListView.setOnItemClickListener(this);

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

    }

    private class QariListAdapter extends BaseAdapter implements Filterable {
        private Context mContext;

        private LayoutInflater mInflater;


        public QariListAdapter(Context context, ArrayList<CustomObject> list) {
            this.mContext = context;
            unfilteredData = list;
            filteredData = list;
            this.mInflater = LayoutInflater.from(mContext);
        }

        @Override
        public int getCount() {
            return filteredData.size();
        }

        @Override
        public CustomObject getItem(int position) {
            return filteredData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            CustomObject CustomObject = getItem(position);
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.custom_dialog_list_row, null);
                holder.workFlowName = (TextView) convertView.findViewById(R.id.textview_custom_dialog_list);
                convertView.setTag(holder);


            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.workFlowName.setText(filteredData.get(position).getName());

            return convertView;
        }

        @Override
        public Filter getFilter() {
            Filter filter = new Filter() {

                @SuppressWarnings("unchecked")
                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    if (results.count == 0) {
                        notifyDataSetInvalidated();
                    } else {

                        filteredData = (ArrayList<CustomObject>) results.values;
                        notifyDataSetChanged();
                    }
                }

                @Override
                protected FilterResults performFiltering(CharSequence charSequence) {

                    String charString = charSequence.toString();
                    if (charString.isEmpty()) {
                        filteredData = unfilteredData;
                    } else {
                        ArrayList<CustomObject> filteredList = new ArrayList<>();
                        for (CustomObject row : unfilteredData) {
                            if (row.getName().toLowerCase().contains(charString.toLowerCase())) {
                                filteredList.add(row);
                            }
                        }

                        filteredData = filteredList;
                    }

                    FilterResults filterResults = new FilterResults();
                    filterResults.values = filteredData;
                    return filterResults;

                }
            };

            return filter;
        }
    }

    private class ViewHolder {
        TextView workFlowName;

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        if (listclicklistener != null) {
            listclicklistener.onWorkFlowListclick(filteredData.get(position));
        }
    }
}
