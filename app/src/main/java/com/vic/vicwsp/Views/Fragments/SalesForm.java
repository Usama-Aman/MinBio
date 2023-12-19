package com.vic.vicwsp.Views.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.vic.vicwsp.Controllers.Helpers.CustomSearchableSpinner;
import com.vic.vicwsp.Controllers.Interfaces.Api;
import com.vic.vicwsp.Models.PathCheck;
import com.vic.vicwsp.Models.Response.EditProduct.EditProductModel;
import com.vic.vicwsp.Models.Response.GradientsResearch.Classes;
import com.vic.vicwsp.Models.Response.GradientsResearch.Country;
import com.vic.vicwsp.Models.Response.GradientsResearch.Gradients;
import com.vic.vicwsp.Models.Response.GradientsResearch.Size;
import com.vic.vicwsp.Models.Response.GradientsResearch.Unit;
import com.vic.vicwsp.Models.Response.GradientsResearch.Vat;
import com.vic.vicwsp.Models.Response.SalesForm.SalesFormModel;
import com.vic.vicwsp.Network.RetrofitClient;
import com.vic.vicwsp.R;
import com.vic.vicwsp.Utils.AppUtils;
import com.vic.vicwsp.Utils.ApplicationClass;
import com.vic.vicwsp.Utils.Common;
import com.vic.vicwsp.Utils.Constants;
import com.vic.vicwsp.Utils.DecimalDigitsInputFilter;
import com.vic.vicwsp.Utils.SharedPreference;
import com.vic.vicwsp.Views.Activities.MainActivity;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.suke.widget.SwitchButton;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static androidx.constraintlayout.widget.Constraints.TAG;
import static com.vic.vicwsp.Utils.Common.dissmissDialog;
import static com.vic.vicwsp.Utils.Common.showDialog;
import static com.vic.vicwsp.Utils.Common.showToast;

public class SalesForm extends Fragment implements View.OnClickListener {


    private View v;
    private SalesFormModel salesFormModel;
    private ArrayList<String> strings = new ArrayList<>();
    private EditText salesPrice, salesQuantity, salesCase, minPrice, salesDiscount, etSaleName, etSaleDetail;
    private int isNego = 0, isBio = 0;
    private SwitchButton negoButton, bioButton;
    private ConstraintLayout btnSales;
    private TextView priceSign, tvPriceSales;
    private Spinner tvStockUnit;
    private int categoryId, productId, countryId = 0, sizeId = 0, classId = 0, unitId = 0, vatId = 0;
    private ConstraintLayout cartConstraintLayout;
    private int productIdFromBundle = 0;
    private CustomSearchableSpinner originSpinner, salesSizesSpinner, salesClassesSpinner, vatSpinner;
    private ArrayList<Country> countriesArrayList = new ArrayList<>();
    private ArrayList<Size> sizesArrayList = new ArrayList<>();
    private ArrayList<Classes> classesArrayList = new ArrayList<>();
    private ArrayList<Unit> units = new ArrayList<>();
    private ArrayList<Vat> vats = new ArrayList<>();
    //    private String unit = "";
    private TextView tvNegoThreshold, tvSaleCaseUnit;
    private ImageView photo1, photo2, photo3, upl1, upl2, upl3, cross1, cross2, cross3;
    private ImageView addProduct, dropdownSaleCase, dropdownInventory;
    private EditProductModel editProductModel;
    private int saleProductId = 0;
    private int imageUploadCount = 0;
    private HashMap<Integer, PathCheck> hashMap = new HashMap<>();
    private String mesageToShowAtTheEnd = "", subunit_id = "", vatValue = "";
    private int sendFilesCount = 0;
    private boolean uploadStatus = false;
    private AlertDialog alertDialog;
    private int loopCounter = 0;

