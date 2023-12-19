package com.vic.vicwsp.Views.Fragments;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.vic.vicwsp.Controllers.Helpers.AutoSuggestAdapter;
import com.vic.vicwsp.Controllers.Helpers.CustomSearchableSpinner;
import com.vic.vicwsp.Controllers.Helpers.WorkFlowSelectionDialog;
import com.vic.vicwsp.Controllers.Interfaces.Api;
import com.vic.vicwsp.Controllers.Interfaces.SocketCallback;
import com.vic.vicwsp.Models.CustomObject;
import com.vic.vicwsp.Models.Response.SearchData.Country;
import com.vic.vicwsp.Models.Response.SearchData.Merchant;
import com.vic.vicwsp.Models.Response.SearchData.SearchDataModel;
import com.vic.vicwsp.Network.RetrofitClient;
import com.vic.vicwsp.R;
import com.vic.vicwsp.Utils.AppUtils;
import com.vic.vicwsp.Utils.ApplicationClass;
import com.vic.vicwsp.Utils.Common;
import com.vic.vicwsp.Utils.Constants;
import com.vic.vicwsp.Utils.SharedPreference;
import com.vic.vicwsp.Views.Activities.MainActivity;
import com.vic.vicwsp.Views.Activities.SignIn;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static androidx.constraintlayout.widget.Constraints.TAG;
import static com.vic.vicwsp.Utils.ApplicationClass.update_product;
import static com.vic.vicwsp.Utils.Common.dissmissDialog;
import static com.vic.vicwsp.Utils.Common.showToast;
import static com.vic.vicwsp.Views.Activities.MainActivity.sockets;

public class Research extends Fragment implements SocketCallback {

    private View v;
    private RecyclerView filerResultRecycler;
    private EditText min, max;
    private AutoCompleteTextView productSearch;
    private ImageView research;
    private ImageView logout, back;
    private ConstraintLayout cartConstraint, vendorConstraint;
    private SearchDataModel searchDataModel;
    private CustomSearchableSpinner originSpinner;
    private int productId = 0, merchantId = 0, originId = 0;
    private ArrayList<Country> countries = new ArrayList<>();
    private ArrayList<Merchant> merchants = new ArrayList<>();
    private TextView vendorsSpinnerText;
    private String vendorText = "";
    private ConstraintLayout crossSearch, supportConstraint;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_research, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        initViews();

        if (searchDataModel == null)
            getResearchData();