    private double priceWithDot = 0.0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_sales_form, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        initViews();
        return v;
    }


    //Initializing the views
    private void initViews() {


        RecyclerView recyclerView = getActivity().findViewById(R.id.pricesTicker);
        recyclerView.setVisibility(View.VISIBLE);
        cartConstraintLayout = getActivity().findViewById(R.id.cartConstraintLayout);
        cartConstraintLayout.setVisibility(View.VISIBLE);
        tvPriceSales = v.findViewById(R.id.tvPriceSales);
        salesDiscount = v.findViewById(R.id.etSalesDiscount);


        minPrice = v.findViewById(R.id.etSalesMinPrice);
        salesPrice = v.findViewById(R.id.etSalesPrice);
        priceSign = v.findViewById(R.id.tvSalesPriceSign);
        salesCase = v.findViewById(R.id.etSalesCase);
        salesCase.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(5, 2)});

        originSpinner = v.findViewById(R.id.salesOriginSspinner);
        priceSign.setText(SharedPreference.getSimpleString(getContext(), Constants.currency));
        salesQuantity = v.findViewById(R.id.etSalesQuantity);
        negoButton = v.findViewById(R.id.negoSwtichSales);
        tvNegoThreshold = v.findViewById(R.id.tvNegoThreshold);

        dropdownInventory = v.findViewById(R.id.dropDownInventory);
        dropdownSaleCase = v.findViewById(R.id.dropDownSaleCase);

        salesSizesSpinner = v.findViewById(R.id.salesSizesSspinner);
        salesClassesSpinner = v.findViewById(R.id.salesClassSspinner);
        vatSpinner = v.findViewById(R.id.vatSspinner);

        tvStockUnit = v.findViewById(R.id.tvStockUnit);
        tvSaleCaseUnit = v.findViewById(R.id.tvSaleCaseUnit);

        cross1 = v.findViewById(R.id.cross1);
        cross1.setOnClickListener(this);
        cross2 = v.findViewById(R.id.cross2);
        cross2.setOnClickListener(this);
        cross3 = v.findViewById(R.id.cross3);
        cross3.setOnClickListener(this);

        photo1 = v.findViewById(R.id.photo1);
        upl1 = v.findViewById(R.id.upl1);
        photo1.setOnClickListener(this);
        photo2 = v.findViewById(R.id.photo2);
        upl2 = v.findViewById(R.id.upl2);
        photo2.setOnClickListener(this);
        photo3 = v.findViewById(R.id.photo3);
        upl3 = v.findViewById(R.id.upl3);
        photo3.setOnClickListener(this);

        etSaleName = v.findViewById(R.id.etSalesName);
        etSaleDetail = v.findViewById(R.id.etSalesDetails);

        negoButton.setOnCheckedChangeListener((view, isChecked) -> {
            if (isChecked) {
                isNego = 1;
                tvNegoThreshold.setVisibility(View.VISIBLE);
                minPrice.setVisibility(View.VISIBLE);
            } else {
                isNego = 0;
                tvNegoThreshold.setVisibility(View.GONE);
                minPrice.setVisibility(View.GONE);
            }
        });
        bioButton = v.findViewById(R.id.bioSwtichSales);
        bioButton.setOnCheckedChangeListener((view, isChecked) -> {
            if (isChecked)
                isBio = 1;
            else
                isBio = 0;
        });
        btnSales = v.findViewById(R.id.btnSales);
        btnSales.setOnClickListener(view -> {
            validate();
        });

        TextView heading = v.findViewById(R.id.tvMerchant);
        heading.setText(getContext().getResources().getString(R.string.sales_form, ""));

        if (!getArguments().getBoolean("fromEdit"))
            if (SharedPreference.getSimpleString(getContext(), Constants.isBioOn).equals("true")) {
                bioButton.setChecked(true);
                isBio = 1;
            }


        if (getArguments().getBoolean("fromEdit")) {
            productIdFromBundle = getArguments().getInt("productId");
            saleProductId = getArguments().getInt("saleProductId");
            getGradients();

        } else {
            productIdFromBundle = getArguments().getInt("productId");
            getGradients();
        }

        localeDecimalInput();
        savingPreviousValueOnFocus();
    }

    private void localeDecimalInput() {

        salesPrice.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().contains(".") || editable.toString().contains(","))
                    salesPrice.setKeyListener(DigitsKeyListener.getInstance("0123456789"));
                else
                    salesPrice.setKeyListener(DigitsKeyListener.getInstance("0123456789.,"));
            }
        });
    }

    private void savingPreviousValueOnFocus() {

        final String[] previousValue = new String[1];

        salesQuantity.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                previousValue[0] = salesQuantity.getText().toString();
                salesQuantity.setText("");


            } else {
                if (salesQuantity.getText().toString().equals(""))
                    salesQuantity.setText(previousValue[0]);
                else
                    previousValue[0] = salesQuantity.getText().toString();

                inventoryCalculationFormula();
            }
        });

        salesPrice.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                previousValue[0] = salesPrice.getText().toString();
                salesPrice.setText("");
            } else {
                if (salesPrice.getText().toString().equals(""))
                    salesPrice.setText(previousValue[0]);
                else
                    previousValue[0] = salesPrice.getText().toString();
            }
        });

        salesCase.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                previousValue[0] = salesCase.getText().toString();
                salesCase.setText("");
            } else {
                if (salesCase.getText().toString().equals(""))
                    salesCase.setText(previousValue[0]);
                else
                    previousValue[0] = salesCase.getText().toString();

                inventoryCalculationFormula();
            }
        });


        minPrice.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                previousValue[0] = minPrice.getText().toString();
                minPrice.setText("");
            } else {
                if (minPrice.getText().toString().equals(""))
                    minPrice.setText(previousValue[0]);
                else
                    previousValue[0] = minPrice.getText().toString();
            }
        });


        salesDiscount.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                previousValue[0] = salesDiscount.getText().toString();
                salesDiscount.setText("");
            } else {
                if (salesDiscount.getText().toString().equals(""))
                    salesDiscount.setText(previousValue[0]);
                else
                    previousValue[0] = salesDiscount.getText().toString();
            }
        });


    }

    //validating the fields
    private void validate() {


        if (etSaleName.getText().toString().isEmpty()) {
            showToast(((AppCompatActivity) getContext()), getContext().getResources().getString(R.string.SalesFormErrorMessagePlease_enter_product_name), false);
            return;
        }

        if (salesQuantity.getText().toString().isEmpty()) {
            showToast(((AppCompatActivity) getContext()), getContext().getResources().getString(R.string.SalesFormErrorMessagePleaseEnterQuantity), false);
            return;
        }

        if (AppUtils.convertStringToDouble(salesQuantity.getText().toString()) <= 0) {

            showToast(((AppCompatActivity) getContext()), getContext().getResources().getString(R.string.SalesFormErrorMessageQuantityCantBeZero), false);
            return;
        }

        if (salesPrice.getText().toString().isEmpty()) {
            showToast(((AppCompatActivity) getContext()), getContext().getResources().getString(R.string.SalesFormErrorMessagePleaseEnterPrice), false);
            return;
        }

        if (salesPrice.getText().toString().equals(".") || salesPrice.getText().toString().equals(",")) {
            showToast(((AppCompatActivity) getContext()), getContext().getResources().getString(R.string.SalesFormErrorMessagePleaseEnterPrice), false);
            return;
        }

        priceWithDot = AppUtils.convertStringToDouble(salesPrice.getText().toString().replaceAll("\\,+", ".").replaceAll("\\.+", "."));

        if (priceWithDot <= 0) {
            showToast(((AppCompatActivity) getContext()), getContext().getResources().getString(R.string.SalesFormErrorMessagePriceCantBeZero), false);
            return;
        }

        if (salesCase.getText().toString().isEmpty()) {
            showToast(((AppCompatActivity) getContext()), getContext().getResources().getString(R.string.SalesFormErrorMessageEnterSalesCaseAmount), false);

            return;
        }

        if (AppUtils.convertStringToFloat(salesCase.getText().toString()) <= 0) {
            showToast(((AppCompatActivity) getContext()), getContext().getResources().getString(R.string.SalesFormErrorMessageSaleCaseCantBeZero), false);
            return;
        }

    /*    if (Integer.parseInt(salesQuantity.getText().toString()) % Integer.parseInt(salesCase.getText().toString()) != 0) {
            showToast((AppCompatActivity) getContext(), getContext().getResources().getString(R.string.SalesFormErrorMessageQuantityShouldMatchIwthBox), false);
            return;
        }*/
    /*    if (AppUtils.convertStringToFloat(salesQuantity.getText().toString()) % AppUtils.convertStringToFloat(salesCase.getText().toString()) != 0) {
            showToast((AppCompatActivity) getContext(), getContext().getResources().getString(R.string.SalesFormErrorMessageQuantityShouldMatchIwthBox), false);
            return;
        }*/

        if (isNego == 1) {

            if (minPrice.getText().toString().isEmpty()) {

                showToast(((AppCompatActivity) getContext()), getContext().getResources().getString(R.string.SalesFormErrorMessageplease_enter_min_price), false);
                return;
            }

            if (AppUtils.convertStringToDouble(minPrice.getText().toString()) == 0) {
                showToast(((AppCompatActivity) getContext()), getContext().getResources().getString(R.string.SalesFormErrorMessageMin_price_cant_be_zero), false);
                return;
            }

            if (Common.round(AppUtils.convertStringToDouble(minPrice.getText().toString()), 2) >= Common.round(priceWithDot, 2)) {
                showToast(((AppCompatActivity) getContext()), getContext().getResources().getString(R.string.min_price_cannot_be_greater_than_actual_price), false);
                return;
            }
        }

        if (!salesDiscount.getText().toString().isEmpty())
            if (AppUtils.convertStringToDouble(salesDiscount.getText().toString()) > 100) {
                showToast(((AppCompatActivity) getContext()), getContext().getResources().getString(R.string.discount_cannot_be_grator_than_100), false);
                return;
            }

        if (etSaleDetail.getText().toString().isEmpty()) {
            showToast(((AppCompatActivity) getContext()), getContext().getResources().getString(R.string.SalesFormErrorMessageplease_enter_product_detail), false);

            return;
        }

        if (hashMap.size() == 0) {
            showToast((AppCompatActivity) getContext(), getContext().getResources().getString(R.string.add_one_picture_of_product), false);
            return;
        }

        addProduct();

    }

    //calling the add product api
    private void addProduct() {

        String discount, min, max;

        if (salesDiscount.getText().toString().isEmpty())
            discount = "0.00";
        else
            discount = salesDiscount.getText().toString();

        if (isNego == 0) {
            min = "0.0";
        } else {
            min = minPrice.getText().toString();
            min = String.valueOf(Math.floor(AppUtils.convertStringToDouble(min) * 100) / 100);
        }

        double x = Math.floor(priceWithDot * 100) / 100;

        showDialog(getContext());

        Api api = RetrofitClient.getClient().create(Api.class);
        Call<ResponseBody> call = null;

        if (getArguments().getBoolean("fromEdit")) {

            call = api.addProductWithId("Bearer " +
                            SharedPreference.getSimpleString(getContext(), Constants.accessToken), saleProductId,
                    productIdFromBundle, isBio, isNego, x, AppUtils.convertStringToFloat(salesQuantity.getText().toString()),
                    AppUtils.convertStringToDouble(min), AppUtils.convertStringToFloat(salesCase.getText().toString()), vatValue, vatId,
                    AppUtils.convertStringToDouble(discount), sizeId, classId, unitId, subunit_id, countryId, etSaleName.getText().toString(),
                    etSaleDetail.getText().toString());

        } else {

            call = api.addProductWithoutId("Bearer " +
                            SharedPreference.getSimpleString(getContext(), Constants.accessToken), productIdFromBundle, isBio, isNego,
                    x, AppUtils.convertStringToFloat(salesQuantity.getText().toString()),
                    AppUtils.convertStringToDouble(min), AppUtils.convertStringToFloat(salesCase.getText().toString()), vatValue, vatId,
                    AppUtils.convertStringToDouble(discount), sizeId, classId, countryId, unitId, subunit_id, etSaleName.getText().toString(),
                    etSaleDetail.getText().toString());

        }

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    JSONObject jsonObject = null;
                    if (response.isSuccessful()) {
                        jsonObject = new JSONObject(response.body().string());
                        mesageToShowAtTheEnd = jsonObject.getString("message");

                        JSONObject data = jsonObject.getJSONObject("data");
                        saleProductId = data.getInt("subproduct_id");

                        sendingFiles();

                        //sending emit of the Prices if updated
                        JSONObject emitJson = new JSONObject();
                        emitJson.put("price", String.valueOf(priceWithDot));
                        emitJson.put("merchant_id", SharedPreference.getSimpleString(getContext(), Constants.userId));
                        emitJson.put("product_id", String.valueOf(productIdFromBundle));
                        emitJson.put("subproduct_id", String.valueOf(saleProductId));

                        ApplicationClass applicationClass = (ApplicationClass) getActivity().getApplication();
                        applicationClass.emit(ApplicationClass.update_product, emitJson, getContext());
                    } else {
                        Common.dissmissDialog();
                        jsonObject = new JSONObject(response.errorBody().string());
                        showToast(((AppCompatActivity) getContext()), jsonObject.getString("message"), false);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Common.dissmissDialog();
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });


    }

    private void getSalesFormDataUpdated() {
        Api api = RetrofitClient.getClient().create(Api.class);
        Call<EditProductModel> call;
        if (getArguments().getBoolean("fromEdit")) {
            call = api.getEditProduct("Bearer " +
                    SharedPreference.getSimpleString(getContext(), Constants.accessToken), saleProductId);

        } else {

            call = api.getEditProduct("Bearer " +
                    SharedPreference.getSimpleString(getContext(), Constants.accessToken), productIdFromBundle);
        }


        call.enqueue(new Callback<EditProductModel>() {
            @Override
            public void onResponse(Call<EditProductModel> call, Response<EditProductModel> response) {
                dissmissDialog();
                JSONObject jsonObject = null;
                try {
                    if (response.isSuccessful()) {

                        editProductModel = new EditProductModel(response.body().getData(), response.body().getStatus(),
                                response.body().getMessage());

                        salesPrice.setText(editProductModel.getData().getPrice());
                        salesCase.setText(String.valueOf(editProductModel.getData().getSaleCase()));
                        salesQuantity.setText(String.valueOf(editProductModel.getData().getStock()));
                        minPrice.setText(editProductModel.getData().getMinprice());
                        if (!editProductModel.getData().getDiscount().equals("0.00"))
                            salesDiscount.setText(editProductModel.getData().getDiscount());
                        countryId = (editProductModel.getData().getCountryId());
                        sizeId = (editProductModel.getData().getSizeId());
                        unitId = (editProductModel.getData().getUnitId());
                        subunit_id = String.valueOf(editProductModel.getData().getSubunit_id());
                        vatId = editProductModel.getData().getVatId();
                        vatValue = editProductModel.getData().getVat();
                        classId = (editProductModel.getData().getClassId());
                        etSaleName.setText(editProductModel.getData().getProductVariety());
                        etSaleDetail.setText(editProductModel.getData().getProDescription());


                        hashMap.clear();
                        for (int i = 0; i < editProductModel.getData().getFiles().size(); i++) {
                            hashMap.put(i, new PathCheck(editProductModel.getData().getFiles().get(i).getId(),
                                    editProductModel.getData().getFiles().get(i).getImagePath(), true));
                            try {
                                if (i == 0) {

                                    Glide.with(getContext()).load(editProductModel.getData().getFiles().get(i).getImagePath()).into(photo1);

                                    upl1.setVisibility(View.GONE);
                                    cross1.setVisibility(View.VISIBLE);
                                    photo1.setEnabled(false);
                                } else if (i == 1) {

                                    Glide.with(getContext()).load(editProductModel.getData().getFiles().get(i).getImagePath()).into(photo2);

                                    upl2.setVisibility(View.GONE);
                                    cross2.setVisibility(View.VISIBLE);
                                    photo2.setEnabled(false);
                                } else if (i == 2) {

                                    Glide.with(getContext()).load(editProductModel.getData().getFiles().get(i).getImagePath()).into(photo3);

                                    upl3.setVisibility(View.GONE);
                                    cross3.setVisibility(View.VISIBLE);
                                    photo3.setEnabled(false);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }


                        for (int i = 0; i < countriesArrayList.size(); i++) {
                            if (countriesArrayList.get(i).getId() == countryId) {
                                originSpinner.setSelection(i);
                            }
                        }
                        for (int i = 0; i < sizesArrayList.size(); i++) {
                            if (sizesArrayList.get(i).getId() == sizeId) {
                                salesSizesSpinner.setSelection(i);
                            }
                        }
                        for (int i = 0; i < classesArrayList.size(); i++) {
                            if (classesArrayList.get(i).getId() == classId) {
                                salesClassesSpinner.setSelection(i);
                            }
                        }
                        for (int i = 0; i < vats.size(); i++) {
                            if (vats.get(i).getId() == vatId) {
                                vatSpinner.setSelection(i);
                            }
                        }
                        for (int i = 0; i < units.size(); i++) {
                            if (units.get(i).getId() == unitId) {
                                tvStockUnit.setSelection(i);
                                tvSaleCaseUnit.setText(units.get(i).getSign());
                                tvPriceSales.setText(getContext().getResources().getString(R.string.priceperkg, units.get(i).getSign()));
                            }
                        }

                        if (editProductModel.getData().getIsNego() == 1) {
                            negoButton.setChecked(true);
                        } else
                            negoButton.setChecked(false);

                        if (editProductModel.getData().getIsBio() == 1) {
                            bioButton.setChecked(true);
                        } else
                            bioButton.setChecked(false);

                    } else {
                        jsonObject = new JSONObject(response.errorBody().string());
                        showToast(((AppCompatActivity) getContext()), jsonObject.getString("message"), false);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<EditProductModel> call, Throwable t) {
                dissmissDialog();
            }
        });
    }

    private void getGradients() {
        showDialog(getContext());
        Api api = RetrofitClient.getClient().create(Api.class);

        Call<Gradients> call = api.getGradients("Bearer " +
                SharedPreference.getSimpleString(getContext(), Constants.accessToken), productIdFromBundle);

        call.enqueue(new Callback<Gradients>() {
            @Override
            public void onResponse(Call<Gradients> call, Response<Gradients> response) {
                Common.dissmissDialog();
                JSONObject jsonObject = null;
                try {
                    if (response.isSuccessful()) {
                        countriesArrayList.clear();
                        sizesArrayList.clear();
                        classesArrayList.clear();
                        units.clear();
                        vats.clear();

                        Gradients gradients = new Gradients(response.body().getData(), response.body().getStatus(), response.body().getMessage());

                        countriesArrayList.addAll(gradients.getData().getCountries());
                        sizesArrayList.addAll(gradients.getData().getSizes());
                        classesArrayList.addAll(gradients.getData().getClasses());
                        units.addAll(gradients.getData().getUnits());
                        vats.addAll(gradients.getData().getVats());

                        setUpCountriesSpinner();

                    } else {
                        jsonObject = new JSONObject(response.errorBody().string());
                        showToast(((AppCompatActivity) getContext()), jsonObject.getString("message"), false);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<Gradients> call, Throwable t) {
                Common.dissmissDialog();
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });

    }

    private void setUpCountriesSpinner() {
        ArrayList<String> strings = new ArrayList<>();

        if (SharedPreference.getSimpleString(getContext(), Constants.language).equals("en"))
            for (int i = 0; i < countriesArrayList.size(); i++)
                strings.add(countriesArrayList.get(i).getName());
        else if (SharedPreference.getSimpleString(getContext(), Constants.language).equals("fr"))
            for (int i = 0; i < countriesArrayList.size(); i++)
                strings.add(countriesArrayList.get(i).getNameFr());


        originSpinner.setPositiveButton(getContext().getResources().getString(R.string.close_spinner));
        originSpinner.setTitle(getContext().getResources().getString(R.string.slect_origin));

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, strings);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        originSpinner.setAdapter(adapter);
        originSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                countryId = countriesArrayList.get(adapterView.getSelectedItemPosition()).getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        setUpSizesSpinner();

    }

    private void setUpSizesSpinner() {

        ArrayList<String> strings = new ArrayList<>();

        for (int i = 0; i < sizesArrayList.size(); i++) {
            strings.add(sizesArrayList.get(i).getSign());
        }

        salesSizesSpinner.setPositiveButton(getContext().getResources().getString(R.string.close_spinner));
        salesSizesSpinner.setTitle(getContext().getResources().getString(R.string.product_select_size));

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, strings);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        salesSizesSpinner.setAdapter(adapter);
        salesSizesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                sizeId = sizesArrayList.get(adapterView.getSelectedItemPosition()).getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        setUpClassSpinner();
    }

    private void setUpClassSpinner() {

        ArrayList<String> strings = new ArrayList<>();

        for (int i = 0; i < classesArrayList.size(); i++) {
            strings.add(classesArrayList.get(i).getSign());
        }

        salesClassesSpinner.setPositiveButton(getContext().getResources().getString(R.string.close_spinner));
        salesClassesSpinner.setTitle(getContext().getResources().getString(R.string.product_select_class));

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, strings);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        salesClassesSpinner.setAdapter(adapter);
        salesClassesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                classId = classesArrayList.get(adapterView.getSelectedItemPosition()).getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        ArrayList<String> unitStrings = new ArrayList<>();
        ArrayList<String> saleCaseUnitStrings = new ArrayList<>();

        for (int j = 0; j < units.size(); j++)
            unitStrings.add(units.get(j).getSign());

        ArrayAdapter dataAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, unitStrings);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tvStockUnit.setAdapter(dataAdapter);

        tvStockUnit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                unitId = units.get(adapterView.getSelectedItemPosition()).getId();
                tvSaleCaseUnit.setText(units.get(adapterView.getSelectedItemPosition()).getSign());
                tvPriceSales.setText(getContext().getResources().getString(R.string.priceperkg, units.get(adapterView.getSelectedItemPosition()).getSign()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        ArrayList<String> vatsStrings = new ArrayList<>();

        for (int j = 0; j < vats.size(); j++)
            vatsStrings.add(vats.get(j).getValue());

        vatSpinner.setPositiveButton(getContext().getResources().getString(R.string.close_spinner));
        vatSpinner.setTitle(getContext().getResources().getString(R.string.product_select_vat));

        ArrayAdapter vatsAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, vatsStrings);
        vatsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        vatSpinner.setAdapter(vatsAdapter);

        vatSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                vatId = vats.get(adapterView.getSelectedItemPosition()).getId();
                vatValue = vats.get(adapterView.getSelectedItemPosition()).getValue();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        if (getArguments().getBoolean("fromEdit")) {
            getSalesFormDataUpdated();
        }
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.photo1:
                ImagePicker.Companion.with(this)
                        .crop(1f, 1f)
                        .compress(800)
                        .maxResultSize(1080, 1080)
                        .start(201);
                break;
            case R.id.photo2:
                ImagePicker.Companion.with(this)
                        .crop(1f, 1f)
                        .compress(800)
                        .maxResultSize(1080, 1080)
                        .start(202);
                break;
            case R.id.photo3:
                ImagePicker.Companion.with(this)
                        .crop(1f, 1f)
                        .compress(800)
                        .maxResultSize(1080, 1080)
                        .start(203);
                break;
            case R.id.cross1:

                alertDialog = new AlertDialog.Builder(getContext())
                        .setMessage(getContext().getResources().getString(R.string.delete_picture))
                        .setPositiveButton(getContext().getResources().getString(R.string.yes), (dialogInterface, i) -> {
                            if (hashMap.containsKey(0))
                                if (hashMap.get(0).isURL())
                                    deleteImage(hashMap.get(0).getId());
                            hashMap.remove(0);
                            cross1.setVisibility(View.GONE);
                            upl1.setVisibility(View.VISIBLE);
                            photo1.setImageDrawable(null);
                            photo1.setEnabled(true);
                        })
                        .setNegativeButton(getContext().getResources().getString(R.string.no), (dialogInterface, i) -> {
                            alertDialog.dismiss();
                        })
                        .show();
                break;
            case R.id.cross2:

                alertDialog = new AlertDialog.Builder(getContext())
                        .setMessage(getContext().getResources().getString(R.string.delete_picture))
                        .setPositiveButton(getContext().getResources().getString(R.string.yes), (dialogInterface, i) -> {
                            if (hashMap.containsKey(1))
                                if (hashMap.get(1).isURL())
                                    deleteImage(hashMap.get(1).getId());
                            hashMap.remove(1);
                            cross2.setVisibility(View.GONE);
                            upl2.setVisibility(View.VISIBLE);
                            photo2.setImageDrawable(null);
                            photo2.setEnabled(true);
                        })
                        .setNegativeButton(getContext().getResources().getString(R.string.no), (dialogInterface, i) -> {
                            alertDialog.dismiss();
                        })
                        .show();
                break;
            case R.id.cross3:

                alertDialog = new AlertDialog.Builder(getContext())
                        .setMessage(getContext().getResources().getString(R.string.delete_picture))
                        .setPositiveButton(getContext().getResources().getString(R.string.yes), (dialogInterface, i) -> {
                            if (hashMap.containsKey(2))
                                if (hashMap.get(2).isURL())
                                    deleteImage(hashMap.get(2).getId());
                            hashMap.remove(2);
                            cross3.setVisibility(View.GONE);
                            upl3.setVisibility(View.VISIBLE);
                            photo3.setImageDrawable(null);
                            photo3.setEnabled(true);
                        })
                        .setNegativeButton(getContext().getResources().getString(R.string.no), (dialogInterface, i) -> {
                            alertDialog.dismiss();
                        })
                        .show();
                break;
            default:
                break;

        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 201) {
                Uri fileUri = data.getData();
                if (fileUri != null) {

                    hashMap.put(0, new PathCheck(0, fileUri.getPath(), false));

                    File file = new File(fileUri.getPath());
                    Glide.with(getContext()).load(file).into(photo1);
                    upl1.setVisibility(View.GONE);
                    cross1.setVisibility(View.VISIBLE);
                    photo1.setEnabled(false);
                }
            } else if (requestCode == 202) {
                Uri fileUri = data.getData();
                if (fileUri != null) {

                    hashMap.put(1, new PathCheck(0, fileUri.getPath(), false));
                    File file = new File(fileUri.getPath());
                    Glide.with(getContext()).load(file).into(photo2);
                    upl2.setVisibility(View.GONE);
                    cross2.setVisibility(View.VISIBLE);
                    photo2.setEnabled(false);

                }
            } else if (requestCode == 203) {
                Uri fileUri = data.getData();
                if (fileUri != null) {

                    hashMap.put(2, new PathCheck(0, fileUri.getPath(), false));

                    File file = new File(fileUri.getPath());
                    Glide.with(getContext()).load(file).into(photo3);

                    upl3.setVisibility(View.GONE);
                    cross3.setVisibility(View.VISIBLE);
                    photo3.setEnabled(false);
                }
            }
        }
    }

    private void deleteImage(Integer id) {

        Api api = RetrofitClient.getClient().create(Api.class);

        Call<ResponseBody> call = api.deleteFiles("Bearer " +
                SharedPreference.getSimpleString(getContext(), Constants.accessToken), id);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Common.dissmissDialog();
                JSONObject jsonObject = null;
                try {
                    if (response.isSuccessful()) {

                        jsonObject = new JSONObject(response.body().string());
                        if (jsonObject.getBoolean("status")) {
                            Common.showToast(((AppCompatActivity) getContext()), jsonObject.getString("message"), true);
                        } else
                            Common.showToast(((AppCompatActivity) getContext()), jsonObject.getString("message"), false);

                    } else {
                        jsonObject = new JSONObject(response.errorBody().string());
                        Common.showToast(((AppCompatActivity) getContext()), jsonObject.getString("message"), false);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Common.dissmissDialog();
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });

    }

    private void sendingFiles() {

        if (hashMap.size() == 0) {
            goBack();
            return;
        }

        if (hashMap.containsKey(loopCounter)) {
            if (!hashMap.get(loopCounter).isURL()) {
                if (!hashMap.get(loopCounter).getPath().equals("")) {
                    sendToServer(hashMap.get(loopCounter).getPath());
                } else {
                    hashMap.remove(loopCounter);
                    loopCounter++;
                    sendingFiles();
                }
            } else {
                hashMap.remove(loopCounter);
                loopCounter++;
                sendingFiles();
            }
        } else {
            hashMap.remove(loopCounter);
            loopCounter++;
            sendingFiles();
        }

    }

    private void sendToServer(String s) {

        File file = new File(s);

        RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), reqFile);
        RequestBody spi = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(saleProductId));

        Api api = RetrofitClient.getClient().create(Api.class);
        Call<ResponseBody> call = api.sendFiles("Bearer " +
                SharedPreference.getSimpleString(getContext(), Constants.accessToken), spi, body);


        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                JSONObject jsonObject = null;
                try {
                    if (response.isSuccessful()) {

                        jsonObject = new JSONObject(response.body().string());

                        if (jsonObject.has("status")) {
                            if (jsonObject.getBoolean("status")) {
                                hashMap.remove(loopCounter);
                                loopCounter++;
                                sendingFiles();
                            }
                        }
                        Log.d(TAG, "onResponse: Success" + response);
                    } else
                        Log.d(TAG, "onResponse: Error" + response);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Common.dissmissDialog();
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });


    }

    private void goBack() {

        Common.dissmissDialog();
        showToast(((AppCompatActivity) getContext()), mesageToShowAtTheEnd, true);
        ((MainActivity) getContext()).comeToHome();

    }

    private void inventoryCalculationFormula() {

        if (salesQuantity.getText().toString().isEmpty()) {
            return;
        }

        if (AppUtils.convertStringToDouble(salesQuantity.getText().toString()) <= 0) {
            return;
        }

        if (salesCase.getText().toString().isEmpty()) {
            return;
        }

        if (AppUtils.convertStringToFloat(salesCase.getText().toString()) <= 0) {
            return;
        }

        if (AppUtils.convertStringToFloat(salesQuantity.getText().toString()) % AppUtils.convertStringToFloat(salesCase.getText().toString()) == 0) {
            return;
        }


        float quotient = AppUtils.convertStringToFloat(salesQuantity.getText().toString()) / AppUtils.convertStringToFloat(salesCase.getText().toString());
        float productResult = (int) AppUtils.convertStringToFloat(String.format(Locale.ENGLISH,"%.2f", quotient)) * AppUtils.convertStringToFloat(salesCase.getText().toString());
        salesQuantity.setText(String.format(Locale.ENGLISH, "%.1f", productResult));


    }
}