        if (searchDataModel != null) {
            try {
                setUpVendorAndOrigin();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        sockets.initializeCallback(this);
        if (((MainActivity) getContext()).tickerModelArrayList.size() == 0) {
            Log.d("isConnected", String.valueOf(sockets.getSocket().connected()));
            sockets.emit(ApplicationClass.ticker);
        }

        return v;
    }

    private void initViews() {
        RecyclerView recyclerView = getActivity().findViewById(R.id.pricesTicker);
        recyclerView.setVisibility(View.VISIBLE);

        supportConstraint = getActivity().findViewById(R.id.toolbarSupport);
        supportConstraint.setVisibility(View.VISIBLE);
        supportConstraint.setOnClickListener(v -> {
            ((MainActivity) getContext()).showSupportDialog();
        });

        ConstraintLayout changeLanguageLayout = getActivity().findViewById(R.id.changeLanguageLayout);
        changeLanguageLayout.setVisibility(View.GONE);

        TextView notificationBadge = getActivity().findViewById(R.id.notificationBadge);
        notificationBadge.setVisibility(View.VISIBLE);
        ImageView ivNotifications = getActivity().findViewById(R.id.ivNotifications);
        ivNotifications.setVisibility(View.VISIBLE);

        //Updating the notification badge
        int c = ((MainActivity) getContext()).notificationCount;
        ((MainActivity) getContext()).notificationCount = 0;
        ((MainActivity) getContext()).setNotificationBadge(c, 0);

        logout = getActivity().findViewById(R.id.ivToolbarLogout);
        back = getActivity().findViewById(R.id.ivToolbarBack);
        cartConstraint = getActivity().findViewById(R.id.cartConstraintLayout);
        cartConstraint.setVisibility(View.VISIBLE);
        logout.setVisibility(View.GONE);
        back.setVisibility(View.GONE);
        crossSearch = v.findViewById(R.id.crossSearch);
        productSearch = v.findViewById(R.id.etSearch);
        vendorsSpinnerText = v.findViewById(R.id.vendorsSpinnerText);
        originSpinner = v.findViewById(R.id.originSpinner);
        vendorConstraint = v.findViewById(R.id.vendorConstraint);

        min = v.findViewById(R.id.etMinPrice);
        max = v.findViewById(R.id.etMaxPrice);
        research = v.findViewById(R.id.btnResearch);
        research.setOnClickListener(view -> validate());

        crossSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                productSearch.setText("");
            }
        });

    }

    //    validating the fields
    private void validate() {

        if (!min.getText().toString().isEmpty() && !max.getText().toString().isEmpty()) {
            if (AppUtils.convertStringToDouble(min.getText().toString()) >= AppUtils.convertStringToDouble(max.getText().toString())) {
                showToast(((AppCompatActivity) getContext()), getContext().getResources().getString(R.string.min_price_less), false);
                return;
            }
            if (AppUtils.convertStringToDouble(max.getText().toString()) <= AppUtils.convertStringToDouble(min.getText().toString())) {
                showToast(((AppCompatActivity) getContext()), getContext().getResources().getString(R.string.max_price_greater), false);
                return;
            }

            sendDataToBundle();

            return;
        } else {
            sendDataToBundle();
        }
    }

    private void sendDataToBundle() {
        Bundle bundle = new Bundle();
        bundle.putInt("productId", productId);
        bundle.putInt("merchantId", merchantId);
        bundle.putInt("originId", originId);
        bundle.putString("max", max.getText().toString());
        bundle.putString("min", min.getText().toString());

        ResearchResult researchResult = new ResearchResult();
        researchResult.setArguments(bundle);
        getFragmentManager().beginTransaction().replace(R.id.mainFragment, researchResult, "").addToBackStack("").commit();

    }

    //Calling the gradient api to get the gradients
    private void getResearchData() {
        Common.showDialog(getContext());
        Api api = RetrofitClient.getClient().create(Api.class);

        Call<SearchDataModel> call = api.getResearchData("Bearer " +
                SharedPreference.getSimpleString(getContext(), Constants.accessToken));

        call.enqueue(new Callback<SearchDataModel>() {
            @Override
            public void onResponse(Call<SearchDataModel> call, Response<SearchDataModel> response) {
                dissmissDialog();
                if (response.isSuccessful()) {
                    searchDataModel = new SearchDataModel(response.body().getStatus(), response.body().getMessage(), response.body().getData());
                    merchants.clear();
                    countries.clear();

                    merchants.add(new Merchant(0, "All", "All"));
                    countries.add(new Country(0, "All", "All", ""));

                    merchants.addAll(searchDataModel.getData().getMerchants());
                    countries.addAll(searchDataModel.getData().getCountries());

                    try {
                        setUpVendorAndOrigin();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (response.code() == 401) {
                    SharedPreference.saveSimpleString(getContext(), Constants.isLoggedIn, "false");
                    Intent i = new Intent(getContext(), SignIn.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                } else {
                    try {
                        JSONObject jsonObject = new JSONObject(response.errorBody().string());
                        showToast(((AppCompatActivity) getContext()), jsonObject.getString("message"), false);
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<SearchDataModel> call, Throwable t) {
                dissmissDialog();
                t.getMessage();
            }
        });


    }

    private void setUpVendorAndOrigin() throws Exception {

        ArrayList<CustomObject> products = new ArrayList<>();

        if (SharedPreference.getSimpleString(getContext(), Constants.language).equals("en")) {
            for (int i = 0; i < searchDataModel.getData().getProducts().size(); i++) {
                products.add(new CustomObject(searchDataModel.getData().getProducts().get(i).getId(), searchDataModel.getData().getProducts().get(i).getName()));
            }
        } else if (SharedPreference.getSimpleString(getContext(), Constants.language).equals("fr")) {
            for (int i = 0; i < searchDataModel.getData().getProducts().size(); i++) {
                products.add(new CustomObject(searchDataModel.getData().getProducts().get(i).getId(), searchDataModel.getData().getProducts().get(i).getNameFr()));
            }
        }

        try {
            AutoSuggestAdapter adapter = new AutoSuggestAdapter(getContext(), R.layout.item_search_filter, products);
            productSearch.setThreshold(1);
            productSearch.setAdapter(adapter);
        } catch (Exception e) {
            e.printStackTrace();
        }

        productSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CustomObject customObject = (CustomObject) parent.getItemAtPosition(position);
                productId = (int) customObject.getId();
            }
        });

        productSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (productSearch.getText().toString().isEmpty())
                    productId = 0;
            }
        });


        ArrayList<String> c = new ArrayList<>();

        if (SharedPreference.getSimpleString(getContext(), Constants.language).equals("en"))
            for (int i = 0; i < countries.size(); i++)
                c.add(countries.get(i).getName());
        else if (SharedPreference.getSimpleString(getContext(), Constants.language).equals("fr"))
            for (int i = 0; i < countries.size(); i++)
                c.add(countries.get(i).getNameFR());

        originSpinner.setPositiveButton(getContext().getResources().getString(R.string.close_spinner));
        originSpinner.setTitle(getContext().getResources().getString(R.string.researchSelectOrigin));

        ArrayAdapter countriesAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, c);
        originSpinner.setAdapter(countriesAdapter);

        originSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                originId = countries.get(adapterView.getSelectedItemPosition()).getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        if (vendorText.equals(""))
            vendorsSpinnerText.setText("All");
        else
            vendorsSpinnerText.setText(vendorText);

        ArrayList<CustomObject> vendors = new ArrayList<>();
        for (int i = 0; i < merchants.size(); i++)
            vendors.add(new CustomObject(merchants.get(i).getId(), merchants.get(i).getCompanyName()));


        vendorConstraint.setOnClickListener(v -> {
            WorkFlowSelectionDialog workFlowSelectionDialog = new WorkFlowSelectionDialog(getContext(), vendors);
            workFlowSelectionDialog.setOnworkflowlistclicklistener(customObject -> {
                merchantId = customObject.getId();
                vendorText = customObject.getName();
                vendorsSpinnerText.setText(customObject.getName());
                workFlowSelectionDialog.dismiss();
            });
            workFlowSelectionDialog.show();
        });
    }

    @Override
    public void onSuccess(JSONObject jsonObject, String tag) {
        if (tag.equals(update_product)) {
            updateTheProductPrice(jsonObject);
        } else if (tag.equals(ApplicationClass.ticker)) {
            getActivity().runOnUiThread(() -> {
                try {
                    Log.d(ContentValues.TAG, "onSuccess: " + "Ticker is Caling again n again");
                    ((MainActivity) getContext()).initializeTicker(jsonObject);
                    System.gc();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

    @Override
    public void onFailure(String message, String tag) {
        Log.d(TAG, "onFailure: " + tag);
    }

    private void updateTheProductPrice(JSONObject jsonObject) {
        try {
            String merchantId = jsonObject.getString("merchant_id");
            String productId = jsonObject.getString("product_id");
            String subProductId = jsonObject.getString("subproduct_id");
            String price = jsonObject.getString("price");
            String minPrice = jsonObject.getString("minprice");
            String subprominprice = jsonObject.getString("subprominprice");

            ((MainActivity) getContext()).updateTicker(Integer.parseInt(productId), Integer.parseInt(merchantId),
                    (price), (minPrice));


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Common.dissmissDialog();
    }
}
